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

package info.kunalsheth.grpcvisualizer.cli;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import org.fusesource.jansi.HtmlAnsiOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

import static info.kunalsheth.grpcvisualizer.GrpcVisualizer.allMessages;
import static info.kunalsheth.grpcvisualizer.GrpcVisualizer.getFile;
import static info.kunalsheth.grpcvisualizer.cli.Digraph.render;
import static java.nio.file.Files.readString;
import static spark.Spark.*;

public class DigraphServer {

    private static final Object lock = new Object();
    private static final Map<String, DescriptorProto> messages = allMessages();

    static {
        port(8383);

        get("/", (request, response) -> {
            synchronized (lock) {
                response.type("image/svg+xml");
                return readString(
                        render(messages, null, "svg", true).toPath()
                );
            }
        });

        get("/:node", (request, response) -> {
            DescriptorProto node = messages.get(request.params("node"));
            if (node == null) {
                notFound("Node '" + request.params("node") + "' not found");
                return null;
            } else return generateHtml(node);
        });
    }

    public static void load() {
        // trigger JVM classloading
    }

    private static String generateHtml(DescriptorProto node) throws IOException, InterruptedException {
        ByteArrayOutputStream html = new ByteArrayOutputStream(1000);
        PrintStream ansiToHtml = new PrintStream(new HtmlAnsiOutputStream(html));
        MessageCLI.print(ansiToHtml, messages, node);
        ansiToHtml.close();

        String svg = readString(
                render(messages, node, "svg", true).toPath()
        );

        return "" +
                "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head><title>grpc-visualizer | " + getFile().getName() + "</title></head>\n" +
                "<body>\n" +
                "<button onClick=\"history.back()\">Back</button>\n" +
                "<div>\n" +
                svg +
                "</div>\n" +
                "<hr>\n" +
                "<pre style=\"line-height: 1em; font-size: 125%;\">\n" +
                html +
                "</pre>\n" +
                "</body>\n" +
                "</html>";
    }
}
