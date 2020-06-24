package xyz.srclab.common.lang.finder;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.collection.MapKit;

import java.util.Map;
import java.util.function.Predicate;

final class FinderSupport {

    @SafeVarargs
    static <K, V, E> Finder<K, V> pairFinder(E... table) {
        return new SimplePairFinder<>(table);
    }

    @SafeVarargs
    static <K, V, E> Finder<K, V> pairFinder(Predicate<? super K> predicate, E... table) {
        return new PredicatePairFinder<>(predicate, table);
    }

    private static final class SimplePairFinder<K, V, E> implements Finder<K, V> {

        private final Map<K, @Nullable V> tableMap;

        private SimplePairFinder(E[] table) {
            this.tableMap = MapKit.pairToMap(table);
        }

        @Nullable
        @Override
        public V get(K key) {
            return tableMap.get(key);
        }
    }

    private static final class PredicatePairFinder<K, V, E> implements Finder<K, V> {

        private final Predicate<? super K> predicate;
        private final Map<K, @Nullable V> tableMap;
        private final Object[] table;

        @SafeVarargs
        private PredicatePairFinder(Predicate<? super K> predicate, E... table) {
            this.predicate = predicate;
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
            for (int i = 0; i < table.length; i++) {
                if (predicate.test(Cast.as(table[i]))) {
                    if (i + 1 < table.length) {
                        return Cast.nullable(table[i + 1]);
                    }
                    return null;
                }
            }
            return null;
        }
    }
}
