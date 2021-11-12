@file:JvmName("BBools")

package xyz.srclab.common.base

import kotlin.text.toBoolean as toBooleanKt

fun Any?.toBoolean(): Boolean {
    return when (this) {
        null -> false
        is Boolean -> this
        is Number -> toInt() != 0
        else -> toString().toBooleanKt()
    }
}

fun CharSequence.isTrue(vararg accepts: CharSequence): Boolean {
    for (accept in accepts) {
        if (this.equals(accept, false)) {
            return true
        }
    }
    return false
}

fun CharSequence.isTrueIgnoreCase(vararg accepts: CharSequence): Boolean {
    for (accept in accepts) {
        if (this.equals(accept, true)) {
            return true
        }
    }
    return false
}

@JvmOverloads
fun CharSequence.isTrue(accepts: Iterable<CharSequence>, ignoreCase: Boolean = true): Boolean {
    for (accept in accepts) {
        if (this.equals(accept, ignoreCase)) {
            return true
        }
    }
    return false
}