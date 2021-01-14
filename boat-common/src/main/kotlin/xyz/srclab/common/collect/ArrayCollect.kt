@file:JvmName("ArrayCollect")
@file:JvmMultifileClass

package xyz.srclab.common.collect

import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.loadClassOrThrow
import xyz.srclab.common.jvm.toJvmDescriptor
import xyz.srclab.common.reflect.rawClass
import xyz.srclab.common.reflect.upperClass
import java.lang.reflect.*
import java.util.*
import kotlin.collections.joinTo as joinToKt
import kotlin.collections.joinToString as joinToStringKt

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

fun Array<out Any?>.toStringArray(): Array<String> {
    if (this.javaClass == Array<String>::class.java) {
        return this.asAny()
    }
    val result = arrayOfNulls<String>(this.size)
    for (i in this.indices) {
        result[i] = this[i].toString()
    }
    return result.asAny()
}

fun Array<out Any?>.toStringOrNullArray(): Array<String?> {
    if (this.javaClass == Array<String>::class.java) {
        return this.asAny()
    }
    val result = arrayOfNulls<String>(this.size)
    for (i in this.indices) {
        result[i] = if (this[i] === null) null else this[i].toString()
    }
    return result.asAny()
}

fun Any?.toString(): String {
    return toStringKt()
}

fun Any?.anyOrArrayToString(): String {
    return when (this) {
        null -> toStringKt()
        is Array<*> -> Arrays.toString(this)
        is BooleanArray -> Arrays.toString(this)
        is ByteArray -> Arrays.toString(this)
        is ShortArray -> Arrays.toString(this)
        is CharArray -> Arrays.toString(this)
        is IntArray -> Arrays.toString(this)
        is LongArray -> Arrays.toString(this)
        is FloatArray -> Arrays.toString(this)
        is DoubleArray -> Arrays.toString(this)
        else -> toString()
    }
}

fun Any?.anyOrArrayDeepToString(): String {
    return when (this) {
        null -> toStringKt()
        is Array<*> -> Arrays.deepToString(this)
        is BooleanArray -> Arrays.toString(this)
        is ByteArray -> Arrays.toString(this)
        is ShortArray -> Arrays.toString(this)
        is CharArray -> Arrays.toString(this)
        is IntArray -> Arrays.toString(this)
        is LongArray -> Arrays.toString(this)
        is FloatArray -> Arrays.toString(this)
        is DoubleArray -> Arrays.toString(this)
        else -> toString()
    }
}

fun <A> newArray(vararg elements: A): Array<A> {
    return elements.asAny()
}

fun Class<*>.arrayClass(): Class<*> {
    val jvmName = this.toJvmDescriptor()
    return "[${jvmName.replace("/", ".")}".loadClassOrThrow<Any?>()
}

fun <T> GenericArrayType.rawComponentType(): Class<T> {
    val componentType = when (val genericComponentType = this.genericComponentType) {
        is Class<*> -> genericComponentType
        is ParameterizedType -> genericComponentType.rawClass
        is GenericArrayType -> genericComponentType.rawComponentType()
        is TypeVariable<*> -> genericComponentType.upperClass
        is WildcardType -> genericComponentType.upperClass
        else -> throw IllegalArgumentException("Unknown generic component type: $genericComponentType")
    }
    return componentType.asAny()
}

fun <A> Class<*>.componentTypeToArray(length: Int): A {
    return java.lang.reflect.Array.newInstance(this, length).asAny()
}

fun <A> Class<*>.arrayTypeToArray(length: Int): A {
    return java.lang.reflect.Array.newInstance(this.componentType, length).asAny()
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

@JvmOverloads
fun Any.arrayJoinToString(
    separator: CharSequence = ", ",
    transform: ((Any?) -> CharSequence)? = null
): String {
    return this.arrayJoinToString0(separator = separator, transform = transform)
}

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
        else -> throw IllegalArgumentException("Not an array: $this")
    }
}

@JvmOverloads
fun <A : Appendable> Any.arrayJoinTo(
    buffer: A,
    separator: CharSequence = ", ",
    transform: ((Any?) -> CharSequence)? = null
): A {
    return this.arrayJoinTo0(buffer = buffer, separator = separator, transform = transform)
}

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

fun <A : Appendable> Any.arrayJoinTo0(
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
        else -> throw IllegalArgumentException("Not an array: $this")
    }
}