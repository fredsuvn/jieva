@file:JvmName("Nulls")

package xyz.srclab.common.base

fun Any?.isNull(): Boolean {
    return this === null
}

fun Any?.isNotNull(): Boolean {
    return this !== null
}

@Throws(NullPointerException::class)
fun <T : Any> notNull(obj: T?): T {
    return obj!!
}

@Throws(NullPointerException::class)
fun <T : Any> notNull(obj: T?, message: CharSequence): T {
    return obj ?: throw NullPointerException(message.toString())
}

@Throws(NullPointerException::class)
fun <T : Any> notNull(obj: T?, message: () -> CharSequence): T {
    return obj ?: throw NullPointerException(message().toString())
}

fun <T : Any> orElse(obj: T?, value: T): T {
    return obj ?: value
}