package xyz.srclab.common.lang.finder;

import xyz.srclab.annotation.Nullable;

import java.util.function.BiPredicate;

public interface Finder<T> {

    @SafeVarargs
    static <T> Finder<T> newSimpleFinder(T... table) {
        return FinderSupport.newSimpleFinder(table);
    }

    static <T> Finder<T> newSimpleFinder(Iterable<? extends T> table) {
        return FinderSupport.newSimpleFinder(table);
    }

    static <T> Finder<T> newPredicateFinder(T[] table, BiPredicate<Object, ? super T> predicate) {
        return FinderSupport.newPredicateFinder(table, predicate);
    }

    static <T> Finder<T> newPredicateFinder(Iterable<? extends T> table, BiPredicate<Object, ? super T> predicate) {
        return FinderSupport.newPredicateFinder(table, predicate);
    }

    @SafeVarargs
    static <T, E> Finder<T> newMapFinder(E... table) {
        return FinderSupport.newMapFinder(table);
    }

    static <T, E> Finder<T> newMapFinder(Iterable<? extends E> table) {
        return FinderSupport.newMapFinder(table);
    }

    static <K, V, E> Finder<V> newPredicateMapFinder(E[] table, BiPredicate<Object, ? super K> predicate) {
        return FinderSupport.newPredicateMapFinder(table, predicate);
    }

    static <K, V, E> Finder<V> newPredicateMapFinder(Iterable<? extends E> table, BiPredicate<Object, ? super K> predicate) {
        return FinderSupport.newPredicateMapFinder(table, predicate);
    }

    @Nullable
    T find(Object key);
}
