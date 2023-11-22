package xyz.fsgek.common.collect;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.Gek;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Array utilities.
 *
 * @author fredsuvn
 */
public class GekArray {

    /**
     * Returns given arguments as an array.
     *
     * @param args given arguments
     * @param <T>  component type
     * @return given arguments as an array
     */
    @SafeVarargs
    public static <T> T[] array(T... args) {
        return args;
    }

    /**
     * Returns whether given array is null or empty.
     *
     * @param array given array
     * @return whether given array is null or empty
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
     * Returns whether given array is not null and empty.
     *
     * @param array given array
     * @return whether given array is not null and empty
     */
    public static boolean isNotEmpty(@Nullable Object array) {
        return !isEmpty(array);
    }

    /**
     * Maps source array of type T[] to dest array of type R[].
     * If the dest array's length equals to source array, the mapped elements will be put into the dest array,
     * else create and put into a new array.
     *
     * @param source the source array
     * @param dest   the dest array
     * @param mapper given mapper
     * @param <T>    component type of source array
     * @param <R>    component type of dest array
     * @return the dest array
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
     * @return created array
     */
    public static <A> A newArray(Class<?> componentType, int length) {
        return Gek.as(Array.newInstance(componentType, length));
    }

    /**
     * Returns value from given array at specified index, if failed to obtain, return null.
     *
     * @param array given array
     * @param index specified index
     * @param <T>   component type
     * @return value from given array at specified index, if failed to obtain, return null
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
     * @param <T>          component type
     * @return value from given array at specified index, if the value is null or failed to obtain, return default value
     */
    public static <T> T get(@Nullable T[] array, int index, @Nullable T defaultValue) {
        if (array == null || index < 0 || index >= array.length) {
            return defaultValue;
        }
        T value = array[index];
        return value == null ? defaultValue : value;
    }

    /**
     * Returns index of given element at given array, or -1 if not found.
     *
     * @param array   given array
     * @param element given element
     * @param <T>     component type
     * @return index of given element at given array, or -1 if not found
     */
    public static <T> int indexOf(T[] array, T element) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(element, array[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns a wrapper list for given array, the list's size is fixed and any operation will reflect to each other.
     *
     * @param array given array
     * @param <T>   component type
     * @return a wrapper list for given array
     */
    public static <T> List<T> asList(T[] array) {
        return Arrays.asList(array);
    }

    /**
     * Returns a wrapper list for given array, the list's size is fixed and any operation will reflect to each other.
     *
     * @param array given array
     * @return a wrapper list for given array
     */
    public static List<Boolean> asList(boolean[] array) {
        return new BooleanArrayList(array);
    }

    /**
     * Returns a wrapper list for given array, the list's size is fixed and any operation will reflect to each other.
     *
     * @param array given array
     * @return a wrapper list for given array
     */
    public static List<Byte> asList(byte[] array) {
        return new ByteArrayList(array);
    }

    /**
     * Returns a wrapper list for given array, the list's size is fixed and any operation will reflect to each other.
     *
     * @param array given array
     * @return a wrapper list for given array
     */
    public static List<Short> asList(short[] array) {
        return new ShortArrayList(array);
    }

    /**
     * Returns a wrapper list for given array, the list's size is fixed and any operation will reflect to each other.
     *
     * @param array given array
     * @return a wrapper list for given array
     */
    public static List<Character> asList(char[] array) {
        return new CharacterArrayList(array);
    }

    /**
     * Returns a wrapper list for given array, the list's size is fixed and any operation will reflect to each other.
     *
     * @param array given array
     * @return a wrapper list for given array
     */
    public static List<Integer> asList(int[] array) {
        return new IntegerArrayList(array);
    }

    /**
     * Returns a wrapper list for given array, the list's size is fixed and any operation will reflect to each other.
     *
     * @param array given array
     * @return a wrapper list for given array
     */
    public static List<Long> asList(long[] array) {
        return new LongArrayList(array);
    }

    /**
     * Returns a wrapper list for given array, the list's size is fixed and any operation will reflect to each other.
     *
     * @param array given array
     * @return a wrapper list for given array
     */
    public static List<Float> asList(float[] array) {
        return new FloatArrayList(array);
    }

    /**
     * Returns a wrapper list for given array, the list's size is fixed and any operation will reflect to each other.
     *
     * @param array given array
     * @return a wrapper list for given array
     */
    public static List<Double> asList(double[] array) {
        return new DoubleArrayList(array);
    }

    private static final class BooleanArrayList extends AbstractList<Boolean> {

        private final boolean[] array;

        private BooleanArrayList(boolean[] array) {
            this.array = array;
        }

        @Override
        public Boolean get(int index) {
            return array[index];
        }

        @Override
        public Boolean set(int index, Boolean element) {
            Boolean old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class ByteArrayList extends AbstractList<Byte> {

        private final byte[] array;

        private ByteArrayList(byte[] array) {
            this.array = array;
        }

        @Override
        public Byte get(int index) {
            return array[index];
        }

        @Override
        public Byte set(int index, Byte element) {
            Byte old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class ShortArrayList extends AbstractList<Short> {

        private final short[] array;

        private ShortArrayList(short[] array) {
            this.array = array;
        }

        @Override
        public Short get(int index) {
            return array[index];
        }

        @Override
        public Short set(int index, Short element) {
            short old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class CharacterArrayList extends AbstractList<Character> {

        private final char[] array;

        private CharacterArrayList(char[] array) {
            this.array = array;
        }

        @Override
        public Character get(int index) {
            return array[index];
        }

        @Override
        public Character set(int index, Character element) {
            Character old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class IntegerArrayList extends AbstractList<Integer> {

        private final int[] array;

        private IntegerArrayList(int[] array) {
            this.array = array;
        }

        @Override
        public Integer get(int index) {
            return array[index];
        }

        @Override
        public Integer set(int index, Integer element) {
            Integer old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class LongArrayList extends AbstractList<Long> {

        private final long[] array;

        private LongArrayList(long[] array) {
            this.array = array;
        }

        @Override
        public Long get(int index) {
            return array[index];
        }

        @Override
        public Long set(int index, Long element) {
            Long old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class FloatArrayList extends AbstractList<Float> {

        private final float[] array;

        private FloatArrayList(float[] array) {
            this.array = array;
        }

        @Override
        public Float get(int index) {
            return array[index];
        }

        @Override
        public Float set(int index, Float element) {
            Float old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    private static final class DoubleArrayList extends AbstractList<Double> {

        private final double[] array;

        private DoubleArrayList(double[] array) {
            this.array = array;
        }

        @Override
        public Double get(int index) {
            return array[index];
        }

        @Override
        public Double set(int index, Double element) {
            Double old = array[index];
            array[index] = element;
            return old;
        }

        @Override
        public int size() {
            return array.length;
        }
    }
}
