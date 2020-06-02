package xyz.srclab.common.array;

import com.google.common.collect.Iterables;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.OutReturn;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.collection.IterableKit;
import xyz.srclab.common.reflect.TypeKit;
import xyz.srclab.common.reflect.TypeRef;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.function.Function;

public class ArrayKit {

    public static <T> T[] newArray(Class<T> componentType, int length) {
        return Cast.as(Array.newInstance(componentType, length));
    }

    public static <T> T[] newArray(Type componentType, int length) {
        return newArray(TypeKit.getRawType(componentType), length);
    }

    public static <T> T[] newArray(TypeRef<T> componentType, int length) {
        return newArray(componentType.getType(), length);
    }

    public static <T> T[] toArray(Iterable<? extends T> iterable, Class<T> componentType) {
        return Iterables.toArray(iterable, componentType);
    }

    public static <T> T[] toArray(Iterable<? extends T> iterable, Type componentType) {
        return toArray(iterable, TypeKit.getRawType(componentType));
    }

    public static <T> T[] toArray(Iterable<? extends T> iterable, TypeRef<T> componentType) {
        return toArray(iterable, componentType.getType());
    }

    public static <NT, OT> NT[] map(
            Iterable<? extends OT> iterable, Class<NT> newComponentType, Function<OT, NT> elementMapper) {
        Iterable<NT> newIterable = IterableKit.map(iterable, elementMapper);
        return toArray(newIterable, newComponentType);
    }

    public static <NT, OT> NT[] map(
            Iterable<? extends OT> iterable, Type newComponentType, Function<OT, NT> elementMapper) {
        Iterable<NT> newIterable = IterableKit.map(iterable, elementMapper);
        return toArray(newIterable, newComponentType);
    }

    public static <NT, OT> NT[] map(
            Iterable<? extends OT> iterable, TypeRef<NT> newComponentType, Function<OT, NT> elementMapper) {
        Iterable<NT> newIterable = IterableKit.map(iterable, elementMapper);
        return toArray(newIterable, newComponentType);
    }

    public static <T> T[] buildArray(@OutReturn T[] array, ObjectSupplier<T> supplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static boolean[] buildArray(@OutReturn boolean[] array, BooleanSupplier supplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static byte[] buildArray(@OutReturn byte[] array, ByteSupplier supplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static short[] buildArray(@OutReturn short[] array, ShortSupplier supplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static char[] buildArray(@OutReturn char[] array, CharSupplier supplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static int[] buildArray(@OutReturn int[] array, IntSupplier supplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static long[] buildArray(@OutReturn long[] array, LongSupplier supplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static float[] buildArray(@OutReturn float[] array, FloatSupplier supplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static double[] buildArray(@OutReturn double[] array, DoubleSupplier supplier) {
        for (int i = 0; i < array.length; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static <T> T[] buildArray(@OutReturn T[] array, int from, int to, ObjectSupplier<T> supplier) {
        Checker.checkBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static boolean[] buildArray(@OutReturn boolean[] array, int from, int to, BooleanSupplier supplier) {
        Checker.checkBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static byte[] buildArray(@OutReturn byte[] array, int from, int to, ByteSupplier supplier) {
        Checker.checkBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static short[] buildArray(@OutReturn short[] array, int from, int to, ShortSupplier supplier) {
        Checker.checkBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static char[] buildArray(@OutReturn char[] array, int from, int to, CharSupplier supplier) {
        Checker.checkBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static int[] buildArray(@OutReturn int[] array, int from, int to, IntSupplier supplier) {
        Checker.checkBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static long[] buildArray(@OutReturn long[] array, int from, int to, LongSupplier supplier) {
        Checker.checkBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static float[] buildArray(@OutReturn float[] array, int from, int to, FloatSupplier supplier) {
        Checker.checkBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static double[] buildArray(@OutReturn double[] array, int from, int to, DoubleSupplier supplier) {
        Checker.checkBounds(array.length, from, to);
        for (int i = from; i < to; i++) {
            array[i] = supplier.get(i);
        }
        return array;
    }

    public static Type getGenericComponentType(Type type) {
        if (type instanceof GenericArrayType) {
            return ((GenericArrayType) type).getGenericComponentType();
        }
        Class<?> rawType = TypeKit.getRawType(type);
        if (!rawType.isArray()) {
            throw new IllegalArgumentException("Type is not array: " + type);
        }
        return rawType.getComponentType();
    }

    public static Class<?> findArrayType(Type componentType) {
        return ArrayTypeTable.search(TypeKit.getRawType(componentType));
    }

    public interface ObjectSupplier<E> {
        @Nullable
        E get(int index);
    }

    public interface BooleanSupplier {
        boolean get(int index);
    }

    public interface ByteSupplier {
        byte get(int index);
    }

    public interface ShortSupplier {
        short get(int index);
    }

    public interface CharSupplier {
        char get(int index);
    }

    public interface IntSupplier {
        int get(int index);
    }

    public interface LongSupplier {
        long get(int index);
    }

    public interface FloatSupplier {
        float get(int index);
    }

    public interface DoubleSupplier {
        double get(int index);
    }

    private static final class ArrayTypeTable {

        // Key: component type, value: array type
        private static final Cache<Class<?>, Class<?>> cache = Cache.newL2();

        private static final Class<?>[] table = {
                Object.class, Object[].class,
                String.class, String[].class,
                boolean.class, boolean[].class,
                byte.class, byte[].class,
                short.class, short[].class,
                char.class, char[].class,
                int.class, int[].class,
                long.class, long[].class,
                float.class, float[].class,
                double.class, double[].class,
                Boolean.class, Boolean[].class,
                Byte.class, Byte[].class,
                Short.class, Short[].class,
                Character.class, Character[].class,
                Integer.class, Integer[].class,
                Long.class, Long[].class,
                Float.class, Float[].class,
                Double.class, Double[].class,
        };

        private static Class<?> search(Class<?> componentType) {
            for (int i = 0; i < table.length; ) {
                if (table[i].equals(componentType)) {
                    return table[i + 1];
                } else {
                    i += 2;
                }
            }
            return cache.getNonNull(componentType, ArrayTypeTable::find);
        }

        private static Class<?> find(Class<?> componentType) {
            return newArray(componentType, 0).getClass().getComponentType();
        }
    }
}
