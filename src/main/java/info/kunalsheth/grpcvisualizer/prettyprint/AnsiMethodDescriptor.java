/*
 * Copyright 2019 Kunal Sheth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT
 * SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

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
