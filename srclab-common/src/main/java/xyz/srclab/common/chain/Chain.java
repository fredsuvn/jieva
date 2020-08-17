package xyz.srclab.common.chain;

import org.jetbrains.annotations.NotNull;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.ListKit;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.collection.SetKit;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Provides chaining operation like {@link java.util.stream.Stream} and type conversion for iterable type.
 *
 * @author sunqian
 */
public interface Chain<T> extends Stream<T> {

    @Override
    Chain<T> filter(Predicate<@Nullable ? super T> predicate);

    default Chain<T> elementNonNull() {
        return filter(e -> e != null);
    }

    @Override
    <R> Chain<R> map(Function<@Nullable ? super T, @Nullable ? extends R> mapper);

    @Override
    IntChain mapToInt(ToIntFunction<@Nullable ? super T> mapper);

    @Override
    LongChain mapToLong(ToLongFunction<@Nullable ? super T> mapper);

    @Override
    DoubleChain mapToDouble(ToDoubleFunction<@Nullable ? super T> mapper);

    @Override
    <R> Chain<R> flatMap(Function<@Nullable ? super T, ? extends Stream<? extends R>> mapper);

    @Override
    IntChain flatMapToInt(Function<@Nullable ? super T, ? extends IntStream> mapper);

    @Override
    LongChain flatMapToLong(Function<@Nullable ? super T, ? extends LongStream> mapper);

    @Override
    DoubleChain flatMapToDouble(Function<@Nullable ? super T, ? extends DoubleStream> mapper);

    @Override
    Chain<T> distinct();

    @Override
    Chain<T> sorted();

    @Override
    Chain<T> sorted(Comparator<@Nullable ? super T> comparator);

    @Override
    Chain<T> peek(Consumer<@Nullable ? super T> action);

    @Override
    Chain<T> limit(long maxSize);

    @Override
    Chain<T> skip(long n);

    @Override
    void forEach(Consumer<@Nullable ? super T> action);

    @Override
    void forEachOrdered(Consumer<@Nullable ? super T> action);

    @Override
    Object[] toArray();

    @Override
    <A> A[] toArray(IntFunction<A[]> generator);

    @Nullable
    @Override
    T reduce(@Nullable T identity, BinaryOperator<@Nullable T> accumulator);

    @Override
    Optional<T> reduce(BinaryOperator<@Nullable T> accumulator);

    @Nullable
    @Override
    <U> U reduce(
            @Nullable U identity,
            BiFunction<@Nullable U, @Nullable ? super T, U> accumulator,
            BinaryOperator<@Nullable U> combiner
    );

    @Nullable
    default T reduceNullable(BinaryOperator<@Nullable T> accumulator) {
        return reduce(null, accumulator);
    }

    default T reduceNonNull(BinaryOperator<@Nullable T> accumulator) {
        return reduce(accumulator).orElseThrow(NullPointerException::new);
    }

    @Override
    <R> R collect(
            Supplier<R> supplier,
            BiConsumer<R, @Nullable ? super T> accumulator,
            BiConsumer<R, R> combiner
    );

    @Override
    <R, A> R collect(Collector<@Nullable ? super T, @Nullable A, R> collector);

    default List<T> toList() {
        return collect(Collectors.toList());
    }

    default List<T> toList(Supplier<List<T>> supplier) {
        return collect(new ChainCollector<>(
                supplier,
                List::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                ChainCollector.CH_ID)
        );
    }

    @Immutable
    default List<T> toImmutableList() {
        return ListKit.immutable(toList());
    }

    default Set<T> toSet() {
        return collect(Collectors.toSet());
    }

    default Set<T> toSet(Supplier<Set<T>> supplier) {
        return collect(new ChainCollector<>(
                supplier,
                Set::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                ChainCollector.CH_UNORDERED_ID)
        );
    }

    @Immutable
    default Set<T> toImmutableSet() {
        return SetKit.immutable(toSet());
    }

    default <K, V> Map<K, V> toMap(
            Function<@Nullable ? super T, @Nullable ? extends K> keyMapper,
            Function<@Nullable ? super T, @Nullable ? extends V> valueMapper
    ) {
        return toMap(keyMapper, valueMapper, (v1, v2) -> v2);
    }

    @Immutable
    default <K, V> Map<K, V> toImmutableMap(
            Function<@Nullable ? super T, @Nullable ? extends K> keyMapper,
            Function<@Nullable ? super T, @Nullable ? extends V> valueMapper
    ) {
        return MapKit.immutable(toMap(keyMapper, valueMapper));
    }

    default <K, V> Map<K, V> toMap(
            Function<@Nullable ? super T, @Nullable ? extends K> keyMapper,
            Function<@Nullable ? super T, @Nullable ? extends V> valueMapper,
            BinaryOperator<@Nullable V> mergeFunction
    ) {
        return collect(Collectors.toMap(keyMapper, valueMapper, mergeFunction, LinkedHashMap::new));
    }

    @Immutable
    default <K, V> Map<K, V> toImmutableMap(
            Function<@Nullable ? super T, @Nullable ? extends K> keyMapper,
            Function<@Nullable ? super T, @Nullable ? extends V> valueMapper,
            BinaryOperator<@Nullable V> mergeFunction
    ) {
        return MapKit.immutable(toMap(keyMapper, valueMapper, mergeFunction));
    }

    default <K, V> Map<K, V> toMap(
            Function<@Nullable ? super T, @Nullable ? extends K> keyMapper,
            Function<@Nullable ? super T, @Nullable ? extends V> valueMapper,
            BinaryOperator<@Nullable V> mergeFunction,
            Supplier<Map<K,V>> supplier
    ) {
        BiConsumer<Map<K,V>, T> accumulator
                = (map, element) -> map.merge(keyMapper.apply(element),
                valueMapper.apply(element), mergeFunction);
        return new Collectors.CollectorImpl<>(mapSupplier, accumulator, mapMerger(mergeFunction), CH_ID);
        return collect(Collectors.toMap(keyMapper, valueMapper, mergeFunction, supplier));
    }

    @NotNull
    @Override
    Optional<T> min(Comparator<? super T> comparator);

    @NotNull
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

    @NotNull
    @Override
    Optional<T> findFirst();

    @NotNull
    @Override
    Optional<T> findAny();

    @NotNull
    @Override
    Iterator<T> iterator();

    @NotNull
    @Override
    Spliterator<T> spliterator();

    @Override
    boolean isParallel();

    @NotNull
    @Override
    Chain<T> sequential();

    @NotNull
    @Override
    Chain<T> parallel();

    @NotNull
    @Override
    Chain<T> unordered();

    @NotNull
    @Override
    Chain<T> onClose(Runnable closeHandler);

    @Override
    void close();
}
