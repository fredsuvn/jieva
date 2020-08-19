package xyz.srclab.common.chain;

import xyz.srclab.annotation.Nullable;

import java.util.*;
import java.util.function.*;
import java.util.stream.DoubleStream;

/**
 * @author sunqian
 */
public interface DoubleChain extends DoubleStream {

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
