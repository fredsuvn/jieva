@file:JvmName("Gets")

package xyz.srclab.common.base

import xyz.srclab.common.convert.Converter

private val defaultConverter = Converter.DEFAULT

fun <T : Any> get(any: T?, defaultValue: T): T {
    return any ?: defaultValue
}

fun <T : Any> get(any: Any?, defaultValue: T, converter: Converter = defaultConverter): T {
    return converter.convertOrDefault(any, defaultValue.javaClass, defaultValue)
}

fun <T : Any> get(collection: Collection<*>, index: Int, defaultValue: T, converter: Converter = defaultConverter): T {
    val element = collection.elementAt(index)
    return get(element, defaultValue, converter)
}

//fun <T : Any> getOrNull(collection: Collection<*>?, index: Int, converter: Converter = defaultConverter): T? {
//    val element = collection.elementAt(index)
//    return get(element, defaultValue, converter)
//}