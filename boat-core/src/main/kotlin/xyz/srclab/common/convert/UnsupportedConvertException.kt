package xyz.srclab.common.convert

import java.lang.reflect.Type

open class UnsupportedConvertException : RuntimeException {
    constructor(fromType: Type, toType: Type) : super("Convert is unsupported: $fromType -> $toType")
    constructor(from: Any?, toType: Type) : super("Convert is unsupported: ${from?.javaClass} -> $toType")
}