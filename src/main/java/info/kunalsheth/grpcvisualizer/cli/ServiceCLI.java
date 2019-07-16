package info.kunalsheth.grpcvisualizer.cli;

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DescriptorProtos.MethodDescriptorProto;
import info.kunalsheth.grpcvisualizer.prettyprint.AnsiMethodDescriptor;
import info.kunalsheth.grpcvisualizer.prettyprint.Padding;
import org.fusesource.jansi.Ansi;

import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static info.kunalsheth.grpcvisualizer.prettyprint.AnsiInstantiator.ansi;
import static info.kunalsheth.grpcvisualizer.prettyprint.AnsiInstantiator.noAnsi;
import static org.fusesource.jansi.Ansi.Attribute.INTENSITY_FAINT;

public final class ServiceCLI {

    public static final Ansi.Color FG_CLIENT = Ansi.Color.GREEN;
    public static final Ansi.Color FG_SERVER = Ansi.Color.MAGENTA;

    public static void print(DescriptorProtos.ServiceDescriptorProto service) {
        System.out.println(ansi()
                .a(INTENSITY_FAINT).a("service ").reset()
                .bold().a(Ansi.Attribute.UNDERLINE).a(service.getName()).reset().a(':')
        );

        List<MethodDescriptorProto> methods = service.getMethodList();

        Supplier<Stream<BiFunction<Ansi, MethodDescriptorProto, String>>> methodToStrings = () -> Stream.of(
                AnsiMethodDescriptor::nameLine,
                AnsiMethodDescriptor::inputLine,
                AnsiMethodDescriptor::outputLine
        );

        int spacing = methods.stream()
                .flatMap(m -> methodToStrings.get().map(
                        f -> f.apply(noAnsi(), m)
                ))

                .mapToInt(String::length)
                .max().orElse(0)
                + 2;

        String emptyPrint = Padding.spaces(spacing);
        System.out.println(ansi()
                .fg(FG_CLIENT).a("───┐ ").reset().a(emptyPrint).fg(FG_SERVER).a(" ┌───")
        );

        Queue<Character> clientLabel = Padding.toCharQueue("CLIENT");
        Supplier<Character> clientLabelChar = () -> {
            Character c = clientLabel.poll();
            if (c == null) c = ' ';
            return c;
        };

        Queue<Character> serverLabel = Padding.toCharQueue("SERVER");
        Supplier<Character> serverLabelChar = () -> {
            Character c = serverLabel.poll();
            if (c == null) c = ' ';
            return c;
        };

        Runnable blankLn = () -> System.out.println(ansi()
                .fg(FG_CLIENT).a(clientLabelChar.get() + "  │ ").reset()
                .a(emptyPrint)
                .fg(FG_SERVER).a(" │  " + serverLabelChar.get()).reset()
        );


        blankLn.run();

        methods.forEach(m -> {
            String[] plainLns = methodToStrings.get().map(f ->
                    f.apply(noAnsi(), m)
            ).toArray(String[]::new);

            String[] prettyLns = methodToStrings.get().map(f ->
                    f.apply(ansi(), m)
            ).toArray(String[]::new);

            for (int i = 0; i < plainLns.length; i++)
                System.out.println(ansi()
                        .fg(FG_CLIENT).a(clientLabelChar.get() + "  │ ").reset()
                        .a(
                                Padding.pad(plainLns[i], prettyLns[i], spacing)
                        )
                        .fg(FG_SERVER).a(" │  " + serverLabelChar.get()).reset()
                );

            blankLn.run();
        });

        while (!clientLabel.isEmpty() || !serverLabel.isEmpty()) blankLn.run();
    }

    private ServiceCLI() {
        throw new IllegalAccessError();
    }
}
