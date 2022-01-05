@file:JvmName("BArray")

package xyz.srclab.common.collect

import xyz.srclab.common.base.asTyped
import xyz.srclab.common.base.toKotlinFun
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Type
import java.util.*
import java.util.function.Function
import kotlin.collections.joinTo as joinToKt
import kotlin.collections.joinToString as joinToStringKt

private const val NOT_ARRAY_TYPE_PREFIX = "Not an array type"

fun <T> Class<T>.newArray(length: Int): Array<T> {
    return java.lang.reflect.Array.newInstance(this, length).asTyped()
}

fun <T> Type.newArray(length: Int): Array<T> {
    return this.rawClass.newArray(length).asTyped()
}

/**
 * Returns a fixed-size array associated given array,
 * or throws [IllegalArgumentException] if given object is not an array.
 */
@JvmName("asList")
fun <T> Any.arrayAsList(): MutableList<T> {
    return when (this) {
        is Array<*> -> this.asList().asTyped()
        is BooleanArray -> this.asList().asTyped()
        is ByteArray -> this.asList().asTyped()
        is ShortArray -> this.asList().asTyped()
        is CharArray -> this.asList().asTyped()
        is IntArray -> this.asList().asTyped()
        is LongArray -> this.asList().asTyped()
        is FloatArray -> this.asList().asTyped()
        is DoubleArray -> this.asList().asTyped()
        else -> throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX: ${this.javaClass}")
    }
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun <T> Array<T>.asList(): MutableList<T> {
    return Arrays.asList(*this)
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun ByteArray.asList(): MutableList<Byte> {
    return ArrayBridgeList(object : ArrayBridge<Byte> {
        override val size: Int get() = this@asList.size
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(element: Byte): Boolean = this@asList.contains(element)
        override fun get(index: Int): Byte = this@asList[index]
        override fun set(index: Int, element: Byte): Byte = this@asList[index].let { this@asList[index] = element;it }
        override fun indexOf(element: Byte): Int = this@asList.indexOf(element)
        override fun lastIndexOf(element: Byte): Int = this@asList.lastIndexOf(element)
    })
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun ShortArray.asList(): MutableList<Short> {
    return ArrayBridgeList(object : ArrayBridge<Short> {
        override val size: Int get() = this@asList.size
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(element: Short): Boolean = this@asList.contains(element)
        override fun get(index: Int): Short = this@asList[index]
        override fun set(index: Int, element: Short): Short = this@asList[index].let { this@asList[index] = element;it }
        override fun indexOf(element: Short): Int = this@asList.indexOf(element)
        override fun lastIndexOf(element: Short): Int = this@asList.lastIndexOf(element)
    })
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun IntArray.asList(): MutableList<Int> {
    return ArrayBridgeList(object : ArrayBridge<Int> {
        override val size: Int get() = this@asList.size
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(element: Int): Boolean = this@asList.contains(element)
        override fun get(index: Int): Int = this@asList[index]
        override fun set(index: Int, element: Int): Int = this@asList[index].let { this@asList[index] = element;it }
        override fun indexOf(element: Int): Int = this@asList.indexOf(element)
        override fun lastIndexOf(element: Int): Int = this@asList.lastIndexOf(element)
    })
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun LongArray.asList(): MutableList<Long> {
    return ArrayBridgeList(object : ArrayBridge<Long> {
        override val size: Int get() = this@asList.size
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(element: Long): Boolean = this@asList.contains(element)
        override fun get(index: Int): Long = this@asList[index]
        override fun set(index: Int, element: Long): Long = this@asList[index].let { this@asList[index] = element;it }
        override fun indexOf(element: Long): Int = this@asList.indexOf(element)
        override fun lastIndexOf(element: Long): Int = this@asList.lastIndexOf(element)
    })
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun FloatArray.asList(): MutableList<Float> {
    return ArrayBridgeList(object : ArrayBridge<Float> {
        override val size: Int get() = this@asList.size
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(element: Float): Boolean = this@asList.contains(element)
        override fun get(index: Int): Float = this@asList[index]
        override fun set(index: Int, element: Float): Float = this@asList[index].let { this@asList[index] = element;it }
        override fun indexOf(element: Float): Int = this@asList.indexOf(element)
        override fun lastIndexOf(element: Float): Int = this@asList.lastIndexOf(element)
    })
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun DoubleArray.asList(): MutableList<Double> {
    return ArrayBridgeList(object : ArrayBridge<Double> {
        override val size: Int get() = this@asList.size
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(element: Double): Boolean = this@asList.contains(element)
        override fun get(index: Int): Double = this@asList[index]
        override fun set(index: Int, element: Double): Double =
            this@asList[index].let { this@asList[index] = element;it }

        override fun indexOf(element: Double): Int = this@asList.indexOf(element)
        override fun lastIndexOf(element: Double): Int = this@asList.lastIndexOf(element)
    })
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun BooleanArray.asList(): MutableList<Boolean> {
    return ArrayBridgeList(object : ArrayBridge<Boolean> {
        override val size: Int get() = this@asList.size
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(element: Boolean): Boolean = this@asList.contains(element)
        override fun get(index: Int): Boolean = this@asList[index]
        override fun set(index: Int, element: Boolean): Boolean =
            this@asList[index].let { this@asList[index] = element;it }

        override fun indexOf(element: Boolean): Int = this@asList.indexOf(element)
        override fun lastIndexOf(element: Boolean): Int = this@asList.lastIndexOf(element)
    })
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun CharArray.asList(): MutableList<Char> {
    return ArrayBridgeList(object : ArrayBridge<Char> {
        override val size: Int get() = this@asList.size
        override fun isEmpty(): Boolean = this@asList.isEmpty()
        override fun contains(element: Char): Boolean = this@asList.contains(element)
        override fun get(index: Int): Char = this@asList[index]
        override fun set(index: Int, element: Char): Char = this@asList[index].let { this@asList[index] = element;it }
        override fun indexOf(element: Char): Int = this@asList.indexOf(element)
        override fun lastIndexOf(element: Char): Int = this@asList.lastIndexOf(element)
    })
}

@JvmOverloads
@JvmName("joinToString")
fun Any.arrayJoinToString(
    separator: CharSequence = ", ",
    transform: Function<Any?, CharSequence>? = null
): String {
    return this.arrayJoinToString0(separator = separator, transform = transform)
}

@JvmName("joinToString")
fun Any.arrayJoinToString(
    separator: CharSequence = ", ",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: Function<Any?, CharSequence>? = null
): String {
    return this.arrayJoinToString0(
        separator = separator,
        limit = limit,
        truncated = truncated,
        transform = transform,
    )
}

@JvmName("joinToString")
fun Any.arrayJoinToString(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: Function<Any?, CharSequence>? = null
): String {
    return this.arrayJoinToString0(separator, prefix, suffix, limit, truncated, transform)
}

private fun Any.arrayJoinToString0(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: Function<Any?, CharSequence>? = null
): String {
    return when (this) {
        is Array<*> -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is BooleanArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is ByteArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is ShortArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is CharArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is IntArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is LongArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is FloatArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is DoubleArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        else -> throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX: ${this.javaClass}")
    }
}

@JvmOverloads
@JvmName("joinTo")
fun <A : Appendable> Any.arrayJoinTo(
    dest: A,
    separator: CharSequence = ", ",
    transform: Function<Any?, CharSequence>? = null
): A {
    return this.arrayJoinTo0(dest = dest, separator = separator, transform = transform)
}

@JvmName("joinTo")
fun <A : Appendable> Any.arrayJoinTo(
    dest: A,
    separator: CharSequence = ", ",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: Function<Any?, CharSequence>? = null
): A {
    return this.arrayJoinTo0(
        dest = dest,
        separator = separator,
        limit = limit,
        truncated = truncated,
        transform = transform
    )
}

@JvmName("joinTo")
fun <A : Appendable> Any.arrayJoinTo(
    dest: A,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: Function<Any?, CharSequence>? = null
): A {
    return this.arrayJoinTo0(dest, separator, prefix, suffix, limit, truncated, transform)
}

private fun <A : Appendable> Any.arrayJoinTo0(
    dest: A,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: Function<Any?, CharSequence>? = null
): A {
    return when (this) {
        is Array<*> -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is BooleanArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is ByteArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is ShortArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is CharArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is IntArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is LongArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is FloatArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        is DoubleArray -> joinToKt(dest, separator, prefix, suffix, limit, truncated, transform?.toKotlinFun())
        else -> throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX: ${this.javaClass}")
    }
}