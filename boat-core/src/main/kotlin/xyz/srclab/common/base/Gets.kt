@file:JvmName("Gets")

package xyz.srclab.common.base

import xyz.srclab.common.convert.Converter
import java.lang.reflect.Type

private val defaultConverter = Converter.DEFAULT

fun <T : Any> get(any: T?, defaultValue: T): T {
    return any ?: defaultValue
}

@JvmOverloads
fun <T : Any> get(
    iterable: Iterable<*>,
    index: Int,
    toType: Class<T>,
    converter: Converter = defaultConverter
): T {
    val element = iterable.elementAt(index)
    return converter.convert(element, toType)
}

@JvmOverloads
fun <T : Any> get(
    iterable: Iterable<*>,
    index: Int,
    toType: Type,
    converter: Converter = defaultConverter
): T {
    val element = iterable.elementAt(index)
    return converter.convert(element, toType)
}

@JvmOverloads
fun <T : Any> get(
    map: Map<*, *>,
    key: Any?,
    toType: Class<T>,
    converter: Converter = defaultConverter
): T {
    val value = map[key]
    return converter.convert(value, toType)
}

@JvmOverloads
fun <T : Any> get(
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