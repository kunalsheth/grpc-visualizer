package info.kunalsheth.grpcvisualizer.prettyprint;

import com.google.protobuf.DescriptorProtos;
import org.fusesource.jansi.Ansi;

import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
import static info.kunalsheth.grpcvisualizer.cli.MessageCLI.FG_CUSTOM_TYPE;
import static info.kunalsheth.grpcvisualizer.cli.MessageCLI.FG_PRIMITIVE_TYPE;
import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT;
import static org.fusesource.jansi.Ansi.Attribute.ITALIC;

public final class AnsiFieldDescriptor {

    public static String messageLine(Ansi a, DescriptorProtos.DescriptorProto m) {
        return a
                .a(INTENSITY_FAINT).a("message ").reset()
                .fg(FG_CUSTOM_TYPE).bold().a(m.getName()).reset()
                .toString();
    }

    public static String simpleTypeName(DescriptorProtos.FieldDescriptorProto m) {
        return simpleTypeName(m.getTypeName());
    }

    public static String simpleTypeName(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public static String fieldLine(Ansi a, DescriptorProtos.FieldDescriptorProto m, boolean isPrimitive) {

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
