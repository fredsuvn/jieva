/**
 * Enum utilities.
 */
@file:JvmName("BtEnum")

package xyz.srclab.common.base

import xyz.srclab.common.asType

/**
 * Returns enum instance of given [name], or null if it doesn't exit.
 */
@JvmOverloads
fun <T> Class<*>.getEnum(name: CharSequence, ignoreCase: Boolean = false): T? {
    if (!ignoreCase) {
        return name.getEnum0(this).asType()
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
 * Returns enum instance of given [index], or null if it doesn't exit or index out of bounds.
 */
fun <T> Class<*>.getEnum(index: Int): T? {
    val values = this.enumConstants
    if (values.isNullOrEmpty()) {
        return null
    }
    if (index.isInBounds(0, values.size)) {
        return values[index].asType()
    }
    return null
}

private fun CharSequence.getEnum0(type: Class<*>): Any? {
    return try {
        return BtJava.getEnum(type, this.toString())
    } catch (e: Exception) {
        null
    }
}