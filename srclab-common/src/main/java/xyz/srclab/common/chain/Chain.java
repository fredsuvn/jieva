package xyz.srclab.common.chain;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.collection.ListKit;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.collection.SetKit;
import xyz.srclab.common.lang.count.Counter;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Provides chaining operation like {@link java.util.stream.Stream} and type conversion for iterable type.
 *
 * @author sunqian
 */
public interface Chain<T> extends Stream<T>, Iterable<T> {

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

    //IntChain:

    static IntChain from(IntStream intStream) {
        return IntChain.from(intStream);
    }

    static IntChain from(int[] array) {
        return IntChain.from(array);
    }

    static IntChain from(int[] array, int startInclusive, int endExclusive) {
        return IntChain.from(array, startInclusive, endExclusive);
    }

    //LongChain:

    static LongChain from(LongStream longStream) {
        return LongChain.from(longStream);
    }

    static LongChain from(long[] array) {
        return LongChain.from(array);
    }

    static LongChain from(long[] array, int startInclusive, int endExclusive) {
        return LongChain.from(array, startInclusive, endExclusive);
    }

    //DoubleChain:

    static DoubleChain from(DoubleStream doubleStream) {
        return DoubleChain.from(doubleStream);
    }

    static DoubleChain from(double[] array) {
        return DoubleChain.from(array);
    }

    static DoubleChain from(double[] array, int startInclusive, int endExclusive) {
        return DoubleChain.from(array, startInclusive, endExclusive);
    }

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

    default void forEachOrdered(ObjLongConsumer<@Nullable ? super T> action) {
        Counter counter = Counter.fromZero();
        forEachOrdered(t -> action.accept(t, counter.getLongAndIncrement()));
    }

    @Override
    Object[] toArray();

    @Override
    <A> A[] toArray(IntFunction<A[]> generator);

    @Nullable
    @Override
    T reduce(@Nullable T identity, BinaryOperator<@Nullable T> accumulator);

    @Override
    Optional<T> reduce(BinaryOperator<@Nullable T> accumulator) throws NullPointerException;

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

    default T reduceNonNull(BinaryOperator<@Nullable T> accumulator) throws NullPointerException {
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

    @Override
    Optional<T> min(Comparator<? super T> comparator) throws NullPointerException;

    default T minNonNull(Comparator<? super T> comparator) throws NullPointerException {
        return min(comparator).orElseThrow(NullPointerException::new);
    }

    @Override
    Optional<T> max(Comparator<? super T> comparator) throws NullPointerException;

    default T maxNonNull(Comparator<? super T> comparator) throws NullPointerException {
        return max(comparator).orElseThrow(NullPointerException::new);
    }

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
