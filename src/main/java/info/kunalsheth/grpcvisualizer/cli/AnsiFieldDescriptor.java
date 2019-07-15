package info.kunalsheth.grpcvisualizer.cli;

import com.google.protobuf.DescriptorProtos;
import org.fusesource.jansi.Ansi;

import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
import static info.kunalsheth.grpcvisualizer.cli.MessageCLI.FG_CUSTOM_TYPE;
import static info.kunalsheth.grpcvisualizer.cli.MessageCLI.FG_PRIMITIVE_TYPE;
import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT;
import static org.fusesource.jansi.Ansi.Attribute.ITALIC;

final class AnsiFieldDescriptor {

    static String messageLine(Ansi a, DescriptorProtos.DescriptorProto m) {
        return a
                .a(INTENSITY_FAINT).a("message ").reset()
                .fg(FG_CUSTOM_TYPE).bold().a(m.getName()).reset()
                .toString();
    }

    static String simpleTypeName(DescriptorProtos.FieldDescriptorProto m) {
        String t = m.getTypeName();
        return t.substring(t.lastIndexOf('.') + 1);
    }

    static String fieldLine(Ansi a, DescriptorProtos.FieldDescriptorProto m, boolean isPrimitive) {

        // type name string manipulation is brittle and hackish. not sure if a better way exists though :(
        String typeName = m.getTypeName().isEmpty() ? m
                .getType()
                .toString()
                .replaceFirst("TYPE_", "")
                .toLowerCase()
                : simpleTypeName(m);

        if (isPrimitive) a = a.fg(FG_PRIMITIVE_TYPE);
        else a = a.fg(FG_CUSTOM_TYPE).bold();

        return a
                .a(typeName)
                .a(m.getLabel() == LABEL_REPEATED ? "[]" : "")
                .reset().a(' ')

                .a(ITALIC).a(m.getName())
                .reset()

                .a(INTENSITY_FAINT).a(" = " + m.getNumber())
                .reset()

                .toString();
    }
}
