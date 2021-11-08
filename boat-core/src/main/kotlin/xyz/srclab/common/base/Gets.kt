@file:JvmName("Gets")

package xyz.srclab.common.base

import xyz.srclab.common.convert.Converter
import java.lang.reflect.Type

private val defaultConverter = Converter.DEFAULT

@Throws(NullPointerException::class)
fun <T : Any> T?.notNull(): T {
    return this!!
}

@Throws(NullPointerException::class)
fun <T : Any> T?.notNull( message: CharSequence): T {
    return this ?: throw NullPointerException(message.toString())
}

@Throws(NullPointerException::class)
fun <T : Any> T?.notNull( message: () -> CharSequence): T {
    return this ?: throw NullPointerException(message().toString())
}

fun <T : Any> T?.orElse( value: T): T {
    return this ?: value
}

fun <T : Any> T?.orElse( supplier: () -> T): T {
    return this ?: supplier()
}

fun <T : Any> T?.orThrow( supplier: () -> Throwable): T {
    return this.orElse { throw supplier() }
}

@Throws(IndexOutOfBoundsException::class,NullPointerException::class)
fun <T : Any> Array<out T?>.notNull(index: Int): T {
    return this[index].notNull()
}

@Throws(IndexOutOfBoundsException::class,NullPointerException::class)
fun <T : Any> Iterable<T?>.notNull(index: Int): T {
    return this.elementAt(index).notNull()
}

@Throws(NullPointerException::class)
fun <T : Any> Map<*, T?>.notNull(key: Any?): T {
    return this[key].notNull()
}

fun <T : Any> Array<out T?>.orNull(index: Int): T? {
    if (!index.isIndexInBounds(0, this.size)) {
        return null
    }
    return this[index]
}

fun <T : Any> Iterable<T?>.orNull(index: Int): T? {
    if (!index.isIndexInBounds(0, this.count())) {
        return null
    }
    return this.elementAtOrNull(index)
}

@JvmOverloads
fun <T : Any> notNull(
    iterable: Iterable<*>,
    index: Int,
    toType: Class<T>,
    converter: Converter = defaultConverter
): T {
    val element = iterable.elementAt(index)
    return converter.convert(element, toType)
}

@JvmOverloads
fun <T : Any> notNull(
    iterable: Iterable<*>,
    index: Int,
    toType: Type,
    converter: Converter = defaultConverter
): T {
    val element = iterable.elementAt(index)
    return converter.convert(element, toType)
}

@JvmOverloads
fun <T : Any> notNull(
    map: Map<*, *>,
    key: Any?,
    toType: Class<T>,
    converter: Converter = defaultConverter
): T {
    val value = map[key]
    return converter.convert(value, toType)
}

@JvmOverloads
fun <T : Any> notNull(
    map: Map<*, *>,
    key: Any?,
    toType: Type,
    converter: Converter = defaultConverter
): T {
    val value = map[key]
    return converter.convert(value, toType)
}

@JvmOverloads
fun <T : Any> getOrDefault(
    iterable: Iterable<*>,
    index: Int,
    toType: Class<T>,
    defaultValue: T,
    converter: Converter = defaultConverter
): T {
    val element = iterable.elementAt(index)
    return converter.convertOrDefault(element, toType, defaultValue)
}

@JvmOverloads
fun <T : Any> getOrDefault(
    iterable: Iterable<*>,
    index: Int,
    toType: Type,
    defaultValue: T,
    converter: Converter = defaultConverter
): T {
    val element = iterable.elementAt(index)
    return converter.convertOrDefault(element, toType, defaultValue)
}

@JvmOverloads
fun <T : Any> getOrDefault(
    map: Map<*, *>,
    key: Any?,
    toType: Class<T>,
    defaultValue: T,
    converter: Converter = defaultConverter
): T {
    val value = map[key]
    return converter.convertOrDefault(value, toType, defaultValue)
}

@JvmOverloads
fun <T : Any> getOrDefault(
    map: Map<*, *>,
    key: Any?,
    toType: Type,
    defaultValue: T,
    converter: Converter = defaultConverter
): T {
    val value = map[key]
    return converter.convertOrDefault(value, toType, defaultValue)
}

@JvmOverloads
fun <T : Any> getOrNull(
    iterable: Iterable<*>,
    index: Int,
    toType: Class<T>,
    converter: Converter = defaultConverter
): T? {
    val element = iterable.elementAt(index)
    return converter.convertOrNull(element, toType)
}

@JvmOverloads
fun <T : Any> getOrNull(
    iterable: Iterable<*>,
    index: Int,
    toType: Type,
    converter: Converter = defaultConverter
): T? {
    val element = iterable.elementAt(index)
    return converter.convertOrNull(element, toType)
}

@JvmOverloads
fun <T : Any> getOrNull(
    map: Map<*, *>,
    key: Any?,
    toType: Class<T>,
    converter: Converter = defaultConverter
): T? {
    val value = map[key]
    return converter.convertOrNull(value, toType)
}

@JvmOverloads
fun <T : Any> getOrNull(
    map: Map<*, *>,
    key: Any?,
    toType: Type,
    converter: Converter = defaultConverter
): T? {
    val value = map[key]
    return converter.convertOrNull(value, toType)
}