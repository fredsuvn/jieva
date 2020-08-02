package xyz.srclab.common.base;

public class Bytes {

    public static String toString(byte[] bytes) {
        return new String(bytes, Defaults.CHARSET);
    }

    public static byte[] toBytes(char[] chars) {
        return new String(chars).getBytes(Defaults.CHARSET);
    }

    public static byte[] toBytes(CharSequence chars) {
        return chars.toString().getBytes(Defaults.CHARSET);
    }
}
