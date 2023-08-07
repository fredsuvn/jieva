package xyz.srclab.common.convert

import xyz.srclab.common.base.defaultSerialVersion
import java.io.Serializable
import java.lang.reflect.Type

open class BtConvertException : RuntimeException, Serializable {
    constructor(fromType: Type, toType: Type) : super("Unsupported convert: $fromType -> $toType")
    constructor(from: Any?, toType: Type) : super("Unsupported convert: ${from?.javaClass} -> $toType")
    constructor(fromType: Type, toType: Type, cause: Throwable) : super("Unsupported convert: $fromType -> $toType", cause)
    constructor(from: Any?, toType: Type, cause: Throwable) : super("Unsupported convert: ${from?.javaClass} -> $toType", cause)

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}