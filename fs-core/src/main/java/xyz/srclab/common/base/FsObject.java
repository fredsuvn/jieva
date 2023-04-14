package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.build.annotations.FsMethods;

import java.util.Arrays;
import java.util.Objects;

/**
 * Utilities for Object.
 *
 * @author fredsuvn
 */
@FsMethods
public class FsObject {

    /**
     * Returns default value if given object is null, or given object itself if it is not null.
     *
     * @param obj          given object
     * @param defaultValue default value
     */
    public static <T> T notNull(@Nullable T obj, T defaultValue) {
        return obj == null ? defaultValue : obj;
    }

    /**
     * Returns hash code follows:
     * <ul>
     * <li>returns Objects.hashCode for given object if it is not an array;</li>
     * <li>if given object is primitive array, returns Arrays.hashCode for it;</li>
     * <li>if given object is Object[], returns Arrays.deepHashCode for it;</li>
     * <li>else returns Objects.hashCode for given object</li>
     * </ul>
     * <p>
     * This method is same as: hash(obj, true, true)
     *
     * @param obj given object
     */
    public static int hash(@Nullable Object obj) {
        return hash(obj, true, true);
    }

    /**
     * Returns deep-hash-code for given objects.
     *
     * @param objs given objects
     */
    public static int hash(Object... objs) {
        return Arrays.deepHashCode(objs);
    }

    /**
     * Returns hash code follows:
     * <ul>
     * <li>if given object is primitive array and array-check is true, returns Arrays.hashCode for it;</li>
     * <li>if given object is Object[] and both array-check and deep-to-string are true,
     * returns Arrays.deepHashCode for it;</li>
     * <li>if given object is Object[] and array-check is true and deep-to-string is false,
     * returns Arrays.hashCode for it;</li>
     * <li>else returns Objects.hashCode for given object</li>
     * </ul>
     *
     * @param obj        given object
     * @param arrayCheck the array-check
     * @param deepHash   whether deep-hash
     */
    public static int hash(@Nullable Object obj, boolean arrayCheck, boolean deepHash) {
        if (obj == null || !arrayCheck) {
            return Objects.hashCode(obj);
        }
        Class<?> type = obj.getClass();
        if (!type.isArray()) {
            return obj.hashCode();
        }
        if (obj instanceof Object[]) {
            return deepHash ? Arrays.deepHashCode((Object[]) obj) : Arrays.hashCode((Object[]) obj);
        }
        if (obj instanceof boolean[]) {
            return Arrays.hashCode((boolean[]) obj);
        }
        if (obj instanceof byte[]) {
            return Arrays.hashCode((byte[]) obj);
        }
        if (obj instanceof short[]) {
            return Arrays.hashCode((short[]) obj);
        }
        if (obj instanceof char[]) {
            return Arrays.hashCode((char[]) obj);
        }
        if (obj instanceof int[]) {
            return Arrays.hashCode((int[]) obj);
        }
        if (obj instanceof long[]) {
            return Arrays.hashCode((long[]) obj);
        }
        if (obj instanceof float[]) {
            return Arrays.hashCode((float[]) obj);
        }
        if (obj instanceof double[]) {
            return Arrays.hashCode((double[]) obj);
        }
        return obj.hashCode();
    }

    /**
     * Returns identity hash code for given object, same as {@link System#identityHashCode(Object)}.
     *
     * @param obj given object
     */
    public static int systemHash(@Nullable Object obj) {
        return System.identityHashCode(obj);
    }

    /**
     * Returns result of equaling follows:
     * <ul>
     * <li>returns Objects.equals for given objects if they are not arrays;</li>
     * <li>if given objects are arrays of which types are same primitive type, returns Arrays.equals for them;</li>
     * <li>if given objects are object array, returns Arrays.deepEquals for them;</li>
     * <li>else returns Objects.equals for given objects</li>
     * </ul>
     * <p>
     * This method is same as: equals(a, b, true, true)
     *
     * @param a given object a
     * @param b given object b
     */
    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return equals(a, b, true, true);
    }

    /**
     * Returns deep-equals for given objects.
     *
     * @param objs given objects
     */
    public static boolean equals(Object... objs) {
        if (objs.length <= 1) {
            return true;
        }
        if (objs.length == 2) {
            return equals(objs[0], objs[1]);
        }
        for (int i = 0; i < objs.length - 2; i++) {
            if (!equals(objs[i], objs[i + 1])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns result of equaling follows:
     * <ul>
     * <li>if given objects are arrays of which types are same primitive type and array-check is true,
     * returns Arrays.equals for them;</li>
     * <li>if given objects are object array and both array-check and deep-equals are true,
     * returns Arrays.deepEquals for them;</li>
     * <li>if given objects are object array and array-check is true and deep-equals is false,
     * returns Arrays.equals for them;</li>
     * <li>else returns Objects.equals for given objects</li>
     * </ul>
     *
     * @param a          given object a
     * @param b          given object b
     * @param arrayCheck the array-check
     * @param deepEquals whether deep-equals
     */
    public static boolean equals(@Nullable Object a, @Nullable Object b, boolean arrayCheck, boolean deepEquals) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        if (!arrayCheck) {
            return Objects.equals(a, b);
        }
        Class<?> typeA = a.getClass();
        Class<?> typeB = b.getClass();
        if (typeA.isArray() && typeB.isArray()) {
            if (a instanceof boolean[] && b instanceof boolean[]) {
                return Arrays.equals((boolean[]) a, (boolean[]) b);
            }
            if (a instanceof byte[] && b instanceof byte[]) {
                return Arrays.equals((byte[]) a, (byte[]) b);
            }
            if (a instanceof short[] && b instanceof short[]) {
                return Arrays.equals((short[]) a, (short[]) b);
            }
            if (a instanceof char[] && b instanceof char[]) {
                return Arrays.equals((char[]) a, (char[]) b);
            }
            if (a instanceof int[] && b instanceof int[]) {
                return Arrays.equals((int[]) a, (int[]) b);
            }
            if (a instanceof long[] && b instanceof long[]) {
                return Arrays.equals((long[]) a, (long[]) b);
            }
            if (a instanceof float[] && b instanceof float[]) {
                return Arrays.equals((float[]) a, (float[]) b);
            }
            if (a instanceof double[] && b instanceof double[]) {
                return Arrays.equals((double[]) a, (double[]) b);
            }
            return deepEquals ? Arrays.deepEquals((Object[]) a, (Object[]) b) : Arrays.equals((Object[]) a, (Object[]) b);
        }
        return Objects.equals(a, b);
    }
}
