@file:JvmName("Bools")

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