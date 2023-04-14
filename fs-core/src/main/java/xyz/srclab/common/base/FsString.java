package xyz.srclab.common.base;

import org.jetbrains.annotations.NotNull;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.build.annotations.FsMethods;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Utilities for String.
 *
 * @author fredsuvn
 */
@FsMethods
public class FsString {

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
     * Concatenates given strings.
     *
     * @param strings given strings
     */
    public static String concat(String... strings) {
        int length = 0;
        for (String string : strings) {
            length += string.length();
        }
        char[] chars = new char[length];
        int offset = 0;
        for (String string : strings) {
            string.getChars(0, string.length(), chars, offset);
            offset += string.length();
        }
        return new String(chars);
    }

    /**
     * Concatenates toString of given arguments.
     *
     * @param args given arguments
     */
    public static String concat(Object... args) {
        if (FsObject.equals(String.class, args.getClass().getComponentType())) {
            return concat((String[]) args);
        }
        return concat(FsArray.map(args, new String[args.length], String::valueOf));
    }

    /**
     * Concatenates toString of given arguments.
     *
     * @param args given arguments
     */
    public static String concat(Iterable<?> args) {
        if (args instanceof Collection) {
            String[] array = new String[((Collection<?>) args).size()];
            int i = 0;
            for (Object arg : args) {
                array[i] = String.valueOf(arg);
                i++;
            }
            return concat(array);
        } else {
            List<String> list = new LinkedList<>();
            for (Object arg : args) {
                list.add(String.valueOf(arg));
            }
            return concat(list);
        }
    }

    /**
     * Joins given strings with given separator.
     *
     * @param separator given separator
     * @param strings   given strings
     */
    public static String join(String separator, String... strings) {
        int length = 0;
        for (String string : strings) {
            length += string.length();
        }
        if (strings.length > 1) {
            length += (strings.length - 1) * separator.length();
        }
        char[] chars = new char[length];
        int offset = 0;
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            string.getChars(0, string.length(), chars, offset);
            offset += string.length();
            if (i < strings.length - 1) {
                separator.getChars(0, separator.length(), chars, offset);
                offset += separator.length();
            }
        }
        return new String(chars);
    }

    /**
     * Joins toString of given arguments with given separator.
     *
     * @param separator given separator
     * @param args      given arguments
     */
    public static String join(String separator, Object... args) {
        if (FsObject.equals(String.class, args.getClass().getComponentType())) {
            return join(separator, (String[]) args);
        }
        return join(separator, FsArray.map(args, new String[args.length], String::valueOf));
    }

    /**
     * Joins toString of given arguments with given separator.
     *
     * @param separator given separator
     * @param args      given arguments
     */
    public static String join(String separator, Iterable<?> args) {
        if (args instanceof Collection) {
            String[] array = new String[((Collection<?>) args).size()];
            int i = 0;
            for (Object arg : args) {
                array[i] = String.valueOf(arg);
                i++;
            }
            return join(separator, array);
        } else {
            List<String> list = new LinkedList<>();
            for (Object arg : args) {
                list.add(String.valueOf(arg));
            }
            return join(separator, list);
        }
    }

    /**
     * Returns an object which is lazy for executing method {@link Object#toString()},
     * the executing was provided by given supplier.
     * <p>
     * Note returned {@link CharSequence}'s other methods (such as {@link CharSequence#length()})
     * were based on its toString() (and the toString() is lazy).
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
