package xyz.srclab.common.chain;

import java.util.DoubleSummaryStatistics;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.function.*;
import java.util.stream.DoubleStream;

/**
 * @author sunqian
 */
public class DoubleStreamChain implements DoubleChain {

    private DoubleStream doubleStream;

    public DoubleStreamChain(DoubleStream doubleStream) {
        this.doubleStream = doubleStream;
    }

    @Override
    public DoubleChain filter(DoublePredicate predicate) {
        doubleStream = doubleStream.filter(predicate);
        return this;
    }

    @Override
    public DoubleChain map(DoubleUnaryOperator mapper) {
        doubleStream = doubleStream.map(mapper);
        return this;
    }

    @Override
    public <U> Chain<U> mapToObj(DoubleFunction<? extends U> mapper) {
        return new StreamChain<>(doubleStream.mapToObj(mapper));
    }

    @Override
    public IntChain mapToInt(DoubleToIntFunction mapper) {
        return new IntStreamChain(doubleStream.mapToInt(mapper));
    }

    @Override
    public LongChain mapToLong(DoubleToLongFunction mapper) {
        return new LongStreamChain(doubleStream.mapToLong(mapper));
    }

    @Override
    public DoubleChain flatMap(DoubleFunction<? extends DoubleStream> mapper) {
        doubleStream = doubleStream.flatMap(mapper);
        return this;
    }

    @Override
    public DoubleChain distinct() {
        doubleStream = doubleStream.distinct();
        return this;
    }

    @Override
    public DoubleChain sorted() {
        doubleStream = doubleStream.sorted();
        return this;
    }

    @Override
    public DoubleChain peek(DoubleConsumer action) {
        doubleStream = doubleStream.peek(action);
        return this;
    }

    @Override
    public DoubleChain limit(long maxSize) {
        doubleStream = doubleStream.limit(maxSize);
        return this;
    }

    @Override
    public DoubleChain skip(long n) {
        doubleStream = doubleStream.skip(n);
        return this;
    }

    @Override
    public void forEach(DoubleConsumer action) {
        doubleStream.forEach(action);
    }

    @Override
    public void forEachOrdered(DoubleConsumer action) {
        doubleStream.forEachOrdered(action);
    }

    @Override
    public double[] toArray() {
        return doubleStream.toArray();
    }

    @Override
    public double reduce(double identity, DoubleBinaryOperator op) {
        return doubleStream.reduce(identity, op);
    }

    @Override
    public OptionalDouble reduce(DoubleBinaryOperator op) {
        return doubleStream.reduce(op);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator, BiConsumer<R, R> combiner) {
        return doubleStream.collect(supplier, accumulator, combiner);
    }

    @Override
    public double sum() {
        return doubleStream.sum();
    }

    @Override
    public OptionalDouble min() {
        return doubleStream.min();
    }

    @Override
    public OptionalDouble max() {
        return doubleStream.max();
    }

    @Override
    public long count() {
        return doubleStream.count();
    }

    @Override
    public OptionalDouble average() {
        return doubleStream.average();
    }

    @Override
    public DoubleSummaryStatistics summaryStatistics() {
        return doubleStream.summaryStatistics();
    }

    @Override
    public boolean anyMatch(DoublePredicate predicate) {
        return doubleStream.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(DoublePredicate predicate) {
        return doubleStream.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(DoublePredicate predicate) {
        return doubleStream.noneMatch(predicate);
    }

    @Override
    public OptionalDouble findFirst() {
        return doubleStream.findFirst();
    }

    @Override
    public OptionalDouble findAny() {
        return doubleStream.findAny();
    }

    @Override
    public Chain<Double> boxed() {
        return new StreamChain<>(doubleStream.boxed());
    }

    @Override
    public DoubleChain sequential() {
        doubleStream = doubleStream.sequential();
        return this;
    }

    @Override
    public DoubleChain parallel() {
        doubleStream = doubleStream.parallel();
        return this;
    }

    @Override
    public PrimitiveIterator.OfDouble iterator() {
        return doubleStream.iterator();
    }

    @Override
    public Spliterator.OfDouble spliterator() {
        return doubleStream.spliterator();
    }

    @Override
    public boolean isParallel() {
        return doubleStream.isParallel();
    }

    @Override
    public DoubleChain unordered() {
        doubleStream = doubleStream.unordered();
        return this;
    }

    @Override
    public DoubleChain onClose(Runnable closeHandler) {
        doubleStream = doubleStream.onClose(closeHandler);
        return this;
    }

    @Override
    public void close() {
        doubleStream.close();
    }
}
