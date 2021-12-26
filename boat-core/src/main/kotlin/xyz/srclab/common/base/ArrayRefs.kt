package xyz.srclab.common.base

/**
 * Base array reference interface.
 *
 * @param A type of array
 * @param R type of array ref
 */
interface ArrayRefBase<A, R : ArrayRefBase<A, R>> {

    /**
     * Start index inclusive.
     */
    val startIndex: Int

    /**
     * End index exclusive.
     */
    val endIndex: Int

    /**
     * Offset position inclusive.
     */
    val offset: Int get() = startIndex

    /**
     * Length.
     */
    val length: Int get() = endIndex - startIndex

    /**
     * Returns actual index of super-sequence, computed from given [index] which is a relative index.
     */
    fun actualIndex(index: Int) = startIndex + index

    /**
     * Returns a new array ref which refers to subsequence of current ref.
     */
    fun subArray(startIndex: Int, endIndex: Int): R

    /**
     * Copies and returns a new array of current range.
     */
    fun copyOfRange(): A
}

/**
 * Array reference.
 */
interface ArrayRef<T> : ArrayRefBase<Array<T>, ArrayRef<T>> {

    val array: Array<T>

    fun get(index: Int): T {
        index.checkIndexInBounds(0, length)
        return array[actualIndex(index)]
    }

    fun set(index: Int, value: T) {
        index.checkIndexInBounds(0, length)
        array[actualIndex(index)] = value
    }

    companion object {

        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun <T> Array<T>.asRef(startIndex: Int = 0, endIndex: Int = this.size): ArrayRef<T> {
            checkRangeInBounds(startIndex, endIndex, 0, this.size)
            return ArrayRefImpl(this, startIndex, endIndex)
        }

        @JvmName("offset")
        @JvmOverloads
        @JvmStatic
        fun <T> Array<T>.offsetRef(offset: Int = 0, length: Int = this.size): ArrayRef<T> {
            return asRef(offset, offset + length)
        }

        private class ArrayRefImpl<T>(
            override val array: Array<T>,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : ArrayRef<T> {

            override fun copyOfRange(): Array<T> {
                return array.copyOfRange(startIndex, endIndex)
            }

            override fun subArray(startIndex: Int, endIndex: Int): ArrayRef<T> {
                checkRangeInBounds(startIndex, endIndex, 0, length)
                return array.asRef(actualIndex(startIndex), actualIndex(endIndex))
            }
        }
    }
}

/**
 * Boolean array reference.
 */
interface BooleansRef : ArrayRefBase<BooleanArray, BooleansRef> {

    val array: BooleanArray

    fun get(index: Int): Boolean {
        index.checkIndexInBounds(0, length)
        return array[actualIndex(index)]
    }

    fun set(index: Int, value: Boolean) {
        index.checkIndexInBounds(0, length)
        array[actualIndex(index)] = value
    }

    companion object {

        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun BooleanArray.asRef(startIndex: Int = 0, endIndex: Int = this.size): BooleansRef {
            checkRangeInBounds(startIndex, endIndex, 0, this.size)
            return BooleansRefImpl(this, startIndex, endIndex)
        }

        @JvmName("offset")
        @JvmOverloads
        @JvmStatic
        fun BooleanArray.offsetRef(offset: Int = 0, length: Int = this.size): BooleansRef {
            return asRef(offset, offset + length)
        }

        private class BooleansRefImpl(
            override val array: BooleanArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : BooleansRef {

            override fun copyOfRange(): BooleanArray {
                return array.copyOfRange(startIndex, endIndex)
            }

            override fun subArray(startIndex: Int, endIndex: Int): BooleansRef {
                checkRangeInBounds(startIndex, endIndex, 0, length)
                return array.asRef(actualIndex(startIndex), actualIndex(endIndex))
            }
        }
    }
}

/**
 * Byte array reference.
 */
interface BytesRef : ArrayRefBase<ByteArray, BytesRef> {

    val array: ByteArray

    fun get(index: Int): Byte {
        index.checkIndexInBounds(0, length)
        return array[actualIndex(index)]
    }

    fun set(index: Int, value: Byte) {
        index.checkIndexInBounds(0, length)
        array[actualIndex(index)] = value
    }

    companion object {

        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun ByteArray.asRef(startIndex: Int = 0, endIndex: Int = this.size): BytesRef {
            checkRangeInBounds(startIndex, endIndex, 0, this.size)
            return BytesRefImpl(this, startIndex, endIndex)
        }

        @JvmName("offset")
        @JvmOverloads
        @JvmStatic
        fun ByteArray.offsetRef(offset: Int = 0, length: Int = this.size): BytesRef {
            return asRef(offset, offset + length)
        }

        private class BytesRefImpl(
            override val array: ByteArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : BytesRef {

            override fun copyOfRange(): ByteArray {
                return array.copyOfRange(startIndex, endIndex)
            }

            override fun subArray(startIndex: Int, endIndex: Int): BytesRef {
                checkRangeInBounds(startIndex, endIndex, 0, length)
                return array.asRef(actualIndex(startIndex), actualIndex(endIndex))
            }
        }
    }
}

/**
 * Char array reference.
 */
interface CharsRef : ArrayRefBase<CharArray, CharsRef> {

    override val startIndex: Int
    override val endIndex: Int
    override val length: Int

    val array: CharArray

    fun get(index: Int): Char {
        index.checkIndexInBounds(0, length)
        return array[actualIndex(index)]
    }

    fun set(index: Int, value: Char) {
        index.checkIndexInBounds(0, length)
        array[actualIndex(index)] = value
    }

    companion object {

        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun CharArray.asRef(startIndex: Int = 0, endIndex: Int = this.size): CharsRef {
            checkRangeInBounds(startIndex, endIndex, 0, this.size)
            return CharsRefImpl(this, startIndex, endIndex)
        }

        @JvmName("offset")
        @JvmOverloads
        @JvmStatic
        fun CharArray.offsetRef(offset: Int = 0, length: Int = this.size): CharsRef {
            return asRef(offset, offset + length)
        }

        private class CharsRefImpl(
            override val array: CharArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : CharsRef {
            override val length: Int = endIndex - startIndex

            override fun copyOfRange(): CharArray {
                return array.copyOfRange(startIndex, endIndex)
            }

            override fun subArray(startIndex: Int, endIndex: Int): CharsRef {
                checkRangeInBounds(startIndex, endIndex, 0, length)
                return array.asRef(actualIndex(startIndex), actualIndex(endIndex))
            }
        }
    }
}

/**
 * Short array reference.
 */
interface ShortsRef : ArrayRefBase<ShortArray, ShortsRef> {

    val array: ShortArray

    fun get(index: Int): Short {
        index.checkIndexInBounds(0, length)
        return array[actualIndex(index)]
    }

    fun set(index: Int, value: Short) {
        index.checkIndexInBounds(0, length)
        array[actualIndex(index)] = value
    }

    companion object {

        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun ShortArray.asRef(startIndex: Int = 0, endIndex: Int = this.size): ShortsRef {
            checkRangeInBounds(startIndex, endIndex, 0, this.size)
            return ShortsRefImpl(this, startIndex, endIndex)
        }

        @JvmName("offset")
        @JvmOverloads
        @JvmStatic
        fun ShortArray.offsetRef(offset: Int = 0, length: Int = this.size): ShortsRef {
            return asRef(offset, offset + length)
        }

        private class ShortsRefImpl(
            override val array: ShortArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : ShortsRef {

            override fun copyOfRange(): ShortArray {
                return array.copyOfRange(startIndex, endIndex)
            }

            override fun subArray(startIndex: Int, endIndex: Int): ShortsRef {
                checkRangeInBounds(startIndex, endIndex, 0, length)
                return array.asRef(actualIndex(startIndex), actualIndex(endIndex))
            }
        }
    }
}

/**
 * Int array reference.
 */
interface IntsRef : ArrayRefBase<IntArray, IntsRef> {

    val array: IntArray

    fun get(index: Int): Int {
        index.checkIndexInBounds(0, length)
        return array[actualIndex(index)]
    }

    fun set(index: Int, value: Int) {
        index.checkIndexInBounds(0, length)
        array[actualIndex(index)] = value
    }

    companion object {

        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun IntArray.asRef(startIndex: Int = 0, endIndex: Int = this.size): IntsRef {
            checkRangeInBounds(startIndex, endIndex, 0, this.size)
            return IntsRefImpl(this, startIndex, endIndex)
        }

        @JvmName("offset")
        @JvmOverloads
        @JvmStatic
        fun IntArray.offsetRef(offset: Int = 0, length: Int = this.size): IntsRef {
            return asRef(offset, offset + length)
        }

        private class IntsRefImpl(
            override val array: IntArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : IntsRef {

            override fun copyOfRange(): IntArray {
                return array.copyOfRange(startIndex, endIndex)
            }

            override fun subArray(startIndex: Int, endIndex: Int): IntsRef {
                checkRangeInBounds(startIndex, endIndex, 0, length)
                return array.asRef(actualIndex(startIndex), actualIndex(endIndex))
            }
        }
    }
}

/**
 * Long array reference.
 */
interface LongsRef : ArrayRefBase<LongArray, LongsRef> {

    val array: LongArray

    fun get(index: Int): Long {
        index.checkIndexInBounds(0, length)
        return array[actualIndex(index)]
    }

    fun set(index: Int, value: Long) {
        index.checkIndexInBounds(0, length)
        array[actualIndex(index)] = value
    }

    companion object {

        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun LongArray.asRef(startIndex: Int = 0, endIndex: Int = this.size): LongsRef {
            checkRangeInBounds(startIndex, endIndex, 0, this.size)
            return LongsRefImpl(this, startIndex, endIndex)
        }

        @JvmName("offset")
        @JvmOverloads
        @JvmStatic
        fun LongArray.offsetRef(offset: Int = 0, length: Int = this.size): LongsRef {
            return asRef(offset, offset + length)
        }

        private class LongsRefImpl(
            override val array: LongArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : LongsRef {

            override fun copyOfRange(): LongArray {
                return array.copyOfRange(startIndex, endIndex)
            }

            override fun subArray(startIndex: Int, endIndex: Int): LongsRef {
                checkRangeInBounds(startIndex, endIndex, 0, length)
                return array.asRef(actualIndex(startIndex), actualIndex(endIndex))
            }
        }
    }
}

/**
 * Float array reference.
 */
interface FloatsRef : ArrayRefBase<FloatArray, FloatsRef> {

    val array: FloatArray

    fun get(index: Int): Float {
        index.checkIndexInBounds(0, length)
        return array[actualIndex(index)]
    }

    fun set(index: Int, value: Float) {
        index.checkIndexInBounds(0, length)
        array[actualIndex(index)] = value
    }

    companion object {

        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun FloatArray.asRef(startIndex: Int = 0, endIndex: Int = this.size): FloatsRef {
            checkRangeInBounds(startIndex, endIndex, 0, this.size)
            return FloatsRefImpl(this, startIndex, endIndex)
        }

        @JvmName("offset")
        @JvmOverloads
        @JvmStatic
        fun FloatArray.offsetRef(offset: Int = 0, length: Int = this.size): FloatsRef {
            return asRef(offset, offset + length)
        }

        private class FloatsRefImpl(
            override val array: FloatArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : FloatsRef {

            override fun copyOfRange(): FloatArray {
                return array.copyOfRange(startIndex, endIndex)
            }

            override fun subArray(startIndex: Int, endIndex: Int): FloatsRef {
                checkRangeInBounds(startIndex, endIndex, 0, length)
                return array.asRef(actualIndex(startIndex), actualIndex(endIndex))
            }
        }
    }
}

/**
 * Double array reference.
 */
interface DoublesRef : ArrayRefBase<DoubleArray, DoublesRef> {

    val array: DoubleArray

    fun get(index: Int): Double {
        index.checkIndexInBounds(0, length)
        return array[actualIndex(index)]
    }

    fun set(index: Int, value: Double) {
        index.checkIndexInBounds(0, length)
        array[actualIndex(index)] = value
    }

    companion object {

        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun DoubleArray.asRef(startIndex: Int = 0, endIndex: Int = this.size): DoublesRef {
            checkRangeInBounds(startIndex, endIndex, 0, this.size)
            return DoublesRefImpl(this, startIndex, endIndex)
        }

        @JvmName("offset")
        @JvmOverloads
        @JvmStatic
        fun DoubleArray.offsetRef(offset: Int = 0, length: Int = this.size): DoublesRef {
            return asRef(offset, offset + length)
        }

        private class DoublesRefImpl(
            override val array: DoubleArray,
            override val startIndex: Int,
            override val endIndex: Int,
        ) : DoublesRef {

            override fun copyOfRange(): DoubleArray {
                return array.copyOfRange(startIndex, endIndex)
            }

            override fun subArray(startIndex: Int, endIndex: Int): DoublesRef {
                checkRangeInBounds(startIndex, endIndex, 0, length)
                return array.asRef(actualIndex(startIndex), actualIndex(endIndex))
            }
        }
    }
}