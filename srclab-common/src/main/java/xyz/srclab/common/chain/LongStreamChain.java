package xyz.srclab.common.chain;

import java.util.*;
import java.util.function.*;
import java.util.stream.LongStream;

/**
 * @author sunqian
 */
public class LongStreamChain implements LongChain {

    private LongStream longStream;

    public LongStreamChain(LongStream longStream) {
        this.longStream = longStream;
    }

    @Override
    public LongChain filter(LongPredicate predicate) {
        longStream = longStream.filter(predicate);
        return this;
    }

    @Override
    public LongChain map(LongUnaryOperator mapper) {
        longStream = longStream.map(mapper);
        return this;
    }

    @Override
    public <U> Chain<U> mapToObj(LongFunction<? extends U> mapper) {
        return new StreamChain<>(longStream.mapToObj(mapper));
    }

    @Override
    public IntChain mapToInt(LongToIntFunction mapper) {
        return new IntStreamChain(longStream.mapToInt(mapper));
    }

    @Override
    public DoubleChain mapToDouble(LongToDoubleFunction mapper) {
        return new DoubleStreamChain(longStream.mapToDouble(mapper));
    }

    @Override
    public LongChain flatMap(LongFunction<? extends LongStream> mapper) {
        longStream = longStream.flatMap(mapper);
        return this;
    }

    @Override
    public LongChain distinct() {
        longStream = longStream.distinct();
        return this;
    }

    @Override
    public LongChain sorted() {
        longStream = longStream.sorted();
        return this;
    }

    @Override
    public LongChain peek(LongConsumer action) {
        longStream = longStream.peek(action);
        return this;
    }

    @Override
    public LongChain limit(long maxSize) {
        longStream = longStream.limit(maxSize);
        return this;
    }

    @Override
    public LongChain skip(long n) {
        longStream = longStream.skip(n);
        return this;
    }

    @Override
    public void forEach(LongConsumer action) {
        longStream.forEach(action);
    }

    @Override
    public void forEachOrdered(LongConsumer action) {
        longStream.forEachOrdered(action);
    }

    @Override
    public long[] toArray() {
        return longStream.toArray();
    }

    @Override
    public long reduce(long identity, LongBinaryOperator op) {
        return longStream.reduce(identity, op);
    }

    @Override
    public OptionalLong reduce(LongBinaryOperator op) {
        return longStream.reduce(op);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator, BiConsumer<R, R> combiner) {
        return longStream.collect(supplier, accumulator, combiner);
    }

    @Override
    public long sum() {
        return longStream.sum();
    }

    @Override
    public OptionalLong min() {
        return longStream.min();
    }

    @Override
    public OptionalLong max() {
        return longStream.max();
    }

    @Override
    public long count() {
        return longStream.count();
    }

    @Override
    public OptionalDouble average() {
        return longStream.average();
    }

    @Override
    public LongSummaryStatistics summaryStatistics() {
        return longStream.summaryStatistics();
    }

    @Override
    public boolean anyMatch(LongPredicate predicate) {
        return longStream.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(LongPredicate predicate) {
        return longStream.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(LongPredicate predicate) {
        return longStream.noneMatch(predicate);
    }

    @Override
    public OptionalLong findFirst() {
        return longStream.findFirst();
    }

    @Override
    public OptionalLong findAny() {
        return longStream.findAny();
    }

    @Override
    public DoubleChain asDoubleStream() {
        return new DoubleStreamChain(longStream.asDoubleStream());
    }

    @Override
    public Chain<Long> boxed() {
        return new StreamChain<>(longStream.boxed());
    }

    @Override
    public LongChain sequential() {
        longStream = longStream.sequential();
        return this;
    }

    @Override
    public LongChain parallel() {
        longStream = longStream.parallel();
        return this;
    }

    @Override
    public PrimitiveIterator.OfLong iterator() {
        return longStream.iterator();
    }

    @Override
    public Spliterator.OfLong spliterator() {
        return longStream.spliterator();
    }

    @Override
    public boolean isParallel() {
        return longStream.isParallel();
    }

    @Override
    public LongChain unordered() {
        longStream = longStream.unordered();
        return this;
    }

    @Override
    public LongChain onClose(Runnable closeHandler) {
        longStream = longStream.onClose(closeHandler);
        return this;
    }

    @Override
    public void close() {
        longStream.close();
    }
}
