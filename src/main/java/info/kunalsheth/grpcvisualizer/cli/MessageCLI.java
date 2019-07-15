package info.kunalsheth.grpcvisualizer.cli;

import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import org.fusesource.jansi.Ansi;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static info.kunalsheth.grpcvisualizer.cli.AnsiFieldDescriptor.messageLine;
import static info.kunalsheth.grpcvisualizer.cli.AnsiFieldDescriptor.simpleTypeName;
import static info.kunalsheth.grpcvisualizer.cli.AnsiInstantiator.ansi;
import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT;

public final class MessageCLI {

    static final Ansi.Color FG_CUSTOM_TYPE = Ansi.Color.CYAN;
    static final Ansi.Color FG_PRIMITIVE_TYPE = Ansi.Color.BLUE;

    private static String faint(String txt) {
        return ansi().a(INTENSITY_FAINT).a(txt).reset().toString();
    }

    private static final String middleElement = faint("├─ ");
    private static final String lastElement = faint("└─ ");

    private static final String indentIncr = faint("   ");
    private static final String lastIndentIncr = faint("│  ");

    private static final String cyclic = ansi().bold().fgRed().a(" ↺").reset().toString();

    public static void print(
            Map<String, DescriptorProto> allMsgs,
            DescriptorProto msg
    ) {
        System.out.println(
                messageLine(ansi(), msg)
        );

        Set<DescriptorProto> visited = new HashSet<>();
        visited.add(msg);

        List<FieldDescriptorProto> children = msg.getFieldList();

        for (int i = 0; i < children.size(); i++)
            print(
                    allMsgs, visited,
                    children.get(i), " ",
                    i == children.size() - 1
            );
    }

    private static void print(
            Map<String, DescriptorProto> messageTypes,
            Set<DescriptorProto> visited,
            FieldDescriptorProto field,
            String indent, boolean last
    ) {
        DescriptorProto type = messageTypes.get(simpleTypeName(field));
        boolean isPrimitive = messageTypes.get(simpleTypeName(field)) == null;
        boolean wasVisited = !isPrimitive && visited.contains(type);

        System.out.println(indent +
                (last ? lastElement : middleElement) +
                AnsiFieldDescriptor.fieldLine(ansi(),
                        field, isPrimitive
                ) +
                (wasVisited ? cyclic : "")
        );

        if (!isPrimitive) {
            indent += last ? indentIncr : lastIndentIncr;

            if (!wasVisited) {
                visited = new HashSet<>(visited);
                visited.add(type);

                List<FieldDescriptorProto> children = type.getFieldList();
                for (int i = 0; i < children.size(); i++)
                    print(
                            messageTypes, visited,
                            children.get(i), indent,
                            i == children.size() - 1
                    );
            } /*else System.out.println(indent + lastElement + ansi()
                    .bold().fg(FG_PRIMITIVE_TYPE).a("...").reset().toString()
            );*/
        }
    }
}