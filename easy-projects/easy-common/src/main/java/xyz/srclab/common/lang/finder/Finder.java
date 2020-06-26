package xyz.srclab.common.lang.finder;

import xyz.srclab.annotation.Nullable;

import java.util.function.Predicate;

public interface Finder<K, V> {

    @SafeVarargs
    static <E> Finder<E, E> newSetFinder(E... table) {
        return FinderSupport.newSetFinder(table);
    }

    @SafeVarargs
    static <E> Finder<E, E> newSetFinder(Predicate<? super E> predicate, E... table) {
        return FinderSupport.newSetFinder(predicate, table);
    }

    @SafeVarargs
    static <K, V, E> Finder<K, V> newMapFinder(E... table) {
        return FinderSupport.newMapFinder(table);
    }

    @SafeVarargs
    static <K, V, E> Finder<K, V> newMapFinder(Predicate<? super K> predicate, E... table) {
        return FinderSupport.newMapFinder(predicate, table);
    }

    @Nullable
    V get(K key);
}
