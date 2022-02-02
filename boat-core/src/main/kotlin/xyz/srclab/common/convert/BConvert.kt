@file:JvmName("BConvert")

package xyz.srclab.common.convert

import xyz.srclab.common.reflect.TypeRef
import java.lang.reflect.Type

private val defaultConverter
    get() = Converter.defaultConverter()

@Throws(ConvertException::class)
fun <T : Any> Any?.convert(toType: Class<T>): T {
    return defaultConverter.convert(this, toType)
}

@Throws(ConvertException::class)
fun <T : Any> Any?.convert(toType: Type): T {
    return defaultConverter.convert(this, toType)
}

@Throws(ConvertException::class)
fun <T : Any> Any?.convert(toType: TypeRef<T>): T {
    return defaultConverter.convert(this, toType)
}

@Throws(ConvertException::class)
fun <T : Any> Any?.convert(fromType: Type, toType: Class<T>): T {
    return defaultConverter.convert(this, fromType, toType)
}

@Throws(ConvertException::class)
fun <T : Any> Any?.convert(fromType: Type, toType: Type): T {
    return defaultConverter.convert(this, fromType, toType)
}

fun <T : Any> Any?.convertOrNull(toType: Class<T>): T? {
    return defaultConverter.convertOrNull(this, toType)
}

fun <T : Any> Any?.convertOrNull(toType: Type): T? {
    return defaultConverter.convertOrNull(this, toType)
}

fun <T : Any> Any?.convertOrNull(toType: TypeRef<T>): T? {
    return defaultConverter.convertOrNull(this, toType)
}

fun <T : Any> Any?.convertOrNull(fromType: Type, toType: Class<T>): T? {
    return defaultConverter.convertOrNull(this, fromType, toType)
}

fun <T : Any> Any?.convertOrNull(fromType: Type, toType: Type): T? {
    return defaultConverter.convertOrNull(this, fromType, toType)
}