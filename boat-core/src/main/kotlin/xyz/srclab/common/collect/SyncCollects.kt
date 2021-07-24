@file:JvmName("Collects")
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