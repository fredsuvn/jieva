package xyz.srclab.common.base

import java.io.Serializable
import kotlin.math.min

/**
 * Object array reference, refers to the source array with start index inclusive and end index exclusive.
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
     * Returns elements at [index] of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): T

    /**
     * Sets elements [value] at [index] of this reference, returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: T): T

    /**
     * Returns array reference of sub-range of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): ArrayRef<T>

    /**
     * Returns copy of content of this reference.
     */
    fun copy(): Array<T>

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: Array<T>): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this reference into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: Array<T>, startIndex: Int): Int

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: ArrayRef<T>): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        /**
         * Returns array reference of [this].
         */
        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun <T> Array<T>.arrayRef(startIndex: Int = 0, endIndex: Int = this.size): ArrayRef<T> {
            return ArrayRefImpl(this, startIndex, endIndex)
        }

        private data class ArrayRefImpl<T>(
            override val source: Array<T>,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : ArrayRef<T>, Serializable {

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
 * Boolean array reference, refers to the source array with start index inclusive and end index exclusive.
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
     * Returns elements at [index] of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Boolean

    /**
     * Sets elements [value] at [index] of this reference, returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Boolean): Boolean

    /**
     * Returns array reference of sub-range of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): BooleanArrayRef

    /**
     * Returns copy of content of this reference.
     */
    fun copy(): BooleanArray

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: BooleanArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this reference into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: BooleanArray, startIndex: Int): Int

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: BooleanArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        /**
         * Returns array reference of [this].
         */
        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun BooleanArray.arrayRef(startIndex: Int = 0, endIndex: Int = this.size): BooleanArrayRef {
            return BooleanArrayRefImp(this, startIndex, endIndex)
        }

        private data class BooleanArrayRefImp(
            override val source: BooleanArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : BooleanArrayRef, Serializable {

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
 * Byte array reference, refers to the source array with start index inclusive and end index exclusive.
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
     * Returns elements at [index] of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Byte

    /**
     * Sets elements [value] at [index] of this reference, returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Byte): Byte

    /**
     * Returns array reference of sub-range of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): ByteArrayRef

    /**
     * Returns copy of content of this reference.
     */
    fun copy(): ByteArray

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: ByteArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this reference into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: ByteArray, startIndex: Int): Int

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: ByteArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        /**
         * Returns array reference of [this].
         */
        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun ByteArray.arrayRef(startIndex: Int = 0, endIndex: Int = this.size): ByteArrayRef {
            return ByteArrayRefImp(this, startIndex, endIndex)
        }

        private data class ByteArrayRefImp(
            override val source: ByteArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : ByteArrayRef, Serializable {

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
 * Short array reference, refers to the source array with start index inclusive and end index exclusive.
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
     * Returns elements at [index] of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Short

    /**
     * Sets elements [value] at [index] of this reference, returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Short): Short

    /**
     * Returns array reference of sub-range of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): ShortArrayRef

    /**
     * Returns copy of content of this reference.
     */
    fun copy(): ShortArray

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: ShortArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this reference into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: ShortArray, startIndex: Int): Int

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: ShortArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        /**
         * Returns array reference of [this].
         */
        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun <T> ShortArray.arrayRef(startIndex: Int = 0, endIndex: Int = this.size): ShortArrayRef {
            return ShortArrayRefImp(this, startIndex, endIndex)
        }

        private data class ShortArrayRefImp(
            override val source: ShortArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : ShortArrayRef, Serializable {

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
 * Char array reference, refers to the source array with start index inclusive and end index exclusive.
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
     * Returns elements at [index] of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Char

    /**
     * Sets elements [value] at [index] of this reference, returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Char): Char

    /**
     * Returns array reference of sub-range of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): CharArrayRef

    /**
     * Returns copy of content of this reference.
     */
    fun copy(): CharArray

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: CharArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this reference into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: CharArray, startIndex: Int): Int

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: CharArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        /**
         * Returns array reference of [this].
         */
        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun CharArray.arrayRef(startIndex: Int = 0, endIndex: Int = this.size): CharArrayRef {
            return CharArrayRefImp(this, startIndex, endIndex)
        }

        private data class CharArrayRefImp(
            override val source: CharArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : CharArrayRef, Serializable {

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
 * Int array reference, refers to the source array with start index inclusive and end index exclusive.
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
     * Returns elements at [index] of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Int

    /**
     * Sets elements [value] at [index] of this reference, returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Int): Int

    /**
     * Returns array reference of sub-range of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): IntArrayRef

    /**
     * Returns copy of content of this reference.
     */
    fun copy(): IntArray

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: IntArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this reference into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: IntArray, startIndex: Int): Int

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: IntArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        /**
         * Returns array reference of [this].
         */
        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun IntArray.arrayRef(startIndex: Int = 0, endIndex: Int = this.size): IntArrayRef {
            return IntArrayRefImp(this, startIndex, endIndex)
        }

        private data class IntArrayRefImp(
            override val source: IntArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : IntArrayRef, Serializable {

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
 * Long array reference, refers to the source array with start index inclusive and end index exclusive.
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
     * Returns elements at [index] of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Long

    /**
     * Sets elements [value] at [index] of this reference, returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Long): Long

    /**
     * Returns array reference of sub-range of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): LongArrayRef

    /**
     * Returns copy of content of this reference.
     */
    fun copy(): LongArray

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: LongArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this reference into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: LongArray, startIndex: Int): Int

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: LongArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        /**
         * Returns array reference of [this].
         */
        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun LongArray.arrayRef(startIndex: Int = 0, endIndex: Int = this.size): LongArrayRef {
            return LongArrayRefImp(this, startIndex, endIndex)
        }

        private data class LongArrayRefImp(
            override val source: LongArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : LongArrayRef, Serializable {

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
 * Float array reference, refers to the source array with start index inclusive and end index exclusive.
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
     * Returns elements at [index] of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Float

    /**
     * Sets elements [value] at [index] of this reference, returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Float): Float

    /**
     * Returns array reference of sub-range of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): FloatArrayRef

    /**
     * Returns copy of content of this reference.
     */
    fun copy(): FloatArray

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: FloatArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this reference into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: FloatArray, startIndex: Int): Int

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: FloatArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        /**
         * Returns array reference of [this].
         */
        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun FloatArray.arrayRef(startIndex: Int = 0, endIndex: Int = this.size): FloatArrayRef {
            return FloatArrayRefImp(this, startIndex, endIndex)
        }

        private data class FloatArrayRefImp(
            override val source: FloatArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : FloatArrayRef, Serializable {

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
 * Double array reference, refers to the source array with start index inclusive and end index exclusive.
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
     * Returns elements at [index] of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun get(index: Int): Double

    /**
     * Sets elements [value] at [index] of this reference, returns old value.
     */
    @Throws(IndexOutOfBoundsException::class)
    operator fun set(index: Int, value: Double): Double

    /**
     * Returns array reference of sub-range of this reference.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun subRef(startIndex: Int, endIndex: Int): DoubleArrayRef

    /**
     * Returns copy of content of this reference.
     */
    fun copy(): DoubleArray

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: DoubleArray): Int {
        return copyTo(dest, 0)
    }

    /**
     * Copies content of this reference into [dest] from [startIndex], returns copied size.
     */
    @Throws(IndexOutOfBoundsException::class)
    fun copyTo(dest: DoubleArray, startIndex: Int): Int

    /**
     * Copies content of this reference into [dest], returns copied size.
     */
    fun copyTo(dest: DoubleArrayRef): Int {
        return copyTo(dest.source, dest.startIndex)
    }

    companion object {

        /**
         * Returns array reference of [this].
         */
        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun DoubleArray.arrayRef(startIndex: Int = 0, endIndex: Int = this.size): DoubleArrayRef {
            return DoubleArrayRefImp(this, startIndex, endIndex)
        }

        private data class DoubleArrayRefImp(
            override val source: DoubleArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : DoubleArrayRef, Serializable {

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