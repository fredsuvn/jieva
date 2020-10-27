@file:JvmName("ArrayOps")
@file:JvmMultifileClass

package xyz.srclab.common.collection

import xyz.srclab.common.base.asAny
import java.lang.reflect.GenericArrayType
import java.lang.reflect.Type

val Type.isArray: Boolean
    @JvmName("isArray") get() {
        return when (this) {
            is Class<*> -> this.isArray
            is GenericArrayType -> true
            else -> false
        }
    }

val Type.componentType: Type?
    get() {
        return when (this) {
            is Class<*> -> this.componentType
            is GenericArrayType -> this.genericComponentType
            else -> null
        }
    }

fun <A> Class<*>.componentTypeToArray(length: Int): A {
    return java.lang.reflect.Array.newInstance(this, length).asAny()
}

fun <A> Class<*>.arrayTypeToArray(length: Int): A {
    if (!this.isArray) {
        throw IllegalArgumentException("$this is not an array type.")
    }
    return this.componentType.componentTypeToArray(length)
}

fun <T> Any.arrayAsList(): MutableList<T> {
    val result = this.arrayAsListOrNull<T>()
    if (result === null) {
        throw IllegalArgumentException("Cannot from array to MutableList: $this.")
    }
    return result
}

fun <T> Any.arrayAsListOrNull(): MutableList<T>? {
    return when (this) {
        is Array<*> -> this.asList().asAny()
        is BooleanArray -> this.asList().asAny()
        is ByteArray -> this.asList().asAny()
        is ShortArray -> this.asList().asAny()
        is CharArray -> this.asList().asAny()
        is IntArray -> this.asList().asAny()
        is LongArray -> this.asList().asAny()
        is FloatArray -> this.asList().asAny()
        is DoubleArray -> this.asList().asAny()
        else -> null
    }
}

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