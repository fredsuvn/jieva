@file:JvmName("Collects")
@file:JvmMultifileClass

package xyz.srclab.common.collect

import kotlin.collections.joinTo as joinToKt
import kotlin.collections.joinToString as joinToStringKt

//Processing:

fun <T> Iterable<T>.collecting(): Collecting<T> {
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
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((T) -> CharSequence)? = null
): A {
    return this.joinToKt(destination, separator, prefix, postfix, limit, truncated, transform)
}