package info.kunalsheth.grpcvisualizer.cli;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import info.kunalsheth.grpcvisualizer.algo.ProtoGraph;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
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

    public static File render(
            Map<String, DescriptorProto> messages, DescriptorProto parent, String format, boolean gui
    ) throws IOException, InterruptedException {
        LinkedList<String> dotSource = new LinkedList<>();

        if (parent == null) messages.forEach((containerName, containerNode) -> {
            dotSource.add(printNode(containerNode, gui));

            containerNode.getFieldList().stream()
                    .filter(f -> messages.containsKey(simpleTypeName(f)))
                    .map(f -> printEdge(containerNode, f, false))
                    .forEach(dotSource::add);
        });
        else {
            Set<ProtoGraph.EdgeData> edges = dfIter(messages, parent);

            edges.forEach(e -> dotSource.add(
                    printEdge(e.from, e.to, e.isBackEdge)
            ));

            edges.stream()
                    .flatMap(e -> Stream.of(
                            e.from,
                            messages.get(simpleTypeName(e.to))
                    ))
                    .distinct()
                    .forEach(node -> dotSource.add(
                            printNode(node, gui)
                    ));
        }


        File dotFile = File.createTempFile(
                "GrpcVisualizer", ".dot"
        );
        PrintWriter pw = new PrintWriter(dotFile);
        pw.println("digraph {");
        pw.println("rankdir=\"LR\";");
        dotSource.stream()
                .map(s -> s.split("\n"))
                .flatMap(Arrays::stream)
                .sorted()
                .distinct()
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

        if (!gui) System.out.println("Open " + output.getCanonicalPath() + " to view digraph.");

        return output;
    }

    private static String printEdge(DescriptorProto container, FieldDescriptorProto f, boolean isBackEdge) {
        boolean isRepeated = f.getLabel() == LABEL_REPEATED;
        String label = f.getName() + typeSuffix(f);

        String attrs = " [ " +
                "fontsize=12 " +
                "fontname=Courier " +
                "fontcolor=gray25 " +
                "style=" + (isRepeated ? "dashed" : "solid") + " " +
                "color=" + (isBackEdge ? "red" : "gray") + " ";

        String terminator = " ];";

        if (f.hasOneofIndex()) return "" +

                simpleTypeName(container) +
                " -> " + virtualNodeName(container, f.getOneofIndex()) +
                attrs +
                "dir=none " +
                "label=\"oneof " + container.getOneofDecl(f.getOneofIndex()).getName() + "\" " +
                terminator +

                '\n' +

                virtualNodeName(container, f.getOneofIndex()) +
                " -> " + simpleTypeName(f) +
                attrs +
                "label=\"" + label + "\" " +
                "constraint=" + !isBackEdge +
                terminator;

        else return simpleTypeName(container) + " -> " + simpleTypeName(f) +
                attrs +
                "label=\"" + label + "\" " +
                "constraint=" + !isBackEdge +
                terminator;
    }

    private static String printNode(DescriptorProto container, boolean gui) {
        String virtualNodes = "";
        for (int i = 0; i < container.getOneofDeclCount(); i++) {
            // don't care about efficiency here. Clean code is better
            //noinspection StringConcatenationInLoop
            virtualNodes += virtualNodeName(container, i) + " [" +
                    "shape=point " +
                    "color=gray " +
                    "height=0.01 " +
                    "width=0.01 " +
                    "];\n";
        }

        return virtualNodes + simpleTypeName(container) + " [" +
                "shape=box " +
                "fontname=Helvetica " +
                "fontcolor=cyan4 " +
                "style=rounded " +
                (gui ? "URL=\"/" + simpleTypeName(container) + "\" " : "") +
                "tooltip=\"" + tooltip(container) + "\" " +
                "]; ";
    }

    private static String virtualNodeName(DescriptorProto container, int idx) {
        return simpleTypeName(container) + idx;
    }

    private static String tooltip(DescriptorProto containerNode) {
        return containerNode.getFieldList()
                .stream()
                .map(f -> simpleTypeName(f) + typeSuffix(f) + " " + f.getName())
                .collect(joining("\\n"));
    }
}