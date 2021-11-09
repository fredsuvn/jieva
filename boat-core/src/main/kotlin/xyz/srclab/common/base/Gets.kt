@file:JvmName("Gets")

package xyz.srclab.common.base

import xyz.srclab.common.convert.Converter
import xyz.srclab.common.convert.convertOrElse

@Throws(NullPointerException::class)
fun <T : Any> T?.notNull(): T {
    return this!!
}

@Throws(NullPointerException::class)
fun <T : Any> T?.notNull(message: CharSequence): T {
    return this ?: throw NullPointerException(message.toString())
}

@Throws(NullPointerException::class)
fun <T : Any> T?.notNull(message: () -> CharSequence): T {
    return this ?: throw NullPointerException(message().toString())
}

fun <T> T?.orElse(value: T): T {
    return this ?: value
}

fun <T> T?.orElse(supplier: () -> T): T {
    return this ?: supplier()
}

fun <T : Any> T?.orThrow(supplier: () -> Throwable): T {
    return this.orElse { throw supplier() }
}

fun <T : Any> Array<out T?>.orNull(index: Int): T? {
    return this.elementAtOrNull(index)
}

fun <T : Any> Iterable<T?>.orNull(index: Int): T? {
    return this.elementAtOrNull(index)
}

fun <T> Array<out T?>.orElse(index: Int, value: T): T {
    return this.orNull(index) ?: value
}

fun <T> Iterable<T?>.orElse(index: Int, value: T): T {
    return this.orNull(index) ?: value
}

fun <T> Map<*, T?>.orElse(key: Any?, value: T): T {
    return this[key] ?: value
}

fun <T> Array<out T?>.orElse(index: Int, supplier: () -> T): T {
    return this.orNull(index) ?: supplier()
}

fun <T> Iterable<T?>.orElse(index: Int, supplier: () -> T): T {
    return this.orNull(index) ?: supplier()
}

fun <T> Map<*, T?>.orElse(key: Any?, supplier: () -> T): T {
    return this[key] ?: supplier()
}

fun <T : Any> Array<out T?>.orThrow(index: Int, supplier: () -> Throwable): T {
    return this.orNull(index) ?: throw supplier()
}

fun <T : Any> Iterable<T?>.orThrow(index: Int, supplier: () -> Throwable): T {
    return this.orNull(index) ?: throw supplier()
}

fun <T : Any> Map<*, T?>.orThrow(key: Any?, supplier: () -> Throwable): T {
    return this[key] ?: throw supplier()
}

@JvmOverloads
fun <T:Any> Array<*>.orNull(
    index: Int,
    type: Class<out T>,
    defaultValue: T? = null,
    converter: Converter = Converter.defaultConverter()
): T? {
    return converter.convertOrElse(this.orNull(index), type, defaultValue)
}

@JvmOverloads
fun <T:Any> Iterable<*>.orNull(
    index: Int,
    type: Class<out T>,
    defaultValue: T? = null,
    converter: Converter = Converter.defaultConverter()
): T? {
    return converter.convertOrElse(this.orNull(index), type, defaultValue)
}

@JvmOverloads
fun <T:Any> Map<*,*>.orNull(
    key: Any?,
    type: Class<out T>,
    defaultValue: T? = null,
    converter: Converter = Converter.defaultConverter()
): T? {
    return converter.convertOrElse(this[key], type, defaultValue)
}

@JvmOverloads
fun <T:Any> Array<*>.orNull(
    index: Int,
    type: Class<out T>,
    defaultValue: ()->T? ,
    converter: Converter = Converter.defaultConverter()
): T? {
    return converter.convertOrElse(this.orNull(index), type, defaultValue)
}

@JvmOverloads
fun <T:Any> Iterable<*>.orNull(
    index: Int,
    type: Class<out T>,
    defaultValue: ()->T? ,
    converter: Converter = Converter.defaultConverter()
): T? {
    return converter.convertOrElse(this.orNull(index), type, defaultValue)
}

@JvmOverloads
fun <T:Any> Map<*,*>.orNull(
    key: Any?,
    type: Class<out T>,
    defaultValue: ()->T? ,
    converter: Converter = Converter.defaultConverter()
): T? {
    return converter.convertOrElse(this[key], type, defaultValue)
}