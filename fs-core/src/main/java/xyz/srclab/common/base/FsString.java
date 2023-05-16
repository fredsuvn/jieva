package xyz.srclab.common.base;

import org.jetbrains.annotations.NotNull;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.build.annotations.FsMethods;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.function.Supplier;

/**
 * Utilities for String.
 *
 * @author fredsuvn
 */
@FsMethods
public class FsString {

    /**
     * Returns whether given chars is null or empty.
     *
     * @param chars given chars
     */
    public static boolean isEmpty(@Nullable CharSequence chars) {
        return chars == null || chars.length() == 0;
    }

    /**
     * Returns whether given chars is blank: null, empty or whitespace.
     *
     * @param chars given chars
     */
    public static boolean isBlank(@Nullable CharSequence chars) {
        if (chars == null || chars.length() == 0) {
            return true;
        }
        for (int i = 0; i < chars.length(); i++) {
            char c = chars.charAt(i);
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether given chars can match given regex.
     *
     * @param regex given regex
     * @param chars given chars
     */
    public static boolean matches(CharSequence regex, @Nullable CharSequence chars) {
        if (chars == null) {
            return false;
        }
        return chars.toString().matches(regex.toString());
    }

    /**
     * Returns ture if any given chars is empty.
     *
     * @param chars given chars
     */
    public static boolean anyEmpty(CharSequence... chars) {
        for (CharSequence c : chars) {
            if (isEmpty(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns ture if any given chars is blank: null, empty or whitespace.
     *
     * @param chars given chars
     */
    public static boolean anyBlank(CharSequence... chars) {
        for (CharSequence c : chars) {
            if (isBlank(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns ture if any given chars can match given regex.
     *
     * @param chars given chars
     * @param regex given regex
     */
    public static boolean anyMatches(CharSequence regex, CharSequence... chars) {
        for (CharSequence c : chars) {
            if (matches(regex, c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns ture if all given chars is empty.
     *
     * @param chars given chars
     */
    public static boolean allEmpty(CharSequence... chars) {
        for (CharSequence c : chars) {
            if (!isEmpty(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns ture if all given chars is blank: null, empty or whitespace.
     *
     * @param chars given chars
     */
    public static boolean allBlank(CharSequence... chars) {
        for (CharSequence c : chars) {
            if (!isBlank(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns ture if all given chars can match given regex.
     *
     * @param chars given chars
     * @param regex given regex
     */
    public static boolean allMatches(CharSequence regex, CharSequence... chars) {
        for (CharSequence c : chars) {
            if (!matches(regex, c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether given chars starts with given start chars.
     *
     * @param chars given chars
     * @param start given start chars
     */
    public static boolean startsWith(@Nullable CharSequence chars, @Nullable CharSequence start) {
        if (chars == null || start == null) {
            return false;
        }
        if (chars.length() < start.length()) {
            return false;
        }
        for (int i = 0; i < start.length(); i++) {
            if (chars.charAt(i) != start.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether given chars ends with given start chars.
     *
     * @param chars given chars
     * @param end   given start chars
     */
    public static boolean endsWith(@Nullable CharSequence chars, @Nullable CharSequence end) {
        if (chars == null || end == null) {
            return false;
        }
        if (chars.length() < end.length()) {
            return false;
        }
        for (int i = chars.length() - 1, j = end.length() - 1; j >= 0; i--, j--) {
            if (chars.charAt(i) != end.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a string follows:
     * <ul>
     * <li>returns String.valueOf for given object if it is not an array;</li>
     * <li>if given object is primitive array, returns Arrays.toString for it;</li>
     * <li>if given object is Object[], returns Arrays.deepToString for it;</li>
     * <li>else returns String.valueOf for given object</li>
     * </ul>
     * <p>
     * This method is same as: toString(obj, true, true)
     *
     * @param obj given object
     */
    public static String toString(@Nullable Object obj) {
        return toString(obj, true, true);
    }

    /**
     * Returns deep-array-to-string for given objects.
     *
     * @param objs given objects
     */
    public static String toString(Object... objs) {
        return Arrays.deepToString(objs);
    }

    /**
     * Returns a string follows:
     * <ul>
     * <li>if given object is primitive array and array-check is true, returns Arrays.toString for it;</li>
     * <li>if given object is Object[] and both array-check and deep-to-string are true,
     * returns Arrays.deepToString for it;</li>
     * <li>if given object is Object[] and array-check is true and deep-to-string is false,
     * returns Arrays.toString for it;</li>
     * <li>else returns String.valueOf for given object</li>
     * </ul>
     *
     * @param obj          given object
     * @param arrayCheck   the array-check
     * @param deepToString whether deep-to-string
     */
    public static String toString(@Nullable Object obj, boolean arrayCheck, boolean deepToString) {
        if (obj == null || !arrayCheck) {
            return String.valueOf(obj);
        }
        Class<?> type = obj.getClass();
        if (!type.isArray()) {
            return obj.toString();
        }
        if (obj instanceof Object[]) {
            return deepToString ? Arrays.deepToString((Object[]) obj) : Arrays.toString((Object[]) obj);
        }
        if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        }
        if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        }
        if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        }
        if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        }
        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        }
        if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        }
        if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        }
        return obj.toString();
    }

    /**
     * Encodes given string to bytes with default charset {@link FsDefault#charset()}.
     *
     * @param chars given string
     */
    public static byte[] toBytes(CharSequence chars) {
        return toBytes(chars, FsDefault.charset());
    }

    /**
     * Encodes given string to bytes with given charset.
     *
     * @param chars   given string
     * @param charset given charset
     */
    public static byte[] toBytes(CharSequence chars, String charset) {
        Charset cs = Charset.forName(charset);
        return toBytes(chars, cs);
    }

    /**
     * Encodes given string to bytes with given charset.
     *
     * @param chars   given string
     * @param charset given charset
     */
    public static byte[] toBytes(CharSequence chars, Charset charset) {
        return chars.toString().getBytes(charset);
    }

    /**
     * Returns a string starts with given start string.
     * If given source string starts with given start string, return itself;
     * if not, return start + src.
     *
     * @param src   given source string
     * @param start given start string
     */
    public static String startWith(CharSequence src, CharSequence start) {
        if (startsWith(src, start)) {
            return src.toString();
        }
        return start.toString() + src;
    }

    /**
     * Returns a string doesn't start with given start string.
     * If given source string starts with given start string, remove the start chars and return;
     * else return source string.
     *
     * @param src   given source string
     * @param start given start string
     */
    public static String removeStart(CharSequence src, CharSequence start) {
        if (src.length() < start.length()) {
            return src.toString();
        }
        for (int i = 0; i < start.length(); i++) {
            if (src.charAt(i) != start.charAt(i)) {
                return src.toString();
            }
        }
        return src.subSequence(start.length(), src.length()).toString();
    }

    /**
     * Returns a string ends with given end string.
     * If given source string ends with given end string, return itself;
     * if not, return src + end.
     *
     * @param src given source string
     * @param end given end string
     */
    public static String endWith(CharSequence src, CharSequence end) {
        if (endsWith(src, end)) {
            return src.toString();
        }
        return src.toString() + end;
    }

    /**
     * Returns a string doesn't end with given end string.
     * If given source string ends with given end string, remove the end chars and return;
     * else return source string.
     *
     * @param src given source string
     * @param end given end string
     */
    public static String removeEnd(CharSequence src, CharSequence end) {
        if (src.length() < end.length()) {
            return src.toString();
        }
        for (int i = src.length() - 1, j = end.length() - 1; j >= 0; i--, j--) {
            if (src.charAt(i) != end.charAt(j)) {
                return src.toString();
            }
        }
        return src.subSequence(0, src.length() - end.length()).toString();
    }

    /**
     * Concatenates toString of given arguments.
     *
     * @param args given arguments
     */
    public static String concat(Object... args) {
        StringJoiner joiner = new StringJoiner("");
        for (Object arg : args) {
            joiner.add(String.valueOf(arg));
        }
        return joiner.toString();
    }

    /**
     * Concatenates toString of given arguments.
     *
     * @param args given arguments
     */
    public static String concat(Iterable<?> args) {
        StringJoiner joiner = new StringJoiner("");
        for (Object arg : args) {
            joiner.add(String.valueOf(arg));
        }
        return joiner.toString();
    }

    /**
     * Joins toString of given arguments with given separator.
     *
     * @param separator given separator
     * @param args      given arguments
     */
    public static String join(String separator, Object... args) {
        StringJoiner joiner = new StringJoiner(separator);
        for (Object arg : args) {
            joiner.add(String.valueOf(arg));
        }
        return joiner.toString();
    }

    /**
     * Joins toString of given arguments with given separator.
     *
     * @param separator given separator
     * @param args      given arguments
     */
    public static String join(String separator, Iterable<?> args) {
        StringJoiner joiner = new StringJoiner(separator);
        for (Object arg : args) {
            joiner.add(String.valueOf(arg));
        }
        return joiner.toString();
    }

    /**
     * Returns an object which is lazy for executing method {@link Object#toString()},
     * the executing was provided by given supplier.
     * <p>
     * Note returned {@link CharSequence}'s other methods (such as {@link CharSequence#length()})
     * were based on its lazy toString().
     *
     * @param supplier given supplier
     */
    public static CharSequence lazyString(Supplier<CharSequence> supplier) {
        return new CharSequence() {

            private String toString = null;

            @Override
            public int length() {
                return toString().length();
            }

            @Override
            public char charAt(int index) {
                return toString().charAt(index);
            }

            @NotNull
            @Override
            public CharSequence subSequence(int start, int end) {
                return toString().subSequence(start, end);
            }

            @Override
            public String toString() {
                if (toString == null) {
                    toString = String.valueOf(supplier.get());
                }
                return toString;
            }
        };
    }
}
