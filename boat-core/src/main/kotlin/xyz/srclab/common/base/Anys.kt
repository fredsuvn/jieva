@file:JvmName("Anys")

package xyz.srclab.common.base

import java.util.*
import kotlin.toString as toStringKt

@JvmName("as")
fun <T> Any?.asAny(): T {
    return this as T
}

@JvmName("as")
fun <T> Nothing?.asAny(): T {
    return this as T
}

fun <T : Any> T?.asNotNull(): T {
    return this as T
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

fun hash(vararg args: Any?): Int {
    return Objects.hash(*args)
}

fun deepHash(vararg args: Any?): Int {
    return args.contentDeepHashCode()
}

//ToString:

fun Any?.toString(): String {
    return this.toStringKt()
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