package xyz.srclab.common.array;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.WrittenReturn;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.collection.IterableHelper;
import xyz.srclab.common.reflect.TypeHelper;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.function.Function;

public class ArrayHelper {

    private static final Cache<Type, Class<?>> elementToArrayTypeCache = Cache.newGcThreadLocalL2();

    public static <E> E[] toArray(Iterable<? extends E> iterable, Type componentType) {
        Collection<E> collection = IterableHelper.asCollection(iterable);
        Class<?> classType = TypeHelper.getRawType(componentType);
        E[] array = (E[]) Array.newInstance(classType, collection.size());
        int i = 0;
        for (E e : collection) {
            array[i++] = e;
        }
        return array;
    }

    public static <E> E[] toArray(Iterable<? extends E> iterable, TypeRef<E> componentType) {
        return toArray(iterable, componentType.getType());
    }

    public static <NE, OE> NE[] toArray(
            Iterable<? extends OE> iterable, Type componentType, Function<OE, NE> elementMapper) {
        Iterable<NE> newIterable = IterableHelper.map(iterable, elementMapper);
        return toArray(newIterable, componentType);
    }

    public static <NE, OE> NE[] toArray(
            Iterable<? extends OE> iterable, TypeRef<NE> componentType, Function<OE, NE> elementMapper) {
        return toArray(iterable, componentType.getType(), elementMapper);
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

    public static byte[] buildArray(@WrittenReturn byte[] array, ByteElementSupplier each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static char[] buildArray(@WrittenReturn char[] array, CharElementSupplier each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static int[] buildArray(@WrittenReturn int[] array, IntElementSupplier each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static long[] buildArray(@WrittenReturn long[] array, LongElementSupplier each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static float[] buildArray(@WrittenReturn float[] array, FloatElementSupplier each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static double[] buildArray(@WrittenReturn double[] array, DoubleElementSupplier each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static boolean[] buildArray(@WrittenReturn boolean[] array, BooleanElementSupplier each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static short[] buildArray(@WrittenReturn short[] array, ShortElementSupplier each) {
        for (int i = 0; i < array.length; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static <E> E[] buildArray(@WrittenReturn E[] array, ElementSupplier<E> elementSupplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = elementSupplier.get(i);
        }
        return array;
    }

    public static <E> E[] buildArray(
            @WrittenReturn Object[] array, TypeRef<E> type, ElementSupplier<E> elementSupplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = elementSupplier.get(i);
        }
        return (E[]) array;
    }

    public static byte[] buildArray(@WrittenReturn byte[] array, int from, int to, ByteElementSupplier each) {
        Checker.checkSubBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static char[] buildArray(@WrittenReturn char[] array, int from, int to, CharElementSupplier each) {
        Checker.checkSubBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static int[] buildArray(@WrittenReturn int[] array, int from, int to, IntElementSupplier each) {
        Checker.checkSubBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static long[] buildArray(@WrittenReturn long[] array, int from, int to, LongElementSupplier each) {
        Checker.checkSubBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static float[] buildArray(@WrittenReturn float[] array, int from, int to, FloatElementSupplier each) {
        Checker.checkSubBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static double[] buildArray(@WrittenReturn double[] array, int from, int to, DoubleElementSupplier each) {
        Checker.checkSubBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static boolean[] buildArray(@WrittenReturn boolean[] array, int from, int to, BooleanElementSupplier each) {
        Checker.checkSubBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static short[] buildArray(@WrittenReturn short[] array, int from, int to, ShortElementSupplier each) {
        Checker.checkSubBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = each.get(i);
        }
        return array;
    }

    public static <E> E[] buildArray(@WrittenReturn E[] array, int from, int to, ElementSupplier<E> elementSupplier) {
        Checker.checkSubBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = elementSupplier.get(i);
        }
        return array;
    }

    public static <E> E[] buildArray(
            @WrittenReturn Object[] array, TypeRef<E> type, int from, int to, ElementSupplier<E> elementSupplier) {
        Checker.checkSubBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = elementSupplier.get(i);
        }
        return (E[]) array;
    }

    public static Class<?> findArrayType(Class<?> elementType) {
        return elementToArrayTypeCache.getNonNull(
                elementType,
                o -> findArrayType0(elementType)
        );
    }

    private static Class<?> findArrayType0(Class<?> elementType) {
        return Array.newInstance(elementType, 0).getClass();
    }

    public static Type getGenericComponentType(Type type) {
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        }
        return TypeHelper.getRawType(type).getComponentType();
    }

    public interface ElementSupplier<E> {
        @Nullable
        E get(int index);
    }

    public interface ByteElementSupplier {
        byte get(int index);
    }

    public interface CharElementSupplier {
        char get(int index);
    }

    public interface IntElementSupplier {
        int get(int index);
    }

    public interface LongElementSupplier {
        long get(int index);
    }

    public interface FloatElementSupplier {
        float get(int index);
    }

    public interface DoubleElementSupplier {
        double get(int index);
    }

    public interface BooleanElementSupplier {
        boolean get(int index);
    }

    public interface ShortElementSupplier {
        short get(int index);
    }
}
