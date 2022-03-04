package xyz.srclab.common.collect

import xyz.srclab.common.base.defaultSerialVersion
import java.io.Serializable

/**
 * Bridge between array and [MutableList].
 */
class ArrayBridgeList<T>(
    private val arrayBridge: ArrayBridge<T>,
) : AbstractMutableList<T>(), RandomAccess, Serializable {

    override val size: Int get() = arrayBridge.size

    override fun isEmpty(): Boolean = arrayBridge.isEmpty()
    override fun contains(element: T): Boolean = arrayBridge.contains(element)
    override fun get(index: Int): T = arrayBridge[index]
    override fun set(index: Int, element: T): T = arrayBridge[index].let { arrayBridge[index] = element;it }
    override fun indexOf(element: T): Int = arrayBridge.indexOf(element)
    override fun lastIndexOf(element: T): Int = arrayBridge.lastIndexOf(element)
    override fun add(index: Int, element: T) = throw UnsupportedOperationException()
    override fun removeAt(index: Int): T = throw UnsupportedOperationException()

    companion object {
        private val serialVersionUID: Long = defaultSerialVersion()
    }
}

/**
 * Provides base info for [ArrayBridgeList].
 */
// Fuck un-supporting-primitive-generic-type! DAMN!
interface ArrayBridge<T> {

    val size: Int
    fun isEmpty(): Boolean
    fun contains(element: T): Boolean
    operator fun get(index: Int): T
    operator fun set(index: Int, element: T): T
    fun indexOf(element: T): Int
    fun lastIndexOf(element: T): Int

    class OfArray<T>(private val array: Array<T>) : ArrayBridge<T>, Serializable {
        override val size: Int get() = array.size
        override fun isEmpty(): Boolean = array.isEmpty()
        override fun contains(element: T): Boolean = array.contains(element)
        override fun get(index: Int): T = array[index]
        override fun set(index: Int, element: T): T = array[index].let { array[index] = element;it }
        override fun indexOf(element: T): Int = array.indexOf(element)
        override fun lastIndexOf(element: T): Int = array.lastIndexOf(element)

        companion object {
            private val serialVersionUID: Long = defaultSerialVersion()
        }
    }

    class OfBooleanArray(private val array: BooleanArray) : ArrayBridge<Boolean>, Serializable {
        override val size: Int get() = array.size
        override fun isEmpty(): Boolean = array.isEmpty()
        override fun contains(element: Boolean): Boolean = array.contains(element)
        override fun get(index: Int): Boolean = array[index]
        override fun set(index: Int, element: Boolean): Boolean = array[index].let { array[index] = element;it }
        override fun indexOf(element: Boolean): Int = array.indexOf(element)
        override fun lastIndexOf(element: Boolean): Int = array.lastIndexOf(element)

        companion object {
            private val serialVersionUID: Long = defaultSerialVersion()
        }
    }

    class OfByteArray(private val array: ByteArray) : ArrayBridge<Byte>, Serializable {
        override val size: Int get() = array.size
        override fun isEmpty(): Boolean = array.isEmpty()
        override fun contains(element: Byte): Boolean = array.contains(element)
        override fun get(index: Int): Byte = array[index]
        override fun set(index: Int, element: Byte): Byte = array[index].let { array[index] = element;it }
        override fun indexOf(element: Byte): Int = array.indexOf(element)
        override fun lastIndexOf(element: Byte): Int = array.lastIndexOf(element)

        companion object {
            private val serialVersionUID: Long = defaultSerialVersion()
        }
    }

    class OfShortArray(private val array: ShortArray) : ArrayBridge<Short>, Serializable {
        override val size: Int get() = array.size
        override fun isEmpty(): Boolean = array.isEmpty()
        override fun contains(element: Short): Boolean = array.contains(element)
        override fun get(index: Int): Short = array[index]
        override fun set(index: Int, element: Short): Short = array[index].let { array[index] = element;it }
        override fun indexOf(element: Short): Int = array.indexOf(element)
        override fun lastIndexOf(element: Short): Int = array.lastIndexOf(element)

        companion object {
            private val serialVersionUID: Long = defaultSerialVersion()
        }
    }

    class OfCharArray(private val array: CharArray) : ArrayBridge<Char>, Serializable {
        override val size: Int get() = array.size
        override fun isEmpty(): Boolean = array.isEmpty()
        override fun contains(element: Char): Boolean = array.contains(element)
        override fun get(index: Int): Char = array[index]
        override fun set(index: Int, element: Char): Char = array[index].let { array[index] = element;it }
        override fun indexOf(element: Char): Int = array.indexOf(element)
        override fun lastIndexOf(element: Char): Int = array.lastIndexOf(element)

        companion object {
            private val serialVersionUID: Long = defaultSerialVersion()
        }
    }

    class OfIntArray(private val array: IntArray) : ArrayBridge<Int>, Serializable {
        override val size: Int get() = array.size
        override fun isEmpty(): Boolean = array.isEmpty()
        override fun contains(element: Int): Boolean = array.contains(element)
        override fun get(index: Int): Int = array[index]
        override fun set(index: Int, element: Int): Int = array[index].let { array[index] = element;it }
        override fun indexOf(element: Int): Int = array.indexOf(element)
        override fun lastIndexOf(element: Int): Int = array.lastIndexOf(element)

        companion object {
            private val serialVersionUID: Long = defaultSerialVersion()
        }
    }

    class OfLongArray(private val array: LongArray) : ArrayBridge<Long>, Serializable {
        override val size: Int get() = array.size
        override fun isEmpty(): Boolean = array.isEmpty()
        override fun contains(element: Long): Boolean = array.contains(element)
        override fun get(index: Int): Long = array[index]
        override fun set(index: Int, element: Long): Long = array[index].let { array[index] = element;it }
        override fun indexOf(element: Long): Int = array.indexOf(element)
        override fun lastIndexOf(element: Long): Int = array.lastIndexOf(element)

        companion object {
            private val serialVersionUID: Long = defaultSerialVersion()
        }
    }

    class OfFloatArray(private val array: FloatArray) : ArrayBridge<Float>, Serializable {
        override val size: Int get() = array.size
        override fun isEmpty(): Boolean = array.isEmpty()
        override fun contains(element: Float): Boolean = array.any { it == element }
        override fun get(index: Int): Float = array[index]
        override fun set(index: Int, element: Float): Float = array[index].let { array[index] = element;it }
        override fun indexOf(element: Float): Int = array.indexOfFirst { it == element }
        override fun lastIndexOf(element: Float): Int = array.indexOfLast { it == element }

        companion object {
            private val serialVersionUID: Long = defaultSerialVersion()
        }
    }

    class OfDoubleArray(private val array: DoubleArray) : ArrayBridge<Double>, Serializable {
        override val size: Int get() = array.size
        override fun isEmpty(): Boolean = array.isEmpty()
        override fun contains(element: Double): Boolean = array.any { it == element }
        override fun get(index: Int): Double = array[index]
        override fun set(index: Int, element: Double): Double = array[index].let { array[index] = element;it }
        override fun indexOf(element: Double): Int = array.indexOfFirst { it == element }
        override fun lastIndexOf(element: Double): Int = array.indexOfLast { it == element }

        companion object {
            private val serialVersionUID: Long = defaultSerialVersion()
        }
    }

    companion object {

        @JvmStatic
        fun <T> Array<T>.toArrayBridge(): ArrayBridge<T> {
            return OfArray(this)
        }

        @JvmStatic
        fun BooleanArray.toArrayBridge(): ArrayBridge<Boolean> {
            return OfBooleanArray(this)
        }

        @JvmStatic
        fun ByteArray.toArrayBridge(): ArrayBridge<Byte> {
            return OfByteArray(this)
        }

        @JvmStatic
        fun ShortArray.toArrayBridge(): ArrayBridge<Short> {
            return OfShortArray(this)
        }

        @JvmStatic
        fun CharArray.toArrayBridge(): ArrayBridge<Char> {
            return OfCharArray(this)
        }

        @JvmStatic
        fun IntArray.toArrayBridge(): ArrayBridge<Int> {
            return OfIntArray(this)
        }

        @JvmStatic
        fun LongArray.toArrayBridge(): ArrayBridge<Long> {
            return OfLongArray(this)
        }

        @JvmStatic
        fun FloatArray.toArrayBridge(): ArrayBridge<Float> {
            return OfFloatArray(this)
        }

        @JvmStatic
        fun DoubleArray.toArrayBridge(): ArrayBridge<Double> {
            return OfDoubleArray(this)
        }
    }
}