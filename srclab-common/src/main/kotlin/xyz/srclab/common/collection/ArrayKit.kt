@file:JvmName("ArrayKit")
@file:JvmMultifileClass

package xyz.srclab.common.collection

import xyz.srclab.common.base.asAny
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.GenericArrayType
import java.lang.reflect.Type

val Type.isArray: Boolean
    @JvmName("isArray") get() {
        return when (this) {
            is GenericArrayType -> true
            else -> this.rawClass.isArray
        }
    }

val Type.componentType: Type
    get() {
        return when (this) {
            is GenericArrayType -> this.genericComponentType
            else -> this.rawClass.componentType
        }
    }

fun <A> Class<*>.componentTypeToArrayInstance(length: Int): A {
    return java.lang.reflect.Array.newInstance(this, length).asAny()
}

fun <A> Class<*>.arrayTypeToArrayInstance(length: Int): A {
    return java.lang.reflect.Array.newInstance(this.componentType, length).asAny()
}

fun <T> Array<T>.asList(): MutableList<T> {
    return java.util.Arrays.asList(*this)
}

fun BooleanArray.asList(): MutableList<Boolean> {
    val array = this
    val bridge = object : ArrayBridge<Boolean> {
        override val size: Int = array.size
        override fun get(index: Int): Boolean {
            return array[index]
        }

        override fun set(index: Int, element: Boolean) {
            array[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

fun ByteArray.asList(): MutableList<Byte> {
    val array = this
    val bridge = object : ArrayBridge<Byte> {
        override val size: Int = array.size
        override fun get(index: Int): Byte {
            return array[index]
        }

        override fun set(index: Int, element: Byte) {
            array[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

fun ShortArray.asList(): MutableList<Short> {
    val array = this
    val bridge = object : ArrayBridge<Short> {
        override val size: Int = array.size
        override fun get(index: Int): Short {
            return array[index]
        }

        override fun set(index: Int, element: Short) {
            array[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

fun CharArray.asList(): MutableList<Char> {
    val array = this
    val bridge = object : ArrayBridge<Char> {
        override val size: Int = array.size
        override fun get(index: Int): Char {
            return array[index]
        }

        override fun set(index: Int, element: Char) {
            array[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

fun IntArray.asList(): MutableList<Int> {
    val array = this
    val bridge = object : ArrayBridge<Int> {
        override val size: Int = array.size
        override fun get(index: Int): Int {
            return array[index]
        }

        override fun set(index: Int, element: Int) {
            array[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

fun LongArray.asList(): MutableList<Long> {
    val array = this
    val bridge = object : ArrayBridge<Long> {
        override val size: Int = array.size
        override fun get(index: Int): Long {
            return array[index]
        }

        override fun set(index: Int, element: Long) {
            array[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

fun FloatArray.asList(): MutableList<Float> {
    val array = this
    val bridge = object : ArrayBridge<Float> {
        override val size: Int = array.size
        override fun get(index: Int): Float {
            return array[index]
        }

        override fun set(index: Int, element: Float) {
            array[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}

fun DoubleArray.asList(): MutableList<Double> {
    val array = this
    val bridge = object : ArrayBridge<Double> {
        override val size: Int = array.size
        override fun get(index: Int): Double {
            return array[index]
        }

        override fun set(index: Int, element: Double) {
            array[index] = element
        }
    }
    return ArrayBridgeList(bridge)
}