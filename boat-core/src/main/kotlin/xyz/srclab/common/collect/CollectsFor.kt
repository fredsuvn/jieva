@file:JvmName("Collects")
@file:JvmMultifileClass

package xyz.srclab.common.collect

import java.util.*
import kotlin.collections.joinTo as joinToKt
import kotlin.collections.joinToString as joinToStringKt

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