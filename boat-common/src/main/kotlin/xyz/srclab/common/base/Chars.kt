@file:JvmName("Chars")
@file:JvmMultifileClass

package xyz.srclab.common.base

import org.apache.commons.lang3.StringUtils
import java.nio.charset.Charset
import java.util.*
import kotlin.toString as toStringKt

fun CharSequence?.isNumeric(): Boolean {
    return StringUtils.isNumeric(this)
}

fun CharSequence?.isNumericSpace(): Boolean {
    return StringUtils.isNumericSpace(this)
}

fun CharSequence?.isWhitespace(): Boolean {
    return StringUtils.isWhitespace(this)
}

fun CharSequence.toCharSet(): Charset {
    return Charset.forName(this.toString())
}

fun CharArray.toChars(): String {
    return String(this)
}

fun ByteArray.toChars(charset: CharSequence): String {
    return toChars(charset.toCharSet())
}

@JvmOverloads
fun ByteArray.toChars(charset: Charset = Defaults.charset): String {
    return String(this, charset)
}

fun CharArray.toBytes(charset: CharSequence): ByteArray {
    return toBytes(charset.toCharSet())
}

@JvmOverloads
fun CharArray.toBytes(charset: Charset = Defaults.charset): ByteArray {
    return toChars().toByteArray(charset)
}

fun CharSequence.toBytes(charset: CharSequence): ByteArray {
    return toBytes(charset.toCharSet())
}

@JvmOverloads
fun CharSequence.toBytes(charset: Charset = Defaults.charset): ByteArray {
    return toString().toByteArray(charset)
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