@file:JvmName("JvmKit")
@file:JvmMultifileClass

package xyz.srclab.common.jvm

import java.lang.reflect.Method

fun Class<*>.toJvmDescriptor(): String {
    if (this.isArray) {
        return this.name.replace('.', '/')
    }
    return when (this) {
        Boolean::class.javaPrimitiveType -> "Z"
        Byte::class.javaPrimitiveType -> "B"
        Short::class.javaPrimitiveType -> "S"
        Char::class.javaPrimitiveType -> "C"
        Int::class.javaPrimitiveType -> "I"
        Long::class.javaPrimitiveType -> "J"
        Float::class.javaPrimitiveType -> "F"
        Double::class.javaPrimitiveType -> "D"
        Void::class.javaPrimitiveType -> "V"
        else -> "L${this.name.replace('.', '/')};"
    }
}

fun Method.toJvmDescriptor(): String {
    return "(${this.parameterTypes.joinToString("") { it.toJvmDescriptor() }})" +
            this.returnType.toJvmDescriptor()
}