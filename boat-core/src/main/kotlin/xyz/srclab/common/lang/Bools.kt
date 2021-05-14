@file:JvmName("Bools")
@file:JvmMultifileClass

package xyz.srclab.common.lang

import kotlin.text.toBoolean as toBooleanKt

fun Any?.toBoolean(): Boolean {
    return when (this) {
        null -> false
        is Boolean -> this
        is Number -> toInt() != 0
        else -> toString().toBooleanKt()
    }
}