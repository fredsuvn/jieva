/**
 * Array utilities.
 */
@file:JvmName("BtArray")

package xyz.srclab.common.collect

import xyz.srclab.common.base.*
import xyz.srclab.common.collect.ArrayBridge.Companion.toArrayBridge
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Type
import java.util.function.Function
import kotlin.collections.joinTo as joinToKt
import kotlin.collections.joinToString as joinToStringKt

private const val NOT_ARRAY_TYPE_PREFIX = "Not an array type"

/**
 * Returns length of [this] array.
 */
@JvmName("getLength")
fun Any.arrayLength(): Int {
    return java.lang.reflect.Array.getLength(this)
}

/**
 * Returns a new array which is a copy of the specified range of the original array.
 *
 * @param fromIndex start index inclusive
 * @param toIndex end index exclusive
 */
@JvmName("copyOfRange")
@JvmOverloads
fun <A : Any> A.arrayCopyOfRange(fromIndex: Int = 0, toIndex: Int = this.arrayLength()): A {
    return when (this) {
        is Array<*> -> this.copyOfRange(fromIndex, toIndex)
        is BooleanArray -> this.copyOfRange(fromIndex, toIndex)
        is ByteArray -> this.copyOfRange(fromIndex, toIndex)
        is ShortArray -> this.copyOfRange(fromIndex, toIndex)
        is CharArray -> this.copyOfRange(fromIndex, toIndex)
        is IntArray -> this.copyOfRange(fromIndex, toIndex)
        is LongArray -> this.copyOfRange(fromIndex, toIndex)
        is FloatArray -> this.copyOfRange(fromIndex, toIndex)
        is DoubleArray -> this.copyOfRange(fromIndex, toIndex)
        else -> throw IllegalArgumentException("Not an array: $this!")
    }.asType()
}

/**
 * Returns new array of [elements].
 */
@JvmName("of")
fun <T> newArray(vararg elements: T): Array<T> {
    return elements.asType()
}

/**
 * Returns new array of [type].
 *
 * @param A array type
 */
@JvmName("ofType")
fun <A> newArrayOfType(type: Type, length: Int): A {
    return java.lang.reflect.Array.newInstance(type.rawClass.componentType, length).asType()
}

/**
 * Array adds [value] at [index], returns a new array.
 */
@JvmName("add")
fun <A : Any> A.arrayAdd(value: Any?, index: Int): A {
    val length = this.arrayLength()
    index.checkInBounds(0, length + 1)
    val newArray = newArrayOfType<A>(this.javaClass, length + 1)
    if (index > 0) {
        System.arraycopy(this, 0, newArray, 0, index)
    }
    java.lang.reflect.Array.set(newArray, index, value)
    if (index < length) {
        System.arraycopy(this, index, newArray, index + 1, remLength(length + 1, index + 1))
    }
    return newArray
}

/**
 * Array removes element at [index], return a new array.
 */
@JvmName("remove")
fun <A : Any> A.arrayRemove(index: Int): A {
    val length = this.arrayLength()
    index.checkInBounds(0, length)
    val newArray = newArrayOfType<A>(this.javaClass, length - 1)
    if (index > 0) {
        System.arraycopy(this, 0, newArray, 0, index)
    }
    if (index < length) {
        System.arraycopy(this, index + 1, newArray, index, remLength(length - 1, index))
    }
    return newArray
}

/**
 * Returns a fixed-size array associated given array,
 * or throws [IllegalArgumentException] if given object is not an array.
 */
@JvmName("asList")
fun <T> Any.arrayAsList(): MutableList<T> {
    return when (this) {
        is Array<*> -> this.asList().asType()
        is BooleanArray -> this.asList().asType()
        is ByteArray -> this.asList().asType()
        is ShortArray -> this.asList().asType()
        is CharArray -> this.asList().asType()
        is IntArray -> this.asList().asType()
        is LongArray -> this.asList().asType()
        is FloatArray -> this.asList().asType()
        is DoubleArray -> this.asList().asType()
        else -> throw IllegalArgumentException("Not an array: $this!")
    }
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun <T> Array<T>.asList(): MutableList<T> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun ByteArray.asList(): MutableList<Byte> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun ShortArray.asList(): MutableList<Short> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun IntArray.asList(): MutableList<Int> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun LongArray.asList(): MutableList<Long> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun FloatArray.asList(): MutableList<Float> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun DoubleArray.asList(): MutableList<Double> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun BooleanArray.asList(): MutableList<Boolean> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun CharArray.asList(): MutableList<Char> {
    return ArrayBridgeList(this.toArrayBridge())
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun <T> Array<T>.indexOf(elements: Array<T>, start: Int = 0, end: Int = elements.size): Int {
    return this.indexOf(0, elements, start, end)
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun BooleanArray.indexOf(elements: BooleanArray, start: Int = 0, end: Int = elements.size): Int {
    return this.indexOf(0, elements, start, end)
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun ByteArray.indexOf(elements: ByteArray, start: Int = 0, end: Int = elements.size): Int {
    return this.indexOf(0, elements, start, end)
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun ShortArray.indexOf(elements: ShortArray, start: Int = 0, end: Int = elements.size): Int {
    return this.indexOf(0, elements, start, end)
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun CharArray.indexOf(elements: CharArray, start: Int = 0, end: Int = elements.size): Int {
    return this.indexOf(0, elements, start, end)
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun IntArray.indexOf(elements: IntArray, start: Int = 0, end: Int = elements.size): Int {
    return this.indexOf(0, elements, start, end)
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun LongArray.indexOf(elements: LongArray, start: Int = 0, end: Int = elements.size): Int {
    return this.indexOf(0, elements, start, end)
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun FloatArray.indexOf(elements: FloatArray, start: Int = 0, end: Int = elements.size): Int {
    return this.indexOf(0, elements, start, end)
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun DoubleArray.indexOf(elements: DoubleArray, start: Int = 0, end: Int = elements.size): Int {
    return this.indexOf(0, elements, start, end)
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun <T> Array<T>.indexOf(offset: Int, elements: Array<T>, start: Int = 0, end: Int = elements.size): Int {
    return indexOf0(this.size, offset, start, end, { this[it] }, { elements[it] })
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun BooleanArray.indexOf(offset: Int, elements: BooleanArray, start: Int = 0, end: Int = elements.size): Int {
    return indexOf0(this.size, offset, start, end, { this[it] }, { elements[it] })
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun ByteArray.indexOf(offset: Int, elements: ByteArray, start: Int = 0, end: Int = elements.size): Int {
    return indexOf0(this.size, offset, start, end, { this[it] }, { elements[it] })
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun ShortArray.indexOf(offset: Int, elements: ShortArray, start: Int = 0, end: Int = elements.size): Int {
    return indexOf0(this.size, offset, start, end, { this[it] }, { elements[it] })
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun CharArray.indexOf(offset: Int, elements: CharArray, start: Int = 0, end: Int = elements.size): Int {
    return indexOf0(this.size, offset, start, end, { this[it] }, { elements[it] })
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun IntArray.indexOf(offset: Int, elements: IntArray, start: Int = 0, end: Int = elements.size): Int {
    return indexOf0(this.size, offset, start, end, { this[it] }, { elements[it] })
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun LongArray.indexOf(offset: Int, elements: LongArray, start: Int = 0, end: Int = elements.size): Int {
    return indexOf0(this.size, offset, start, end, { this[it] }, { elements[it] })
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun FloatArray.indexOf(offset: Int, elements: FloatArray, start: Int = 0, end: Int = elements.size): Int {
    return indexOf0(this.size, offset, start, end, { this[it] }, { elements[it] })
}

/**
 * Returns index of [elements] segment in [this] array.
 */
@JvmOverloads
fun DoubleArray.indexOf(offset: Int, elements: DoubleArray, start: Int = 0, end: Int = elements.size): Int {
    return indexOf0(this.size, offset, start, end, { this[it] }, { elements[it] })
}

private inline fun <T> indexOf0(
    size: Int, offset: Int, start: Int, end: Int,
    arrayGetter: (Int) -> T,
    elementsGetter: (Int) -> T
): Int {
    offset.checkInBounds(0, size)
    checkRangeInBounds(start, end, 0, size)
    var i = offset
    while (i < size) {
        var j = i
        var k = start
        while (j < size && k < end) {
            if (arrayGetter(j) == elementsGetter(k)) {
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

/**
 * Joins array to string.
 */
@JvmName("joinToString")
@JvmOverloads
fun Any.arrayJoinToString(
    separator: CharSequence = ", ",
    transform: Function<Any?, out CharSequence>? = null
): String {
    return this.arrayJoinToString(separator, -1, "...", transform)
}

/**
 * Joins array to string.
 */
@JvmName("joinToString")
@JvmOverloads
fun Any.arrayJoinToString(
    separator: CharSequence,
    limit: Int,
    truncated: CharSequence,
    transform: Function<Any?, out CharSequence>? = null
): String {
    return when (this) {
        is Array<*> -> joinToStringKt(separator, "", "", limit, truncated, transform?.asKotlinFun())
        is BooleanArray -> joinToStringKt(separator, "", "", limit, truncated, transform?.asKotlinFun())
        is ByteArray -> joinToStringKt(separator, "", "", limit, truncated, transform?.asKotlinFun())
        is ShortArray -> joinToStringKt(separator, "", "", limit, truncated, transform?.asKotlinFun())
        is CharArray -> joinToStringKt(separator, "", "", limit, truncated, transform?.asKotlinFun())
        is IntArray -> joinToStringKt(separator, "", "", limit, truncated, transform?.asKotlinFun())
        is LongArray -> joinToStringKt(separator, "", "", limit, truncated, transform?.asKotlinFun())
        is FloatArray -> joinToStringKt(separator, "", "", limit, truncated, transform?.asKotlinFun())
        is DoubleArray -> joinToStringKt(separator, "", "", limit, truncated, transform?.asKotlinFun())
        else -> throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX: ${this.javaClass}")
    }
}

/**
 * Joins array to string to [dest].
 */
@JvmName("joinTo")
@JvmOverloads
fun <A : Appendable> Any.arrayJoinTo(
    dest: A,
    separator: CharSequence = ", ",
    transform: Function<Any?, out CharSequence>? = null
): A {
    return this.arrayJoinTo(dest, separator, -1, "...", transform)
}

/**
 * Joins array to string to [dest].
 */
@JvmName("joinTo")
@JvmOverloads
fun <A : Appendable> Any.arrayJoinTo(
    dest: A,
    separator: CharSequence,
    limit: Int,
    truncated: CharSequence,
    transform: Function<Any?, out CharSequence>? = null
): A {
    return when (this) {
        is Array<*> -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is BooleanArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is ByteArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is ShortArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is CharArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is IntArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is LongArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is FloatArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        is DoubleArray -> joinToKt(dest, separator, "", "", limit, truncated, transform?.asKotlinFun())
        else -> throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX: ${this.javaClass}")
    }
}