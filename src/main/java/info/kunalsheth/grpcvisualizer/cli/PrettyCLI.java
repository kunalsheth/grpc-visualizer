package info.kunalsheth.grpcvisualizer.cli;

import com.google.protobuf.DescriptorProtos.MethodDescriptorProto;

import java.io.Closeable;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

import static info.kunalsheth.grpcvisualizer.cli.ANSI.*;
import static info.kunalsheth.grpcvisualizer.cli.MethodDescriptorFormatter.toPlainString;
import static info.kunalsheth.grpcvisualizer.cli.MethodDescriptorFormatter.toPrettyString;

public class PrettyCLI implements Closeable {

    public static String FG_CLIENT = FG_GREEN;
    public static String FG_SERVER = FG_MAGENTA;

    private final Set<MethodDescriptorProto> prints = new HashSet<>();

    public void print(MethodDescriptorProto m) {
        prints.add(m);
    }

    private String spaces(int num) {
        char[] arr = new char[num];
        Arrays.fill(arr, ' ');
        return new String(arr);
    }

    private Queue<Character> toCharQueue(String text) {
        LinkedList<Character> q = new LinkedList<>();
        for (char c : text.toCharArray()) q.add(c);
        return q;
    }

    private String pad(String plainText, String prettyText, int spacing) {
        if (plainText.length() > spacing)
            throw new IllegalArgumentException("Text cannot be centered within a space smaller than it.");
        if (plainText.contains("\n"))
            throw new IllegalArgumentException("Cannot pad multiline text.");

        int totalPadding = spacing - plainText.length();
        String suffixPad = spaces(totalPadding);
        return prettyText + suffixPad;
    }

    public void close() throws IOException {
        int spacing = prints.stream()

                .map(MethodDescriptorFormatter::toPlainString)
                .map(s -> s.split("\n"))
                .flatMap(Arrays::stream)

                .mapToInt(String::length)
                .max().orElse(0)
                + 2;

        String emptyPrint = spaces(spacing);
        System.out.println(
                FG_GREEN + "___  " + RESET + emptyPrint + FG_MAGENTA + "  ___"
        );

        Queue<Character> clientLabel = toCharQueue("CLIENT");
        Supplier<Character> clientLabelChar = () -> {
            Character c = clientLabel.poll();
            if (c == null) c = ' ';
            return c;
        };

        Queue<Character> serverLabel = toCharQueue("SERVER");
        Supplier<Character> serverLabelChar = () -> {
            Character c = serverLabel.poll();
            if (c == null) c = ' ';
            return c;
        };

        Runnable blankLn = () -> System.out.println(
                FG_CLIENT + clientLabelChar.get() + "  | " + emptyPrint + FG_SERVER + " |  " + serverLabelChar.get() + RESET
        );


        blankLn.run();

        prints.forEach(m -> {
            String[] plainLns = toPlainString(m).split("\n");
            String[] prettyLns = toPrettyString(m).split("\n");

            for (int i = 0; i < plainLns.length; i++)
                System.out.println("" +
                        FG_CLIENT + clientLabelChar.get() + "  | " + RESET +
                        pad(plainLns[i], prettyLns[i], spacing) + RESET +
                        FG_SERVER + " |  " + serverLabelChar.get() + RESET
                );

            blankLn.run();
        });

        while (!clientLabel.isEmpty() || !serverLabel.isEmpty()) blankLn.run();

        System.out.println(RESET);
    }
}
