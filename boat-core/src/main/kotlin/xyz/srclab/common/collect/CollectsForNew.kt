@file:JvmName("Collects")
@file:JvmMultifileClass

package xyz.srclab.common.collect

import xyz.srclab.common.base.asTyped
import kotlin.collections.toList as toListKt

fun <T> newSet(vararg elements: T): LinkedHashSet<T> {
    return LinkedHashSet<T>().addElements(*elements)
}

fun <T> newList(vararg elements: T): ArrayList<T> {
    val list = ArrayList<T>(elements.size)
    list.addAll(elements)
    return list
}

fun <K, V> newMap(vararg keyValues: Any?): LinkedHashMap<K, V> {
    return LinkedHashMap<K, V>().putEntries(*keyValues)
}

fun <K, V> newMap(keyValues: Iterable<Any?>): LinkedHashMap<K, V> {
    return LinkedHashMap<K, V>().putEntries(keyValues)
}

fun <T, C : MutableCollection<T>> C.addElements(vararg elements: T): C {
    return addElements(elements.toListKt())
}

fun <T, C : MutableCollection<T>> C.addElements(elements: Iterable<T>): C {
    this.addAll(elements)
    return this
}

fun <K, V, C : MutableMap<K, V>> C.putEntries(vararg keyValues: Any?): C {
    return putEntries(keyValues.toListKt())
}

fun <K, V, C : MutableMap<K, V>> C.putEntries(keyValues: Iterable<Any?>): C {
    val iterator = keyValues.iterator()
    while (iterator.hasNext()) {
        val key = iterator.next()
        if (iterator.hasNext()) {
            val value = iterator.next()
            this[key.asTyped()] = value.asTyped()
        } else {
            this[key.asTyped()] = null.asTyped()
            break
        }
    }
    return this
}

fun <T> concatList(vararg lists: Iterable<T>): List<T> {
    var size = 0
    for (list in lists) {
        if (list is Collection) {
            size += list.size
        } else {
            size = -1
            break
        }
    }
    val result = if (size <= 0) ArrayList() else ArrayList<T>(size)
    for (list in lists) {
        result.addAll(list)
    }
    return result
}