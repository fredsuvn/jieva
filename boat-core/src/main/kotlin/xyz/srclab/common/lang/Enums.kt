@file:JvmName("Enums")

package xyz.srclab.common.lang

import xyz.srclab.common.exception.ImpossibleException
import java.lang.reflect.Method

@Throws(IllegalArgumentException::class)
@JvmName("value")
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
        java.lang.Enum.valueOf(this.asAny<Class<Enum<*>>>(), name.toString()).asAny()
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
    return t
        ?: try {
            val values: Method = this.getMethod("values")
            val array = values.invoke(null).asAny<Array<Enum<*>>>()
            for (o in array) {
                if (o.name.equals(name.toString(), ignoreCase = true)) {
                    return o.asAny()
                }
            }
            null
        } catch (e: Exception) {
            if (this.isEnum) {
                throw ImpossibleException(e)
            } else {
                throw IllegalArgumentException("Must be an enum type: $this", e)
            }
        }
}

@Throws(IndexOutOfBoundsException::class)
@JvmName("value")
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