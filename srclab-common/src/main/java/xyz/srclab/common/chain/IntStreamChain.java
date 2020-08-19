package xyz.srclab.common.chain;

import java.util.*;
import java.util.function.*;
import java.util.stream.IntStream;

/**
 * @author sunqian
 */
public class IntStreamChain implements IntChain {

    private IntStream intStream;

    public IntStreamChain(IntStream intStream) {
        this.intStream = intStream;
    }

    @Override
    public IntChain filter(IntPredicate predicate) {
        intStream = intStream.filter(predicate);
        return this;
    }

    @Override
    public IntChain map(IntUnaryOperator mapper) {
        intStream = intStream.map(mapper);
        return this;
    }

    @Override
    public <U> Chain<U> mapToObj(IntFunction<? extends U> mapper) {
        return new StreamChain<>(intStream.mapToObj(mapper));
    }

    @Override
    public LongChain mapToLong(IntToLongFunction mapper) {
        return new LongStreamChain(intStream.mapToLong(mapper));
    }

    @Override
    public DoubleChain mapToDouble(IntToDoubleFunction mapper) {
        return new DoubleStreamChain(intStream.mapToDouble(mapper));
    }

    @Override
    public IntChain flatMap(IntFunction<? extends IntStream> mapper) {
        intStream = intStream.flatMap(mapper);
        return this;
    }

    @Override
    public IntChain distinct() {
        intStream = intStream.distinct();
        return this;
    }

    @Override
    public IntChain sorted() {
        intStream = intStream.sorted();
        return this;
    }

    @Override
    public IntChain peek(IntConsumer action) {
        intStream = intStream.peek(action);
        return this;
    }

    @Override
    public IntChain limit(long maxSize) {
        intStream = intStream.limit(maxSize);
        return this;
    }

    @Override
    public IntChain skip(long n) {
        intStream = intStream.skip(n);
        return this;
    }

    @Override
    public void forEach(IntConsumer action) {
        intStream.forEach(action);
    }

    @Override
    public void forEachOrdered(IntConsumer action) {
        intStream.forEachOrdered(action);
    }

    @Override
    public int[] toArray() {
        return intStream.toArray();
    }

    @Override
    public int reduce(int identity, IntBinaryOperator op) {
        return intStream.reduce(identity, op);
    }

    @Override
    public OptionalInt reduce(IntBinaryOperator op) {
        return intStream.reduce(op);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjIntConsumer<R> accumulator, BiConsumer<R, R> combiner) {
        return intStream.collect(supplier, accumulator, combiner);
    }

    @Override
    public int sum() {
        return intStream.sum();
    }

    @Override
    public OptionalInt min() {
        return intStream.min();
    }

    @Override
    public OptionalInt max() {
        return intStream.max();
    }

    @Override
    public long count() {
        return intStream.count();
    }

    @Override
    public OptionalDouble average() {
        return intStream.average();
    }

    @Override
    public IntSummaryStatistics summaryStatistics() {
        return intStream.summaryStatistics();
    }

    @Override
    public boolean anyMatch(IntPredicate predicate) {
        return intStream.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(IntPredicate predicate) {
        return intStream.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(IntPredicate predicate) {
        return intStream.noneMatch(predicate);
    }

    @Override
    public OptionalInt findFirst() {
        return intStream.findFirst();
    }

    @Override
    public OptionalInt findAny() {
        return intStream.findAny();
    }

    @Override
    public LongChain asLongStream() {
        return new LongStreamChain(intStream.asLongStream());
    }

    @Override
    public DoubleChain asDoubleStream() {
        return new DoubleStreamChain(intStream.asDoubleStream());
    }

    @Override
    public Chain<Integer> boxed() {
        return new StreamChain<>(intStream.boxed());
    }

    @Override
    public IntChain sequential() {
        intStream = intStream.sequential();
        return this;
    }

    @Override
    public IntChain parallel() {
        intStream = intStream.parallel();
        return this;
    }

    @Override
    public PrimitiveIterator.OfInt iterator() {
        return intStream.iterator();
    }

    @Override
    public Spliterator.OfInt spliterator() {
        return intStream.spliterator();
    }

    @Override
    public boolean isParallel() {
        return intStream.isParallel();
    }

    @Override
    public IntChain unordered() {
        intStream = intStream.unordered();
        return this;
    }

    @Override
    public IntChain onClose(Runnable closeHandler) {
        intStream = intStream.onClose(closeHandler);
        return this;
    }

    @Override
    public void close() {
        intStream.close();
    }
}
