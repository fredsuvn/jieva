package xyz.srclab.common.lang.finder;

import xyz.srclab.annotation.Nullable;

import java.util.function.BiPredicate;

public interface Finder<K, V> {

    @SafeVarargs
    static <T> Finder<T, T> hashFinder(T... table) {
        return FinderSupport.newHashFinder(table);
    }

    static <T> Finder<T, T> hashFinder(Iterable<? extends T> table) {
        return FinderSupport.newHashFinder(table);
    }

    static <T> Finder<T, T> hashPredicateFinder(T[] table, BiPredicate<? super T, ? super T> predicate) {
        return FinderSupport.newHashPredicateFinder(table, predicate);
    }

    static <T> Finder<T, T> hashPredicateFinder(
            Iterable<? extends T> table, BiPredicate<? super T, ? super T> predicate) {
        return FinderSupport.newHashPredicateFinder(table, predicate);
    }

    @SafeVarargs
    static <K, V, E> Finder<K, V> pairHashFinder(E... table) {
        return FinderSupport.newPairHashFinder(table);
    }

    static <K, V, E> Finder<K, V> pairHashFinder(Iterable<? extends E> table) {
        return FinderSupport.newPairHashFinder(table);
    }

    static <K, V, E> Finder<K, V> pairHashPredicateFinder(E[] table, BiPredicate<? super K, ? super K> predicate) {
        return FinderSupport.newPairHashPredicateFinder(table, predicate);
    }

    static <K, V, E> Finder<K, V> pairHashPredicateFinder(
            Iterable<? extends E> table, BiPredicate<? super K, ? super K> predicate) {
        return FinderSupport.newPairHashPredicateFinder(table, predicate);
    }

    @SafeVarargs
    static <K, V> Finder<K, V> concat(Finder<K, V>... finders) {
        return FinderSupport.newConcatFinder(finders);
    }

    static <K, V> Finder<K, V> concat(Iterable<? extends Finder<K, V>> finders) {
        return FinderSupport.newConcatFinder(finders);
    }

    boolean has(K key);

    @Nullable
    V find(K key);
}
