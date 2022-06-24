@file:JvmName("BEnum")

package xyz.srclab.common.base

import java.io.Serializable

/**
 * Returns enum instance of given [name].
 */
@JvmName("getValue")
@Throws(NoSuchEnumException::class)
@JvmOverloads
fun <T : Enum<T>> Class<*>.enumValue(name: CharSequence, ignoreCase: Boolean = false): T {
    if (!ignoreCase) {
        return name.toEnum(this).asType()
    }
    val values: Array<out Enum<*>>? = this.enumConstants?.asType()
    if (values.isNullOrEmpty()) {
        throw NoSuchEnumException(name.toString())
    }
    for (value in values) {
        if (value.name.contentEquals(name, true)) {
            return value.asTyped()
        }
    }
    throw NoSuchEnumException(name.toString())
}

/**
 * Returns enum instance of given [name], or null if it doesn't exit.
 */
@JvmName("getValueOrNull")
@JvmOverloads
fun <T> Class<*>.enumValueOrNull(name: CharSequence, ignoreCase: Boolean = false): T? {
    if (!ignoreCase) {
        return name.toEnumOrNull(this).asType()
    }
    return try {
        val values: Array<out Enum<*>>? = this.enumConstants?.asType()
        if (values.isNullOrEmpty()) {
            return null
        }
        for (value in values) {
            if (value.name.contentEquals(name, true)) {
                return value.asTyped()
            }
        }
        null
    } catch (e: Exception) {
        null
    }
}

/**
 * Returns enum instance of given [index].
 */
@JvmName("getValue")
@Throws(NoSuchEnumException::class)
fun <T> Class<*>.enumValue(index: Int): T {
    return enumValueOrNull(index) ?: throw NoSuchEnumException("$this[$index]")
}

/**
 * Returns enum instance of given [index], or null if it doesn't exit or index out of bounds.
 */
@JvmName("getValueOrNull")
fun <T> Class<*>.enumValueOrNull(index: Int): T? {
    val values = this.enumConstants
    if (values.isNullOrEmpty()) {
        return null
    }
    if (index.isInBounds(0, values.size)) {
        return values[index].asType()
    }
    return null
}

private fun CharSequence.toEnum(type: Class<*>): Any {
    try {
        return JavaEnum.valueOf(type.asType<Class<Enum<*>>>(), this.toString())
    } catch (e: Exception) {
        throw NoSuchEnumException(this.toString())
    }
}

private fun CharSequence.toEnumOrNull(type: Class<*>): Any? {
    return try {
        this.toEnum(type)
    } catch (e: Exception) {
        null
    }
}

/**
 * No such enum exception.
 */
open class NoSuchEnumException(message: String?) : RuntimeException(message), Serializable {
    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}