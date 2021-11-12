@file:JvmName("BArrays")

package xyz.srclab.common.collect

import xyz.srclab.common.base.asTyped
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Type
import kotlin.collections.joinTo as joinToKt
import kotlin.collections.joinToString as joinToStringKt

private const val NOT_ARRAY_TYPE_PREFIX = "Not a array type"

fun <T> Type.newArray(length: Int): Array<T> {
    return java.lang.reflect.Array.newInstance(this.rawClass, length).asTyped()
}

@JvmOverloads
fun <T> Array<T>.refOfRange(startIndex: Int = 0, endIndex: Int = this.size): BArrayRef<T> {
    return BArrayRef(this, startIndex, endIndex)
}

@JvmOverloads
fun BooleanArray.refOfRange(startIndex: Int = 0, endIndex: Int = this.size): BBooleanArrayRef {
    return BBooleanArrayRef(this, startIndex, endIndex)
}

@JvmOverloads
fun ByteArray.refOfRange(startIndex: Int = 0, endIndex: Int = this.size): BByteArrayRef {
    return BByteArrayRef(this, startIndex, endIndex)
}

@JvmOverloads
fun ShortArray.refOfRange(startIndex: Int = 0, endIndex: Int = this.size): BShortArrayRef {
    return BShortArrayRef(this, startIndex, endIndex)
}

@JvmOverloads
fun CharArray.refOfRange(startIndex: Int = 0, endIndex: Int = this.size): BCharArrayRef {
    return BCharArrayRef(this, startIndex, endIndex)
}

@JvmOverloads
fun IntArray.refOfRange(startIndex: Int = 0, endIndex: Int = this.size): BIntArrayRef {
    return BIntArrayRef(this, startIndex, endIndex)
}

@JvmOverloads
fun LongArray.refOfRange(startIndex: Int = 0, endIndex: Int = this.size): BLongArrayRef {
    return BLongArrayRef(this, startIndex, endIndex)
}

@JvmOverloads
fun FloatArray.refOfRange(startIndex: Int = 0, endIndex: Int = this.size): BFloatArrayRef {
    return BFloatArrayRef(this, startIndex, endIndex)
}

@JvmOverloads
fun DoubleArray.refOfRange(startIndex: Int = 0, endIndex: Int = this.size): BDoubleArrayRef {
    return BDoubleArrayRef(this, startIndex, endIndex)
}

@JvmOverloads
fun <T> Array<T>.refOfOffset(offset: Int = 0, length: Int = this.size - offset): BArrayRef<T> {
    return BArrayRef(this, offset, offset + length)
}

@JvmOverloads
fun BooleanArray.refOfOffset(offset: Int = 0, length: Int = this.size - offset): BBooleanArrayRef {
    return BBooleanArrayRef(this, offset, offset + length)
}

@JvmOverloads
fun ByteArray.refOfOffset(offset: Int = 0, length: Int = this.size - offset): BByteArrayRef {
    return BByteArrayRef(this, offset, offset + length)
}

@JvmOverloads
fun ShortArray.refOfOffset(offset: Int = 0, length: Int = this.size - offset): BShortArrayRef {
    return BShortArrayRef(this, offset, offset + length)
}

@JvmOverloads
fun CharArray.refOfOffset(offset: Int = 0, length: Int = this.size - offset): BCharArrayRef {
    return BCharArrayRef(this, offset, offset + length)
}

@JvmOverloads
fun IntArray.refOfOffset(offset: Int = 0, length: Int = this.size - offset): BIntArrayRef {
    return BIntArrayRef(this, offset, offset + length)
}

@JvmOverloads
fun LongArray.refOfOffset(offset: Int = 0, length: Int = this.size - offset): BLongArrayRef {
    return BLongArrayRef(this, offset, offset + length)
}

@JvmOverloads
fun FloatArray.refOfOffset(offset: Int = 0, length: Int = this.size - offset): BFloatArrayRef {
    return BFloatArrayRef(this, offset, offset + length)
}

@JvmOverloads
fun DoubleArray.refOfOffset(offset: Int = 0, length: Int = this.size - offset): BDoubleArrayRef {
    return BDoubleArrayRef(this, offset, offset + length)
}

/**
 * Returns a fixed-size array associated given array,
 * or throws [IllegalArgumentException] if given object is not an array.
 */
fun <T> Any.arrayAsList(): MutableList<T> {
    return arrayAsListOrNull() ?: throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX: $this")
}

/**
 * Returns a fixed-size array associated given array,
 * or null if given object is not an array.
 */
fun <T> Any.arrayAsListOrNull(): MutableList<T>? {
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
        else -> null
    }
}

/**
 * Returns a fixed-size array associated given array
 */
fun <T> Array<T>.asList(): MutableList<T> {
    val bridge = object : ArrayBridge<T> {
        override val size: Int = this@asList.size

        override fun get(index: Int): T {
            return this@asList[index]
        }

        override fun set(index: Int, element: T) {
            this@asList[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

/**
 * Returns a fixed-size array associated given array
 */
fun BooleanArray.asList(): MutableList<Boolean> {
    val bridge = object : ArrayBridge<Boolean> {
        override val size: Int = this@asList.size

        override fun get(index: Int): Boolean {
            return this@asList[index]
        }

        override fun set(index: Int, element: Boolean) {
            this@asList[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

/**
 * Returns a fixed-size array associated given array
 */
fun ByteArray.asList(): MutableList<Byte> {
    val bridge = object : ArrayBridge<Byte> {
        override val size: Int = this@asList.size

        override fun get(index: Int): Byte {
            return this@asList[index]
        }

        override fun set(index: Int, element: Byte) {
            this@asList[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

/**
 * Returns a fixed-size array associated given array
 */
fun ShortArray.asList(): MutableList<Short> {
    val bridge = object : ArrayBridge<Short> {
        override val size: Int = this@asList.size

        override fun get(index: Int): Short {
            return this@asList[index]
        }

        override fun set(index: Int, element: Short) {
            this@asList[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

/**
 * Returns a fixed-size array associated given array
 */
fun CharArray.asList(): MutableList<Char> {
    val bridge = object : ArrayBridge<Char> {
        override val size: Int = this@asList.size

        override fun get(index: Int): Char {
            return this@asList[index]
        }

        override fun set(index: Int, element: Char) {
            this@asList[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

/**
 * Returns a fixed-size array associated given array
 */
fun IntArray.asList(): MutableList<Int> {
    val bridge = object : ArrayBridge<Int> {
        override val size: Int = this@asList.size

        override fun get(index: Int): Int {
            return this@asList[index]
        }

        override fun set(index: Int, element: Int) {
            this@asList[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

/**
 * Returns a fixed-size array associated given array
 */
fun LongArray.asList(): MutableList<Long> {
    val bridge = object : ArrayBridge<Long> {
        override val size: Int = this@asList.size

        override fun get(index: Int): Long {
            return this@asList[index]
        }

        override fun set(index: Int, element: Long) {
            this@asList[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

/**
 * Returns a fixed-size array associated given array
 */
fun FloatArray.asList(): MutableList<Float> {
    val bridge = object : ArrayBridge<Float> {
        override val size: Int = this@asList.size

        override fun get(index: Int): Float {
            return this@asList[index]
        }

        override fun set(index: Int, element: Float) {
            this@asList[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

/**
 * Returns a fixed-size array associated given array
 */
fun DoubleArray.asList(): MutableList<Double> {
    val bridge = object : ArrayBridge<Double> {
        override val size: Int = this@asList.size

        override fun get(index: Int): Double {
            return this@asList[index]
        }

        override fun set(index: Int, element: Double) {
            this@asList[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

@JvmOverloads
@JvmName("joinToString")
fun Any.arrayJoinToString(
    separator: CharSequence = ", ",
    transform: ((Any?) -> CharSequence)? = null
): String {
    return this.arrayJoinToString0(separator = separator, transform = transform)
}

@JvmName("joinToString")
fun Any.arrayJoinToString(
    separator: CharSequence = ", ",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((Any?) -> CharSequence)? = null
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
    transform: ((Any?) -> CharSequence)? = null
): String {
    return this.arrayJoinToString0(separator, prefix, suffix, limit, truncated, transform)
}

private fun Any.arrayJoinToString0(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((Any?) -> CharSequence)? = null
): String {
    return when (this) {
        is Array<*> -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform)
        is BooleanArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform)
        is ByteArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform)
        is ShortArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform)
        is CharArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform)
        is IntArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform)
        is LongArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform)
        is FloatArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform)
        is DoubleArray -> joinToStringKt(separator, prefix, suffix, limit, truncated, transform)
        else -> throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX: ${this.javaClass}")
    }
}

@JvmOverloads
@JvmName("joinTo")
fun <A : Appendable> Any.arrayJoinTo(
    buffer: A,
    separator: CharSequence = ", ",
    transform: ((Any?) -> CharSequence)? = null
): A {
    return this.arrayJoinTo0(buffer = buffer, separator = separator, transform = transform)
}

@JvmName("joinTo")
fun <A : Appendable> Any.arrayJoinTo(
    buffer: A,
    separator: CharSequence = ", ",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((Any?) -> CharSequence)? = null
): A {
    return this.arrayJoinTo0(
        buffer = buffer,
        separator = separator,
        limit = limit,
        truncated = truncated,
        transform = transform
    )
}

@JvmName("joinTo")
fun <A : Appendable> Any.arrayJoinTo(
    buffer: A,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((Any?) -> CharSequence)? = null
): A {
    return this.arrayJoinTo0(buffer, separator, prefix, suffix, limit, truncated, transform)
}

private fun <A : Appendable> Any.arrayJoinTo0(
    buffer: A,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    suffix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((Any?) -> CharSequence)? = null
): A {
    return when (this) {
        is Array<*> -> joinToKt(buffer, separator, prefix, suffix, limit, truncated, transform)
        is BooleanArray -> joinToKt(buffer, separator, prefix, suffix, limit, truncated, transform)
        is ByteArray -> joinToKt(buffer, separator, prefix, suffix, limit, truncated, transform)
        is ShortArray -> joinToKt(buffer, separator, prefix, suffix, limit, truncated, transform)
        is CharArray -> joinToKt(buffer, separator, prefix, suffix, limit, truncated, transform)
        is IntArray -> joinToKt(buffer, separator, prefix, suffix, limit, truncated, transform)
        is LongArray -> joinToKt(buffer, separator, prefix, suffix, limit, truncated, transform)
        is FloatArray -> joinToKt(buffer, separator, prefix, suffix, limit, truncated, transform)
        is DoubleArray -> joinToKt(buffer, separator, prefix, suffix, limit, truncated, transform)
        else -> throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX: ${this.javaClass}")
    }
}