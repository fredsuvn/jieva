/**
 * Enum utilities.
 */
@file:JvmName("BtEnum")

package xyz.srclab.common.base

import java.io.Serializable

/**
 * Returns enum instance of given [name].
 */
@Throws(NoSuchEnumException::class)
@JvmOverloads
fun <T : Enum<T>> Class<*>.getEnum(name: CharSequence, ignoreCase: Boolean = false): T {
    if (!ignoreCase) {
        return name.getEnum(this).asType()
    }
    val values: Array<out Enum<*>>? = this.enumConstants?.asType()
    if (values.isNullOrEmpty()) {
        throw NoSuchEnumException(name.toString())
    }
    for (value in values) {
        if (value.name.contentEquals(name, true)) {
            return value.asType()
        }
    }
    throw NoSuchEnumException(name.toString())
}

/**
 * Returns enum instance of given [name], or null if it doesn't exit.
 */
@JvmOverloads
fun <T> Class<*>.getEnumOrNull(name: CharSequence, ignoreCase: Boolean = false): T? {
    if (!ignoreCase) {
        return name.getEnumOrNull(this).asType()
    }
    return try {
        val values: Array<out Enum<*>>? = this.enumConstants?.asType()
        if (values.isNullOrEmpty()) {
            return null
        }
        for (value in values) {
            if (value.name.contentEquals(name, true)) {
                return value.asType()
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
@Throws(NoSuchEnumException::class)
fun <T> Class<*>.getEnum(index: Int): T {
    return getEnumOrNull(index) ?: throw NoSuchEnumException("$this[$index]")
}

/**
 * Returns enum instance of given [index], or null if it doesn't exit or index out of bounds.
 */
fun <T> Class<*>.getEnumOrNull(index: Int): T? {
    val values = this.enumConstants
    if (values.isNullOrEmpty()) {
        return null
    }
    if (index.isInBounds(0, values.size)) {
        return values[index].asType()
    }
    return null
}

private fun CharSequence.getEnum(type: Class<*>): Any {
    try {
        return BtJava.getEnum(type, this.toString())
    } catch (e: Exception) {
        throw NoSuchEnumException(this.toString(), e)
    }
}

private fun CharSequence.getEnumOrNull(type: Class<*>): Any? {
    return try {
        this.getEnum(type)
    } catch (e: Exception) {
        null
    }
}

/**
 * No such enum exception.
 */
open class NoSuchEnumException @JvmOverloads constructor(
    message: String?, cause: Throwable? = null) : RuntimeException(message, cause), Serializable {
    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}