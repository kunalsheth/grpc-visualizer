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
        LinkedList<String> dotSource = new LinkedList<>();

        if (parent == null) messages.forEach((containerName, containerNode) -> {
            dotSource.add(printNode(containerName, containerNode));

            containerNode.getFieldList().stream()
                    .filter(f -> messages.containsKey(simpleTypeName(f)))
                    .map(f -> printEdge(containerName, f, false))
                    .forEach(dotSource::add);
        });
        else {
            Set<ProtoGraph.EdgeData> edges = dfIter(messages, parent);

            edges.forEach(e -> {
                String containerName = simpleTypeName(e.from);
                dotSource.add(
                        printEdge(containerName, e.to, e.isBackEdge)
                );
            });

            edges.stream()
                    .flatMap(e -> Stream.of(
                            e.from,
                            messages.get(simpleTypeName(e.to))
                    ))
                    .distinct()
                    .forEach(node -> dotSource.add(
                            printNode(simpleTypeName(node), node)
                    ));


        }


        File dotFile = File.createTempFile(
                "GrpcVisualizer", ".dot"
        );
        PrintWriter pw = new PrintWriter(dotFile);
        pw.println("digraph {");
        pw.println("rankdir=\"LR\";");
        dotSource.stream()
                .sorted()
                .forEach(pw::println);
        pw.println("}");
        pw.close();


        Process p = new ProcessBuilder("dot", "-?").start();
        p.waitFor();
        if (p.exitValue() != 0) errCrash("graphviz dot doesn't seem to be installed", 4);

        File output = new File("digraph." + format);
        p = new ProcessBuilder("dot", "-T" + format, dotFile.getAbsolutePath())
                .inheritIO()
                .redirectOutput(output)
                .start();
        p.waitFor();
        if (p.exitValue() != 0) errCrash("dot -T" + format + " did not exit cleanly", 5);

        System.out.println("Open " + output.getCanonicalPath() + " to view digraph.");
    }

    private static String printEdge(String container, FieldDescriptorProto f, boolean isBackEdge) {
        boolean isRepeated = f.getLabel() == LABEL_REPEATED;
        String label = f.getName() + typeSuffix(f);

        String attrs = " [ " +
                "fontsize=12 " +
                "fontname=Courier " +
                "fontcolor=gray25 " +
                "label=\"" + label + "\" " +
                "style=" + (isRepeated ? "dashed" : "solid") + " " +
                "color=" + (isBackEdge ? "red" : "gray") + " " +
                "constraint=" + !isBackEdge + " " +
                " ];";

        return container + " -> " + simpleTypeName(f) + attrs;
    }

    private static String printNode(String containerName, DescriptorProto containerNode) {
        return containerName + " [" +
                "shape=box " +
                "fontname=Helvetica " +
                "fontcolor=cyan4 " +
                "style=rounded " +
                "tooltip=\"" + tooltip(containerNode) + "\" " +
                "]; ";
    }

    private static String tooltip(DescriptorProto containerNode) {
        return containerNode.getFieldList()
                .stream()
                .map(f -> simpleTypeName(f) + typeSuffix(f) + " " + f.getName())
                .collect(joining("\n"));
    }
}