package info.kunalsheth.grpcvisualizer.cli;

import com.google.protobuf.DescriptorProtos;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
import static info.kunalsheth.grpcvisualizer.GrpcVisualizer.errCrash;
import static info.kunalsheth.grpcvisualizer.prettyprint.AnsiFieldDescriptor.simpleTypeName;
import static info.kunalsheth.grpcvisualizer.prettyprint.AnsiFieldDescriptor.typeSuffix;

public class Digraph {

    public static void render(
            Map<String, DescriptorProtos.DescriptorProto> messages, String format
    ) throws IOException, InterruptedException {
        File dotSource = File.createTempFile(
                "GrpcVisualizer", ".dot"
        );

        PrintWriter dot = new PrintWriter(dotSource);
        dot.println("digraph {");
        dot.println("rankdir=\"LR\";");
        messages.forEach((containerName, containerNode) -> {
            String tooltip = containerNode.getFieldList()
                    .stream()
                    .map(f -> simpleTypeName(f) + typeSuffix(f) + " " + f.getName())
                    .collect(Collectors.joining("\n"));

            dot.println(containerName + " [" +
                    "shape=box " +
                    "fontname=Helvetica " +
                    "fontcolor=cyan4 " +
                    "style=rounded " +
                    "tooltip=\"" + tooltip + "\" " +
                    "]; ");

            containerNode.getFieldList().stream()
                    .filter(f -> messages.containsKey(simpleTypeName(f)))
                    .map(f -> arrowPrint(containerName, f))
                    .forEach(dot::println);
        });
        dot.println("}");
        dot.close();


        Process p = new ProcessBuilder("dot", "-?").start();
        p.waitFor();
        if (p.exitValue() != 0) errCrash("graphviz dot doesn't seem to be installed", 4);

        File output = new File("digraph." + format);
        p = new ProcessBuilder("dot", "-T" + format, dotSource.getAbsolutePath())
                .inheritIO()
                .redirectOutput(output)
                .start();
        p.waitFor();
        if (p.exitValue() != 0) errCrash("dot -T" + format + " did not exit cleanly", 5);

        System.out.println("Open " + output.getCanonicalPath() + " to view digraph.");
    }

    private static String arrowPrint(String container, DescriptorProtos.FieldDescriptorProto f) {
        boolean isRepeated = f.getLabel() == LABEL_REPEATED;
        String label = f.getName() + typeSuffix(f);

        String attrs = " [ " +
                "fontsize=12 " +
                "fontname=Courier " +
                "fontcolor=gray25 " +
                "label=\"" + label + "\" " +
                "style=" + (isRepeated ? "dashed" : "solid") + " " +
                "color=gray " +
                " ];";

        return container + " -> " + simpleTypeName(f) + attrs;
    }
}