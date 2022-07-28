package xyz.srclab.common.collect

import xyz.srclab.common.defaultSerialVersion
import java.io.Serializable

/**
 * [MutableList] to wrap the [array].
 */
class ArrayWrapper<T>(
    private val array: Array<T>,
) : AbstractMutableList<T>(), RandomAccess, Serializable {

    override val size: Int get() = array.size

    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: T): Boolean = array.contains(element)
    override fun get(index: Int): T = array[index]
    override fun set(index: Int, element: T): T = array[index].let { array[index] = element;it }
    override fun indexOf(element: T): Int = array.indexOf(element)
    override fun lastIndexOf(element: T): Int = array.lastIndexOf(element)
    override fun add(index: Int, element: T) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): T = throw UnsupportedOperationException()

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}

/**
 * [MutableList] to wrap the [array].
 */
class BooleanArrayWrapper(
    private val array: BooleanArray,
) : AbstractMutableList<Boolean>(), RandomAccess, Serializable {

    override val size: Int get() = array.size

    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: Boolean): Boolean = array.contains(element)
    override fun get(index: Int): Boolean = array[index]
    override fun set(index: Int, element: Boolean): Boolean = array[index].let { array[index] = element;it }
    override fun indexOf(element: Boolean): Int = array.indexOf(element)
    override fun lastIndexOf(element: Boolean): Int = array.lastIndexOf(element)
    override fun add(index: Int, element: Boolean) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Boolean = throw UnsupportedOperationException()

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}

/**
 * [MutableList] to wrap the [array].
 */
class ByteArrayWrapper(
    private val array: ByteArray,
) : AbstractMutableList<Byte>(), RandomAccess, Serializable {

    override val size: Int get() = array.size

    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: Byte): Boolean = array.contains(element)
    override fun get(index: Int): Byte = array[index]
    override fun set(index: Int, element: Byte): Byte = array[index].let { array[index] = element;it }
    override fun indexOf(element: Byte): Int = array.indexOf(element)
    override fun lastIndexOf(element: Byte): Int = array.lastIndexOf(element)
    override fun add(index: Int, element: Byte) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Byte = throw UnsupportedOperationException()

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}

/**
 * [MutableList] to wrap the [array].
 */
class ShortArrayWrapper(
    private val array: ShortArray,
) : AbstractMutableList<Short>(), RandomAccess, Serializable {

    override val size: Int get() = array.size

    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: Short): Boolean = array.contains(element)
    override fun get(index: Int): Short = array[index]
    override fun set(index: Int, element: Short): Short = array[index].let { array[index] = element;it }
    override fun indexOf(element: Short): Int = array.indexOf(element)
    override fun lastIndexOf(element: Short): Int = array.lastIndexOf(element)
    override fun add(index: Int, element: Short) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Short = throw UnsupportedOperationException()

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}

/**
 * [MutableList] to wrap the [array].
 */
class CharArrayWrapper(
    private val array: CharArray,
) : AbstractMutableList<Char>(), RandomAccess, Serializable {

    override val size: Int get() = array.size

    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: Char): Boolean = array.contains(element)
    override fun get(index: Int): Char = array[index]
    override fun set(index: Int, element: Char): Char = array[index].let { array[index] = element;it }
    override fun indexOf(element: Char): Int = array.indexOf(element)
    override fun lastIndexOf(element: Char): Int = array.lastIndexOf(element)
    override fun add(index: Int, element: Char) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Char = throw UnsupportedOperationException()

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}

/**
 * [MutableList] to wrap the [array].
 */
class IntArrayWrapper(
    private val array: IntArray,
) : AbstractMutableList<Int>(), RandomAccess, Serializable {

    override val size: Int get() = array.size

    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: Int): Boolean = array.contains(element)
    override fun get(index: Int): Int = array[index]
    override fun set(index: Int, element: Int): Int = array[index].let { array[index] = element;it }
    override fun indexOf(element: Int): Int = array.indexOf(element)
    override fun lastIndexOf(element: Int): Int = array.lastIndexOf(element)
    override fun add(index: Int, element: Int) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Int = throw UnsupportedOperationException()

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}

/**
 * [MutableList] to wrap the [array].
 */
class LongArrayWrapper(
    private val array: LongArray,
) : AbstractMutableList<Long>(), RandomAccess, Serializable {

    override val size: Int get() = array.size

    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: Long): Boolean = array.contains(element)
    override fun get(index: Int): Long = array[index]
    override fun set(index: Int, element: Long): Long = array[index].let { array[index] = element;it }
    override fun indexOf(element: Long): Int = array.indexOf(element)
    override fun lastIndexOf(element: Long): Int = array.lastIndexOf(element)
    override fun add(index: Int, element: Long) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Long = throw UnsupportedOperationException()

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}

/**
 * [MutableList] to wrap the [array].
 */
class FloatArrayWrapper(
    private val array: FloatArray,
) : AbstractMutableList<Float>(), RandomAccess, Serializable {

    override val size: Int get() = array.size

    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: Float): Boolean = array.any { it == element }
    override fun get(index: Int): Float = array[index]
    override fun set(index: Int, element: Float): Float = array[index].let { array[index] = element;it }
    override fun indexOf(element: Float): Int = array.indexOfFirst { it == element }
    override fun lastIndexOf(element: Float): Int = array.indexOfLast { it == element }
    override fun add(index: Int, element: Float) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Float = throw UnsupportedOperationException()

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}

/**
 * [MutableList] to wrap the [array].
 */
class DoubleArrayWrapper(
    private val array: DoubleArray,
) : AbstractMutableList<Double>(), RandomAccess, Serializable {

    override val size: Int get() = array.size

    override fun isEmpty(): Boolean = array.isEmpty()
    override fun contains(element: Double): Boolean = array.any { it == element }
    override fun get(index: Int): Double = array[index]
    override fun set(index: Int, element: Double): Double = array[index].let { array[index] = element;it }
    override fun indexOf(element: Double): Int = array.indexOfFirst { it == element }
    override fun lastIndexOf(element: Double): Int = array.indexOfLast { it == element }
    override fun add(index: Int, element: Double) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): Double = throw UnsupportedOperationException()

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}