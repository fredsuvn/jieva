@file:JvmName("Collects")
@file:JvmMultifileClass

package xyz.srclab.common.collect

import xyz.srclab.common.lang.asAny
import kotlin.collections.joinTo as joinToKt
import kotlin.collections.joinToString as joinToStringKt
import kotlin.collections.toList as toListKt

//Creation:

@JvmName("newCollection")
fun <T, C : MutableCollection<T>> C.addElements(vararg elements: T): C {
    return addElements(elements.toListKt())
}

@JvmName("newCollection")
fun <T, C : MutableCollection<T>> C.addElements(elements: Iterable<T>): C {
    this.addAll(elements)
    return this
}

@JvmName("newMap")
fun <K, V, C : MutableMap<K, V>> C.putEntries(vararg keyValues: Any?): C {
    return putEntries(keyValues.toListKt())
}

@JvmName("newMap")
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

fun <T> Iterable<T>.collect(): Collecting<T> {
    return Collecting(this)
}

fun <K, V> Map<K, V>.mapping(): Mapping<K, V> {
    return Mapping(this)
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
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): String {
    return this.joinToStringKt(separator, prefix, postfix, limit, truncated, transform)
}

@JvmOverloads
fun <T, A : Appendable> Iterable<T>.joinTo(
    buffer: A,
    separator: CharSequence = ", ",
    transform: ((T) -> CharSequence)? = null
): A {
    return this.joinToKt(buffer = buffer, separator = separator, transform = transform)
}

fun <T, A : Appendable> Iterable<T>.joinTo(
    buffer: A,
    separator: CharSequence = ", ",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): A {
    return this.joinToKt(
        buffer = buffer,
        separator = separator,
        limit = limit,
        truncated = truncated,
        transform = transform
    )
}

fun <T, A : Appendable> Iterable<T>.joinTo(
    buffer: A,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): A {
    return this.joinToKt(buffer, separator, prefix, postfix, limit, truncated, transform)
}

//As mutable

fun <T> Iterable<T>.asMutable(): MutableIterable<T> {
    return this.asAny()
}

fun <T> Collection<T>.asMutable(): MutableCollection<T> {
    return this.asAny()
}

fun <T> Set<T>.asMutable(): MutableSet<T> {
    return this.asAny()
}

fun <T> List<T>.asMutable(): MutableList<T> {
    return this.asAny()
}

fun <K, V> Map<K, V>.asMutable(): MutableMap<K, V> {
    return this.asAny()
}