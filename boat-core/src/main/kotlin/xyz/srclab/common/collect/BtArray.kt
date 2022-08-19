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
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun <T> Array<T>.asList(): MutableList<T> {
    return ObjectArrayList(this)
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun BooleanArray.asList(): MutableList<Boolean> {
    return BooleanArrayList(this)
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun ByteArray.asList(): MutableList<Byte> {
    return ByteArrayList(this)
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun ShortArray.asList(): MutableList<Short> {
    return ShortArrayList(this)
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun CharArray.asList(): MutableList<Char> {
    return CharArrayList(this)
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun IntArray.asList(): MutableList<Int> {
    return IntArrayList(this)
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun LongArray.asList(): MutableList<Long> {
    return LongArrayList(this)
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun FloatArray.asList(): MutableList<Float> {
    return FloatArrayList(this)
}

/**
 * Returns a fixed-size [MutableList] that wraps the original array.
 */
fun DoubleArray.asList(): MutableList<Double> {
    return DoubleArrayList(this)
}

//private inline fun indexOf0(size:Int, offset: Int, getter:Int->) {
//    offset.checkInBounds(0, size)
//    var i = offset
//    while (i < size) {
//        if (this[i] == element) {
//            return i
//        }
//        i++
//    }
//    return -1
//}

/**
 * Returns index of element-sequence which specified by [subArray]
 * from [subStartIndex] inclusive to [subEndIndex] exclusive.
 */
@JvmOverloads
fun <T> Array<T>.indexOf(
    offset: Int, subArray: Array<T>, subStartIndex: Int = 0, subEndIndex: Int = subArray.size): Int {
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
    offset: Int, subArray: BooleanArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size): Int {
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
    offset: Int, subArray: ByteArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size): Int {
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
    offset: Int, subArray: ShortArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size): Int {
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
    offset: Int, subArray: CharArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size): Int {
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
    offset: Int, subArray: IntArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size): Int {
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
    offset: Int, subArray: LongArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size): Int {
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
    offset: Int, subArray: FloatArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size): Int {
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
    offset: Int, subArray: DoubleArray, subStartIndex: Int = 0, subEndIndex: Int = subArray.size): Int {
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