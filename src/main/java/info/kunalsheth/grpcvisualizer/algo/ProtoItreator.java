package info.kunalsheth.grpcvisualizer.algo;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DescriptorProtos.DescriptorProto;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

import static info.kunalsheth.grpcvisualizer.prettyprint.AnsiFieldDescriptor.simpleTypeName;

public class ProtoItreator {

    public static void dfIter(
            Map<String, DescriptorProto> messages,
            DescriptorProto parent,
            BiConsumer<DescriptorProto, Boolean> action
    ) {
        dfIter(parent, action, messages, new HashSet<>());
    }

    private static void dfIter(
            DescriptorProto parent,
            BiConsumer<DescriptorProto, Boolean> action,

            Map<String, DescriptorProto> messages,
            Set<DescriptorProto> visited
    ) {
        parent.getFieldList().stream()
                .map(f -> messages.get(simpleTypeName(f)))
                .filter(Objects::nonNull)
                .forEach(f -> {
                    action.accept(f, visited.contains(f));

                    Set<DescriptorProto> newVisited = new HashSet<>(visited);
                    newVisited.add(f);

                    dfIter(f, action, messages, newVisited);
                });
    }
}
