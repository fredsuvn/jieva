package xyz.srclab.common.base;

import xyz.srclab.annotation.NotNull;

public class Chars {

    @NotNull
    public static String toString(@NotNull char[] chars) {
        return new String(chars);
    }

    @NotNull
    public static String toString(@NotNull byte[] bytes) {
        return new String(bytes, Defaults.CHARSET);
    }

    @NotNull
    public static byte[] toBytes(@NotNull char[] chars) {
        return new String(chars).getBytes(Defaults.CHARSET);
    }

    @NotNull
    public static byte[] toBytes(@NotNull CharSequence chars) {
        return chars.toString().getBytes(Defaults.CHARSET);
    }
}
