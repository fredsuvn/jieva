@file:JvmName("Collects")
@file:JvmMultifileClass

package xyz.srclab.common.collect

import xyz.srclab.common.lang.asAny
import kotlin.collections.toList as toListKt

fun <T> newSet(vararg keyValues: T): LinkedHashSet<T> {
    return LinkedHashSet<T>().addElements(*keyValues)
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
            this[key.asAny()] = value.asAny()
        } else {
            this[key.asAny()] = null.asAny()
            break
        }
    }
    return this
}