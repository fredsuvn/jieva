@file:JvmName("Equal")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.util.*

fun Any?.equals(other: Any?): Boolean {
    return Objects.equals(this, other)
}

fun Any?.anyOrArrayEquals(other: Any?): Boolean {
    if (this === other) {
        return true
    }
    if (this === null || other === null) {
        return false
    }
    return when {
        this is BooleanArray && other is BooleanArray -> this.contentEquals(other)
        this is ShortArray && other is ShortArray -> this.contentEquals(other)
        this is CharArray && other is CharArray -> this.contentEquals(other)
        this is IntArray && other is IntArray -> this.contentEquals(other)
        this is LongArray && other is LongArray -> this.contentEquals(other)
        this is FloatArray && other is FloatArray -> this.contentEquals(other)
        this is DoubleArray && other is DoubleArray -> this.contentEquals(other)
        this is Array<*> && other is Array<*> -> this.contentEquals(other)
        else -> this == other
    }
}

fun Any?.anyOrArrayDeepEquals(other: Any?): Boolean {
    if (this === other) {
        return true
    }
    if (this === null || other === null) {
        return false
    }
    return when {
        this is BooleanArray && other is BooleanArray -> this.contentEquals(other)
        this is ShortArray && other is ShortArray -> this.contentEquals(other)
        this is CharArray && other is CharArray -> this.contentEquals(other)
        this is IntArray && other is IntArray -> this.contentEquals(other)
        this is LongArray && other is LongArray -> this.contentEquals(other)
        this is FloatArray && other is FloatArray -> this.contentEquals(other)
        this is DoubleArray && other is DoubleArray -> this.contentEquals(other)
        this is Array<*> && other is Array<*> -> this.contentDeepEquals(other)
        else -> this == other
    }
}