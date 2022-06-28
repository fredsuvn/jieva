package xyz.srclab.common.base

import kotlin.math.min

/**
 * Array reference for [T], represents a range of array.
 *
 * @param T components type of array
 */
interface ArrayRef<T> {

    /**
     * Source array.
     */
    val source: Array<T>

    /**
     * Start index of source array, inclusive.
     */
    val startIndex: Int

    /**
     * End index of source array, exclusive
     */
    val endIndex: Int

    /**
     * Length of this range.
     */
    val length: Int

    /**
     * Returns elements at [index] of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): T

    /**
     * Sets elements [value] at [index], returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: T): T

    /**
     * Returns reference of sub-range of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): ArrayRef<T>

    /**
     * Returns copy of content of this range.
     */
    fun copy(): Array<T>

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: Array<T>): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this range into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: Array<T>, startIndex: Int): Int

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: ArrayRef<T>): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun <T> Array<T>.subRef(startIndex: Int = 0, endIndex: Int = this.size): ArrayRef<T> {
            return ArrayRefImpl(this, startIndex, endIndex)
        }

        private data class ArrayRefImpl<T>(
            override val source: Array<T>,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : ArrayRef<T> {

            override val length: Int = endIndex - startIndex

            override fun get(index: Int): T = source[startIndex + index]

            override fun set(index: Int, value: T): T {
                val old = source[startIndex + index]
                source[startIndex + index] = value
                return old
            }

            override fun subRef(startIndex: Int, endIndex: Int): ArrayRef<T> {
                checkRangeInBounds(startIndex, endIndex, 0, this.endIndex)
                return ArrayRefImpl(source, this.startIndex + startIndex, this.startIndex + endIndex)
            }

            override fun copy(): Array<T> {
                return source.copyOfRange(startIndex, endIndex)
            }

            override fun copyTo(dest: Array<T>, startIndex: Int): Int {
                val len = min(length, dest.size - startIndex)
                System.arraycopy(source, this.startIndex, dest, startIndex, len)
                return len
            }
        }
    }
}

/**
 * Array reference for [BooleanArray], represents a range of array.
 */
interface BooleanArrayRef {

    /**
     * Source array.
     */
    val source: BooleanArray

    /**
     * Start index of source array, inclusive.
     */
    val startIndex: Int

    /**
     * End index of source array, exclusive
     */
    val endIndex: Int

    /**
     * Length of this range.
     */
    val length: Int

    /**
     * Returns elements at [index] of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Boolean

    /**
     * Sets elements [value] at [index], returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Boolean): Boolean

    /**
     * Returns reference of sub-range of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): BooleanArrayRef

    /**
     * Returns copy of content of this range.
     */
    fun copy(): BooleanArray

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: BooleanArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this range into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: BooleanArray, startIndex: Int): Int

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: BooleanArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun <T> BooleanArray.subRef(startIndex: Int = 0, endIndex: Int = this.size): BooleanArrayRef {
            return BooleanArrayRefImp(this, startIndex, endIndex)
        }

        private data class BooleanArrayRefImp(
            override val source: BooleanArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : BooleanArrayRef {

            override val length: Int = endIndex - startIndex

            override fun get(index: Int): Boolean = source[startIndex + index]

            override fun set(index: Int, value: Boolean): Boolean {
                val old = source[startIndex + index]
                source[startIndex + index] = value
                return old
            }

            override fun subRef(startIndex: Int, endIndex: Int): BooleanArrayRef {
                checkRangeInBounds(startIndex, endIndex, 0, this.endIndex)
                return BooleanArrayRefImp(source, this.startIndex + startIndex, this.startIndex + endIndex)
            }

            override fun copy(): BooleanArray {
                return source.copyOfRange(startIndex, endIndex)
            }

            override fun copyTo(dest: BooleanArray, startIndex: Int): Int {
                val len = min(length, dest.size - startIndex)
                System.arraycopy(source, this.startIndex, dest, startIndex, len)
                return len
            }
        }
    }
}

/**
 * Array reference for [ByteArray], represents a range of array.
 */
interface ByteArrayRef {

    /**
     * Source array.
     */
    val source: ByteArray

    /**
     * Start index of source array, inclusive.
     */
    val startIndex: Int

    /**
     * End index of source array, exclusive
     */
    val endIndex: Int

    /**
     * Length of this range.
     */
    val length: Int

    /**
     * Returns elements at [index] of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Byte

    /**
     * Sets elements [value] at [index], returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Byte): Byte

    /**
     * Returns reference of sub-range of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): ByteArrayRef

    /**
     * Returns copy of content of this range.
     */
    fun copy(): ByteArray

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: ByteArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this range into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: ByteArray, startIndex: Int): Int

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: ByteArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun <T> ByteArray.subRef(startIndex: Int = 0, endIndex: Int = this.size): ByteArrayRef {
            return ByteArrayRefImp(this, startIndex, endIndex)
        }

        private data class ByteArrayRefImp(
            override val source: ByteArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : ByteArrayRef {

            override val length: Int = endIndex - startIndex

            override fun get(index: Int): Byte = source[startIndex + index]

            override fun set(index: Int, value: Byte): Byte {
                val old = source[startIndex + index]
                source[startIndex + index] = value
                return old
            }

            override fun subRef(startIndex: Int, endIndex: Int): ByteArrayRef {
                checkRangeInBounds(startIndex, endIndex, 0, this.endIndex)
                return ByteArrayRefImp(source, this.startIndex + startIndex, this.startIndex + endIndex)
            }

            override fun copy(): ByteArray {
                return source.copyOfRange(startIndex, endIndex)
            }

            override fun copyTo(dest: ByteArray, startIndex: Int): Int {
                val len = min(length, dest.size - startIndex)
                System.arraycopy(source, this.startIndex, dest, startIndex, len)
                return len
            }
        }
    }
}

/**
 * Array reference for [ShortArray], represents a range of array.
 */
interface ShortArrayRef {

    /**
     * Source array.
     */
    val source: ShortArray

    /**
     * Start index of source array, inclusive.
     */
    val startIndex: Int

    /**
     * End index of source array, exclusive
     */
    val endIndex: Int

    /**
     * Length of this range.
     */
    val length: Int

    /**
     * Returns elements at [index] of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Short

    /**
     * Sets elements [value] at [index], returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Short): Short

    /**
     * Returns reference of sub-range of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): ShortArrayRef

    /**
     * Returns copy of content of this range.
     */
    fun copy(): ShortArray

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: ShortArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this range into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: ShortArray, startIndex: Int): Int

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: ShortArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun <T> ShortArray.subRef(startIndex: Int = 0, endIndex: Int = this.size): ShortArrayRef {
            return ShortArrayRefImp(this, startIndex, endIndex)
        }

        private data class ShortArrayRefImp(
            override val source: ShortArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : ShortArrayRef {

            override val length: Int = endIndex - startIndex

            override fun get(index: Int): Short = source[startIndex + index]

            override fun set(index: Int, value: Short): Short {
                val old = source[startIndex + index]
                source[startIndex + index] = value
                return old
            }

            override fun subRef(startIndex: Int, endIndex: Int): ShortArrayRef {
                checkRangeInBounds(startIndex, endIndex, 0, this.endIndex)
                return ShortArrayRefImp(source, this.startIndex + startIndex, this.startIndex + endIndex)
            }

            override fun copy(): ShortArray {
                return source.copyOfRange(startIndex, endIndex)
            }

            override fun copyTo(dest: ShortArray, startIndex: Int): Int {
                val len = min(length, dest.size - startIndex)
                System.arraycopy(source, this.startIndex, dest, startIndex, len)
                return len
            }
        }
    }
}

/**
 * Array reference for [CharArray], represents a range of array.
 */
interface CharArrayRef {

    /**
     * Source array.
     */
    val source: CharArray

    /**
     * Start index of source array, inclusive.
     */
    val startIndex: Int

    /**
     * End index of source array, exclusive
     */
    val endIndex: Int

    /**
     * Length of this range.
     */
    val length: Int

    /**
     * Returns elements at [index] of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Char

    /**
     * Sets elements [value] at [index], returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Char): Char

    /**
     * Returns reference of sub-range of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): CharArrayRef

    /**
     * Returns copy of content of this range.
     */
    fun copy(): CharArray

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: CharArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this range into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: CharArray, startIndex: Int): Int

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: CharArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun <T> CharArray.subRef(startIndex: Int = 0, endIndex: Int = this.size): CharArrayRef {
            return CharArrayRefImp(this, startIndex, endIndex)
        }

        private data class CharArrayRefImp(
            override val source: CharArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : CharArrayRef {

            override val length: Int = endIndex - startIndex

            override fun get(index: Int): Char = source[startIndex + index]

            override fun set(index: Int, value: Char): Char {
                val old = source[startIndex + index]
                source[startIndex + index] = value
                return old
            }

            override fun subRef(startIndex: Int, endIndex: Int): CharArrayRef {
                checkRangeInBounds(startIndex, endIndex, 0, this.endIndex)
                return CharArrayRefImp(source, this.startIndex + startIndex, this.startIndex + endIndex)
            }

            override fun copy(): CharArray {
                return source.copyOfRange(startIndex, endIndex)
            }

            override fun copyTo(dest: CharArray, startIndex: Int): Int {
                val len = min(length, dest.size - startIndex)
                System.arraycopy(source, this.startIndex, dest, startIndex, len)
                return len
            }
        }
    }
}

/**
 * Array reference for [IntArray], represents a range of array.
 */
interface IntArrayRef {

    /**
     * Source array.
     */
    val source: IntArray

    /**
     * Start index of source array, inclusive.
     */
    val startIndex: Int

    /**
     * End index of source array, exclusive
     */
    val endIndex: Int

    /**
     * Length of this range.
     */
    val length: Int

    /**
     * Returns elements at [index] of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Int

    /**
     * Sets elements [value] at [index], returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Int): Int

    /**
     * Returns reference of sub-range of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): IntArrayRef

    /**
     * Returns copy of content of this range.
     */
    fun copy(): IntArray

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: IntArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this range into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: IntArray, startIndex: Int): Int

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: IntArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun <T> IntArray.subRef(startIndex: Int = 0, endIndex: Int = this.size): IntArrayRef {
            return IntArrayRefImp(this, startIndex, endIndex)
        }

        private data class IntArrayRefImp(
            override val source: IntArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : IntArrayRef {

            override val length: Int = endIndex - startIndex

            override fun get(index: Int): Int = source[startIndex + index]

            override fun set(index: Int, value: Int): Int {
                val old = source[startIndex + index]
                source[startIndex + index] = value
                return old
            }

            override fun subRef(startIndex: Int, endIndex: Int): IntArrayRef {
                checkRangeInBounds(startIndex, endIndex, 0, this.endIndex)
                return IntArrayRefImp(source, this.startIndex + startIndex, this.startIndex + endIndex)
            }

            override fun copy(): IntArray {
                return source.copyOfRange(startIndex, endIndex)
            }

            override fun copyTo(dest: IntArray, startIndex: Int): Int {
                val len = min(length, dest.size - startIndex)
                System.arraycopy(source, this.startIndex, dest, startIndex, len)
                return len
            }
        }
    }
}

/**
 * Array reference for [LongArray], represents a range of array.
 */
interface LongArrayRef {

    /**
     * Source array.
     */
    val source: LongArray

    /**
     * Start index of source array, inclusive.
     */
    val startIndex: Int

    /**
     * End index of source array, exclusive
     */
    val endIndex: Int

    /**
     * Length of this range.
     */
    val length: Int

    /**
     * Returns elements at [index] of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Long

    /**
     * Sets elements [value] at [index], returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Long): Long

    /**
     * Returns reference of sub-range of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): LongArrayRef

    /**
     * Returns copy of content of this range.
     */
    fun copy(): LongArray

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: LongArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this range into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: LongArray, startIndex: Int): Int

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: LongArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun <T> LongArray.subRef(startIndex: Int = 0, endIndex: Int = this.size): LongArrayRef {
            return LongArrayRefImp(this, startIndex, endIndex)
        }

        private data class LongArrayRefImp(
            override val source: LongArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : LongArrayRef {

            override val length: Int = endIndex - startIndex

            override fun get(index: Int): Long = source[startIndex + index]

            override fun set(index: Int, value: Long): Long {
                val old = source[startIndex + index]
                source[startIndex + index] = value
                return old
            }

            override fun subRef(startIndex: Int, endIndex: Int): LongArrayRef {
                checkRangeInBounds(startIndex, endIndex, 0, this.endIndex)
                return LongArrayRefImp(source, this.startIndex + startIndex, this.startIndex + endIndex)
            }

            override fun copy(): LongArray {
                return source.copyOfRange(startIndex, endIndex)
            }

            override fun copyTo(dest: LongArray, startIndex: Int): Int {
                val len = min(length, dest.size - startIndex)
                System.arraycopy(source, this.startIndex, dest, startIndex, len)
                return len
            }
        }
    }
}

/**
 * Array reference for [FloatArray], represents a range of array.
 */
interface FloatArrayRef {

    /**
     * Source array.
     */
    val source: FloatArray

    /**
     * Start index of source array, inclusive.
     */
    val startIndex: Int

    /**
     * End index of source array, exclusive
     */
    val endIndex: Int

    /**
     * Length of this range.
     */
    val length: Int

    /**
     * Returns elements at [index] of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Float

    /**
     * Sets elements [value] at [index], returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Float): Float

    /**
     * Returns reference of sub-range of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): FloatArrayRef

    /**
     * Returns copy of content of this range.
     */
    fun copy(): FloatArray

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: FloatArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this range into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: FloatArray, startIndex: Int): Int

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: FloatArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun <T> FloatArray.subRef(startIndex: Int = 0, endIndex: Int = this.size): FloatArrayRef {
            return FloatArrayRefImp(this, startIndex, endIndex)
        }

        private data class FloatArrayRefImp(
            override val source: FloatArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : FloatArrayRef {

            override val length: Int = endIndex - startIndex

            override fun get(index: Int): Float = source[startIndex + index]

            override fun set(index: Int, value: Float): Float {
                val old = source[startIndex + index]
                source[startIndex + index] = value
                return old
            }

            override fun subRef(startIndex: Int, endIndex: Int): FloatArrayRef {
                checkRangeInBounds(startIndex, endIndex, 0, this.endIndex)
                return FloatArrayRefImp(source, this.startIndex + startIndex, this.startIndex + endIndex)
            }

            override fun copy(): FloatArray {
                return source.copyOfRange(startIndex, endIndex)
            }

            override fun copyTo(dest: FloatArray, startIndex: Int): Int {
                val len = min(length, dest.size - startIndex)
                System.arraycopy(source, this.startIndex, dest, startIndex, len)
                return len
            }
        }
    }
}

/**
 * Array reference for [DoubleArray], represents a range of array.
 */
interface DoubleArrayRef {

    /**
     * Source array.
     */
    val source: DoubleArray

    /**
     * Start index of source array, inclusive.
     */
    val startIndex: Int

    /**
     * End index of source array, exclusive
     */
    val endIndex: Int

    /**
     * Length of this range.
     */
    val length: Int

    /**
     * Returns elements at [index] of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Double

    /**
     * Sets elements [value] at [index], returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Double): Double

    /**
     * Returns reference of sub-range of this range.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): DoubleArrayRef

    /**
     * Returns copy of content of this range.
     */
    fun copy(): DoubleArray

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: DoubleArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this range into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: DoubleArray, startIndex: Int): Int

    /**
     * Copies content of this range into [dest], returns copied size.
     */
    fun copyTo(dest: DoubleArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun <T> DoubleArray.subRef(startIndex: Int = 0, endIndex: Int = this.size): DoubleArrayRef {
            return DoubleArrayRefImp(this, startIndex, endIndex)
        }

        private data class DoubleArrayRefImp(
            override val source: DoubleArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : DoubleArrayRef {

            override val length: Int = endIndex - startIndex

            override fun get(index: Int): Double = source[startIndex + index]

            override fun set(index: Int, value: Double): Double {
                val old = source[startIndex + index]
                source[startIndex + index] = value
                return old
            }

            override fun subRef(startIndex: Int, endIndex: Int): DoubleArrayRef {
                checkRangeInBounds(startIndex, endIndex, 0, this.endIndex)
                return DoubleArrayRefImp(source, this.startIndex + startIndex, this.startIndex + endIndex)
            }

            override fun copy(): DoubleArray {
                return source.copyOfRange(startIndex, endIndex)
            }

            override fun copyTo(dest: DoubleArray, startIndex: Int): Int {
                val len = min(length, dest.size - startIndex)
                System.arraycopy(source, this.startIndex, dest, startIndex, len)
                return len
            }
        }
    }
}