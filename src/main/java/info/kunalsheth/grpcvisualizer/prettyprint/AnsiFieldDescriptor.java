package info.kunalsheth.grpcvisualizer.prettyprint;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import org.fusesource.jansi.Ansi;

import static com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED;
import static info.kunalsheth.grpcvisualizer.cli.MessageCLI.FG_TYPE;
import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT;
import static org.fusesource.jansi.Ansi.Attribute.ITALIC;

public final class AnsiFieldDescriptor {

    public static String messageLine(Ansi a, DescriptorProto m) {
        return a
                .a(INTENSITY_FAINT).a("message ").reset()
                .fgBright(FG_TYPE).bold().a(m.getName()).reset()
                .toString();
    }

    public static String simpleTypeName(FieldDescriptorProto m) {
        // type name string manipulation is brittle and hackish. not sure if a better way exists though :(
        return m.getTypeName().isEmpty() ? m
                .getType()
                .toString()
                .replaceFirst("TYPE_", "")
                .toLowerCase()
                : simpleTypeName(m.getTypeName());
    }

    public static String simpleTypeName(DescriptorProtos.DescriptorProto m) {
        return simpleTypeName(m.getName());
    }

    public static String typeSuffix(FieldDescriptorProto m) {
        return m.getLabel() == LABEL_REPEATED ? "[]" : "";
    }

    public static String simpleTypeName(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    public static String fieldLine(Ansi a, FieldDescriptorProto m, boolean isPrimitive) {
        if (isPrimitive) a = a.fg(FG_TYPE);
        else a = a.fgBright(FG_TYPE).bold();

        return a
                .a(simpleTypeName(m))
                .a(typeSuffix(m))
                .reset().a(' ')

                .a(ITALIC).a(m.getName())
                .reset()

                .a(INTENSITY_FAINT).a(" = " + m.getNumber())
                .reset()

                .toString();
    }
}
