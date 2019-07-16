package info.kunalsheth.grpcvisualizer.prettyprint;

import com.google.protobuf.DescriptorProtos;
import org.fusesource.jansi.Ansi;

import static info.kunalsheth.grpcvisualizer.cli.ServiceCLI.FG_CLIENT;
import static info.kunalsheth.grpcvisualizer.cli.ServiceCLI.FG_SERVER;
import static info.kunalsheth.grpcvisualizer.prettyprint.AnsiFieldDescriptor.simpleTypeName;
import static org.fusesource.jansi.Ansi.Attribute.ITALIC;

public final class AnsiMethodDescriptor {

    private static final String basicRightArrow = "──>";
    private static final String basicLeftArrow = "<──";
    private static final String streamRightArrow = ">>>";
    private static final String streamLeftArrow = "<<<";

    private static final String indent = "   ";

    public static String nameLine(Ansi a, DescriptorProtos.MethodDescriptorProto m) {
        return a
                .bold().a(m.getName()).reset().a(':')
                .toString();
    }

    public static String inputLine(Ansi a, DescriptorProtos.MethodDescriptorProto m) {
        String inputArrow = m.getClientStreaming() ? streamRightArrow : basicRightArrow;

        return a
                .a(indent + inputArrow + ' ').a(ITALIC).fg(FG_CLIENT).a(
                        simpleTypeName(m.getInputType())
                ).reset()
                .toString();
    }

    public static String outputLine(Ansi a, DescriptorProtos.MethodDescriptorProto m) {
        String outputArrow = m.getServerStreaming() ? streamLeftArrow : basicLeftArrow;

        return a
                .a(indent + outputArrow + ' ').a(ITALIC).fg(FG_SERVER).a(
                        simpleTypeName(m.getOutputType())
                ).reset()
                .toString();
    }
}
