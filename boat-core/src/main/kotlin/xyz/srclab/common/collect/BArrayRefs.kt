package xyz.srclab.common.collect

/**
 * Base interface for ref of array.
 *
 * @param A type of array
 * @param T type of array's components
 */
interface BArrayRefBase<A : Any, T> {

    /**
     * Returns source array.
     */
    val array: A

    val startIndex: Int
    val endIndex: Int
    val offset: Int get() = startIndex
    val length: Int get() = endIndex - startIndex

    operator fun get(index: Int): T

    operator fun set(index: Int, value: T)

    /**
     * Returns a new array which is a copy of current range.
     */
    fun copyOfRange(): A

    companion object {
    }
}

open class BArrayRef<T>(
    override val array: Array<T>,
    override val startIndex: Int = 0,
    override val endIndex: Int = array.size
) : BArrayRefBase<Array<T>, T> {

    override fun get(index: Int): T {
        return array[startIndex + index]
    }

    override fun set(index: Int, value: T) {
        array[startIndex + index] = value
    }

    override fun copyOfRange(): Array<T> {
        return array.copyOfRange(startIndex, endIndex)
    }
}

open class BBooleanArrayRef(
    override val array: BooleanArray,
    override val startIndex: Int = 0,
    override val endIndex: Int = array.size
) : BArrayRefBase<BooleanArray, Boolean> {

    override fun get(index: Int): Boolean {
        return array[startIndex + index]
    }

    override fun set(index: Int, value: Boolean) {
        array[startIndex + index] = value
    }

    override fun copyOfRange(): BooleanArray {
        return array.copyOfRange(startIndex, endIndex)
    }
}

open class BByteArrayRef(
    override val array: ByteArray,
    override val startIndex: Int = 0,
    override val endIndex: Int = array.size
) : BArrayRefBase<ByteArray, Byte> {

    override fun get(index: Int): Byte {
        return array[startIndex + index]
    }

    override fun set(index: Int, value: Byte) {
        array[startIndex + index] = value
    }

    override fun copyOfRange(): ByteArray {
        return array.copyOfRange(startIndex, endIndex)
    }
}

open class BShortArrayRef(
    override val array: ShortArray,
    override val startIndex: Int = 0,
    override val endIndex: Int = array.size
) : BArrayRefBase<ShortArray, Short> {

    override fun get(index: Int): Short {
        return array[startIndex + index]
    }

    override fun set(index: Int, value: Short) {
        array[startIndex + index] = value
    }

    override fun copyOfRange(): ShortArray {
        return array.copyOfRange(startIndex, endIndex)
    }
}

open class BCharArrayRef(
    override val array: CharArray,
    override val startIndex: Int = 0,
    override val endIndex: Int = array.size
) : BArrayRefBase<CharArray, Char> {

    override fun get(index: Int): Char {
        return array[startIndex + index]
    }

    override fun set(index: Int, value: Char) {
        array[startIndex + index] = value
    }

    override fun copyOfRange(): CharArray {
        return array.copyOfRange(startIndex, endIndex)
    }
}

open class BIntArrayRef(
    override val array: IntArray,
    override val startIndex: Int = 0,
    override val endIndex: Int = array.size
) : BArrayRefBase<IntArray, Int> {

    override fun get(index: Int): Int {
        return array[startIndex + index]
    }

    override fun set(index: Int, value: Int) {
        array[startIndex + index] = value
    }

    override fun copyOfRange(): IntArray {
        return array.copyOfRange(startIndex, endIndex)
    }
}

open class BLongArrayRef(
    override val array: LongArray,
    override val startIndex: Int = 0,
    override val endIndex: Int = array.size
) : BArrayRefBase<LongArray, Long> {

    override fun get(index: Int): Long {
        return array[startIndex + index]
    }

    override fun set(index: Int, value: Long) {
        array[startIndex + index] = value
    }

    override fun copyOfRange(): LongArray {
        return array.copyOfRange(startIndex, endIndex)
    }
}

open class BFloatArrayRef(
    override val array: FloatArray,
    override val startIndex: Int = 0,
    override val endIndex: Int = array.size
) : BArrayRefBase<FloatArray, Float> {

    override fun get(index: Int): Float {
        return array[startIndex + index]
    }

    override fun set(index: Int, value: Float) {
        array[startIndex + index] = value
    }

    override fun copyOfRange(): FloatArray {
        return array.copyOfRange(startIndex, endIndex)
    }
}

open class BDoubleArrayRef(
    override val array: DoubleArray,
    override val startIndex: Int = 0,
    override val endIndex: Int = array.size
) : BArrayRefBase<DoubleArray, Double> {

    override fun get(index: Int): Double {
        return array[startIndex + index]
    }

    override fun set(index: Int, value: Double) {
        array[startIndex + index] = value
    }

    override fun copyOfRange(): DoubleArray {
        return array.copyOfRange(startIndex, endIndex)
    }
}