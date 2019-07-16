package info.kunalsheth.grpcvisualizer.prettyprint;

import org.fusesource.jansi.Ansi;

public final class AnsiInstantiator {

    public synchronized static Ansi ansi() {
        Ansi.setEnabled(true);
        return Ansi.ansi();
    }

    public synchronized static Ansi noAnsi() {
        Ansi.setEnabled(false);
        return Ansi.ansi();
    }
}
