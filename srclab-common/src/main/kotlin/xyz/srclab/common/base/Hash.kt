@file:JvmName("Hash")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.util.*

fun Any?.hash(): Int {
    return this.hashCode()
}

fun Any?.elementHash(): Int {
    if (this === null) {
        return this.hashCode()
    }
    return when (this) {
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

fun Any?.elementDeepHash(): Int {
    if (this === null) {
        return this.hashCode()
    }
    return when (this) {
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

fun hash(vararg args: Any?): Int {
    return args.contentHashCode()
}

fun deepHash(vararg args: Any?): Int {
    return args.contentDeepHashCode()
}