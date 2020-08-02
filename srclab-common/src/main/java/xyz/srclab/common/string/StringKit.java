package xyz.srclab.common.string;

import com.google.common.base.CharMatcher;
import xyz.srclab.common.array.ArrayKit;

import java.util.StringJoiner;
import java.util.function.Function;

public class StringKit {

    public static final CharMatcher DOT_CHAR_MATCHER = CharMatcher.is('.');

    public static final CharMatcher JAVA_NAMING_MATCHER = CharMatcher.inRange('0', '9')
            .or(CharMatcher.inRange('a', 'z'))
            .or(CharMatcher.inRange('A', 'Z'))
            .or(CharMatcher.anyOf("_$"));

    public static final CharMatcher NON_JAVA_NAMING_MATCHER = JAVA_NAMING_MATCHER.negate();

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
