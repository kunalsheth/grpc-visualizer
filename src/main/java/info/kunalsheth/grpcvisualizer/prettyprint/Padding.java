package info.kunalsheth.grpcvisualizer.prettyprint;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public final class Padding {

    public static String spaces(int num) {
        char[] arr = new char[num];
        Arrays.fill(arr, ' ');
        return new String(arr);
    }

    public static Queue<Character> toCharQueue(String text) {
        LinkedList<Character> q = new LinkedList<>();
        for (char c : text.toCharArray()) q.add(c);
        return q;
    }

    public static String pad(String plainText, String prettyText, int spacing) {
        if (plainText.length() > spacing)
            throw new IllegalArgumentException("Text cannot be centered within a space smaller than it.");
        if (plainText.contains("\n"))
            throw new IllegalArgumentException("Cannot pad multiline text.");

        int totalPadding = spacing - plainText.length();
        String suffixPad = spaces(totalPadding);
        return prettyText + suffixPad;
    }
}
