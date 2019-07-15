package info.kunalsheth.grpcvisualizer.cli;

import org.fusesource.jansi.Ansi;

final class AnsiInstantiator {

    synchronized static Ansi ansi() {
        Ansi.setEnabled(true);
        return Ansi.ansi();
    }

    synchronized static Ansi noAnsi() {
        Ansi.setEnabled(false);
        return Ansi.ansi();
    }
}
