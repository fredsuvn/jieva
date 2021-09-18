@file:JvmName("Enums")

package xyz.srclab.common.base

import xyz.srclab.common.exception.ImpossibleException
import java.lang.reflect.Method

@Throws(IllegalArgumentException::class)
@JvmName("valueOf")
fun <T : Enum<*>> Class<*>.valueOfEnum(name: CharSequence): T {
    val result = valueOfEnumOrNull<T>(name)
    if (result !== null) {
        return result
    }
    throw IllegalArgumentException("$name not found in $this")
}

@Throws(IllegalArgumentException::class)
@JvmName("valueOrNull")
fun <T : Enum<*>> Class<*>.valueOfEnumOrNull(name: CharSequence): T? {
    return try {
        JavaEnum.valueOf(this.asAny<Class<Enum<*>>>(), name.toString()).asAny()
    } catch (e: IllegalArgumentException) {
        if (this.isEnum) {
            return null
        }
        throw e
    }
}

@Throws(IllegalArgumentException::class)
@JvmName("valueIgnoreCase")
fun <T : Enum<*>> Class<*>.valueOfEnumIgnoreCase(name: CharSequence): T {
    val result = valueOfEnumIgnoreCaseOrNull<T>(name)
    if (result !== null) {
        return result
    }
    throw IllegalArgumentException("$name not found in $this")
}

@Throws(IllegalArgumentException::class)
@JvmName("valueIgnoreCaseOrNull")
fun <T : Enum<*>> Class<*>.valueOfEnumIgnoreCaseOrNull(name: CharSequence): T? {
    val t = valueOfEnumOrNull<T>(name)
    if (t !== null) {
        return t
    }
    val values: Array<out Any> = this.enumConstants
        ?: throw IllegalArgumentException("Must be an enum type: $this")
}

@Throws(IndexOutOfBoundsException::class)
@JvmName("valueOf")
fun <T : Enum<*>> Class<*>.valueOfEnum(index: Int): T {
    val result = valueOfEnumOrNull<T>(index)
    if (result !== null) {
        return result
    }
    throw IndexOutOfBoundsException("Index out of bounds: $index")
}

@JvmName("valueOrNull")
fun <T : Enum<*>> Class<*>.valueOfEnumOrNull(index: Int): T? {
    val values: Array<out Any> = this.enumConstants
        ?: throw IllegalArgumentException("Must be an enum type: $this")
    if (!isIndexInBounds(index, 0, values.size)) {
        return null
    }
    return values[index] as T
}