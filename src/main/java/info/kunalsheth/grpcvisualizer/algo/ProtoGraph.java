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
