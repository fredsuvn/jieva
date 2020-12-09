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