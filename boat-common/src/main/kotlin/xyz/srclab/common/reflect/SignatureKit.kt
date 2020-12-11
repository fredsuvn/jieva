@file:JvmName("SignatureKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Type

fun Constructor<*>.toSignatureString(): String {
    return toSignatureString(this.name, this.parameterTypes)
}

fun Method.toSignatureString(): String {
    return toSignatureString(this.name, this.parameterTypes)
}

/**
 * Returns method signature string like:
 * * name(a.b.C, x.y.Z<T>)
 */
fun toSignatureString(name: CharSequence, parameterTypes: Array<out Type>): String {
    return "$name(${parameterTypes.toParameterTypesString()})"
}

/**
 * Returns parameter types string like:
 * * a.b.C, x.y.Z<T>
 */
fun Array<out Type>.toParameterTypesString(): String {
    return this.joinToString { p -> p.typeName }
}

/**
 * Contracts full signature name:
 * * abc.xyz.Foo -> a.x.Foo
 */
fun CharSequence.contractSignatureName(maxLength: Int): String {
    val split = this.split(".")
    val length = split.size
    val buffer = StringBuilder(split[length - 1])
    for (i in (0..length - 2).reversed()) {
        if (buffer.length >= maxLength) {
            break
        }
        if (split[i].length + 1 + buffer.length <= maxLength) {
            buffer.insert(0, ".")
            buffer.insert(0, split[i])
        } else {
            buffer.insert(0, ".")
            buffer.insert(0, split[i][0])
        }
    }
    return buffer.toString()
}