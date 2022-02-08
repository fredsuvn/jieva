@file:JvmName("BArray")

package xyz.srclab.common.collect

import xyz.srclab.common.base.asTyped
import xyz.srclab.common.base.checkIndexInBounds
import xyz.srclab.common.base.toKotlinFun
import xyz.srclab.common.collect.ArrayBridge.Companion.toArrayBridge
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Type
import java.util.function.Function
import kotlin.collections.joinTo as joinToKt
import kotlin.collections.joinToString as joinToStringKt

private const val NOT_ARRAY_TYPE_PREFIX = "Not an array type"

@JvmName("arrayOf")
fun <T> newArrayOf(vararg elements: T): Array<T> {
    return elements.asTyped()
}

fun <T> Class<T>.newArray(length: Int): Array<T> {
    return java.lang.reflect.Array.newInstance(this, length).asTyped()
}

fun <T> Type.newArray(length: Int): Array<T> {
    return this.rawClass.newArray(length).asTyped()
}

fun Class<*>.newPrimitiveArray(length: Int): Any {
    return java.lang.reflect.Array.newInstance(this, length)
}

@JvmOverloads
fun <T> Array<T>.add(element: T, index: Int = this.size): Array<T> {
    if (index == this.size) {
        val result = this.copyOf(this.size + 1)
        result[this.size] = element
        return result.asTyped()
    }
    index.checkIndexInBounds(0, this.size)
    val result: Array<T?> = this.javaClass.componentType.newArray(this.size + 1).asTyped()
    System.arraycopy(this, 0, result, 0, index)
    result[index] = element
    System.arraycopy(this, index, result, index + 1, this.size - index)
    return result.asTyped()
}

@JvmOverloads
fun <T> Array<T>.remove(index: Int = this.size - 1): Array<T> {
    if (index == this.size - 1) {
        val result = this.copyOf(this.size - 1)
        return result.asTyped()
    }
    index.checkIndexInBounds(0, this.size)
    val result: Array<T?> = this.javaClass.componentType.newArray(this.size - 1).asTyped()
    System.arraycopy(this, 0, result, 0, index)
    System.arraycopy(this, index + 1, result, index, this.size - index - 1)
    return result.asTyped()
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
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun ByteArray.asList(): MutableList<Byte> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun ShortArray.asList(): MutableList<Short> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun IntArray.asList(): MutableList<Int> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun LongArray.asList(): MutableList<Long> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun FloatArray.asList(): MutableList<Float> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun DoubleArray.asList(): MutableList<Double> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun BooleanArray.asList(): MutableList<Boolean> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a [MutableList] that wraps the original array.
 */
fun CharArray.asList(): MutableList<Char> {
    return ArrayBridgeList(this.toArrayBridge())
}

@JvmOverloads
fun <T> Array<T>.indexOfArray(content: Array<T>, start: Int = 0, end: Int = content.size): Int {
    var i = 0
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun BooleanArray.indexOfArray(content: BooleanArray, start: Int = 0, end: Int = content.size): Int {
    var i = 0
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun ByteArray.indexOfArray(content: ByteArray, start: Int = 0, end: Int = content.size): Int {
    var i = 0
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun ShortArray.indexOfArray(content: ShortArray, start: Int = 0, end: Int = content.size): Int {
    var i = 0
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun CharArray.indexOfArray(content: CharArray, start: Int = 0, end: Int = content.size): Int {
    var i = 0
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun IntArray.indexOfArray(content: IntArray, start: Int = 0, end: Int = content.size): Int {
    var i = 0
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun LongArray.indexOfArray(content: LongArray, start: Int = 0, end: Int = content.size): Int {
    var i = 0
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun FloatArray.indexOfArray(content: FloatArray, start: Int = 0, end: Int = content.size): Int {
    var i = 0
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
}

@JvmOverloads
fun DoubleArray.indexOfArray(content: DoubleArray, start: Int = 0, end: Int = content.size): Int {
    var i = 0
    while (i < this.size) {
        var j = i
        var k = start
        while (j < this.size && k < end) {
            if (this[j] == content[k]) {
                j++
                k++
            } else {
                break
            }
        }
        if (k == end) {
            return i
        } else {
            i++
        }
    }
    return -1
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