@file:JvmName("Chars")
@file:JvmMultifileClass

package xyz.srclab.common.base

import org.apache.commons.lang3.StringUtils
import java.nio.charset.Charset
import java.util.*
import kotlin.toString as toStringKt

fun CharSequence?.isWhitespace(): Boolean {
    return StringUtils.isWhitespace(this)
}

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