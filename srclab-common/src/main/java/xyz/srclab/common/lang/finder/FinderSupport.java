package xyz.srclab.common.lang.finder;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.base.As;
import xyz.srclab.common.collection.MapOps;
import xyz.srclab.common.collection.SetOps;

import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;

final class FinderSupport {

    static <K, V> Finder<K, V> getEmptyFinder() {
        return As.notNull(EmptyFinder.INSTANCE);
    }

    @SafeVarargs
    static <T> Finder<T, T> newHashFinder(T... table) {
        return new HashFinder<>(table);
    }

    static <T> Finder<T, T> newHashFinder(Iterable<? extends T> table) {
        return new HashFinder<>(table);
    }

    static <T> Finder<T, T> newHashPredicateFinder(T[] table, BiPredicate<? super T, ? super T> predicate) {
        return new HashPredicateFinder<>(table, predicate);
    }

    static <T> Finder<T, T> newHashPredicateFinder(
            Iterable<? extends T> table, BiPredicate<? super T, ? super T> predicate) {
        return new HashPredicateFinder<>(table, predicate);
    }

    @SafeVarargs
    static <K, V, E> Finder<K, V> newPairHashFinder(E... table) {
        return new PairHashFinder<>(table);
    }

    static <K, V, E> Finder<K, V> newPairHashFinder(Iterable<? extends E> table) {
        return new PairHashFinder<>(table);
    }

    static <K, V, E> Finder<K, V> newPairHashPredicateFinder(E[] table, BiPredicate<? super K, ? super K> predicate) {
        return new PairHashPredicateFinder<>(table, predicate);
    }

    static <K, V, E> Finder<K, V> newPairHashPredicateFinder(
            Iterable<? extends E> table, BiPredicate<? super K, ? super K> predicate) {
        return new PairHashPredicateFinder<>(table, predicate);
    }

    @SafeVarargs
    static <K, V> Finder<K, V> newConcatFinder(Finder<K, V>... finders) {
        return new ConcatFinder<>(finders);
    }

    static <K, V> Finder<K, V> newConcatFinder(Iterable<? extends Finder<K, V>> finders) {
        return new ConcatFinder<>(finders);
    }

    private static final class HashFinder<T> implements Finder<T, T> {

        private final Set<T> tableSet;

        private HashFinder(T[] table) {
            this.tableSet = SetOps.immutable(table);
        }

        private HashFinder(Iterable<? extends T> table) {
            this.tableSet = SetOps.immutable(table);
        }

        @Override
        public boolean has(T key) {
            return tableSet.contains(key);
        }

        @Nullable
        @Override
        public T find(T key) {
            return tableSet.contains(key) ? key : null;
        }
    }

    private static final class HashPredicateFinder<T> implements Finder<T, T> {

        private final Set<T> tableSet;
        private final BiPredicate<? super T, ? super T> predicate;

        private HashPredicateFinder(T[] table, BiPredicate<? super T, ? super T> predicate) {
            this.tableSet = SetOps.immutable(table);
            this.predicate = predicate;
        }

        private HashPredicateFinder(Iterable<? extends T> table, BiPredicate<? super T, ? super T> predicate) {
            this.tableSet = SetOps.immutable(table);
            this.predicate = predicate;
        }

        @Override
        public boolean has(T key) {
            if (tableSet.contains(key)) {
                return true;
            }
            return tableSet.stream().anyMatch(e -> predicate.test(key, e));
        }

        @Nullable
        @Override
        public T find(T key) {
            if (tableSet.contains(key)) {
                return key;
            }
            return tableSet.stream().filter(e -> predicate.test(key, e)).findFirst().orElse(null);
        }
    }

    private static final class PairHashFinder<K, V, E> implements Finder<K, V> {

        private final Map<K, V> tableMap;

        private PairHashFinder(E[] table) {
            this.tableMap = MapOps.pairToMap(table);
        }

        private PairHashFinder(Iterable<? extends E> table) {
            this.tableMap = MapOps.pairToMap(table);
        }

        @Override
        public boolean has(K key) {
            return tableMap.containsKey(key);
        }

        @Nullable
        @Override
        public V find(K key) {
            return tableMap.get(key);
        }
    }

    private static final class PairHashPredicateFinder<K, V, E> implements Finder<K, V> {

        private final Map<K, V> tableMap;
        private final BiPredicate<? super K, ? super K> predicate;

        private PairHashPredicateFinder(E[] table, BiPredicate<? super K, ? super K> predicate) {
            this.tableMap = MapOps.pairToMap(table);
            this.predicate = predicate;
        }

        private PairHashPredicateFinder(Iterable<? extends E> table, BiPredicate<? super K, ? super K> predicate) {
            this.tableMap = MapOps.pairToMap(table);
            this.predicate = predicate;
        }

        @Override
        public boolean has(K key) {
            if (tableMap.containsKey(key)) {
                return true;
            }
            return tableMap.keySet().stream().anyMatch(k -> predicate.test(key, k));
        }

        @Nullable
        @Override
        public V find(K key) {
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

    private static final class ConcatFinder<K, V> implements Finder<K, V> {

        private final Finder<K, V>[] finders;

        @SafeVarargs
        private ConcatFinder(Finder<K, V>... finders) {
            this.finders = finders.clone();
        }

        private ConcatFinder(Iterable<? extends Finder<K, V>> finders) {
            this.finders = ArrayKit.toArray(finders, Finder.class);
        }

        @Override
        public boolean has(K key) {
            for (Finder<K, V> finder : finders) {
                if (finder.has(key)) {
                    return true;
                }
            }
            return false;
        }

        @Nullable
        @Override
        public V find(K key) {
            for (Finder<K, V> finder : finders) {
                @Nullable V result = finder.find(key);
                if (result != null) {
                    return result;
                }
            }
            return null;
        }
    }

    private static final class EmptyFinder implements Finder<Object, Object> {

        public static EmptyFinder INSTANCE = new EmptyFinder();

        private EmptyFinder() {
        }

        @Override
        public boolean has(Object key) {
            return false;
        }

        @Override
        public Object find(Object key) {
            return null;
        }
    }

}
