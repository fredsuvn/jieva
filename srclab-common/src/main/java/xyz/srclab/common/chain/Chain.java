package xyz.srclab.common.chain;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.*;
import java.util.stream.*;

/**
 * Provides chaining operation like {@link java.util.stream.Stream} and type conversion for iterable type.
 *
 * @author sunqian
 */
public interface Chain<T> extends Stream<T> {

    @Override
    Chain<T> filter(Predicate<? super T> predicate);

    @Override
    <R> Chain<R> map(Function<? super T, ? extends R> mapper);

    @Override
    IntStream mapToInt(ToIntFunction<? super T> mapper);

    @Override
    LongStream mapToLong(ToLongFunction<? super T> mapper);

    @Override
    DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper);

    @Override
    <R> Chain<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);

    @Override
    IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper);

    @Override
    LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper);

    @Override
    DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper);

    @Override
    Chain<T> distinct();

    @Override
    Chain<T> sorted();

    @Override
    Chain<T> sorted(Comparator<? super T> comparator);

    @Override
    Chain<T> peek(Consumer<? super T> action);

    @Override
    Chain<T> limit(long maxSize);

    @Override
    Chain<T> skip(long n);

    @Override
    void forEach(Consumer<? super T> action);

    @Override
    void forEachOrdered(Consumer<? super T> action);

    @Override
    Object[] toArray();

    @Override
    <A> A[] toArray(IntFunction<A[]> generator);

    @Override
    T reduce(T identity, BinaryOperator<T> accumulator);

    @Override
    Optional<T> reduce(BinaryOperator<T> accumulator);

    @Override
    <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner);

    @Override
    <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner);

    @Override
    <R, A> R collect(Collector<? super T, A, R> collector);

    @Override
    Optional<T> min(Comparator<? super T> comparator);

    @Override
    Optional<T> max(Comparator<? super T> comparator);

    @Override
    long count();

    @Override
    boolean anyMatch(Predicate<? super T> predicate);

    @Override
    boolean allMatch(Predicate<? super T> predicate);

    @Override
    boolean noneMatch(Predicate<? super T> predicate);

    @Override
    Optional<T> findFirst();

    @Override
    Optional<T> findAny();

    @Override
    Iterator<T> iterator();

    @Override
    Spliterator<T> spliterator();

    @Override
    boolean isParallel();

    @Override
    Stream<T> sequential();

    @Override
    Stream<T> parallel();

    @Override
    Stream<T> unordered();

    @Override
    Stream<T> onClose(Runnable closeHandler);

    @Override
    void close();
}
