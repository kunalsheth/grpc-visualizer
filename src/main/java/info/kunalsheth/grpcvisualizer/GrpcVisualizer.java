package info.kunalsheth.grpcvisualizer;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.DescriptorProtos.ServiceDescriptorProto;
import info.kunalsheth.grpcvisualizer.cli.PrettyCLI;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;

public final class GrpcVisualizer {

    private static final String usage = "java GrpcVisualizer relative/path/to/grpc-config.proto";

    public static void main(String[] args) throws IOException, InterruptedException {

        if (args.length != 1) usgErr();

        File grpcConfig = new File(args[0]);
        if (!grpcConfig.exists()) usgErr();

        Process p = new ProcessBuilder("protoc", "-h").start();
        p.waitFor();

        if (p.exitValue() != 0) err("protoc doesn't seem to be installed", 2);

        File fdsFile = File.createTempFile("GrpcVisualizer", ".proto");

        p = new ProcessBuilder("protoc", "-o", fdsFile.getAbsolutePath(), args[0])
                .inheritIO()
                .start();
        p.waitFor();
        if (p.exitValue() != 0) err("protoc -o did not exit cleanly", 3);


        PrettyCLI cli = new PrettyCLI();


        FileDescriptorSet fds = FileDescriptorSet.parseFrom(new FileInputStream(fdsFile));
        fds.getFileList().stream()

                .map(FileDescriptorProto::getServiceList)
                .flatMap(Collection::stream)

                .map(ServiceDescriptorProto::getMethodList)
                .flatMap(Collection::stream)

                .forEach(cli::print);

        cli.close();
    }

    private static void usgErr() {
        System.out.println(usage);
        System.exit(1);
    }

    private static void err(String msg, int code) {
        System.err.println(msg);
        System.exit(code);
    }
}
