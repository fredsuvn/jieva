package xyz.srclab.common.chain;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.As;
import xyz.srclab.common.collection.MapKit;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * @author sunqian
 */
public class ChainCollector<T, A, R> implements Collector<T, A, R> {

    public static final Set<Collector.Characteristics> CH_CONCURRENT_ID
            = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT,
            Collector.Characteristics.UNORDERED,
            Collector.Characteristics.IDENTITY_FINISH));
    public static final Set<Collector.Characteristics> CH_CONCURRENT_NOID
            = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.CONCURRENT,
            Collector.Characteristics.UNORDERED));
    public static final Set<Collector.Characteristics> CH_ID
            = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.IDENTITY_FINISH));
    public static final Set<Collector.Characteristics> CH_UNORDERED_ID
            = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED,
            Collector.Characteristics.IDENTITY_FINISH));
    public static final Set<Collector.Characteristics> CH_NOID = Collections.emptySet();

    public static <T> ChainCollector<T, ?, List<T>> toList(Supplier<List<T>> listSupplier) {
        return new ChainCollector<>(
                listSupplier,
                List::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                ChainCollector.CH_ID);
    }

    public static <T> ChainCollector<T, ?, Set<T>> toSet(Supplier<Set<T>> setSupplier) {
        return new ChainCollector<>(
                setSupplier,
                Set::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                ChainCollector.CH_UNORDERED_ID);
    }

    public static <T, K, V> ChainCollector<T, ?, Map<K, V>> toMap(
            Function<@Nullable ? super T, @Nullable ? extends K> keyMapper,
            Function<@Nullable ? super T, @Nullable ? extends V> valueMapper,
            BinaryOperator<@Nullable V> mergeFunction,
            Supplier<Map<K, V>> mapSupplier
    ) {
        BiConsumer<Map<K, V>, T> accumulator = (map, element) ->
                MapKit.mergeNullable(map, keyMapper.apply(element), valueMapper.apply(element), mergeFunction);
        return new ChainCollector<>(mapSupplier, accumulator, mapMerger(mergeFunction), ChainCollector.CH_ID);
    }

    private static <K, V, M extends Map<K, V>> BinaryOperator<M> mapMerger(BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            for (Map.Entry<K, V> e : m2.entrySet()) {
                MapKit.mergeNullable(m1, e.getKey(), e.getValue(), mergeFunction);
            }
            return m1;
        };
    }

    private final Supplier<A> supplier;
    private final BiConsumer<A, T> accumulator;
    private final BinaryOperator<A> combiner;
    private final Function<A, R> finisher;
    private final Set<Characteristics> characteristics;

    ChainCollector(Supplier<A> supplier,
                   BiConsumer<A, T> accumulator,
                   BinaryOperator<A> combiner,
                   Function<A, R> finisher,
                   Set<Characteristics> characteristics) {
        this.supplier = supplier;
        this.accumulator = accumulator;
        this.combiner = combiner;
        this.finisher = finisher;
        this.characteristics = characteristics;
    }

    ChainCollector(Supplier<A> supplier,
                   BiConsumer<A, T> accumulator,
                   BinaryOperator<A> combiner,
                   Set<Characteristics> characteristics) {
        this(supplier, accumulator, combiner, As::notNull, characteristics);
    }

    @Override
    public BiConsumer<A, T> accumulator() {
        return accumulator;
    }

    @Override
    public Supplier<A> supplier() {
        return supplier;
    }

    @Override
    public BinaryOperator<A> combiner() {
        return combiner;
    }

    @Override
    public Function<A, R> finisher() {
        return finisher;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return characteristics;
    }
}
