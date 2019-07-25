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
