package xyz.srclab.common.lang.finder;

import xyz.srclab.annotation.Nullable;

import java.util.function.BiPredicate;

public interface Finder<K, V> {

    @SafeVarargs
    static <T> Finder<T, T> newSimpleFinder(T... table) {
        return FinderSupport.newSimpleFinder(table);
    }

    static <T> Finder<T, T> newSimpleFinder(Iterable<? extends T> table) {
        return FinderSupport.newSimpleFinder(table);
    }

    static <T> Finder<T, T> newPredicateFinder(T[] table, BiPredicate<? super T, ? super T> predicate) {
        return FinderSupport.newPredicateFinder(table, predicate);
    }

    static <T> Finder<T, T> newPredicateFinder(
            Iterable<? extends T> table, BiPredicate<? super T, ? super T> predicate) {
        return FinderSupport.newPredicateFinder(table, predicate);
    }

    @SafeVarargs
    static <K, V, E> Finder<K, V> newPairFinder(E... table) {
        return FinderSupport.newPairFinder(table);
    }

    static <K, V, E> Finder<K, V> newPairFinder(Iterable<? extends E> table) {
        return FinderSupport.newPairFinder(table);
    }

    static <K, V, E> Finder<K, V> newPredicatePairFinder(E[] table, BiPredicate<? super K, ? super K> predicate) {
        return FinderSupport.newPredicatePairFinder(table, predicate);
    }

    static <K, V, E> Finder<K, V> newPredicatePairFinder(
            Iterable<? extends E> table, BiPredicate<? super K, ? super K> predicate) {
        return FinderSupport.newPredicatePairFinder(table, predicate);
    }

    boolean has(K key);

    @Nullable
    V find(K key);
}
