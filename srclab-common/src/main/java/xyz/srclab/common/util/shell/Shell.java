package xyz.srclab.common.util.shell;

import xyz.srclab.common.base.Defaults;

public interface Shell {

    static Shell newDefault() {
        return new DefaultShell();
    }

    void print(Object any);

    default void println(Object any) {
        print(any + Defaults.LINE_SEPARATOR);
    }

    String read();
}
