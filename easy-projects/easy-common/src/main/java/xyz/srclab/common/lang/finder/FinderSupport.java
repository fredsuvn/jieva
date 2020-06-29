package xyz.srclab.common.lang.finder;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.collection.SetKit;

import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

final class FinderSupport {

    @SafeVarargs
    static <T> Finder<T, T> newSimpleFinder(T... table) {
        return new SimpleFinder<>(table);
    }

    static <T> Finder<T, T> newSimpleFinder(Iterable<? extends T> table) {
        return new SimpleFinder<>(table);
    }

    static <T> Finder<T, T> newPredicateFinder(T[] table, BiPredicate<? super T, ? super T> predicate) {
        return new PredicateFinder<>(table, predicate);
    }

    static <T> Finder<T, T> newPredicateFinder(
            Iterable<? extends T> table, BiPredicate<? super T, ? super T> predicate) {
        return new PredicateFinder<>(table, predicate);
    }

    @SafeVarargs
    static <K, V, E> Finder<K, V> newMapFinder(E... table) {
        return new MapFinder<>(table);
    }

    static <K, V, E> Finder<K, V> newMapFinder(Iterable<? extends E> table) {
        return new MapFinder<>(table);
    }

    static <K, V, E> Finder<K, V> newPredicateMapFinder(E[] table, BiPredicate<? super K, ? super K> predicate) {
        return new PredicateMapFinder<>(table, predicate);
    }

    static <K, V, E> Finder<K, V> newPredicateMapFinder(
            Iterable<? extends E> table, BiPredicate<? super K, ? super K> predicate) {
        return new PredicateMapFinder<>(table, predicate);
    }

    private static final class SimpleFinder<T> implements Finder<T, T> {

        private final Set<T> tableSet;

        private SimpleFinder(T[] table) {
            this.tableSet = SetKit.immutable(table);
        }

        private SimpleFinder(Iterable<? extends T> table) {
            this.tableSet = SetKit.immutable(table);
        }

        @Override
        public boolean contains(T key) {
            return tableSet.contains(key);
        }

        @Nullable
        @Override
        public T get(T key) {
            return tableSet.contains(key) ? key : null;
        }
    }

    private static final class PredicateFinder<T> implements Finder<T, T> {

        private final Set<T> tableSet;
        private final BiPredicate<? super T, ? super T> predicate;

        private PredicateFinder(T[] table, BiPredicate<? super T, ? super T> predicate) {
            this.tableSet = SetKit.immutable(table);
            this.predicate = predicate;
        }

        private PredicateFinder(Iterable<? extends T> table, BiPredicate<? super T, ? super T> predicate) {
            this.tableSet = SetKit.immutable(table);
            this.predicate = predicate;
        }

        @Override
        public boolean contains(T key) {
            if (tableSet.contains(key)) {
                return true;
            }
            return tableSet.stream().anyMatch(e -> predicate.test(key, e));
        }

        @Nullable
        @Override
        public T get(T key) {
            if (tableSet.contains(key)) {
                return key;
            }
            return tableSet.stream().filter(e -> predicate.test(key, e)).findFirst().orElse(null);
        }
    }

    private static final class MapFinder<K, V, E> implements Finder<K, V> {

        private final Map<K, V> tableMap;

        private MapFinder(E[] table) {
            this.tableMap = MapKit.pairToMap(table);
        }

        private MapFinder(Iterable<? extends E> table) {
            this.tableMap = MapKit.pairToMap(table);
        }

        @Override
        public boolean contains(K key) {
            return tableMap.containsKey(key);
        }

        @Nullable
        @Override
        public V get(K key) {
            return tableMap.get(key);
        }
    }

    private static final class PredicateMapFinder<K, V, E> implements Finder<K, V> {

        private final Map<K, V> tableMap;
        private final BiPredicate<? super K, ? super K> predicate;

        private PredicateMapFinder(E[] table, BiPredicate<? super K, ? super K> predicate) {
            this.tableMap = MapKit.pairToMap(table);
            this.predicate = predicate;
        }

        private PredicateMapFinder(Iterable<? extends E> table, BiPredicate<? super K, ? super K> predicate) {
            this.tableMap = MapKit.pairToMap(table);
            this.predicate = predicate;
        }

        @Override
        public boolean contains(K key) {
            if (tableMap.containsKey(key)) {
                return true;
            }
            return tableMap.keySet().stream().anyMatch(k -> predicate.test(key, k));
        }

        @Nullable
        @Override
        public V get(K key) {
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
