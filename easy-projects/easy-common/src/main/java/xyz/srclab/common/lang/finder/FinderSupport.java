package xyz.srclab.common.lang.finder;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.collection.SetKit;

import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

final class FinderSupport {

    @SafeVarargs
    static <T> Finder<T> newSimpleFinder(T... table) {
        return new SimpleFinder<>(table);
    }

    static <T> Finder<T> newSimpleFinder(Iterable<? extends T> table) {
        return new SimpleFinder<>(table);
    }

    static <T> Finder<T> newPredicateFinder(T[] table, BiPredicate<Object, ? super T> predicate) {
        return new PredicateFinder<>(table, predicate);
    }

    static <T> Finder<T> newPredicateFinder(Iterable<? extends T> table, BiPredicate<Object, ? super T> predicate) {
        return new PredicateFinder<>(table, predicate);
    }

    static <T> Finder<T> newMapFinder(Object... table) {
        return new MapFinder<>(table);
    }

    static <T> Finder<T> newMapFinder(Iterable<?> table) {
        return new MapFinder<>(table);
    }

    static <K, V> Finder<V> newPredicateMapFinder(Object[] table, BiPredicate<Object, ? super K> predicate) {
        return new PredicateMapFinder<>(table, predicate);
    }

    static <K, V> Finder<V> newPredicateMapFinder(Iterable<?> table, BiPredicate<Object, ? super K> predicate) {
        return new PredicateMapFinder<>(table, predicate);
    }

    private static final class SimpleFinder<T> implements Finder<T> {

        private final Set<T> tableSet;

        private SimpleFinder(T[] table) {
            this.tableSet = SetKit.immutable(table);
        }

        private SimpleFinder(Iterable<? extends T> table) {
            this.tableSet = SetKit.immutable(table);
        }

        @Nullable
        @Override
        public T find(Object key) {
            return tableSet.contains(key) ? Cast.as(key) : null;
        }
    }

    private static final class PredicateFinder<T> implements Finder<T> {

        private final Set<T> tableSet;
        private final BiPredicate<Object, ? super T> predicate;

        private PredicateFinder(T[] table, BiPredicate<Object, ? super T> predicate) {
            this.tableSet = SetKit.immutable(table);
            this.predicate = predicate;
        }

        private PredicateFinder(Iterable<? extends T> table, BiPredicate<Object, ? super T> predicate) {
            this.tableSet = SetKit.immutable(table);
            this.predicate = predicate;
        }

        @Nullable
        @Override
        public T find(Object key) {
            if (tableSet.contains(key)) {
                return Cast.as(key);
            }
            return tableSet.stream().filter(v -> predicate.test(key, v)).findFirst().orElse(null);
        }
    }

    private static final class MapFinder<T> implements Finder<T> {

        private final Map<Object, T> tableMap;

        private MapFinder(Object[] table) {
            this.tableMap = MapKit.pairToMap(table);
        }

        private MapFinder(Iterable<?> table) {
            this.tableMap = MapKit.pairToMap(table);
        }

        @Nullable
        @Override
        public T find(Object key) {
            return tableMap.get(key);
        }
    }

    private static final class PredicateMapFinder<K, V> implements Finder<V> {

        private final Map<K, V> tableMap;
        private final BiPredicate<Object, ? super K> predicate;

        private PredicateMapFinder(Object[] table, BiPredicate<Object, ? super K> predicate) {
            this.tableMap = MapKit.pairToMap(table);
            this.predicate = predicate;
        }

        private PredicateMapFinder(Iterable<?> table, BiPredicate<Object, ? super K> predicate) {
            this.tableMap = MapKit.pairToMap(table);
            this.predicate = predicate;
        }

        @Nullable
        @Override
        public V find(Object key) {
            @Nullable V value = tableMap.get(key);
            if (value != null) {
                return value;
            }
            @Nullable K realKey = tableMap.keySet().stream()
                    .filter(k -> predicate.test(key, k))
                    .findFirst()
                    .orElse(null);
            return realKey == null ? null : tableMap.get(realKey);
        }
    }
}
