package xyz.srclab.common.chain;

import xyz.srclab.annotation.Nullable;

import java.util.*;
import java.util.function.*;
import java.util.stream.IntStream;

/**
 * @author sunqian
 */
public interface IntChain extends IntStream, Iterable<Integer> {

    static IntChain from(IntStream intStream) {
        return new IntStreamChain(intStream);
    }

    static IntChain from(boolean[] array) {
        return from(array, 0, array.length);
    }

    static IntChain from(boolean[] array, int startInclusive, int endExclusive) {
        return from(IntStream.range(startInclusive, endExclusive).map(i -> array[i] ? 1 : 0));
    }

    static IntChain from(byte[] array) {
        return from(array, 0, array.length);
    }

    static IntChain from(byte[] array, int startInclusive, int endExclusive) {
        return from(IntStream.range(startInclusive, endExclusive).map(i -> array[i]));
    }

    static IntChain from(short[] array) {
        return from(array, 0, array.length);
    }

    static IntChain from(short[] array, int startInclusive, int endExclusive) {
        return from(IntStream.range(startInclusive, endExclusive).map(i -> array[i]));
    }

    static IntChain from(char[] array) {
        return from(array, 0, array.length);
    }

    static IntChain from(char[] array, int startInclusive, int endExclusive) {
        return from(IntStream.range(startInclusive, endExclusive).map(i -> array[i]));
    }

    static IntChain from(int[] array) {
        return from(array, 0, array.length);
    }

    static IntChain from(int[] array, int startInclusive, int endExclusive) {
        return from(Arrays.stream(array, startInclusive, endExclusive));
    }

    static IntChain from(long[] array) {
        return from(array, 0, array.length);
    }

    static IntChain from(long[] array, int startInclusive, int endExclusive) {
        return from(IntStream.range(startInclusive, endExclusive).map(i -> (int) array[i]));
    }

    static IntChain from(float[] array) {
        return from(array, 0, array.length);
    }

    static IntChain from(float[] array, int startInclusive, int endExclusive) {
        return from(IntStream.range(startInclusive, endExclusive).map(i -> (int) array[i]));
    }

    static IntChain from(double[] array) {
        return from(array, 0, array.length);
    }

    static IntChain from(double[] array, int startInclusive, int endExclusive) {
        return from(IntStream.range(startInclusive, endExclusive).map(i -> (int) array[i]));
    }

    @Override
    IntChain filter(IntPredicate predicate);

    @Override
    IntChain map(IntUnaryOperator mapper);

    default <U> Chain<U> map(IntFunction<@Nullable ? extends U> mapper) {
        return mapToObj(mapper);
    }

    @Override
    <U> Chain<U> mapToObj(IntFunction<@Nullable ? extends U> mapper);

    @Override
    LongChain mapToLong(IntToLongFunction mapper);

    @Override
    DoubleChain mapToDouble(IntToDoubleFunction mapper);

    @Override
    IntChain flatMap(IntFunction<? extends IntStream> mapper);

    @Override
    IntChain distinct();

    @Override
    IntChain sorted();

    @Override
    IntChain peek(IntConsumer action);

    @Override
    IntChain limit(long maxSize);

    @Override
    IntChain skip(long n);

    @Override
    void forEach(IntConsumer action);

    @Override
    void forEachOrdered(IntConsumer action);

    @Override
    int[] toArray();

    @Override
    int reduce(int identity, IntBinaryOperator op);

    @Override
    OptionalInt reduce(IntBinaryOperator op);

    default int reduceInt(IntBinaryOperator op) {
        return reduce(0, op);
    }

    @Override
    <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner);

    @Override
    int sum();

    @Override
    OptionalInt min();

    default int minInt(IntBinaryOperator op) {
        return min().orElse(0);
    }

    @Override
    OptionalInt max();

    default int maxInt(IntBinaryOperator op) {
        return max().orElse(0);
    }

    @Override
    long count();

    @Override
    OptionalDouble average();

    @Override
    IntSummaryStatistics summaryStatistics();

    @Override
    boolean anyMatch(IntPredicate predicate);

    @Override
    boolean allMatch(IntPredicate predicate);

    @Override
    boolean noneMatch(IntPredicate predicate);

    @Override
    OptionalInt findFirst();

    default int findFirstInt() {
        return findFirst().orElse(0);
    }

    @Override
    OptionalInt findAny();

    default int findAnyInt() {
        return findAny().orElse(0);
    }

    @Override
    LongChain asLongStream();

    default LongChain asLongChain() {
        return asLongStream();
    }

    @Override
    DoubleChain asDoubleStream();

    default DoubleChain asDoubleChain() {
        return asDoubleStream();
    }

    @Override
    Chain<Integer> boxed();

    @Override
    IntChain sequential();

    @Override
    IntChain parallel();

    @Override
    PrimitiveIterator.OfInt iterator();

    @Override
    Spliterator.OfInt spliterator();

    @Override
    boolean isParallel();

    @Override
    IntChain unordered();

    @Override
    IntChain onClose(Runnable closeHandler);

    @Override
    void close();
}
