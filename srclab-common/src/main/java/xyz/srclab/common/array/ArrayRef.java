package xyz.srclab.common.array;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.annotation.Out;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.chain.Chain;
import xyz.srclab.common.collection.ListKit;

import java.lang.reflect.Array;
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

    T[] origin();

    int originStartIndex();

    int originEndIndex();

    @Nullable
    T get(int index);

    ArrayRef<T> set(int index, @Nullable T value);

    int length();

    Class<?> componentType();

    ArrayRef<T> subArray(int startInclusive, int endExclusive);

    <U> U[] map(
            IntFunction<U[]> generator, Function<@Nullable ? super T, @Nullable ? extends U> mapper);

    <U> ArrayRef<U> mapRef(
            IntFunction<U[]> generator, Function<@Nullable ? super T, @Nullable ? extends U> mapper);

    ArrayRef<T> copyTo(@Out T[] array, int offset);

    <U> ArrayRef<T> copyTo(
            @Out U[] array, Function<@Nullable ? super T, @Nullable ? extends U> mapper);

    <U> ArrayRef<T> copyTo(
            @Out U[] array, int offset, Function<@Nullable ? super T, @Nullable ? extends U> mapper);

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

    default T[] toArray() {
        return Arrays.copyOfRange(origin(), originStartIndex(), originEndIndex());
    }

    default T[] toArray(int newLength) {
        int from = originStartIndex();
        int to = from + newLength;
        return Arrays.copyOfRange(origin(), from, to);
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
