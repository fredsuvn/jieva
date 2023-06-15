package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.function.Function;

/**
 * Array utilities.
 *
 * @author fredsuvn
 */
public class FsArray {

    /**
     * Returns given arguments as an array.
     *
     * @param args given arguments
     */
    @SafeVarargs
    public static <T> T[] array(T... args) {
        return args;
    }

    /**
     * Returns whether given array if null or empty.
     *
     * @param array given array
     */
    public static boolean isEmpty(@Nullable Object array) {
        if (array == null) {
            return true;
        }
        if (array instanceof Object[]) {
            return ((Object[]) array).length == 0;
        }
        if (array instanceof boolean[]) {
            return ((boolean[]) array).length == 0;
        }
        if (array instanceof byte[]) {
            return ((byte[]) array).length == 0;
        }
        if (array instanceof short[]) {
            return ((short[]) array).length == 0;
        }
        if (array instanceof char[]) {
            return ((char[]) array).length == 0;
        }
        if (array instanceof int[]) {
            return ((int[]) array).length == 0;
        }
        if (array instanceof long[]) {
            return ((long[]) array).length == 0;
        }
        if (array instanceof float[]) {
            return ((float[]) array).length == 0;
        }
        if (array instanceof double[]) {
            return ((double[]) array).length == 0;
        }
        throw new IllegalArgumentException("Given array is not type of array: " + array.getClass());
    }

    /**
     * Maps source array of type T[] to dest array of type R[].
     * If the dest array's length equals to source array, the mapped elements will be put into the dest array,
     * else create and put into a new array.
     *
     * @param <T>    component type of source array
     * @param <R>    component type of dest array
     * @param source the source array
     * @param dest   the dest array
     * @param mapper given mapper
     */
    public static <T, R> R[] map(T[] source, R[] dest, Function<T, R> mapper) {
        R[] result;
        if (dest.length == source.length) {
            result = dest;
        } else {
            result = newArray(dest.getClass().getComponentType(), source.length);
        }
        for (int i = 0; i < source.length; i++) {
            result[i] = mapper.apply(source[i]);
        }
        return result;
    }

    /**
     * Creates a new array of given component type and length.
     *
     * @param componentType given component type
     * @param length        given length
     * @param <A>           type of array, including primitive array
     */
    public static <A> A newArray(Class<?> componentType, int length) {
        return Fs.as(Array.newInstance(componentType, length));
    }

    /**
     * Returns value from given array at specified index, if failed to obtain, return null.
     *
     * @param array given array
     * @param index specified index
     */
    public static <T> T get(@Nullable T[] array, int index) {
        return get(array, index, null);
    }

    /**
     * Returns value from given array at specified index, if the value is null or failed to obtain, return default value.
     *
     * @param array        given array
     * @param index        specified index
     * @param defaultValue default value
     */
    public static <T> T get(@Nullable T[] array, int index, @Nullable T defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        T value = array[index];
        return value == null ? defaultValue : value;
    }
}
