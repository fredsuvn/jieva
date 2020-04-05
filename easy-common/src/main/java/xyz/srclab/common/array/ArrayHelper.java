package xyz.srclab.common.array;

import xyz.srclab.annotation.WriteReturn;
import xyz.srclab.common.base.KeyHelper;
import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;
import xyz.srclab.common.reflect.type.TypeHelper;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;

public class ArrayHelper {

    private static final ThreadLocalCache<Object, Class<?>> arrayTypeCache = new ThreadLocalCache<>();

    private static final ThreadLocalCache<Object, Type> genericComponentTypeCache = new ThreadLocalCache<>();

    public static <A> A newArray(Class<?> componentType, int length) {
        return (A) Array.newInstance(componentType, length);
    }

    public static byte[] newArray(@WriteReturn byte[] array, EachByte each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static char[] newArray(@WriteReturn char[] array, EachChar each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static int[] newArray(@WriteReturn int[] array, EachInt each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static long[] newArray(@WriteReturn long[] array, EachLong each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static float[] newArray(@WriteReturn float[] array, EachFloat each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static double[] newArray(@WriteReturn double[] array, EachDouble each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static boolean[] newArray(@WriteReturn boolean[] array, EachBoolean each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static short[] newArray(@WriteReturn short[] array, EachShort each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static <T> T[] newArray(@WriteReturn T[] array, Each<T> each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static byte[] newArray(@WriteReturn byte[] array, int from, int to, EachByte each) {
        if (from > to) {
            throw new IllegalArgumentException("from > to");
        }
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static char[] newArray(@WriteReturn char[] array, int from, int to, EachChar each) {
        if (from > to) {
            throw new IllegalArgumentException("from > to");
        }
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static int[] newArray(@WriteReturn int[] array, int from, int to, EachInt each) {
        if (from > to) {
            throw new IllegalArgumentException("from > to");
        }
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static long[] newArray(@WriteReturn long[] array, int from, int to, EachLong each) {
        if (from > to) {
            throw new IllegalArgumentException("from > to");
        }
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static float[] newArray(@WriteReturn float[] array, int from, int to, EachFloat each) {
        if (from > to) {
            throw new IllegalArgumentException("from > to");
        }
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static double[] newArray(@WriteReturn double[] array, int from, int to, EachDouble each) {
        if (from > to) {
            throw new IllegalArgumentException("from > to");
        }
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static boolean[] newArray(@WriteReturn boolean[] array, int from, int to, EachBoolean each) {
        if (from > to) {
            throw new IllegalArgumentException("from > to");
        }
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static short[] newArray(@WriteReturn short[] array, int from, int to, EachShort each) {
        if (from > to) {
            throw new IllegalArgumentException("from > to");
        }
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static <T> T[] newArray(@WriteReturn T[] array, int from, int to, Each<T> each) {
        if (from > to) {
            throw new IllegalArgumentException("from > to");
        }
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static Class<?> findArrayType(Class<?> elementType) {
        return arrayTypeCache.getNonNull(
                buildArrayTypeKey(elementType, "findArrayType"),
                o -> findArrayType0(elementType)
        );
    }

    private static Class<?> findArrayType0(Class<?> elementType) {
        return Array.newInstance(elementType, 0).getClass();
    }

    private static Object buildArrayTypeKey(Class<?> cls, String typeScope) {
        return KeyHelper.buildKey(cls, typeScope);
    }

    public static Type getGenericComponentType(Type type) {
        return genericComponentTypeCache.getNonNull(
                buildGenericComponentTypeKey(type, "getGenericComponentType"),
                o -> getGenericComponentType0(type)
        );
    }

    private static Type getGenericComponentType0(Type type) {
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        }
        return TypeHelper.getRawClass(type).getComponentType();
    }

    private static Object buildGenericComponentTypeKey(Type type, String typeScope) {
        return KeyHelper.buildKey(type, typeScope);
    }

    public interface Each<T> {
        T apply(int index);
    }

    public interface EachByte {
        byte apply(int index);
    }

    public interface EachChar {
        char apply(int index);
    }

    public interface EachInt {
        int apply(int index);
    }

    public interface EachLong {
        long apply(int index);
    }

    public interface EachFloat {
        float apply(int index);
    }

    public interface EachDouble {
        double apply(int index);
    }

    public interface EachBoolean {
        boolean apply(int index);
    }

    public interface EachShort {
        short apply(int index);
    }
}
