@file:JvmName("Objs")

package xyz.srclab.common.base

import java.util.*
import kotlin.toString as toStringKt

@JvmName("as")
fun <T> Any?.asTyped(): T {
    return this as T
}

@JvmName("as")
fun <T> Nothing?.asTyped(): T {
    return this as T
}

//Null:

fun Any?.isNull(): Boolean {
    return this === null
}

fun Any?.isNotNull(): Boolean {
    return this !== null
}

//Equals:

fun Any?.equals(other: Any?): Boolean {
    return Objects.equals(this, other)
}

fun Any?.deepEquals(other: Any?): Boolean {
    return Objects.deepEquals(this, other)
}

//Hash:

fun Any?.hash(): Int {
    return this.hashCode()
}

fun Any?.arrayHash(): Int {
    return when (this) {
        is BooleanArray -> Arrays.hashCode(this)
        is ByteArray -> Arrays.hashCode(this)
        is ShortArray -> Arrays.hashCode(this)
        is CharArray -> Arrays.hashCode(this)
        is IntArray -> Arrays.hashCode(this)
        is LongArray -> Arrays.hashCode(this)
        is FloatArray -> Arrays.hashCode(this)
        is DoubleArray -> Arrays.hashCode(this)
        is Array<*> -> Arrays.hashCode(this)
        else -> hash()
    }
}

fun Any?.deepHash(): Int {
    return when (this) {
        is BooleanArray -> Arrays.hashCode(this)
        is ByteArray -> Arrays.hashCode(this)
        is ShortArray -> Arrays.hashCode(this)
        is CharArray -> Arrays.hashCode(this)
        is IntArray -> Arrays.hashCode(this)
        is LongArray -> Arrays.hashCode(this)
        is FloatArray -> Arrays.hashCode(this)
        is DoubleArray -> Arrays.hashCode(this)
        is Array<*> -> Arrays.deepHashCode(this)
        else -> hash()
    }
}

fun arrayHash(vararg args: Any?): Int {
    return args.contentHashCode()
}

fun deepHash(vararg args: Any?): Int {
    return args.contentDeepHashCode()
}

//ToString:

fun Any?.toString(): String {
    return this.toStringKt()
}

fun Any?.arrayToString(): String {
    return when (this) {
        is BooleanArray -> Arrays.toString(this)
        is ByteArray -> Arrays.toString(this)
        is ShortArray -> Arrays.toString(this)
        is CharArray -> Arrays.toString(this)
        is IntArray -> Arrays.toString(this)
        is LongArray -> Arrays.toString(this)
        is FloatArray -> Arrays.toString(this)
        is DoubleArray -> Arrays.toString(this)
        is Array<*> -> Arrays.toString(this)
        else -> this.toStringKt()
    }
}

fun Any?.deepToString(): String {
    return when (this) {
        is BooleanArray -> Arrays.toString(this)
        is ByteArray -> Arrays.toString(this)
        is ShortArray -> Arrays.toString(this)
        is CharArray -> Arrays.toString(this)
        is IntArray -> Arrays.toString(this)
        is LongArray -> Arrays.toString(this)
        is FloatArray -> Arrays.toString(this)
        is DoubleArray -> Arrays.toString(this)
        is Array<*> -> Arrays.deepToString(this)
        else -> this.toStringKt()
    }
}

fun Any?.toCharSeq(): CharSequence {
    return if (this is CharSequence)
        this
    else
        this.toStringKt()
}