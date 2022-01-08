package xyz.srclab.common.convert

import java.lang.reflect.Type

open class UnsupportedConvertException : RuntimeException {
    constructor(fromType: Type, toType: Type) : super("Unsupported convert: $fromType -> $toType")
    constructor(from: Any?, toType: Type) : super("Unsupported convert: ${from?.javaClass} -> $toType")
}