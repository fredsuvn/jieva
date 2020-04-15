package xyz.srclab.common.array;

import xyz.srclab.annotations.WrittenReturn;
import xyz.srclab.common.base.Checker;
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

    public static byte[] newArray(@WrittenReturn byte[] array, EachByte each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static char[] newArray(@WrittenReturn char[] array, EachChar each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static int[] newArray(@WrittenReturn int[] array, EachInt each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static long[] newArray(@WrittenReturn long[] array, EachLong each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static float[] newArray(@WrittenReturn float[] array, EachFloat each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static double[] newArray(@WrittenReturn double[] array, EachDouble each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static boolean[] newArray(@WrittenReturn boolean[] array, EachBoolean each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static short[] newArray(@WrittenReturn short[] array, EachShort each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static <T> T[] newArray(@WrittenReturn T[] array, Each<T> each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static byte[] newArray(@WrittenReturn byte[] array, int from, int to, EachByte each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static char[] newArray(@WrittenReturn char[] array, int from, int to, EachChar each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static int[] newArray(@WrittenReturn int[] array, int from, int to, EachInt each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static long[] newArray(@WrittenReturn long[] array, int from, int to, EachLong each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static float[] newArray(@WrittenReturn float[] array, int from, int to, EachFloat each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static double[] newArray(@WrittenReturn double[] array, int from, int to, EachDouble each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static boolean[] newArray(@WrittenReturn boolean[] array, int from, int to, EachBoolean each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static short[] newArray(@WrittenReturn short[] array, int from, int to, EachShort each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static <T> T[] newArray(@WrittenReturn T[] array, int from, int to, Each<T> each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static Class<?> findArrayType(Class<?> elementType) {
        return arrayTypeCache.getNonNull(
                KeyHelper.buildKey(elementType, "findArrayType"),
                o -> findArrayType0(elementType)
        );
    }

    private static Class<?> findArrayType0(Class<?> elementType) {
        return Array.newInstance(elementType, 0).getClass();
    }

    public static Type getGenericComponentType(Type type) {
        return genericComponentTypeCache.getNonNull(
                KeyHelper.buildKey(type, "getGenericComponentType"),
                o -> getGenericComponentType0(type)
        );
    }

    private static Type getGenericComponentType0(Type type) {
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        }
        return TypeHelper.getRawClass(type).getComponentType();
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
