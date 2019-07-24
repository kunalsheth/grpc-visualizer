package info.kunalsheth.grpcvisualizer;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import info.kunalsheth.grpcvisualizer.cli.Digraph;
import info.kunalsheth.grpcvisualizer.cli.DigraphServer;
import info.kunalsheth.grpcvisualizer.cli.MessageCLI;
import info.kunalsheth.grpcvisualizer.cli.ServiceCLI;
import info.kunalsheth.grpcvisualizer.prettyprint.AnsiFieldDescriptor;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static org.fusesource.jansi.Ansi.ansi;

public final class GrpcVisualizer {

    private static final String usage = "grpc-visualizer path/to/grpc-config.proto [\"command 1\" \"command 2\"...]\n" +
            "\n" +
            "commands: \n" +
            "\t'help'\n" +
            "\t'message [REGEX]' — display structure of message data\n" +
            "\t'digraph (svg|pdf|png|dot) [REGEX]' — display structure of message types in relation to each other\n" +
            "\t'service [REGEX]' — display client/server RPC functions\n" +
            "\t'gui' — open web server on port 8383 for interactive digraphs";
    private static final String messageCmd = "message(.*)";
    private static final String digraphCmd = "digraph\\s+(svg|pdf|png|dot)(.*)";
    private static final String serviceCmd = "service(.*)";
    private static final String guiCmd = "gui";

    private static final String hello = "Welcome to grpc-visualizer. For help, type 'help'.";
    private static final String prompt = "grpc-vis: ";

    private static FileDescriptorSet fds;
    private static File inputFile;

    public static void main(String[] args) throws IOException, InterruptedException {
        // user must specify gRPC config file
        if (args.length < 1) usgErrCrash();

        inputFile = new File(args[0]);
        fds = introspectConfig(args[0]);

        // variable used in lambda expression should be final or effectively final
        boolean isInteractive = args.length == 1;

        if (isInteractive) {
            System.out.println(ansi().eraseScreen().reset());
            System.out.println(hello);
            System.out.println("Analyzing '" + args[0] + "'");
            System.out.print(prompt);
        }

        (isInteractive ?
                new BufferedReader(new InputStreamReader(System.in)).lines() :
                Arrays.stream(args).skip(1)
        )
                .map(String::trim)
                .forEach(cmd -> {
                    if (cmd.equals("?") || cmd.equalsIgnoreCase("help")) {
                        System.out.println(usage);
                    } else if (cmd.matches(messageCmd)) {
                        String regex = ".*" + cmd.replaceAll(messageCmd, "$1").trim() + ".*";
                        if (!isValidRegex(regex)) System.err.println("Invalid regex: " + regex);
                        else printMessages(regex);
                    } else if (cmd.matches(digraphCmd)) {
                        String format = cmd.replaceAll(digraphCmd, "$1");
                        String regex = ".*" + cmd.replaceAll(digraphCmd, "$2").trim() + ".*";
                        if (!isValidRegex(regex)) System.err.println("Invalid regex: " + regex);
                        renderDigraph(format, regex);
                    } else if (cmd.matches(serviceCmd)) {
                        String regex = ".*" + cmd.replaceAll(serviceCmd, "$1").trim() + ".*";
                        if (!isValidRegex(regex)) System.err.println("Invalid regex: " + regex);
                        else printServices(regex);
                    } else if (cmd.matches(guiCmd)) {
                        DigraphServer.load();
                    } else if (!cmd.isEmpty()) {
                        if (isInteractive) {
                            System.err.println("Unrecognized command. Type 'help' for usage information.");
                        } else usgErrCrash();
                    }

                    if (isInteractive) System.out.print(prompt);
                });

        // should br.close()... but doesn't matter since we're exiting anyways
    }

    private static FileDescriptorSet introspectConfig(String grpcConfigPath) throws IOException, InterruptedException {
        // gRPC config file must exist
        File grpcConfig = new File(grpcConfigPath);
        if (!grpcConfig.exists()) usgErrCrash();

        // make sure Google's Protobuf compiler is installed and in PATH
        Process p = new ProcessBuilder("protoc", "-h").start();
        p.waitFor();
        if (p.exitValue() != 0) errCrash("protoc doesn't seem to be installed", 2);

        // generate `FileDescriptorSet` (think of this as reflection, but for Protobuf)
        File fdsFile = File.createTempFile("GrpcVisualizer", ".proto");
        p = new ProcessBuilder("protoc",
                /*"--include_source_info",*/ "--include_imports", "-o",
                fdsFile.getAbsolutePath(), grpcConfigPath
        )
                .inheritIO()
                .start();
        p.waitFor();
        if (p.exitValue() != 0) errCrash("protoc --include_imports -o did not exit cleanly", 3);

        return FileDescriptorSet.parseFrom(new FileInputStream(fdsFile));
    }

    private static void printMessages(String match) {
        Map<String, DescriptorProto> messages = allMessages();

        messages.entrySet().stream()
                .filter(e -> e.getKey().matches(match))
                .map(Map.Entry::getValue)
                .forEach(m -> {
                    MessageCLI.print(System.out, messages, m);
                    System.out.println();
                });
    }

    private static void renderDigraph(String format, String match) {
        Map<String, DescriptorProto> messages = allMessages();

        DescriptorProto parent = null;

        Set<DescriptorProto> matchedMessages = messages.entrySet().stream()
                .filter(e -> e.getKey().matches(match))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
        int matches = matchedMessages.size();

        if (matches == 1) parent = matchedMessages.iterator().next();
        else if (matches > 1)
            System.err.println("Regex matched more than one message. Generating generic digraph.");
        else
            System.err.println("Regex doesn't match any messages. Generating generic digraph.");

        try {
            Digraph.render(messages, parent, format, false);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println();
    }

    private static void printServices(String match) {
        fds.getFileList().stream()
                .map(FileDescriptorProto::getServiceList)
                .flatMap(Collection::stream)
                .filter(s -> s.getName().matches(match))
                .forEach(s -> {
                    ServiceCLI.print(s);
                    System.out.println();
                });
    }

    public static File getFile() {
        return inputFile;
    }

    public static Map<String, DescriptorProto> allMessages() {
        Set<DescriptorProto> messages = fds
                .getFileList()
                .stream()
                .map(FileDescriptorProto::getMessageTypeList)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        while (true) { // don't really care about efficiency...
            Set<DescriptorProto> nestedMessages = messages.stream()
                    .map(DescriptorProto::getNestedTypeList)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toSet());

            int originalSize = messages.size();
            messages.addAll(nestedMessages);
            if (messages.size() == originalSize) break;
        }

        return messages.stream().collect(Collectors.toMap(
                AnsiFieldDescriptor::simpleTypeName,
                identity -> identity
        ));
    }

    private static void usgErrCrash() {
        errCrash(usage, 1);
    }

    public static void errCrash(String msg, int code) {
        System.err.println(msg);
        System.exit(code);
    }

    private static boolean isValidRegex(String pattern) {
        try {
            Pattern.compile(pattern);
            return true;
        } catch (PatternSyntaxException e) {
            return false;
        }
    }
}
