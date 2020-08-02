package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;

import java.util.*;

/**
 * @author sunqian
 */
public class Equal {

    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return Objects.equals(a, b);
    }

    public static boolean deepEquals(@Nullable Object a, @Nullable Object b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return deepEquals0(a, b);
    }

    private static boolean deepEquals0(Object a, Object b) {
        if (a instanceof Object[] && b instanceof Object[]) {
            return deepEquals0((Object[]) a, (Object[]) b);
        }
        if (a instanceof List && b instanceof List) {
            return deepEquals0((List<?>) a, (List<?>) b);
        }
        if (a instanceof Set && b instanceof Set) {
            return deepEquals0((Set<?>) a, (Set<?>) b);
        }
        if (a instanceof Map && b instanceof Map) {
            return deepEquals0((Map<?, ?>) a, (Map<?, ?>) b);
        }
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
        return a.equals(b);
    }

    public static boolean deepEquals(Object[] a, Object[] b) {
        if (a == b) {
            return true;
        }
        return deepEquals0(a, b);
    }

    private static boolean deepEquals0(Object[] a, Object[] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (!deepEquals(a[i], b[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean deepEquals(List<?> a, List<?> b) {
        if (a == b) {
            return true;
        }
        return deepEquals0(a, b);
    }

    private static boolean deepEquals0(List<?> a, List<?> b) {
        return collectionDeepEquals(a, b);
    }

    public static boolean deepEquals(Set<?> a, Set<?> b) {
        if (a == b) {
            return true;
        }
        return deepEquals0(a, b);
    }

    private static boolean deepEquals0(Set<?> a, Set<?> b) {
        return collectionDeepEquals(a, b);
    }

    public static boolean deepEquals(Collection<?> a, Collection<?> b) {
        if (a == b) {
            return true;
        }
        return deepEquals0(a, b);
    }

    private static boolean deepEquals0(Collection<?> a, Collection<?> b) {
        return collectionDeepEquals(a, b);
    }

    public static boolean deepEquals(Iterable<?> a, Iterable<?> b) {
        if (a == b) {
            return true;
        }
        return deepEquals0(a, b);
    }

    private static boolean deepEquals0(Iterable<?> a, Iterable<?> b) {
        return iterableDeepEquals(a, b);
    }

    private static boolean collectionDeepEquals(Collection<?> a, Collection<?> b) {
        if (a.size() != b.size()) {
            return false;
        }
        if (a.isEmpty()) {
            return true;
        }
        return iterableDeepEquals(a, b);
    }

    private static boolean iterableDeepEquals(Iterable<?> a, Iterable<?> b) {
        Iterator<?> iteratorA = a.iterator();
        Iterator<?> iteratorB = b.iterator();
        while (iteratorA.hasNext()) {
            if (!deepEquals(iteratorA.next(), iteratorB.next())) {
                return false;
            }
        }
        return true;
    }

    public static boolean deepEquals(Map<?, ?> a, Map<?, ?> b) {
        if (a == b) {
            return true;
        }
        return deepEquals0(a, b);
    }

    private static boolean deepEquals0(Map<?, ?> a, Map<?, ?> b) {
        if (a.size() != b.size()) {
            return false;
        }
        if (a.isEmpty()) {
            return true;
        }
        Iterator<? extends Map.Entry<?, ?>> iteratorA = a.entrySet().iterator();
        Iterator<? extends Map.Entry<?, ?>> iteratorB = b.entrySet().iterator();
        while (iteratorA.hasNext()) {
            Map.Entry<?, ?> entryA = iteratorA.next();
            Map.Entry<?, ?> entryB = iteratorB.next();
            if (!deepEquals(entryA.getKey(), entryB.getKey())
                    || !deepEquals(entryA.getValue(), entryB.getValue())) {
                return false;
            }
        }
        return true;
    }
}
