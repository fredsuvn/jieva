@file:JvmName("BCollects")
@file:JvmMultifileClass

package xyz.srclab.common.collect

import xyz.srclab.common.base.asTyped
import java.util.*
import kotlin.collections.joinTo as joinToKt
import kotlin.collections.joinToString as joinToStringKt
import kotlin.collections.toList as toListKt

//New:

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
//Join to String:

@JvmOverloads
fun <T> Iterable<T>.joinToString(
    separator: CharSequence = ", ",
    transform: ((T) -> CharSequence)? = null
): String {
    return this.joinToStringKt(separator = separator, transform = transform)
}

fun <T> Iterable<T>.joinToString(
    separator: CharSequence = ", ",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): String {
    return this.joinToStringKt(
        separator = separator,
        limit = limit,
        truncated = truncated,
        transform = transform,
    )
}

fun <T> Iterable<T>.joinToString(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): String {
    return this.joinToStringKt(separator, prefix, suffix, limit, truncated, transform)
}

@JvmOverloads
fun <T, A : Appendable> Iterable<T>.joinTo(
    destination: A,
    separator: CharSequence = ", ",
    transform: ((T) -> CharSequence)? = null
): A {
    return this.joinToKt(buffer = destination, separator = separator, transform = transform)
}

fun <T, A : Appendable> Iterable<T>.joinTo(
    destination: A,
    separator: CharSequence = ", ",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): A {
    return this.joinToKt(
        buffer = destination,
        separator = separator,
        limit = limit,
        truncated = truncated,
        transform = transform
    )
}

fun <T, A : Appendable> Iterable<T>.joinTo(
    destination: A,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): A {
    return this.joinToKt(destination, separator, prefix, suffix, limit, truncated, transform)
}

//Iterator

fun <T> Iterator<T>.asIterable(): Iterable<T> {
    return object : Iterable<T> {
        override fun iterator(): Iterator<T> = this@asIterable
    }
}

//Enumeration

/**
 * For java.
 */
fun <T> Enumeration<T>.asIterator(): Iterator<T> {
    return this.iterator()
}

fun <T> Enumeration<T>.asIterable(): Iterable<T> {
    return asIterator().asIterable()
}

fun <T> Iterator<T>.asEnumeration(): Enumeration<T> {
    return object : Enumeration<T> {
        override fun hasMoreElements(): Boolean = this@asEnumeration.hasNext()
        override fun nextElement(): T = this@asEnumeration.next()
    }
}

fun <T> Iterable<T>.asEnumeration(): Enumeration<T> {
    return this.iterator().asEnumeration()
}