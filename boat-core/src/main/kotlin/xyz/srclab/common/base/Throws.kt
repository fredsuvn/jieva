@file:JvmName("Throws")

package xyz.srclab.common.base

import kotlin.stackTraceToString as stackTraceToStringKt

/**
 * Returns the detailed description of this throwable with its stack trace.
 */
fun Throwable.stackTraceToString(): String {
    return this.stackTraceToStringKt()
}