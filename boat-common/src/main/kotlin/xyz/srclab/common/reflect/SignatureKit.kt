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
@JvmOverloads
fun CharSequence.contractSignatureName(maxLength: Int = 0): String {

    val split = this.split(".")
    val wordCount = split.size
    val tailIndex = split.size - 1

    fun concatWords(split: List<String>, separatorIndex: Int): String {
        val buffer = StringBuilder(split[tailIndex])
        for (i in (0 until tailIndex).reversed()) {
            buffer.insert(0, ".")
            if (i < separatorIndex) {
                buffer.insert(0, split[i][0])
            } else {
                buffer.insert(0, split[i])
            }
        }
        return buffer.toString()
    }

    val minLength = (wordCount - 1) * 2 + split[wordCount - 1].length
    if (minLength >= maxLength) {
        return concatWords(split, tailIndex)
    }
    var newLength = minLength
    for (i in tailIndex - 1 downTo 0) {
        newLength += split[i].length - 1
        if (newLength > maxLength) {
            return concatWords(split, i + 1)
        }
        if (newLength == maxLength) {
            return concatWords(split, i)
        }
    }
    return concatWords(split, 0)
}