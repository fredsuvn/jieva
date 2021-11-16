@file:JvmName("BCollects")
@file:JvmMultifileClass

package xyz.srclab.common.collect

import java.util.*

fun <T> Collection<T>.toSync(): Collection<T> {
    return Collections.synchronizedCollection(this)
}

fun <T> Set<T>.toSync(): Set<T> {
    return Collections.synchronizedSet(this)
}

fun <T> SortedSet<T>.toSync(): SortedSet<T> {
    return Collections.synchronizedSortedSet(this)
}

fun <T> NavigableSet<T>.toSync(): NavigableSet<T> {
    return Collections.synchronizedNavigableSet(this)
}

fun <T> List<T>.toSync(): List<T> {
    return Collections.synchronizedList(this)
}

fun <K, V> Map<K, V>.toSync(): Map<K, V> {
    return Collections.synchronizedMap(this)
}

fun <K, V> SortedMap<K, V>.toSync(): SortedMap<K, V> {
    return Collections.synchronizedSortedMap(this)
}

fun <K, V> NavigableMap<K, V>.toSync(): NavigableMap<K, V> {
    return Collections.synchronizedNavigableMap(this)
}

fun <T> Collection<T>.toUnmodifiable(): Collection<T> {
    return Collections.unmodifiableCollection(this)
}

fun <T> Set<T>.toUnmodifiable(): Set<T> {
    return Collections.unmodifiableSet(this)
}

fun <T> SortedSet<T>.toUnmodifiable(): SortedSet<T> {
    return Collections.unmodifiableSortedSet(this)
}

fun <T> NavigableSet<T>.toUnmodifiable(): NavigableSet<T> {
    return Collections.unmodifiableNavigableSet(this)
}

fun <T> List<T>.toUnmodifiable(): List<T> {
    return Collections.unmodifiableList(this)
}

fun <K, V> Map<K, V>.toUnmodifiable(): Map<K, V> {
    return Collections.unmodifiableMap(this)
}

fun <K, V> SortedMap<K, V>.toUnmodifiable(): SortedMap<K, V> {
    return Collections.unmodifiableSortedMap(this)
}

fun <K, V> NavigableMap<K, V>.toUnmodifiable(): NavigableMap<K, V> {
    return Collections.unmodifiableNavigableMap(this)
}