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

//        get("/svg/:node", (request, response) -> {
//            DescriptorProto node = messages.get(request.params("node"));
//            if (node == null) {
//                notFound("Node '" + request.params("node") + "' not found");
//                return null;
//            } else synchronized (lock) {
//                response.type("image/svg+xml");
//                return readString(
//                        render(messages, node, "svg", true).toPath()
//                );
//            }
//        });
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
                "<button style=\"display: inline; vertical-align: middle;\" onClick=\"history.back()\">Back</button>\n" +
                "<div style=\"display: inline;\">\n" +
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
