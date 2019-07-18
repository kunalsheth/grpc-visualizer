package info.kunalsheth.grpcvisualizer.cli;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import info.kunalsheth.grpcvisualizer.algo.ProtoGraph;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
import static info.kunalsheth.grpcvisualizer.GrpcVisualizer.errCrash;
import static info.kunalsheth.grpcvisualizer.algo.ProtoGraph.dfIter;
import static info.kunalsheth.grpcvisualizer.prettyprint.AnsiFieldDescriptor.simpleTypeName;
import static info.kunalsheth.grpcvisualizer.prettyprint.AnsiFieldDescriptor.typeSuffix;
import static java.util.stream.Collectors.joining;

public class Digraph {

    public static void render(
            Map<String, DescriptorProto> messages, DescriptorProto parent, String format
    ) throws IOException, InterruptedException {
        LinkedList<String> lines = new LinkedList<>();
        BiConsumer<String, DescriptorProto> writeNode = (String containerName, DescriptorProto containerNode) ->
                lines.add(containerName + " [" +
                        "shape=box " +
                        "fontname=Helvetica " +
                        "fontcolor=cyan4 " +
                        "style=rounded " +
                        "tooltip=\"" + tooltip(containerNode) + "\" " +
                        "]; ");

        if (parent == null) messages.forEach((containerName, containerNode) -> {
            writeNode.accept(containerName, containerNode);

            containerNode.getFieldList().stream()
                    .filter(f -> messages.containsKey(simpleTypeName(f)))
                    .map(f -> arrowPrint(containerName, f, false))
                    .forEach(lines::add);
        });
        else {
            Set<ProtoGraph.EdgeData> edges = dfIter(messages, parent);

            edges.forEach(e -> {
                String containerName = simpleTypeName(e.from);
                lines.add(
                        arrowPrint(containerName, e.to, e.isBackEdge)
                );
            });

            edges.stream()
                    .flatMap(e -> Stream.of(
                            e.from,
                            messages.get(simpleTypeName(e.to))
                    ))
                    .distinct()
                    .forEach(node -> writeNode.accept(simpleTypeName(node), node));
        }


        File dotSource = File.createTempFile(
                "GrpcVisualizer", ".dot"
        );
        PrintWriter dot = new PrintWriter(dotSource);
        dot.println("digraph {");
        dot.println("rankdir=\"LR\";");
        lines.stream()
                .sorted()
                .forEach(dot::println);
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

    private static String arrowPrint(String container, FieldDescriptorProto f, boolean highlight) {
        boolean isRepeated = f.getLabel() == LABEL_REPEATED;
        String label = f.getName() + typeSuffix(f);

        String attrs = " [ " +
                "fontsize=12 " +
                "fontname=Courier " +
                "fontcolor=gray25 " +
                "label=\"" + label + "\" " +
                "style=" + (isRepeated ? "dashed" : "solid") + " " +
                "color=" + (highlight ? "red" : "gray") + " " +
                " ];";

        return container + " -> " + simpleTypeName(f) + attrs;
    }

    private static String tooltip(DescriptorProto containerNode) {
        return containerNode.getFieldList()
                .stream()
                .map(f -> simpleTypeName(f) + typeSuffix(f) + " " + f.getName())
                .collect(joining("\n"));
    }
}