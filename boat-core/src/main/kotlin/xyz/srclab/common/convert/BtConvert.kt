/**
 * Conversion utilities.
 */
@file:JvmName("BtConvert")

package xyz.srclab.common.convert

import xyz.srclab.common.base.Val
import xyz.srclab.common.reflect.TypeRef
import java.lang.reflect.Type

private var defaultConverter: Converter = Converter.newConverter()

fun defaultConverter(): Converter {
    return defaultConverter
}

fun setDefaultConverter(converter: Converter) {
    defaultConverter = converter
}

@Throws(BtConvertException::class)
fun <T : Any> Any?.convert(toType: Class<T>): T? {
    return defaultConverter.convert(this, toType)
}

@Throws(BtConvertException::class)
fun <T : Any> Any?.convert(toType: Type): T? {
    return defaultConverter.convert(this, toType)
}

@Throws(BtConvertException::class)
fun <T : Any> Any?.convert(toType: TypeRef<T>): T? {
    return defaultConverter.convert(this, toType)
}

@Throws(BtConvertException::class)
fun <T : Any> Any?.convert(fromType: Type, toType: Class<T>): T? {
    return defaultConverter.convert(this, fromType, toType)
}

@Throws(BtConvertException::class)
fun <T : Any> Any?.convert(fromType: Type, toType: Type): T? {
    return defaultConverter.convert(this, fromType, toType)
}

@Throws(BtConvertException::class)
fun <T : Any> Any?.convert(fromType: Type, toType: TypeRef<T>): T? {
    return defaultConverter.convert(this, fromType, toType)
}

@Throws(BtConvertException::class)
fun <T : Any> Any?.convertVal(toType: Class<T>): Val<T>? {
    return defaultConverter.convertVal(this, toType)
}

@Throws(BtConvertException::class)
fun <T : Any> Any?.convertVal(toType: Type): Val<T>? {
    return defaultConverter.convertVal(this, toType)
}

@Throws(BtConvertException::class)
fun <T : Any> Any?.convertVal(toType: TypeRef<T>): Val<T>? {
    return defaultConverter.convertVal(this, toType)
}

@Throws(BtConvertException::class)
fun <T : Any> Any?.convertVal(fromType: Type, toType: Class<T>): Val<T>? {
    return defaultConverter.convertVal(this, fromType, toType)
}

@Throws(BtConvertException::class)
fun <T : Any> Any?.convertVal(fromType: Type, toType: Type): Val<T>? {
    return defaultConverter.convertVal(this, fromType, toType)
}

@Throws(BtConvertException::class)
fun <T : Any> Any?.convertVal(fromType: Type, toType: TypeRef<T>): Val<T>? {
    return defaultConverter.convertVal(this, fromType, toType)
}