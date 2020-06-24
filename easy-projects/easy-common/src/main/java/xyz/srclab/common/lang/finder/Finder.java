package xyz.srclab.common.lang.finder;

import xyz.srclab.annotation.Nullable;

import java.util.function.Predicate;

public interface Finder<K, V> {

    @SafeVarargs
    static <K, V, E> Finder<K, V> newFinder(E... table) {
        return FinderSupport.newFinder(table);
    }

    @SafeVarargs
    static <K, V, E> Finder<K, V> newFinder(Predicate<? super K> predicate, E... table) {
        return FinderSupport.newFinder(predicate, table);
    }

    @Nullable
    V get(K key);
}
