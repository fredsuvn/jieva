@file:JvmName("Chars")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.nio.charset.Charset
import java.util.*
import kotlin.collections.joinToString as joinToStringKt
import kotlin.toString as toStringKt

fun CharArray.toChars(): String {
    return String(this)
}

fun ByteArray.toChars(charset: String): String {
    return toChars(Charset.forName(charset))
}

@JvmOverloads
fun ByteArray.toChars(charset: Charset = Defaults.charset): String {
    return String(this, charset)
}

fun CharArray.toBytes(charset: String): ByteArray {
    return toBytes(Charset.forName(charset))
}

@JvmOverloads
fun CharArray.toBytes(charset: Charset = Defaults.charset): ByteArray {
    return toChars().toByteArray(charset)
}

fun CharSequence.toBytes(charset: String): ByteArray {
    return toBytes(Charset.forName(charset))
}

@JvmOverloads
fun CharSequence.toBytes(charset: Charset = Defaults.charset): ByteArray {
    return toString().toByteArray(charset)
}

fun Any?.toString(): String {
    return toStringKt()
}

fun Any?.arrayToString(): String {
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

fun Any?.arrayDeepToString(): String {
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

fun Any.joinToString(
    separator: CharSequence = ", ",
    indicator: CharSequence = ": ",
    prefix: CharSequence = "",
    postfix: CharSequence = "",
    limit: Int = -1,
    truncated: CharSequence = "...",
    transform: ((Any?) -> CharSequence)? = null
): String {
    if (this is Iterable<*>) {
        return this.joinToStringKt(separator, prefix, postfix, limit, truncated, transform)
    }
    if (this is Map<*, *>) {
        return this.entries.map { entry ->
            entry.joinToString(separator, indicator, prefix, postfix, limit, truncated, transform)
        }.j
    }
}

interface JoinStyle {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val separator: CharSequence
        @JvmName("separator") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val indicator: CharSequence
        @JvmName("indicator") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val objectPrefix: CharSequence
        @JvmName("objectPrefix") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val objectSuffix: CharSequence
        @JvmName("objectSuffix") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val listPrefix: CharSequence
        @JvmName("listPrefix") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val listSuffix: CharSequence
        @JvmName("listSuffix") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val limit: Int
        @JvmName("limit") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val truncated: CharSequence
        @JvmName("truncated") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val transform: ((Any?) -> CharSequence)?
        @JvmName("transform") get

    companion object {

        val DEFAULT: JoinStyle = object : JoinStyle {
            override val separator: CharSequence
                get() = ", "
            override val indicator: CharSequence
                get() = ": "
            override val objectPrefix: CharSequence
                get() = "{"
            override val objectSuffix: CharSequence
                get() = "}"
            override val listPrefix: CharSequence
                get() = "["
            override val listSuffix: CharSequence
                get() = "]"
            override val limit: Int
                get() = -1
            override val truncated: CharSequence
                get() = "..."
            override val transform: ((Any?) -> CharSequence)?
                get() = null
        }

        val DEFAULT: JoinStyle = object : JoinStyle {
            override val separator: CharSequence
                get() = ", "
            override val indicator: CharSequence
                get() = ": "
            override val objectPrefix: CharSequence
                get() = "{"
            override val objectSuffix: CharSequence
                get() = "}"
            override val listPrefix: CharSequence
                get() = "["
            override val listSuffix: CharSequence
                get() = "]"
            override val limit: Int
                get() = -1
            override val truncated: CharSequence
                get() = "..."
            override val transform: ((Any?) -> CharSequence)?
                get() = null
        }
    }
}