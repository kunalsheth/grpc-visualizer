package info.kunalsheth.grpcvisualizer.algo;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static info.kunalsheth.grpcvisualizer.prettyprint.AnsiFieldDescriptor.simpleTypeName;

public class ProtoGraph {

    public static Set<EdgeData> dfIter(
            Map<String, DescriptorProto> messages,
            DescriptorProto parent
    ) {
        Set<EdgeData> edges = new HashSet<>();

        Set<DescriptorProto> visited = new HashSet<>();

        visited.add(parent);
        dfIter(parent, edges, messages, visited);

        return edges;
    }

    private static void dfIter(
            DescriptorProto parent,
            Set<EdgeData> edges,

            Map<String, DescriptorProto> messages,
            Set<DescriptorProto> visited
    ) {
        parent.getFieldList().stream()
                .filter(f -> messages.containsKey(simpleTypeName(f)))
                .forEach(f -> {
                    DescriptorProto toType = messages.get(simpleTypeName(f));
                    boolean wasVisited = visited.contains(toType);

                    edges.add(new EdgeData(parent, f, wasVisited));

                    Set<DescriptorProto> newVisited = new HashSet<>(visited);
                    newVisited.add(toType);

                    if (!wasVisited) dfIter(toType, edges, messages, newVisited);
                });
    }

    public static class EdgeData {
        public final DescriptorProto from;
        public final FieldDescriptorProto to;
        public final boolean isBackEdge;

        public EdgeData(DescriptorProto from, FieldDescriptorProto to, boolean isBackEdge) {
            Objects.requireNonNull(from);
            Objects.requireNonNull(to);

            this.from = from;
            this.to = to;
            this.isBackEdge = isBackEdge;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof EdgeData)) return false;
            EdgeData edgeData = (EdgeData) o;
            return isBackEdge == edgeData.isBackEdge &&
                    from.equals(edgeData.from) &&
                    to.equals(edgeData.to);
        }

        @Override
        public int hashCode() {
            return Objects.hash(from, to, isBackEdge);
        }
    }
}
