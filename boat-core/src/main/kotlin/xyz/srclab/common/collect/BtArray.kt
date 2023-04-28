/**
 * Array utilities.
 */
@file:JvmName("BtArray")

package xyz.srclab.common.collect

import xyz.srclab.common.asType
import xyz.srclab.common.base.checkInBounds
import xyz.srclab.common.base.checkRangeInBounds
import xyz.srclab.common.reflect.rawClass
import xyz.srclab.common.remLength
import java.io.Serializable
import java.lang.reflect.Type

private const val NOT_ARRAY_TYPE_PREFIX = "Not an array type: "

/**
 * Returns length of [this] array.
 */
@JvmName("getLength")
fun Any.arrayLength(): Int {
    return when (this) {
        is Array<*> -> this.size
        is BooleanArray -> this.size
        is ByteArray -> this.size
        is ShortArray -> this.size
        is CharArray -> this.size
        is IntArray -> this.size
        is LongArray -> this.size
        is FloatArray -> this.size
        is DoubleArray -> this.size
        else -> throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX${this.javaClass}")
    }
}

/**
 * Returns a new array which is a copy of the specified range of the original array.
 *
 * @param fromIndex start index inclusive
 * @param toIndex end index exclusive
 */
@JvmName("copy")
@JvmOverloads
fun <A : Any> A.arrayCopy(fromIndex: Int = 0, toIndex: Int = this.arrayLength()): A {
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
        else -> throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX${this.javaClass}")
    }.asType()
}

/**
 * Returns new array of [arrayType].
 *
 * @param A array type
 */
@JvmName("newArray")
fun <A> arrayOfType(arrayType: Class<A>, length: Int): A {
    return java.lang.reflect.Array.newInstance(arrayType.componentType, length).asType()
}

/**
 * Returns new array of [arrayType].
 *
 * @param A array type
 */
@JvmName("newArray")
fun <A> arrayOfType(arrayType: Type, length: Int): A {
    return java.lang.reflect.Array.newInstance(arrayType.rawClass.componentType, length).asType()
}

/**
 * Array adds [value] at [index], returns a new array.
 */
@JvmName("add")
fun <A : Any> A.arrayAdd(value: Any?, index: Int): A {
    val length = this.arrayLength()
    index.checkInBounds(0, length + 1)
    val newArray = arrayOfType(this.javaClass, length + 1)
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
    val newArray = arrayOfType(this.javaClass, length - 1)
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
 * Returns a fixed-siz [MutableList] to wrap the given array.
 * The returned list will be backed by the given array, that is, any modification to the list
 * will cause the array to be modified and vice versa.
 */
@JvmOverloads
fun <T> Array<T>.asList(startIndex: Int = 0, endIndex: Int = this.size): MutableList<T> {
    checkRangeInBounds(startIndex, endIndex, 0, this.size)
    return ArrayWrapperList(this, startIndex, endIndex)
}

/**
 * Returns a fixed-siz [MutableList] to wrap the given array.
 * The returned list will be backed by the given array, that is, any modification to the list
 * will cause the array to be modified and vice versa.
 */
@JvmOverloads
fun BooleanArray.asList(startIndex: Int = 0, endIndex: Int = this.size): MutableList<Boolean> {
    checkRangeInBounds(startIndex, endIndex, 0, this.size)
    return BooleansWrapperList(this, startIndex, endIndex)
}

/**
 * Returns a fixed-siz [MutableList] to wrap the given array.
 * The returned list will be backed by the given array, that is, any modification to the list
 * will cause the array to be modified and vice versa.
 */
@JvmOverloads
fun ByteArray.asList(startIndex: Int = 0, endIndex: Int = this.size): MutableList<Byte> {
    checkRangeInBounds(startIndex, endIndex, 0, this.size)
    return BytesWrapperList(this, startIndex, endIndex)
}

/**
 * Returns a fixed-siz [MutableList] to wrap the given array.
 * The returned list will be backed by the given array, that is, any modification to the list
 * will cause the array to be modified and vice versa.
 */
@JvmOverloads
fun ShortArray.asList(startIndex: Int = 0, endIndex: Int = this.size): MutableList<Short> {
    checkRangeInBounds(startIndex, endIndex, 0, this.size)
    return ShortsWrapperList(this, startIndex, endIndex)
}

/**
 * Returns a fixed-siz [MutableList] to wrap the given array.
 * The returned list will be backed by the given array, that is, any modification to the list
 * will cause the array to be modified and vice versa.
 */
@JvmOverloads
fun CharArray.asList(startIndex: Int = 0, endIndex: Int = this.size): MutableList<Char> {
    checkRangeInBounds(startIndex, endIndex, 0, this.size)
    return CharsWrapperList(this, startIndex, endIndex)
}

/**
 * Returns a fixed-siz [MutableList] to wrap the given array.
 * The returned list will be backed by the given array, that is, any modification to the list
 * will cause the array to be modified and vice versa.
 */
@JvmOverloads
fun IntArray.asList(startIndex: Int = 0, endIndex: Int = this.size): MutableList<Int> {
    checkRangeInBounds(startIndex, endIndex, 0, this.size)
    return IntsWrapperList(this, startIndex, endIndex)
}

/**
 * Returns a fixed-siz [MutableList] to wrap the given array.
 * The returned list will be backed by the given array, that is, any modification to the list
 * will cause the array to be modified and vice versa.
 */
@JvmOverloads
fun LongArray.asList(startIndex: Int = 0, endIndex: Int = this.size): MutableList<Long> {
    checkRangeInBounds(startIndex, endIndex, 0, this.size)
    return LongsWrapperList(this, startIndex, endIndex)
}

/**
 * Returns a fixed-siz [MutableList] to wrap the given array.
 * The returned list will be backed by the given array, that is, any modification to the list
 * will cause the array to be modified and vice versa.
 */
@JvmOverloads
fun FloatArray.asList(startIndex: Int = 0, endIndex: Int = this.size): MutableList<Float> {
    checkRangeInBounds(startIndex, endIndex, 0, this.size)
    return FloatsWrapperList(this, startIndex, endIndex)
}

/**
 * Returns a fixed-siz [MutableList] to wrap the given array.
 * The returned list will be backed by the given array, that is, any modification to the list
 * will cause the array to be modified and vice versa.
 */
@JvmOverloads
fun DoubleArray.asList(startIndex: Int = 0, endIndex: Int = this.size): MutableList<Double> {
    checkRangeInBounds(startIndex, endIndex, 0, this.size)
    return DoublesWrapperList(this, startIndex, endIndex)
}

/**
 * Returns index of element-sequence which specified by [subArray]
 * from [subStartIndex] inclusive to [subEndIndex] exclusive.
 */
@JvmOverloads
fun <T> Array<T>.indexOf(
    offset: Int, subArray: Array<T>, subStartIndex: Int = 0, subEndIndex: Int = subArray.size
): Int {
    offset.checkInBounds(0, this.size)
    checkRangeInBounds(subStartIndex, subEndIndex, 0, subArray.size)
    val subLen = remLength(subEndIndex, subStartIndex)
    if (this.size < offset + subLen) {
        return -1
    }
    var i = offset
    main@ while (i <= this.size - subLen) {
        if (this[i] != subArray[subStartIndex]) {
            i++
            continue
        }
        for (j in 1 until subLen) {
            if (this[i + j] != subArray[subStartIndex + j]) {
                i++
                continue@main
            }
        }
        return i
    }
    return -1
}

/**
 * Returns index of element-sequence which specified by [subArray]
 * from [subStartIndex] inclusive to [subEndIndex] exclusive.
 */
@JvmOverloads
fun BooleanArray.indexOf(
    offset: Int, subArray: BooleanArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size
): Int {
    offset.checkInBounds(0, this.size)
    checkRangeInBounds(subStartIndex, subEndIndex, 0, subArray.size)
    val subLen = remLength(subEndIndex, subStartIndex)
    if (this.size < offset + subLen) {
        return -1
    }
    var i = offset
    main@ while (i <= this.size - subLen) {
        if (this[i] != subArray[subStartIndex]) {
            i++
            continue
        }
        for (j in 1 until subLen) {
            if (this[i + j] != subArray[subStartIndex + j]) {
                i++
                continue@main
            }
        }
        return i
    }
    return -1
}

/**
 * Returns index of element-sequence which specified by [subArray]
 * from [subStartIndex] inclusive to [subEndIndex] exclusive.
 */
@JvmOverloads
fun ByteArray.indexOf(
    offset: Int, subArray: ByteArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size
): Int {
    offset.checkInBounds(0, this.size)
    checkRangeInBounds(subStartIndex, subEndIndex, 0, subArray.size)
    val subLen = remLength(subEndIndex, subStartIndex)
    if (this.size < offset + subLen) {
        return -1
    }
    var i = offset
    main@ while (i <= this.size - subLen) {
        if (this[i] != subArray[subStartIndex]) {
            i++
            continue
        }
        for (j in 1 until subLen) {
            if (this[i + j] != subArray[subStartIndex + j]) {
                i++
                continue@main
            }
        }
        return i
    }
    return -1
}

/**
 * Returns index of element-sequence which specified by [subArray]
 * from [subStartIndex] inclusive to [subEndIndex] exclusive.
 */
@JvmOverloads
fun ShortArray.indexOf(
    offset: Int, subArray: ShortArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size
): Int {
    offset.checkInBounds(0, this.size)
    checkRangeInBounds(subStartIndex, subEndIndex, 0, subArray.size)
    val subLen = remLength(subEndIndex, subStartIndex)
    if (this.size < offset + subLen) {
        return -1
    }
    var i = offset
    main@ while (i <= this.size - subLen) {
        if (this[i] != subArray[subStartIndex]) {
            i++
            continue
        }
        for (j in 1 until subLen) {
            if (this[i + j] != subArray[subStartIndex + j]) {
                i++
                continue@main
            }
        }
        return i
    }
    return -1
}

/**
 * Returns index of element-sequence which specified by [subArray]
 * from [subStartIndex] inclusive to [subEndIndex] exclusive.
 */
@JvmOverloads
fun CharArray.indexOf(
    offset: Int, subArray: CharArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size
): Int {
    offset.checkInBounds(0, this.size)
    checkRangeInBounds(subStartIndex, subEndIndex, 0, subArray.size)
    val subLen = remLength(subEndIndex, subStartIndex)
    if (this.size < offset + subLen) {
        return -1
    }
    var i = offset
    main@ while (i <= this.size - subLen) {
        if (this[i] != subArray[subStartIndex]) {
            i++
            continue
        }
        for (j in 1 until subLen) {
            if (this[i + j] != subArray[subStartIndex + j]) {
                i++
                continue@main
            }
        }
        return i
    }
    return -1
}

/**
 * Returns index of element-sequence which specified by [subArray]
 * from [subStartIndex] inclusive to [subEndIndex] exclusive.
 */
@JvmOverloads
fun IntArray.indexOf(
    offset: Int, subArray: IntArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size
): Int {
    offset.checkInBounds(0, this.size)
    checkRangeInBounds(subStartIndex, subEndIndex, 0, subArray.size)
    val subLen = remLength(subEndIndex, subStartIndex)
    if (this.size < offset + subLen) {
        return -1
    }
    var i = offset
    main@ while (i <= this.size - subLen) {
        if (this[i] != subArray[subStartIndex]) {
            i++
            continue
        }
        for (j in 1 until subLen) {
            if (this[i + j] != subArray[subStartIndex + j]) {
                i++
                continue@main
            }
        }
        return i
    }
    return -1
}

/**
 * Returns index of element-sequence which specified by [subArray]
 * from [subStartIndex] inclusive to [subEndIndex] exclusive.
 */
@JvmOverloads
fun LongArray.indexOf(
    offset: Int, subArray: LongArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size
): Int {
    offset.checkInBounds(0, this.size)
    checkRangeInBounds(subStartIndex, subEndIndex, 0, subArray.size)
    val subLen = remLength(subEndIndex, subStartIndex)
    if (this.size < offset + subLen) {
        return -1
    }
    var i = offset
    main@ while (i <= this.size - subLen) {
        if (this[i] != subArray[subStartIndex]) {
            i++
            continue
        }
        for (j in 1 until subLen) {
            if (this[i + j] != subArray[subStartIndex + j]) {
                i++
                continue@main
            }
        }
        return i
    }
    return -1
}

/**
 * Returns index of element-sequence which specified by [subArray]
 * from [subStartIndex] inclusive to [subEndIndex] exclusive.
 */
@JvmOverloads
fun FloatArray.indexOf(
    offset: Int, subArray: FloatArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size
): Int {
    offset.checkInBounds(0, this.size)
    checkRangeInBounds(subStartIndex, subEndIndex, 0, subArray.size)
    val subLen = remLength(subEndIndex, subStartIndex)
    if (this.size < offset + subLen) {
        return -1
    }
    var i = offset
    main@ while (i <= this.size - subLen) {
        if (this[i] != subArray[subStartIndex]) {
            i++
            continue
        }
        for (j in 1 until subLen) {
            if (this[i + j] != subArray[subStartIndex + j]) {
                i++
                continue@main
            }
        }
        return i
    }
    return -1
}

/**
 * Returns index of element-sequence which specified by [subArray]
 * from [subStartIndex] inclusive to [subEndIndex] exclusive.
 */
@JvmOverloads
fun DoubleArray.indexOf(
    offset: Int, subArray: DoubleArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size
): Int {
    offset.checkInBounds(0, this.size)
    checkRangeInBounds(subStartIndex, subEndIndex, 0, subArray.size)
    val subLen = remLength(subEndIndex, subStartIndex)
    if (this.size < offset + subLen) {
        return -1
    }
    var i = offset
    main@ while (i <= this.size - subLen) {
        if (this[i] != subArray[subStartIndex]) {
            i++
            continue
        }
        for (j in 1 until subLen) {
            if (this[i + j] != subArray[subStartIndex + j]) {
                i++
                continue@main
            }
        }
        return i
    }
    return -1
}

private class ArrayWrapperList<T>(
    private val array: Array<T>,
    private val startIndex: Int,
    private val endIndex: Int
) : AbstractMutableList<T>(), RandomAccess, Serializable {

    override val size: Int get() = endIndex - startIndex
    override fun isEmpty(): Boolean = size == 0
    override fun contains(element: T): Boolean = indexOf(element) != -1
    override fun get(index: Int): T = array[index + startIndex]
    override fun set(index: Int, element: T): T =
        array[index + startIndex].let { array[index + startIndex] = element;it }

    override fun indexOf(element: T): Int {
        var i = 0
        while (i < size) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun lastIndexOf(element: T): Int {
        var i = size - 1
        while (i >= 0) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun add(index: Int, element: T) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): T = throw UnsupportedOperationException()
}

private class BooleansWrapperList(
    private val array: BooleanArray,
    private val startIndex: Int,
    private val endIndex: Int,
) : AbstractMutableList<Boolean>(), RandomAccess, Serializable {

    override val size: Int get() = endIndex - startIndex
    override fun isEmpty(): Boolean = size == 0
    override fun contains(element: Boolean): Boolean = indexOf(element) != -1
    override fun get(index: Int): Boolean = array[index + startIndex]
    override fun set(index: Int, element: Boolean): Boolean =
        array[index + startIndex].let { array[index + startIndex] = element;it }

    override fun indexOf(element: Boolean): Int {
        var i = 0
        while (i < size) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun lastIndexOf(element: Boolean): Int {
        var i = size - 1
        while (i >= 0) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun add(index: Int, element: Boolean) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Boolean = throw UnsupportedOperationException()
}

private class BytesWrapperList(
    private val array: ByteArray,
    private val startIndex: Int,
    private val endIndex: Int,
) : AbstractMutableList<Byte>(), RandomAccess, Serializable {

    override val size: Int get() = endIndex - startIndex
    override fun isEmpty(): Boolean = size == 0
    override fun contains(element: Byte): Boolean = indexOf(element) != -1
    override fun get(index: Int): Byte = array[index + startIndex]
    override fun set(index: Int, element: Byte): Byte =
        array[index + startIndex].let { array[index + startIndex] = element;it }

    override fun indexOf(element: Byte): Int {
        var i = 0
        while (i < size) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun lastIndexOf(element: Byte): Int {
        var i = size - 1
        while (i >= 0) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun add(index: Int, element: Byte) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Byte = throw UnsupportedOperationException()
}

private class ShortsWrapperList(
    private val array: ShortArray,
    private val startIndex: Int,
    private val endIndex: Int,
) : AbstractMutableList<Short>(), RandomAccess, Serializable {

    override val size: Int get() = endIndex - startIndex
    override fun isEmpty(): Boolean = size == 0
    override fun contains(element: Short): Boolean = indexOf(element) != -1
    override fun get(index: Int): Short = array[index + startIndex]
    override fun set(index: Int, element: Short): Short =
        array[index + startIndex].let { array[index + startIndex] = element;it }

    override fun indexOf(element: Short): Int {
        var i = 0
        while (i < size) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun lastIndexOf(element: Short): Int {
        var i = size - 1
        while (i >= 0) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun add(index: Int, element: Short) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Short = throw UnsupportedOperationException()
}

private class CharsWrapperList(
    private val array: CharArray,
    private val startIndex: Int,
    private val endIndex: Int,
) : AbstractMutableList<Char>(), RandomAccess, Serializable {

    override val size: Int get() = endIndex - startIndex
    override fun isEmpty(): Boolean = size == 0
    override fun contains(element: Char): Boolean = indexOf(element) != -1
    override fun get(index: Int): Char = array[index + startIndex]
    override fun set(index: Int, element: Char): Char =
        array[index + startIndex].let { array[index + startIndex] = element;it }

    override fun indexOf(element: Char): Int {
        var i = 0
        while (i < size) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun lastIndexOf(element: Char): Int {
        var i = size - 1
        while (i >= 0) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun add(index: Int, element: Char) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Char = throw UnsupportedOperationException()
}

private class IntsWrapperList(
    private val array: IntArray,
    private val startIndex: Int,
    private val endIndex: Int,
) : AbstractMutableList<Int>(), RandomAccess, Serializable {

    override val size: Int get() = endIndex - startIndex
    override fun isEmpty(): Boolean = size == 0
    override fun contains(element: Int): Boolean = indexOf(element) != -1
    override fun get(index: Int): Int = array[index + startIndex]
    override fun set(index: Int, element: Int): Int =
        array[index + startIndex].let { array[index + startIndex] = element;it }

    override fun indexOf(element: Int): Int {
        var i = 0
        while (i < size) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun lastIndexOf(element: Int): Int {
        var i = size - 1
        while (i >= 0) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun add(index: Int, element: Int) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Int = throw UnsupportedOperationException()
}

private class LongsWrapperList(
    private val array: LongArray,
    private val startIndex: Int,
    private val endIndex: Int,
) : AbstractMutableList<Long>(), RandomAccess, Serializable {

    override val size: Int get() = endIndex - startIndex
    override fun isEmpty(): Boolean = size == 0
    override fun contains(element: Long): Boolean = indexOf(element) != -1
    override fun get(index: Int): Long = array[index + startIndex]
    override fun set(index: Int, element: Long): Long =
        array[index + startIndex].let { array[index + startIndex] = element;it }

    override fun indexOf(element: Long): Int {
        var i = 0
        while (i < size) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun lastIndexOf(element: Long): Int {
        var i = size - 1
        while (i >= 0) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun add(index: Int, element: Long) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Long = throw UnsupportedOperationException()
}

private class FloatsWrapperList(
    private val array: FloatArray,
    private val startIndex: Int,
    private val endIndex: Int,
) : AbstractMutableList<Float>(), RandomAccess, Serializable {

    override val size: Int get() = endIndex - startIndex
    override fun isEmpty(): Boolean = size == 0
    override fun contains(element: Float): Boolean = indexOf(element) != -1
    override fun get(index: Int): Float = array[index + startIndex]
    override fun set(index: Int, element: Float): Float =
        array[index + startIndex].let { array[index + startIndex] = element;it }

    override fun indexOf(element: Float): Int {
        var i = 0
        while (i < size) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun lastIndexOf(element: Float): Int {
        var i = size - 1
        while (i >= 0) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun add(index: Int, element: Float) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Float = throw UnsupportedOperationException()
}

private class DoublesWrapperList(
    private val array: DoubleArray,
    private val startIndex: Int,
    private val endIndex: Int,
) : AbstractMutableList<Double>(), RandomAccess, Serializable {

    override val size: Int get() = endIndex - startIndex
    override fun isEmpty(): Boolean = size == 0
    override fun contains(element: Double): Boolean = indexOf(element) != -1
    override fun get(index: Int): Double = array[index + startIndex]
    override fun set(index: Int, element: Double): Double =
        array[index + startIndex].let { array[index + startIndex] = element;it }

    override fun indexOf(element: Double): Int {
        var i = 0
        while (i < size) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun lastIndexOf(element: Double): Int {
        var i = size - 1
        while (i >= 0) {
            if (array[i + startIndex] == element) {
                return i
            }
            i++
        }
        return -1
    }

    override fun add(index: Int, element: Double) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Double = throw UnsupportedOperationException()
}