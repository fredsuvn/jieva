package xyz.srclab.common.chain;

import com.google.common.collect.Maps;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Require;
import xyz.srclab.common.collection.ListKit;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.collection.SetKit;
import xyz.srclab.common.lang.count.Counter;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Provides chaining operation like {@link java.util.stream.Stream} and type conversion for iterable type.
 *
 * @author sunqian
 */
public interface Chain<T> extends BaseChain<T, Chain<T>> {

    @SafeVarargs
    static <T> Chain<T> of(T... elements) {
        return from(elements, 0, elements.length);
    }

    static <T> Chain<T> from(Stream<? extends T> stream) {
        return new StreamChain<>(Cast.as(stream));
    }

    static <T> Chain<T> from(T[] array, int startInclusive, int endExclusive) {
        return from(Arrays.stream(array, startInclusive, endExclusive));
    }

    static <T> Chain<T> from(Collection<? extends T> collection) {
        return from(collection.stream());
    }

    static <T> Chain<T> from(Iterable<? extends T> iterable) {
        return from(StreamSupport.stream(iterable.spliterator(), false));
    }

    static <T> Chain<T> from(Iterator<? extends T> iterator) {
        return from(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED));
    }

    static <T> Chain<T> from(Spliterator<? extends T> spliterator) {
        return from(StreamSupport.stream(spliterator, false));
    }

    Chain<T> filter(Predicate<@Nullable ? super T> predicate);

    default Chain<T> elementNonNull() {
        return filter(e -> e != null);
    }

    <R> Chain<R> map(Function<@Nullable ? super T, @Nullable ? extends R> mapper);

    IntChain mapToInt(ToIntFunction<@Nullable ? super T> mapper);

    LongChain mapToLong(ToLongFunction<@Nullable ? super T> mapper);

    DoubleChain mapToDouble(ToDoubleFunction<@Nullable ? super T> mapper);

    <R> Chain<R> flat(Function<@Nullable ? super T, ? extends Chain<? extends R>> mapper);

    IntChain flatToInt(Function<@Nullable ? super T, ? extends IntChain> mapper);

    LongChain flatToLong(Function<@Nullable ? super T, ? extends LongChain> mapper);

    DoubleChain flatToDouble(Function<@Nullable ? super T, ? extends DoubleChain> mapper);

    @Nullable
    T reduce(@Nullable T identity, BinaryOperator<@Nullable T> accumulator);

    @Nullable
    T reduce(BinaryOperator<@Nullable T> accumulator);

    @Nullable
    public <U> U reduce(
            @Nullable U identity,
            BiFunction<@Nullable U, @Nullable ? super T, U> accumulator,
            BinaryOperator<@Nullable U> combiner
    );

    @Nullable
    default T reduceNonNull(@Nullable T identity, BinaryOperator<@Nullable T> accumulator) {
        return Require.nonNull(reduce(identity, accumulator));
    }

    @Nullable
    default T reduceNonNull(BinaryOperator<@Nullable T> accumulator) {
        return Require.nonNull(reduce(accumulator));
    }

    @Nullable
    default <U> U reduceNonNull(
            @Nullable U identity,
            BiFunction<@Nullable U, @Nullable ? super T, U> accumulator,
            BinaryOperator<@Nullable U> combiner
    ) {
        return Require.nonNull(reduce(identity, accumulator, combiner));
    }

    @Nullable
    T min(Comparator<? super T> comparator);

    default T minNonNull(Comparator<? super T> comparator) {
        return Require.nonNull(min(comparator));
    }

    @Nullable
    T max(Comparator<? super T> comparator);

    default T maxNonNull(Comparator<? super T> comparator) {
        return Require.nonNull(max(comparator));
    }

    <R> R collect(
            Supplier<R> supplier,
            BiConsumer<R, @Nullable ? super T> accumulator,
            BiConsumer<R, R> combiner
    );

    <R, A> R collect(Collector<@Nullable ? super T, A, R> collector);

    default List<T> toList() {
        return collect(Collectors.toList());
    }

    default List<T> toList(Supplier<List<T>> listSupplier) {
        return collect(ChainCollector.toList(listSupplier));
    }

    @Immutable
    default List<T> toImmutableList() {
        return ListKit.immutable(toList());
    }

    default Set<T> toSet() {
        return collect(Collectors.toSet());
    }

    default Set<T> toSet(Supplier<Set<T>> setSupplier) {
        return collect(ChainCollector.toSet(setSupplier));
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
            Supplier<Map<K, V>> mapSupplier
    ) {
        return collect(Collectors.toMap(keyMapper, valueMapper, (v1, v2) -> v2, mapSupplier));
    }

    @Immutable
    default <K, V> Map<K, V> toImmutableMap(
            Function<@Nullable ? super T, @Nullable ? extends K> keyMapper,
            Function<@Nullable ? super T, @Nullable ? extends V> valueMapper,
            Supplier<Map<K, V>> mapSupplier
    ) {
        return MapKit.immutable(toMap(keyMapper, valueMapper, mapSupplier));
    }

    default <K, V> Map<K, V> toMap(
            Function<@Nullable ? super T, @Nullable ? extends K> keyMapper,
            Function<@Nullable ? super T, @Nullable ? extends V> valueMapper,
            BinaryOperator<@Nullable V> mergeFunction,
            Supplier<Map<K, V>> mapSupplier
    ) {
        return collect(ChainCollector.toMap(keyMapper, valueMapper, mergeFunction, mapSupplier));
    }

    @Immutable
    default <K, V> Map<K, V> toImmutableMap(
            Function<@Nullable ? super T, @Nullable ? extends K> keyMapper,
            Function<@Nullable ? super T, @Nullable ? extends V> valueMapper,
            BinaryOperator<@Nullable V> mergeFunction,
            Supplier<Map<K, V>> mapSupplier
    ) {
        return MapKit.immutable(toMap(keyMapper, valueMapper, mergeFunction, mapSupplier));
    }

    default Map<T, T> pairToMap(
            Supplier<Map<T, T>> mapSupplier,
            BinaryOperator<@Nullable T> mergeFunction,

    ) {
        return collect(ChainCollector.toMap(keyMapper, valueMapper, mergeFunction, mapSupplier));
    }

    <R> Chain<R> merge(
            Supplier<R> supplier,
            BiFunction<R, @Nullable ? super T, R> accumulator
    );

    <R> Chain<R> merge(Function<@Nullable ? super T, R> accumulator);

    <R> Chain<R> merge(Consumer<T[]> mergeSizer, Function<T[], R> accumulator);

    <R> Chain<R> mergePair(BiFunction<@Nullable ? super T, @Nullable ? super T, R> accumulator);

    <R> R mergeCollect(
            Supplier<R> supplier,
            BiConsumer<R, @Nullable ? super T> accumulator,
            BiConsumer<R, R> combiner
    );

    <R, A> R mergeCollect(Collector<@Nullable ? super T, A, R> collector);

    <K, V> Map<K, V> mergeToMap();

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

    default void forEachOrdered(ObjLongConsumer<@Nullable ? super T> action) {
        Counter counter = Counter.fromZero();
        forEachOrdered(t -> action.accept(t, counter.getLongAndIncrement()));
    }

    @Override
    Object[] toArray();

    @Override
    <A> A[] toArray(IntFunction<A[]> generator);

    @Override
    long count();

    @Override
    boolean anyMatch(Predicate<? super T> predicate);

    @Override
    boolean allMatch(Predicate<? super T> predicate);

    @Override
    boolean noneMatch(Predicate<? super T> predicate);

    @Override
    Optional<T> findFirst() throws NullPointerException;

    @Nullable
    default T findFirstNullable() {
        Iterator<T> iterator = iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    default T findFirstNonNull() throws NullPointerException {
        Iterator<T> iterator = elementNonNull().iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        throw new NullPointerException();
    }

    @Override
    Optional<T> findAny() throws NullPointerException;

    @Nullable
    default T findAnyNullable() {
        return findFirstNullable();
    }

    default T findAnyNonNull() throws NullPointerException {
        return findFirstNonNull();
    }

    @Override
    Iterator<T> iterator();

    @Override
    Spliterator<T> spliterator();

    @Override
    boolean isParallel();

    @Override
    Chain<T> sequential();

    @Override
    Chain<T> parallel();

    @Override
    Chain<T> unordered();

    @Override
    Chain<T> onClose(Runnable closeHandler);

    @Override
    void close();
}
