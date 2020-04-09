package xyz.srclab.common.shell;

import xyz.srclab.common.base.Defaults;

public interface Shell {

    Shell DEFAULT = new DefaultShell();

    void print(Object any);

    default void println(Object any) {
        print(any + Defaults.LINE_SEPARATOR);
    }

    String read();
}
