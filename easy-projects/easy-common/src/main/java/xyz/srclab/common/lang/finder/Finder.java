package xyz.srclab.common.lang.finder;

import xyz.srclab.annotation.Nullable;

import java.util.function.Predicate;

public interface Finder<K, V> {

    static <K, V> Finder<K, V> newFinder(Object... table) {
        return FinderSupport.newFinder(table);
    }

    static <K, V> Finder<K, V> newFinder(Predicate<? super K> predicate, Object... table) {
        return FinderSupport.newFinder(predicate, table);
    }

    @Nullable
    V get(K key);
}
