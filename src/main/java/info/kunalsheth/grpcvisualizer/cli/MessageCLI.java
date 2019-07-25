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
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import info.kunalsheth.grpcvisualizer.prettyprint.AnsiFieldDescriptor;
import org.fusesource.jansi.Ansi;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static info.kunalsheth.grpcvisualizer.prettyprint.AnsiFieldDescriptor.messageLine;
import static info.kunalsheth.grpcvisualizer.prettyprint.AnsiFieldDescriptor.simpleTypeName;
import static info.kunalsheth.grpcvisualizer.prettyprint.AnsiInstantiator.ansi;
import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT;

public final class MessageCLI {

    public static final Ansi.Color FG_TYPE = Ansi.Color.BLUE;
    private static final String middleElement = faint("├─ ");
    private static final String lastElement = faint("└─ ");
    private static final String indentIncr = faint("   ");
    private static final String lastIndentIncr = faint("│  ");
    private static final String cyclic = ansi().bold().fgRed().a(" ↺").reset().toString();

    private static String faint(String txt) {
        return ansi().a(INTENSITY_FAINT).a(txt).reset().toString();
    }

    public static void print(
            PrintStream out,
            Map<String, DescriptorProto> messages,
            DescriptorProto msg
    ) {
        out.println(
                messageLine(ansi(), msg)
        );

        Set<DescriptorProto> visited = new HashSet<>();
        visited.add(msg);

        List<FieldDescriptorProto> children = msg.getFieldList();

        for (int i = 0; i < children.size(); i++)
            print(out,
                    messages, visited,
                    children.get(i), " ",
                    i == children.size() - 1
            );
    }

    private static void print(
            PrintStream out,
            Map<String, DescriptorProto> messages,
            Set<DescriptorProto> visited,
            FieldDescriptorProto field,
            String indent, boolean last
    ) {
        DescriptorProto type = messages.get(simpleTypeName(field));
        boolean isPrimitive = messages.get(simpleTypeName(field)) == null;
        boolean wasVisited = !isPrimitive && visited.contains(type);

        out.println(indent +
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
                    print(out,
                            messages, visited,
                            children.get(i), indent,
                            i == children.size() - 1
                    );
            } /*else out.println(indent + lastElement + ansi()
                    .bold().fg(FG_TYPE).a("...").reset().toString()
            );*/
        }
    }
}
