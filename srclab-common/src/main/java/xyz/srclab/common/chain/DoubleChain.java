package xyz.srclab.common.chain;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.lang.count.IntCounter;

import java.util.*;
import java.util.function.*;
import java.util.stream.DoubleStream;

/**
 * @author sunqian
 */
public interface DoubleChain extends DoubleStream, Iterable<Double> {

    static DoubleChain from(DoubleStream doubleStream) {
        return new DoubleStreamChain(doubleStream);
    }

    static DoubleChain from(boolean[] array) {
        return from(array, 0, array.length);
    }

    static DoubleChain from(boolean[] array, int startInclusive, int endExclusive) {
        return IntChain.from(array, startInclusive, endExclusive).asDoubleChain();
    }

    static DoubleChain from(byte[] array) {
        return from(array, 0, array.length);
    }

    static DoubleChain from(byte[] array, int startInclusive, int endExclusive) {
        return IntChain.from(array, startInclusive, endExclusive).asDoubleChain();
    }

    static DoubleChain from(short[] array) {
        return from(array, 0, array.length);
    }

    static DoubleChain from(short[] array, int startInclusive, int endExclusive) {
        return IntChain.from(array, startInclusive, endExclusive).asDoubleChain();
    }

    static DoubleChain from(char[] array) {
        return from(array, 0, array.length);
    }

    static DoubleChain from(char[] array, int startInclusive, int endExclusive) {
        return IntChain.from(array, startInclusive, endExclusive).asDoubleChain();
    }

    static DoubleChain from(int[] array) {
        return from(array, 0, array.length);
    }

    static DoubleChain from(int[] array, int startInclusive, int endExclusive) {
        return IntChain.from(array, startInclusive, endExclusive).asDoubleChain();
    }

    static DoubleChain from(long[] array) {
        return from(array, 0, array.length);
    }

    static DoubleChain from(long[] array, int startInclusive, int endExclusive) {
        return LongChain.from(array, startInclusive, endExclusive).asDoubleStream();
    }

    static DoubleChain from(float[] array) {
        return from(array, 0, array.length);
    }

    static DoubleChain from(float[] array, int startInclusive, int endExclusive) {
        IntCounter intCounter = IntCounter.fromZero();
        return from(DoubleStream
                .generate(() -> array[startInclusive + intCounter.getAndIncrement()])
                .limit(endExclusive - startInclusive)
        );
    }

    static DoubleChain from(double[] array) {
        return from(array, 0, array.length);
    }

    static DoubleChain from(double[] array, int startInclusive, int endExclusive) {
        return new DoubleStreamChain(Arrays.stream(array, startInclusive, endExclusive));
    }

    @Override
    DoubleChain filter(DoublePredicate predicate);

    @Override
    DoubleChain map(DoubleUnaryOperator mapper);

    default <U> Chain<U> map(DoubleFunction<@Nullable ? extends U> mapper) {
        return mapToObj(mapper);
    }

    @Override
    <U> Chain<U> mapToObj(DoubleFunction<@Nullable ? extends U> mapper);

    @Override
    IntChain mapToInt(DoubleToIntFunction mapper);

    @Override
    LongChain mapToLong(DoubleToLongFunction mapper);

    @Override
    DoubleChain flatMap(DoubleFunction<? extends DoubleStream> mapper);

    @Override
    DoubleChain distinct();

    @Override
    DoubleChain sorted();

    @Override
    DoubleChain peek(DoubleConsumer action);

    @Override
    DoubleChain limit(long maxSize);

    @Override
    DoubleChain skip(long n);

    @Override
    void forEach(DoubleConsumer action);

    @Override
    void forEachOrdered(DoubleConsumer action);

    @Override
    double[] toArray();

    @Override
    double reduce(double identity, DoubleBinaryOperator op);

    @Override
    OptionalDouble reduce(DoubleBinaryOperator op);

    default double reduceDouble(DoubleBinaryOperator op) {
        return reduce(0, op);
    }

    @Override
    <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator, BiConsumer<R, R> combiner);

    @Override
    double sum();

    @Override
    OptionalDouble min();

    default double minDouble(DoubleBinaryOperator op) {
        return min().orElse(0);
    }

    @Override
    OptionalDouble max();

    default double maxDouble(DoubleBinaryOperator op) {
        return max().orElse(0);
    }

    @Override
    long count();

    @Override
    OptionalDouble average();

    @Override
    DoubleSummaryStatistics summaryStatistics();

    @Override
    boolean anyMatch(DoublePredicate predicate);

    @Override
    boolean allMatch(DoublePredicate predicate);

    @Override
    boolean noneMatch(DoublePredicate predicate);

    @Override
    OptionalDouble findFirst();

    default double findFirstDouble() {
        return findFirst().orElse(0);
    }

    @Override
    OptionalDouble findAny();

    default double findAnyDouble() {
        return findAny().orElse(0);
    }

    @Override
    Chain<Double> boxed();

    @Override
    DoubleChain sequential();

    @Override
    DoubleChain parallel();

    @Override
    PrimitiveIterator.OfDouble iterator();

    @Override
    Spliterator.OfDouble spliterator();

    @Override
    boolean isParallel();

    @Override
    DoubleChain unordered();

    @Override
    DoubleChain onClose(Runnable closeHandler);

    @Override
    void close();
}
