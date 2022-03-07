@file:JvmName("BObject")

package xyz.srclab.common.base

import java.util.*
import kotlin.toString as toStringKt

/**
 * Casts [this] to any type.
 */
@JvmName("as")
fun <T> Any?.asTyped(): T {
    return this as T
}

/**
 * Casts [this] to any type.
 */
@JvmSynthetic
@JvmName("as")
fun <T> Nothing?.asTyped(): T {
    return this as T
}

/**
 * Casts [this] to any type, not null.
 */
fun <T : Any> T?.asNotNull(): T {
    return this as T
}

/**
 * Returns whether [this] is null.
 */
fun Any?.isNull(): Boolean {
    return this === null
}

/**
 * Returns whether [this] is not null.
 */
fun Any?.isNotNull(): Boolean {
    return this !== null
}

/**
 * Returns whether [this] equals [other].
 */
fun Any?.equals(other: Any?): Boolean {
    return Objects.equals(this, other)
}

/**
 * Returns whether [this] equals [other], or deep equals for array types.
 */
fun Any?.deepEquals(other: Any?): Boolean {
    return Objects.deepEquals(this, other)
}

/**
 * Returns true if [this] equals any of [others].
 */
fun Any?.equalsAny(vararg others: Any?): Boolean {
    for (other in others) {
        if (equals(other)) {
            return true
        }
    }
    return false
}

/**
 * Returns true if [this] equals each of [others].
 */
fun Any?.equalsAll(vararg others: Any?): Boolean {
    for (other in others) {
        if (!equals(other)) {
            return false
        }
    }
    return true
}

/**
 * Returns hash code of [this].
 */
fun Any?.hash(): Int {
    return this.hashCode()
}

/**
 * Returns hash code of [this], or array hash code if this is an array.
 */
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

/**
 * Returns hash code of [this],
 * or array hash code if this is an array,
 * or deep array hash code if this is a deep array.
 */
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

/**
 * Returns array hash code of [args].
 */
fun arrayHash(vararg args: Any?): Int {
    return args.contentHashCode()
}

/**
 * Returns deep array hash code of [args].
 */
fun deepHash(vararg args: Any?): Int {
    return args.contentDeepHashCode()
}

/**
 * Returns identity hash code with [System.identityHashCode].
 */
fun Any?.idHashCode(): Int {
    return System.identityHashCode(this)
}

/**
 * Returns `toString` of [this].
 */
fun Any?.toString(): String {
    return this.toStringKt()
}

/**
 * Returns `toString` of [this], or array `toString` if this is an array.
 */
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

/**
 * Returns `toString` of [this],
 * or array `toString` if this is an array,
 * or deep array `toString` if this is a deep array.
 */
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

/**
 * If [this] is a [CharSequence], return itself; else return `toString` of this.
 */
fun Any?.toCharSeq(): CharSequence {
    return if (this is CharSequence)
        this
    else
        this.toStringKt()
}