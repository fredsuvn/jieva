@file:JvmName("Converts")

package xyz.srclab.common.convert

import java.lang.reflect.Type

private val defaultConverter = Converter.DEFAULT

@Throws(UnsupportedConvertException::class)
fun <T : Any> Any?.convert(toType: Class<T>): T {
    return defaultConverter.convert(this, toType)
}

@Throws(UnsupportedConvertException::class)
fun <T : Any> Any?.convert(toType: Type): T {
    return defaultConverter.convert(this, toType)
}

@Throws(UnsupportedConvertException::class)
fun <T : Any> Any?.convert(fromType: Type, toType: Class<T>): T {
    return defaultConverter.convert(this, fromType, toType)
}

@Throws(UnsupportedConvertException::class)
fun <T : Any> Any?.convert(fromType: Type, toType: Type): T {
    return defaultConverter.convert(this, fromType, toType)
}

fun <T> Any?.convertOrElse(toType: Class<T>, defaultValue: T): T {
    return defaultConverter.convertOrElse(this, toType, defaultValue)
}

fun <T> Any?.convertOrElse(toType: Type, defaultValue: T): T {
    return defaultConverter.convertOrElse(this, toType, defaultValue)
}

fun <T> Any?.convertOrElse(fromType: Type, toType: Class<T>, defaultValue: T): T {
    return defaultConverter.convertOrElse(this, fromType, toType, defaultValue)
}

fun <T> Any?.convertOrElse(fromType: Type, toType: Type, defaultValue: T): T {
    return defaultConverter.convertOrElse(this, fromType, toType, defaultValue)
}

fun <T> Any?.convertOrElse(toType: Class<T>, defaultValue: () -> T): T {
    return defaultConverter.convertOrElse(this, toType, defaultValue)
}

fun <T> Any?.convertOrElse(toType: Type, defaultValue: () -> T): T {
    return defaultConverter.convertOrElse(this, toType, defaultValue)
}

fun <T> Any?.convertOrElse(fromType: Type, toType: Class<T>, defaultValue: () -> T): T {
    return defaultConverter.convertOrElse(this, fromType, toType, defaultValue)
}

fun <T> Any?.convertOrElse(fromType: Type, toType: Type, defaultValue: () -> T): T {
    return defaultConverter.convertOrElse(this, fromType, toType, defaultValue)
}

fun <T : Any> Any?.convertOrNull(toType: Class<T>): T? {
    return defaultConverter.convertOrNull(this, toType)
}

fun <T : Any> Any?.convertOrNull(toType: Type): T? {
    return defaultConverter.convertOrNull(this, toType)
}

fun <T : Any> Any?.convertOrNull(fromType: Type, toType: Class<T>): T? {
    return defaultConverter.convertOrNull(this, fromType, toType)
}

fun <T : Any> Any?.convertOrNull(fromType: Type, toType: Type): T? {
    return defaultConverter.convertOrNull(this, fromType, toType)
}