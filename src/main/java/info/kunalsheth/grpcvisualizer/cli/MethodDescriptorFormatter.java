package info.kunalsheth.grpcvisualizer.cli;

import com.google.protobuf.DescriptorProtos;

import static info.kunalsheth.grpcvisualizer.cli.ANSI.*;
import static info.kunalsheth.grpcvisualizer.cli.PrettyCLI.FG_CLIENT;
import static info.kunalsheth.grpcvisualizer.cli.PrettyCLI.FG_SERVER;

public class MethodDescriptorFormatter {

    private static final String basicRightArrow = "-->";
    private static final String basicLeftArrow = "<--";
    private static final String streamRightArrow = ">>>";
    private static final String streamLeftArrow = "<<<";

    private static final String indent = "   ";

    public static String toPlainString(DescriptorProtos.MethodDescriptorProto m) {

        String inputArrow = m.getClientStreaming() ? streamRightArrow : basicRightArrow;
        String outputArrow = m.getServerStreaming() ? streamLeftArrow : basicLeftArrow;

        return "" +
                m.getName() + ":\n" +
                indent + inputArrow + " " + m.getInputType() + /*" " + inputArrow +*/ "\n" +
                indent + outputArrow + " " + m.getOutputType() /*+ " " + outputArrow*/;
    }

    public static String toPrettyString(DescriptorProtos.MethodDescriptorProto m) {

        String inputArrow = m.getClientStreaming() ? streamRightArrow : basicRightArrow;
        String outputArrow = m.getServerStreaming() ? streamLeftArrow : basicLeftArrow;

        return "" +
                HIGH_INTENSITY + m.getName() + RESET + ":\n" +
                indent + inputArrow + " " + ITALIC + FG_CLIENT + m.getInputType() + RESET + /*" " + inputArrow +*/ "\n" +
                indent + outputArrow + " " + ITALIC + FG_SERVER + m.getOutputType() + RESET /*+ " " + outputArrow*/;
    }
}
