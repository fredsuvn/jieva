package xyz.srclab.common.chain;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.lang.count.IntCounter;

import java.util.*;
import java.util.function.*;
import java.util.stream.LongStream;

/**
 * @author sunqian
 */
public interface LongChain extends LongStream, Iterable<Long> {

    static LongChain from(LongStream longStream) {
        return new LongStreamChain(longStream);
    }

    static LongChain from(boolean[] array) {
        return from(array, 0, array.length);
    }

    static LongChain from(boolean[] array, int startInclusive, int endExclusive) {
        return IntChain.from(array, startInclusive, endExclusive).asLongChain();
    }

    static LongChain from(byte[] array) {
        return from(array, 0, array.length);
    }

    static LongChain from(byte[] array, int startInclusive, int endExclusive) {
        return IntChain.from(array, startInclusive, endExclusive).asLongChain();
    }

    static LongChain from(short[] array) {
        return from(array, 0, array.length);
    }

    static LongChain from(short[] array, int startInclusive, int endExclusive) {
        return IntChain.from(array, startInclusive, endExclusive).asLongChain();
    }

    static LongChain from(char[] array) {
        return from(array, 0, array.length);
    }

    static LongChain from(char[] array, int startInclusive, int endExclusive) {
        return IntChain.from(array, startInclusive, endExclusive).asLongChain();
    }

    static LongChain from(int[] array) {
        return from(array, 0, array.length);
    }

    static LongChain from(int[] array, int startInclusive, int endExclusive) {
        return IntChain.from(array, startInclusive, endExclusive).asLongChain();
    }

    static LongChain from(long[] array) {
        return from(array, 0, array.length);
    }

    static LongChain from(long[] array, int startInclusive, int endExclusive) {
        return from(Arrays.stream(array, startInclusive, endExclusive));
    }

    static LongChain from(float[] array) {
        return from(array, 0, array.length);
    }

    static LongChain from(float[] array, int startInclusive, int endExclusive) {
        IntCounter intCounter = IntCounter.zero();
        return from(LongStream
                .generate(() -> (long) array[startInclusive + intCounter.getAndIncrement()])
                .limit(endExclusive - startInclusive)
        );
    }

    static LongChain from(double[] array) {
        return from(array, 0, array.length);
    }

    static LongChain from(double[] array, int startInclusive, int endExclusive) {
        return DoubleChain.from(array, startInclusive, endExclusive).mapToLong(d -> (long) d);
    }

    @Override
    LongChain filter(LongPredicate predicate);

    @Override
    LongChain map(LongUnaryOperator mapper);

    default <U> Chain<U> map(LongFunction<@Nullable ? extends U> mapper) {
        return mapToObj(mapper);
    }

    @Override
    <U> Chain<U> mapToObj(LongFunction<@Nullable ? extends U> mapper);

    @Override
    IntChain mapToInt(LongToIntFunction mapper);

    @Override
    DoubleChain mapToDouble(LongToDoubleFunction mapper);

    @Override
    LongChain flatMap(LongFunction<? extends LongStream> mapper);

    @Override
    LongChain distinct();

    @Override
    LongChain sorted();

    @Override
    LongChain peek(LongConsumer action);

    @Override
    LongChain limit(long maxSize);

    @Override
    LongChain skip(long n);

    @Override
    void forEach(LongConsumer action);

    @Override
    void forEachOrdered(LongConsumer action);

    @Override
    long[] toArray();

    @Override
    long reduce(long identity, LongBinaryOperator op);

    @Override
    OptionalLong reduce(LongBinaryOperator op);

    default long reduceLong(LongBinaryOperator op) {
        return reduce(0, op);
    }

    @Override
    <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator, BiConsumer<R, R> combiner);

    @Override
    long sum();

    @Override
    OptionalLong min();

    default long minLong(LongBinaryOperator op) {
        return min().orElse(0);
    }

    @Override
    OptionalLong max();

    default long maxLong(LongBinaryOperator op) {
        return max().orElse(0);
    }

    @Override
    long count();

    @Override
    OptionalDouble average();

    @Override
    LongSummaryStatistics summaryStatistics();

    @Override
    boolean anyMatch(LongPredicate predicate);

    @Override
    boolean allMatch(LongPredicate predicate);

    @Override
    boolean noneMatch(LongPredicate predicate);

    @Override
    OptionalLong findFirst();

    default long findFirstLong() {
        return findFirst().orElse(0);
    }

    @Override
    OptionalLong findAny();

    default long findAnyLong() {
        return findAny().orElse(0);
    }

    @Override
    DoubleChain asDoubleStream();

    default DoubleChain asDoubleChain() {
        return asDoubleStream();
    }

    @Override
    Chain<Long> boxed();

    @Override
    LongChain sequential();

    @Override
    LongChain parallel();

    @Override
    PrimitiveIterator.OfLong iterator();

    @Override
    Spliterator.OfLong spliterator();

    @Override
    boolean isParallel();

    @Override
    LongChain unordered();

    @Override
    LongChain onClose(Runnable closeHandler);

    @Override
    void close();
}
