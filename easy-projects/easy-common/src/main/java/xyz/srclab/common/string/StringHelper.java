package xyz.srclab.common.string;

import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.base.Checker;

import java.util.StringJoiner;
import java.util.function.Function;

public class StringHelper {

    public static int indexOf(CharSequence charSequence, int start, int end, char c) {
        Checker.checkBounds(charSequence.length(), start, end);
        for (int i = start; i < end; i++) {
            if (charSequence.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(CharSequence charSequence, int start, int end, char c) {
        Checker.checkBounds(charSequence.length(), start, end);
        for (int i = end - 1; i >= start; i--) {
            if (charSequence.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(CharSequence charSequence, int start, int end, CharSequence chars) {
        Checker.checkBounds(charSequence.length(), start, end);
        int charsLength = chars.length();
        if (end - start < charsLength) {
            return -1;
        }
        int possibleTo = end - charsLength + 1;
        for (int i = start; i < possibleTo; i++) {
            int c = 0;
            for (int j = 0; j < charsLength; j++) {
                if (charSequence.charAt(i + j) != chars.charAt(j)) {
                    break;
                }
                c++;
            }
            if (c == charsLength) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(CharSequence charSequence, int start, int end, CharSequence chars) {
        Checker.checkBounds(charSequence.length(), start, end);
        int charsLength = chars.length();
        if (end - start < charsLength) {
            return -1;
        }
        int possibleFrom = start + charsLength - 1;
        for (int i = end - 1; i >= possibleFrom; i--) {
            int c = 0;
            int charsFrom = charsLength - 1;
            for (int j = charsFrom; j >= 0; j--) {
                if (charSequence.charAt(i - (charsFrom - j)) != chars.charAt(j)) {
                    break;
                }
                c++;
            }
            if (c == charsLength) {
                return i;
            }
        }
        return -1;
    }

    public static String join(CharSequence delimiter, Object... array) {
        return join(delimiter, array, Object::toString);
    }

    public static <T> String join(CharSequence delimiter, T[] array, Function<T, String> toString) {
        return String.join(delimiter,
                ArrayKit.buildArray(new String[array.length], i -> toString.apply(array[i]))
        );
    }

    public static String join(CharSequence delimiter, Iterable<?> iterable) {
        return join(delimiter, iterable, Object::toString);
    }

    public static <T> String join(
            CharSequence delimiter, Iterable<? extends T> iterable, Function<T, String> toString) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (T t : iterable) {
            joiner.add(toString.apply(t));
        }
        return joiner.toString();
    }
}
