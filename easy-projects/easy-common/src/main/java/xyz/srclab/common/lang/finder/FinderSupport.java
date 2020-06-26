package xyz.srclab.common.lang.finder;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.collection.SetKit;

import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

final class FinderSupport {

    @SafeVarargs
    static <E> Finder<E, E> newSetFinder(E... table) {
        return new SimpleSetFinder<>(table);
    }

    @SafeVarargs
    static <E> Finder<E, E> newSetFinder(Predicate<? super E> predicate, E... table) {
        return new PredicateSetFinder<>(predicate, table);
    }

    @SafeVarargs
    static <K, V, E> Finder<K, V> newMapFinder(E... table) {
        return new SimpleMapFinder<>(table);
    }

    @SafeVarargs
    static <K, V, E> Finder<K, V> newMapFinder(Predicate<? super K> predicate, E... table) {
        return new PredicateMapFinder<>(predicate, table);
    }

    private static final class SimpleSetFinder<E> implements Finder<E, E> {

        private final Set<E> tableSet;

        private SimpleSetFinder(E[] table) {
            this.tableSet = SetKit.toSet(table);
        }

        @Nullable
        @Override
        public E get(E key) {
            return tableSet.contains(key) ? key : null;
        }
    }

    private static final class PredicateSetFinder<E> implements Finder<E, E> {

        private final Predicate<? super E> predicate;
        private final Set<E> tableSet;
        private final E[] table;

        @SafeVarargs
        private PredicateSetFinder(Predicate<? super E> predicate, E... table) {
            this.predicate = predicate;
            this.table = table;
            this.tableSet = SetKit.toSet(table);
        }

        @Nullable
        @Override
        public E get(E key) {
            if (tableSet.contains(key)) {
                return key;
            }
            int i = ArrayKit.find(table, predicate);
            return i >= 0 ? table[i] : null;
        }
    }

    private static final class SimpleMapFinder<K, V, E> implements Finder<K, V> {

        private final Map<K, @Nullable V> tableMap;

        private SimpleMapFinder(E[] table) {
            this.tableMap = MapKit.pairToMap(table);
        }

        @Nullable
        @Override
        public V get(K key) {
            return tableMap.get(key);
        }
    }

    private static final class PredicateMapFinder<K, V, E> implements Finder<K, V> {

        private final Predicate<Object> predicate;
        private final Map<K, @Nullable V> tableMap;
        private final Object[] table;

        @SafeVarargs
        private PredicateMapFinder(Predicate<? super K> predicate, E... table) {
            this.predicate = Cast.as(predicate);
            this.table = table;
            this.tableMap = MapKit.pairToMap(table);
        }

        @Nullable
        @Override
        public V get(K key) {
            @Nullable V value = tableMap.get(key);
            if (value != null) {
                return value;
            }
            int i = ArrayKit.find(table, predicate);
            return i >= 0 ? Cast.nullable(table[i]) : null;
        }
    }
}
