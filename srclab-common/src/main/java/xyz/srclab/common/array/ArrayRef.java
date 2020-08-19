package xyz.srclab.common.array;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.Out;
import xyz.srclab.common.base.Check;
import xyz.srclab.common.chain.Chain;
import xyz.srclab.common.collection.ListKit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * @author sunqian
 */
public interface ArrayRef<T> {

    static <T> ArrayRef<T> of(T[] array) {
        return ArrayRefSupport.newArrayRef(array);
    }

    static <T> ArrayRef<T> of(T[] array, int startIndex, int endIndex) {
        return ArrayRefSupport.newArrayRef(array, startIndex, endIndex);
    }

    T[] origin();

    int originStartIndex();

    int originEndIndex();

    @Nullable
    T get(int index);

    ArrayRef<T> set(int index, @Nullable T value);

    int length();

    Class<?> componentType();

    default ArrayRef<T> subArray(int startIndex, int endIndex) {
        Check.checkIndexRange(startIndex, endIndex, originStartIndex(), originEndIndex());
        return of(origin(), originStartIndex() + startIndex, originStartIndex() + endIndex);
    }

    default <U> U[] map(
            IntFunction<U[]> generator, Function<@Nullable ? super T, @Nullable ? extends U> mapper) {
        U[] result = generator.apply(length());
        copyTo(result, mapper);
        return result;
    }

    default <U> ArrayRef<U> mapRef(
            IntFunction<U[]> generator, Function<@Nullable ? super T, @Nullable ? extends U> mapper) {
        return of(map(generator, mapper));
    }

    default ArrayRef<T> copyTo(@Out T[] array) {
        return copyTo(array, 0);
    }

    default ArrayRef<T> copyTo(@Out T[] array, int offset) {
        Check.checkIndexRange(offset, array.length, array.length);
        T[] origin = origin();
        int sourceStart = originStartIndex();
        int destStart = offset;
        int length = Math.min(length(), array.length - offset);
        System.arraycopy(origin, sourceStart, array, destStart, length);
        return this;
    }

    default <U> ArrayRef<T> copyTo(
            @Out U[] array, Function<@Nullable ? super T, @Nullable ? extends U> mapper) {
        return copyTo(array, 0, mapper);
    }

    default <U> ArrayRef<T> copyTo(
            @Out U[] array, int offset, Function<@Nullable ? super T, @Nullable ? extends U> mapper) {
        Check.checkIndexRange(offset, array.length, array.length);
        T[] origin = origin();
        int start = originStartIndex();
        int end = originEndIndex();
        for (int i = start, j = 0; i < end; i++, j++) {
            array[j + offset] = mapper.apply(origin[i]);
        }
        return this;
    }

    default ArrayRef<T> fill(@Nullable T value) {
        Arrays.fill(origin(), originStartIndex(), originEndIndex(), value);
        return this;
    }

    default ArrayRef<T> fill(IntFunction<@Nullable ? extends T> valueGenerator) {
        Arrays.fill(origin(), originStartIndex(), originEndIndex(), valueGenerator);
        return this;
    }

    default ArrayRef<T> sort() {
        Arrays.sort(origin(), originStartIndex(), originEndIndex());
        return this;
    }

    default ArrayRef<T> sort(Comparator<@Nullable ? super T> comparator) {
        Arrays.sort(origin(), originStartIndex(), originEndIndex(), comparator);
        return this;
    }

    default ArrayRef<T> parallelSort() {
        T[] array = origin();
        if (!Comparable.class.isAssignableFrom(componentType())) {
            throw new IllegalStateException(
                    "Component type of this array is not compatible with " + Comparable.class);
        }
        Arrays.parallelSort((Comparable[]) array, originStartIndex(), originEndIndex());
        return this;
    }

    default ArrayRef<T> parallelSort(Comparator<@Nullable ? super T> comparator) {
        Arrays.parallelSort(origin(), originStartIndex(), originEndIndex(), comparator);
        return this;
    }

    default int binarySearch(@Nullable T value) {
        return Arrays.binarySearch(origin(), originStartIndex(), originEndIndex(), value);
    }

    default int binarySearch(@Nullable T value, Comparator<@Nullable ? super T> comparator) {
        return Arrays.binarySearch(origin(), originStartIndex(), originEndIndex(), value, comparator);
    }

    default T[] toArray() {
        return Arrays.copyOfRange(origin(), originStartIndex(), originEndIndex());
    }

    default T[] toArray(int newLength) {
        int from = originStartIndex();
        int to = from + newLength;
        return Arrays.copyOfRange(origin(), from, to);
    }

    default ArrayRef<T> toArrayRef(int newLength) {
        return of(toArray(newLength));
    }

    default ArrayList<T> toArrayList() {
        ArrayList<T> result = new ArrayList<>(length());
        T[] origin = origin();
        int start = originStartIndex();
        int end = originEndIndex();
        for (int i = start; i < end; i++) {
            result.set(i, origin[i]);
        }
        return result;
    }

    @Immutable
    default List<T> toImmutableList() {
        return ListKit.immutable(toArrayList());
    }

    default Chain<T> toChain() {
        return Chain.from(origin(), originStartIndex(), originEndIndex());
    }
}
