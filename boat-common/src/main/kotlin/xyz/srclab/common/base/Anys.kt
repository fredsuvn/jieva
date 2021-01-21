package xyz.srclab.common.base

import java.util.*

fun Any?.equals(other: Any?): Boolean {
    return Objects.equals(this, other)
}

fun Any?.hash(): Int {
    return this.hashCode()
}

fun hash(vararg args: Any?): Int {
    return args.contentHashCode()
}

fun deepHash(vararg args: Any?): Int {
    return args.contentDeepHashCode()
}

fun Any?.anyOrArrayToString(): String {
    return when (this) {
        is Array<*> -> Arrays.toString(this)
        is BooleanArray -> Arrays.toString(this)
        is ByteArray -> Arrays.toString(this)
        is ShortArray -> Arrays.toString(this)
        is CharArray -> Arrays.toString(this)
        is IntArray -> Arrays.toString(this)
        is LongArray -> Arrays.toString(this)
        is FloatArray -> Arrays.toString(this)
        is DoubleArray -> Arrays.toString(this)
        else -> this.toString()
    }
}

fun Any?.anyOrArrayDeepToString(): String {
    return when (this) {
        is Array<*> -> Arrays.deepToString(this)
        is BooleanArray -> Arrays.toString(this)
        is ByteArray -> Arrays.toString(this)
        is ShortArray -> Arrays.toString(this)
        is CharArray -> Arrays.toString(this)
        is IntArray -> Arrays.toString(this)
        is LongArray -> Arrays.toString(this)
        is FloatArray -> Arrays.toString(this)
        is DoubleArray -> Arrays.toString(this)
        else -> this.toString()
    }
}

fun Any?.anyOrArrayHash(): Int {
    return when (this) {
        null -> this.hashCode()
        is BooleanArray -> Arrays.hashCode(this)
        is ShortArray -> Arrays.hashCode(this)
        is CharArray -> Arrays.hashCode(this)
        is IntArray -> Arrays.hashCode(this)
        is LongArray -> Arrays.hashCode(this)
        is FloatArray -> Arrays.hashCode(this)
        is DoubleArray -> Arrays.hashCode(this)
        is Array<*> -> Arrays.hashCode(this)
        else -> this.hashCode()
    }
}

fun Any?.anyOrArrayDeepHash(): Int {
    return when (this) {
        null -> this.hashCode()
        is BooleanArray -> Arrays.hashCode(this)
        is ShortArray -> Arrays.hashCode(this)
        is CharArray -> Arrays.hashCode(this)
        is IntArray -> Arrays.hashCode(this)
        is LongArray -> Arrays.hashCode(this)
        is FloatArray -> Arrays.hashCode(this)
        is DoubleArray -> Arrays.hashCode(this)
        is Array<*> -> Arrays.deepHashCode(this)
        else -> this.hashCode()
    }
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