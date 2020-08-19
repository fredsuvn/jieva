package xyz.srclab.common.chain;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.*;
import java.util.stream.*;

/**
 * @author sunqian
 */
public class StreamChain<T> implements Chain<T> {

    private Stream<T> stream;

    public StreamChain(Stream<T> stream) {
        this.stream = stream;
    }

    @Override
    public Chain<T> filter(Predicate<? super T> predicate) {
        stream = stream.filter(predicate);
        return this;
    }

    @Override
    public <R> Chain<R> map(Function<? super T, ? extends R> mapper) {
        stream = Cast.as(stream.map(mapper));
        return Cast.as(this);
    }

    @Override
    public IntChain mapToInt(ToIntFunction<? super T> mapper) {
        return new IntStreamChain(stream.mapToInt(mapper));
    }

    @Override
    public LongChain mapToLong(ToLongFunction<? super T> mapper) {
        return new LongStreamChain(stream.mapToLong(mapper));
    }

    @Override
    public DoubleChain mapToDouble(ToDoubleFunction<? super T> mapper) {
        return new DoubleStreamChain(stream.mapToDouble(mapper));
    }

    @Override
    public <R> Chain<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
        stream = Cast.as(stream.flatMap(mapper));
        return Cast.as(this);
    }

    @Override
    public IntChain flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
        return new IntStreamChain(stream.flatMapToInt(mapper));
    }

    @Override
    public LongChain flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
        return new LongStreamChain(stream.flatMapToLong(mapper));
    }

    @Override
    public DoubleChain flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
        return new DoubleStreamChain(stream.flatMapToDouble(mapper));
    }

    @Override
    public Chain<T> distinct() {
        stream = stream.distinct();
        return this;
    }

    @Override
    public Chain<T> sorted() {
        stream = stream.sorted();
        return this;
    }

    @Override
    public Chain<T> sorted(Comparator<? super T> comparator) {
        stream = stream.sorted(comparator);
        return this;
    }

    @Override
    public Chain<T> peek(Consumer<? super T> action) {
        stream = stream.peek(action);
        return this;
    }

    @Override
    public Chain<T> limit(long maxSize) {
        stream = stream.limit(maxSize);
        return this;
    }

    @Override
    public Chain<T> skip(long n) {
        stream = stream.skip(n);
        return this;
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        stream.forEach(action);
    }

    @Override
    public void forEachOrdered(Consumer<? super T> action) {
        stream.forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
        return stream.toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return stream.toArray(generator);
    }

    @Override
    public T reduce(@Nullable T identity, BinaryOperator<@Nullable T> accumulator) {
        return stream.reduce(identity, accumulator);
    }

    @Override
    public Optional<T> reduce(BinaryOperator<@Nullable T> accumulator) throws NullPointerException {
        return stream.reduce(accumulator);
    }

    @Override
    public <U> @Nullable U reduce(
            @Nullable U identity,
            BiFunction<@Nullable U, ? super T, U> accumulator,
            BinaryOperator<@Nullable U> combiner
    ) {
        return stream.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
        return stream.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super T, @Nullable A, R> collector) {
        return stream.collect(collector);
    }

    @Override
    public Optional<T> min(Comparator<? super T> comparator) throws NullPointerException {
        return stream.min(comparator);
    }

    @Override
    public Optional<T> max(Comparator<? super T> comparator) throws NullPointerException {
        return stream.max(comparator);
    }

    @Override
    public long count() {
        return stream.count();
    }

    @Override
    public boolean anyMatch(Predicate<? super T> predicate) {
        return stream.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super T> predicate) {
        return stream.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super T> predicate) {
        return stream.noneMatch(predicate);
    }

    @Override
    public Optional<T> findFirst() throws NullPointerException {
        return stream.findFirst();
    }

    @Override
    public Optional<T> findAny() throws NullPointerException {
        return stream.findAny();
    }

    @Override
    public Iterator<T> iterator() {
        return stream.iterator();
    }

    @Override
    public Spliterator<T> spliterator() {
        return stream.spliterator();
    }

    @Override
    public boolean isParallel() {
        return stream.isParallel();
    }

    @Override
    public Chain<T> sequential() {
        stream = stream.sequential();
        return this;
    }

    @Override
    public Chain<T> parallel() {
        stream = stream.parallel();
        return this;
    }

    @Override
    public Chain<T> unordered() {
        stream = stream.unordered();
        return this;
    }

    @Override
    public Chain<T> onClose(Runnable closeHandler) {
        stream = stream.onClose(closeHandler);
        return this;
    }

    @Override
    public void close() {
        stream.close();
    }
}
