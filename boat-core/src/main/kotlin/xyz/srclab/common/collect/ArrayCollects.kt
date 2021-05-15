@file:JvmName("ArrayCollects")
@file:JvmMultifileClass

package xyz.srclab.common.collect

import xyz.srclab.common.lang.asAny
import kotlin.collections.joinTo as joinToKt
import kotlin.collections.joinToString as joinToStringKt

private const val NOT_ARRAY_TYPE_PREFIX = "Not a array type"

fun <T> newArray(vararg elements: T): Array<T> {
    return elements.asAny()
}

fun <T> Class<T>.newArray(length: Int): Array<T> {
    return java.lang.reflect.Array.newInstance(this, length).asAny()
}

fun <A> Class<*>.newArray(vararg dimensions: Int): A {
    return java.lang.reflect.Array.newInstance(this, *dimensions).asAny()
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

fun <T> Any.arrayAsList(): MutableList<T> {
    val result = this.arrayAsListOrNull<T>()
    if (result === null) {
        throw throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX: ${this.javaClass}")
    }
    return result
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
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((Any?) -> CharSequence)? = null
): String {
    return this.arrayJoinToString0(separator, prefix, postfix, limit, truncated, transform)
}

private fun Any.arrayJoinToString0(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((Any?) -> CharSequence)? = null
): String {
    return when (this) {
        is Array<*> -> joinToStringKt(separator, prefix, postfix, limit, truncated, transform)
        is BooleanArray -> joinToStringKt(separator, prefix, postfix, limit, truncated, transform)
        is ByteArray -> joinToStringKt(separator, prefix, postfix, limit, truncated, transform)
        is ShortArray -> joinToStringKt(separator, prefix, postfix, limit, truncated, transform)
        is CharArray -> joinToStringKt(separator, prefix, postfix, limit, truncated, transform)
        is IntArray -> joinToStringKt(separator, prefix, postfix, limit, truncated, transform)
        is LongArray -> joinToStringKt(separator, prefix, postfix, limit, truncated, transform)
        is FloatArray -> joinToStringKt(separator, prefix, postfix, limit, truncated, transform)
        is DoubleArray -> joinToStringKt(separator, prefix, postfix, limit, truncated, transform)
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
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((Any?) -> CharSequence)? = null
): A {
    return this.arrayJoinTo0(buffer, separator, prefix, postfix, limit, truncated, transform)
}

private fun <A : Appendable> Any.arrayJoinTo0(
    buffer: A,
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((Any?) -> CharSequence)? = null
): A {
    return when (this) {
        is Array<*> -> joinToKt(buffer, separator, prefix, postfix, limit, truncated, transform)
        is BooleanArray -> joinToKt(buffer, separator, prefix, postfix, limit, truncated, transform)
        is ByteArray -> joinToKt(buffer, separator, prefix, postfix, limit, truncated, transform)
        is ShortArray -> joinToKt(buffer, separator, prefix, postfix, limit, truncated, transform)
        is CharArray -> joinToKt(buffer, separator, prefix, postfix, limit, truncated, transform)
        is IntArray -> joinToKt(buffer, separator, prefix, postfix, limit, truncated, transform)
        is LongArray -> joinToKt(buffer, separator, prefix, postfix, limit, truncated, transform)
        is FloatArray -> joinToKt(buffer, separator, prefix, postfix, limit, truncated, transform)
        is DoubleArray -> joinToKt(buffer, separator, prefix, postfix, limit, truncated, transform)
        else -> throw IllegalArgumentException("$NOT_ARRAY_TYPE_PREFIX: ${this.javaClass}")
    }
}

@JvmName("toStringArray")
fun Any.arrayToStringArray(): Array<String> {
    return arrayAsList<Any?>().map { it.toString() }.toArray(String::class.java)
}

@JvmName("toStringOrNullArray")
fun Any.arrayToStringOrNullArray(): Array<String?> {
    return arrayAsList<Any?>().map { it?.toString() }.toArray(String::class.java)
}