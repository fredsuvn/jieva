package xyz.srclab.common.array;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.WrittenReturn;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;
import xyz.srclab.common.collection.iterable.IterableHelper;
import xyz.srclab.common.reflect.type.TypeHelper;
import xyz.srclab.common.reflect.type.TypeRef;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;

public class ArrayHelper {

    private static final ThreadLocalCache<Type, Class<?>> arrayTypeCache = new ThreadLocalCache<>();

    private static final ThreadLocalCache<Type, Type> componentTypeCache = new ThreadLocalCache<>();

    public static <E> E[] toArray(Iterable<E> iterable, Type componentType) {
        Collection<E> collection = IterableHelper.asCollection(iterable);
        E[] array = (E[]) Array.newInstance(TypeHelper.getRawType(componentType), collection.size());
        int i = 0;
        for (E e : collection) {
            array[i++] = e;
        }
        return array;
    }

    public static <T> T[] toArray(Iterable<T> iterable, TypeRef<T> componentType) {
        return toArray(iterable, componentType.getType());
    }

    public static <E> E[] newArray(Class<E> componentType, int length) {
        return (E[]) Array.newInstance(componentType, length);
    }

    public static <E> E[] newArray(Type componentType, int length) {
        return newArray(TypeHelper.getRawType(componentType), length);
    }

    public static <E> E[] newArray(TypeRef<E> componentType, int length) {
        return newArray(componentType.getType(), length);
    }

    public static byte[] buildArray(@WrittenReturn byte[] array, EachByte each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static char[] buildArray(@WrittenReturn char[] array, EachChar each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static int[] buildArray(@WrittenReturn int[] array, EachInt each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static long[] buildArray(@WrittenReturn long[] array, EachLong each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static float[] buildArray(@WrittenReturn float[] array, EachFloat each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static double[] buildArray(@WrittenReturn double[] array, EachDouble each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static boolean[] buildArray(@WrittenReturn boolean[] array, EachBoolean each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static short[] buildArray(@WrittenReturn short[] array, EachShort each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static <E> E[] buildArray(@WrittenReturn E[] array, Each<E> each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static <E> E[] buildArray(@WrittenReturn Object[] array, TypeRef<E> type, Each<E> each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.apply(i);
        }
        return (E[]) array;
    }

    public static byte[] buildArray(@WrittenReturn byte[] array, int from, int to, EachByte each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static char[] buildArray(@WrittenReturn char[] array, int from, int to, EachChar each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static int[] buildArray(@WrittenReturn int[] array, int from, int to, EachInt each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static long[] buildArray(@WrittenReturn long[] array, int from, int to, EachLong each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static float[] buildArray(@WrittenReturn float[] array, int from, int to, EachFloat each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static double[] buildArray(@WrittenReturn double[] array, int from, int to, EachDouble each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static boolean[] buildArray(@WrittenReturn boolean[] array, int from, int to, EachBoolean each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static short[] buildArray(@WrittenReturn short[] array, int from, int to, EachShort each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static <E> E[] buildArray(@WrittenReturn E[] array, int from, int to, Each<E> each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return array;
    }

    public static <E> E[] buildArray(@WrittenReturn Object[] array, TypeRef<E> type, int from, int to, Each<E> each) {
        Checker.checkBoundsFromTo(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.apply(i);
        }
        return (E[]) array;
    }

    public static Class<?> findArrayType(Class<?> elementType) {
        return arrayTypeCache.getNonNull(
                elementType,
                o -> findArrayType0(elementType)
        );
    }

    private static Class<?> findArrayType0(Class<?> elementType) {
        return Array.newInstance(elementType, 0).getClass();
    }

    public static Type getGenericComponentType(Type type) {
        return componentTypeCache.getNonNull(
                type,
                o -> getGenericComponentType0(type)
        );
    }

    private static Type getGenericComponentType0(Type type) {
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        }
        return TypeHelper.getRawType(type).getComponentType();
    }

    public interface Each<E> {
        @Nullable
        E apply(int index);
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
