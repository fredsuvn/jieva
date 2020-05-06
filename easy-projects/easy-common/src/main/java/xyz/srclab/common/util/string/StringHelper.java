package xyz.srclab.common.util.string;

import xyz.srclab.common.array.ArrayHelper;

import java.util.StringJoiner;
import java.util.function.Function;

public class StringHelper {

    public static String join(CharSequence delimiter, Object... array) {
        return join(delimiter, array, Object::toString);
    }

    public static String join(CharSequence delimiter, Object[] array, Function<Object, String> toString) {
        return String.join(delimiter,
                ArrayHelper.buildArray(new String[array.length], i -> toString.apply(array[i]))
        );
    }

    public static String join(CharSequence delimiter, Iterable<?> iterable) {
        return join(delimiter, iterable, Object::toString);
    }

    public static String join(CharSequence delimiter, Iterable<?> iterable, Function<Object, String> toString) {
        StringJoiner joiner = new StringJoiner(delimiter);
        for (Object o : iterable) {
            joiner.add(toString.apply(o));
        }
        return joiner.toString();
    }
}
