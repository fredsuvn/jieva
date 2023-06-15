package xyz.srclab.common.base;

import org.jetbrains.annotations.NotNull;
import xyz.srclab.annotations.Nullable;

import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Supplier;

/**
 * Utilities for String.
 *
 * @author fredsuvn
 */
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
     * Returns whether all chars of given chars are upper case.
     *
     * @param chars given chars
     */
    public static boolean allUpperCase(CharSequence chars) {
        for (int i = 0; i < chars.length(); i++) {
            if (!Character.isUpperCase(chars.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns whether all chars of given chars are lower case.
     *
     * @param chars given chars
     */
    public static boolean allLowerCase(CharSequence chars) {
        if (isEmpty(chars)) {
            return false;
        }
        for (int i = 0; i < chars.length(); i++) {
            if (!Character.isLowerCase(chars.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a String of which first char is upper or lower (according to given upper) of first char of given chars,
     * and the rest chars are unchanged.
     *
     * @param chars given chars
     * @param upper given upper
     */
    public static String firstCase(CharSequence chars, boolean upper) {
        if (isEmpty(chars)) {
            return chars.toString();
        }
        if (Fs.equals(Character.isUpperCase(chars.charAt(0)), upper)) {
            return chars.toString();
        }
        char[] cs = new char[chars.length()];
        cs[0] = upper ? Character.toUpperCase(chars.charAt(0)) : Character.toLowerCase(chars.charAt(0));
        for (int i = 1; i < chars.length(); i++) {
            cs[i] = chars.charAt(i);
        }
        return new String(cs);
    }

    /**
     * Returns a String of which content is upper case of given chars.
     *
     * @param chars given chars
     */
    public static String upperCase(CharSequence chars) {
        if (isEmpty(chars)) {
            return chars.toString();
        }
        char[] cs = new char[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            cs[i] = Character.toUpperCase(chars.charAt(i));
        }
        return new String(cs);
    }

    /**
     * Returns a String of which content is lower case of given chars.
     *
     * @param chars given chars
     */
    public static String lowerCase(CharSequence chars) {
        if (isEmpty(chars)) {
            return chars.toString();
        }
        char[] cs = new char[chars.length()];
        for (int i = 0; i < chars.length(); i++) {
            cs[i] = Character.toLowerCase(chars.charAt(i));
        }
        return new String(cs);
    }

    /**
     * Splits given chars by given separator,
     * using {@link #subRef(CharSequence, int, int)} to generate sub CharSequence.
     * If chars or separator is empty, or separator's length is greater than chars' length,
     * or separator is never matched, return an empty list.
     * <p>
     * Note empty part will be created there is no char between separator and the next separator,
     * position of start or end. That means, if the returned list is not empty, its length is at least 2.
     *
     * @param chars     given chars
     * @param separator given separator
     */
    public static List<CharSequence> split(CharSequence chars, CharSequence separator) {
        return split(chars, separator, FsString::subRef);
    }

    /**
     * Splits given chars by given separator, using given sub-sequence function to generate sub CharSequence.
     * If chars or separator is empty, or separator's length is greater than chars' length,
     * or separator is never matched, return an empty list.
     * <p>
     * Note empty part will be created there is no char between separator and the next separator,
     * position of start or end. That means, if the returned list is not empty, its length is at least 2.
     *
     * @param chars     given chars
     * @param separator given separator
     * @param function  given sub-sequence function
     */
    public static List<CharSequence> split(CharSequence chars, CharSequence separator, SubSequenceFunction function) {
        if (isEmpty(chars) || isEmpty(separator) || separator.length() > chars.length()) {
            return Collections.emptyList();
        }
        List<CharSequence> result = new LinkedList<>();
        int wordStart = 0;
        while (true) {
            int index = indexOf(chars, separator, wordStart);
            if (index >= 0) {
                result.add(function.apply(chars, wordStart, index));
                wordStart = index + separator.length();
                if (wordStart >= chars.length()) {
                    result.add(function.apply(chars, wordStart, chars.length()));
                    return result;
                }
            } else {
                if (result.isEmpty()) {
                    return Collections.emptyList();
                }
                result.add(function.apply(chars, wordStart, chars.length()));
                return result;
            }
        }
    }

    /**
     * Replaces all given matcher in given chars with given replacement.
     *
     * @param chars       given chars
     * @param matcher     given matcher (to replace)
     * @param replacement given replacement
     */
    public static String replace(CharSequence chars, CharSequence matcher, CharSequence replacement) {
        return replace(chars, matcher, replacement, -1);
    }

    /**
     * Replaces given matcher in given chars with given replacement.
     * If given limit &lt; 0, replace all;
     * if given limit = 0, do nothing and return given chars to string;
     * If given limit > 0, this method will replace given limit times.
     * <p>
     * FOr example, replaceFirst is equivalent to
     * <pre>
     *     replace(chars, matcher, replacement, 1);
     * </pre>
     *
     * @param chars       given chars
     * @param matcher     given matcher (to replace)
     * @param replacement given replacement
     * @param limit       given limit
     */
    public static String replace(CharSequence chars, CharSequence matcher, CharSequence replacement, int limit) {
        if (limit == 0 || isEmpty(chars) || chars.length() < matcher.length() || isEmpty(matcher)) {
            return chars.toString();
        }
        if (matcher.length() == replacement.length()) {
            char[] result = new char[chars.length()];
            int cs = 0;
            int rs = 0;
            int count = 0;
            while (cs < chars.length()) {
                int i = indexOf(chars, matcher, cs);
                if (i >= 0) {
                    getChars(chars, cs, i, result, rs);
                    getChars(replacement, result, i);
                    cs = i + matcher.length();
                    rs = i + replacement.length();
                } else {
                    break;
                }
                if (limit > 0 && count++ >= limit) {
                    break;
                }
            }
            if (cs < chars.length()) {
                getChars(chars, cs, chars.length(), result, rs);
            }
            return new String(result);
        }
        StringBuilder sb = new StringBuilder();
        int cs = 0;
        int count = 0;
        while (cs < chars.length()) {
            int i = indexOf(chars, matcher, cs);
            if (i >= 0) {
                sb.append(chars, cs, i);
                sb.append(replacement);
                cs = i + matcher.length();
            } else {
                break;
            }
            if (limit > 0 && count++ >= limit) {
                break;
            }
        }
        if (cs < chars.length()) {
            sb.append(chars, cs, chars.length());
        }
        return sb.toString();
    }

    /**
     * Returns first index of given search word in given chars, starts from index 0,
     * in natural order (0,1,2,3...end).
     * Returns -1 if not found.
     *
     * @param chars  given chars
     * @param search given search word
     */
    public static int indexOf(CharSequence chars, CharSequence search) {
        return indexOf(chars, search, 0);
    }

    /**
     * Returns first index of given search word in given chars, starts from given index,
     * in natural order (0,1,2,3...end).
     * Returns -1 if not found.
     *
     * @param chars  given chars
     * @param search given search word
     * @param from   given index
     */
    public static int indexOf(CharSequence chars, CharSequence search, int from) {
        if (isEmpty(chars) || chars.length() < search.length()) {
            return -1;
        }
        FsCheck.checkArgument(!isEmpty(search), "search string is empty.");
        FsCheck.checkInBounds(from, 0, chars.length());
        if (chars.length() - from < search.length()) {
            return -1;
        }
        for (int i = from; i < chars.length(); i++) {
            if (chars.length() - i < search.length()) {
                return -1;
            }
            boolean match = true;
            for (int j = 0; j < search.length(); j++) {
                if (chars.charAt(i + j) != search.charAt(j)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns last index of given search word in given chars, starts from last index,
     * in reversed natural order (end...3,2,1,0).
     * Returns -1 if not found.
     *
     * @param chars  given chars
     * @param search given search word
     */
    public static int lastIndexOf(CharSequence chars, CharSequence search) {
        if (isEmpty(chars)) {
            return -1;
        }
        return lastIndexOf(chars, search, chars.length() - 1);
    }

    /**
     * Returns last index of given search word in given chars, starts given index,
     * in reversed natural order (end...3,2,1,0).
     * Returns -1 if not found.
     *
     * @param chars  given chars
     * @param search given search word
     * @param from   given index
     */
    public static int lastIndexOf(CharSequence chars, CharSequence search, int from) {
        if (isEmpty(chars) || chars.length() < search.length()) {
            return -1;
        }
        FsCheck.checkArgument(!isEmpty(search), "search string is empty.");
        FsCheck.checkInBounds(from, 0, chars.length());
        if (from + 1 < search.length()) {
            return -1;
        }
        for (int i = from; i >= 0; i--) {
            if (i + 1 < search.length()) {
                return -1;
            }
            boolean match = true;
            for (int j = 0; j < search.length(); j++) {
                if (chars.charAt(i - j) != search.charAt(search.length() - 1 - j)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                return i - search.length() + 1;
            }
        }
        return -1;
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
        return toStringWith(obj, true, true);
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
    public static String toStringWith(@Nullable Object obj, boolean arrayCheck, boolean deepToString) {
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
     * Encodes given string to bytes with default charset {@link FsProps#charset()}.
     *
     * @param chars given string
     */
    public static byte[] toBytes(CharSequence chars) {
        return toBytes(chars, FsProps.charset());
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
     * Puts chars in specified length from given chars into given dest starts at given offset.
     *
     * @param chars  given chars
     * @param dest   given dest
     * @param offset given offset
     */
    public static void getChars(CharSequence chars, char[] dest, int offset) {
        getChars(chars, 0, chars.length(), dest, offset);
    }

    /**
     * Puts chars in specified length from given chars into given dest starts at given offset.
     *
     * @param chars  given chars
     * @param start  start index of given chars inclusive
     * @param end    end index of given chars exclusive
     * @param dest   given dest
     * @param offset given offset
     */
    public static void getChars(CharSequence chars, int start, int end, char[] dest, int offset) {
        if (chars instanceof String) {
            ((String) chars).getChars(start, end, dest, offset);
        } else {
            FsCheck.checkRangeInBounds(start, end, 0, chars.length());
            FsCheck.checkRangeInBounds(offset, end - start, 0, dest.length);
            if (start == end) {
                return;
            }
            for (int i = 0; i < end - start; i++) {
                dest[offset + i] = chars.charAt(start + i);
            }
        }
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
        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            builder.append(arg);
        }
        return builder.toString();
    }

    /**
     * Concatenates toString of given arguments.
     *
     * @param args given arguments
     */
    public static String concat(Iterable<?> args) {
        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            builder.append(arg);
        }
        return builder.toString();
    }

    /**
     * Joins toString of given arguments with given separator.
     *
     * @param separator given separator
     * @param args      given arguments
     */
    public static String join(CharSequence separator, Object... args) {
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
    public static String join(CharSequence separator, Iterable<?> args) {
        StringJoiner joiner = new StringJoiner(separator);
        for (Object arg : args) {
            joiner.add(String.valueOf(arg));
        }
        return joiner.toString();
    }

    /**
     * Converts given chars to int, if given chars is blank or failed to convert, return 0.
     *
     * @param chars given chars
     */
    public static int toInt(@Nullable CharSequence chars) {
        return toInt(chars, 0);
    }

    /**
     * Converts given chars to int, if given chars is blank or failed to convert, return default value.
     *
     * @param chars        given chars
     * @param defaultValue default value
     */
    public static int toInt(@Nullable CharSequence chars, int defaultValue) {
        if (isBlank(chars)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(chars.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Converts given chars to long, if given chars is blank or failed to convert, return 0.
     *
     * @param chars given chars
     */
    public static long toLong(@Nullable CharSequence chars) {
        return toLong(chars, 0);
    }

    /**
     * Converts given chars to long, if given chars is blank or failed to convert, return default value.
     *
     * @param chars        given chars
     * @param defaultValue default value
     */
    public static long toLong(@Nullable CharSequence chars, long defaultValue) {
        if (isBlank(chars)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(chars.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Converts given chars to float, if given chars is blank or failed to convert, return 0.
     *
     * @param chars given chars
     */
    public static float toFloat(@Nullable CharSequence chars) {
        return toFloat(chars, 0);
    }

    /**
     * Converts given chars to float, if given chars is blank or failed to convert, return default value.
     *
     * @param chars        given chars
     * @param defaultValue default value
     */
    public static float toFloat(@Nullable CharSequence chars, float defaultValue) {
        if (isBlank(chars)) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(chars.toString());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Converts given chars to double, if given chars is blank or failed to convert, return 0.
     *
     * @param chars given chars
     */
    public static double toDouble(@Nullable CharSequence chars) {
        return toDouble(chars, 0);
    }

    /**
     * Converts given chars to double, if given chars is blank or failed to convert, return default value.
     *
     * @param chars        given chars
     * @param defaultValue default value
     */
    public static double toDouble(@Nullable CharSequence chars, double defaultValue) {
        if (isBlank(chars)) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(chars.toString());
        } catch (Exception e) {
            return defaultValue;
        }
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

    /**
     * Returns a sub char sequence of given chars from given start index inclusive to end.
     * The returned CharSequence doesn't store content data,
     * it is a reference points given chars and store start and end indexes.
     * That means, the returned CharSequence has very small overhead,
     * but also makes it difficult for the held CharSequence to be garbage collected.
     * <p>
     * Note the method {@link CharSequence#subSequence(int, int)} of returned CharSequence still uses this
     * {@link #subRef(CharSequence, int, int)} method.
     *
     * @param chars given chars
     * @param start given start index inclusive
     */
    public static CharSequence subRef(CharSequence chars, int start) {
        return subRef(chars, start, chars.length());
    }

    /**
     * Returns a sub char sequence of given chars from given start index inclusive to given end index exclusive.
     * The returned CharSequence doesn't store content data,
     * it is a reference points given chars and store start and end indexes.
     * That means, the returned CharSequence has very small overhead,
     * but also makes it difficult for the held CharSequence to be garbage collected.
     * <p>
     * Note the method {@link CharSequence#subSequence(int, int)} of returned CharSequence still uses this
     * {@link #subRef(CharSequence, int, int)} method.
     *
     * @param chars given chars
     * @param start given start index inclusive
     * @param end   given end index exclusive
     */
    public static CharSequence subRef(CharSequence chars, int start, int end) {
        FsCheck.checkRangeInBounds(start, end, 0, chars.length());
        return new CharSequence() {
            @Override
            public int length() {
                return end - start;
            }

            @Override
            public char charAt(int index) {
                FsCheck.checkInBounds(index, 0, length());
                return chars.charAt(start + index);
            }

            @NotNull
            @Override
            public CharSequence subSequence(int s, int e) {
                FsCheck.checkRangeInBounds(s, e, 0, length());
                return subRef(chars, start + s, start + e);
            }

            @Override
            public String toString() {
                return chars.subSequence(start, end).toString();
            }
        };
    }
}
