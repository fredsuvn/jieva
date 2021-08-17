@file:JvmName("Chars")

package xyz.srclab.common.lang

import org.apache.commons.lang3.StringUtils
import java.nio.charset.Charset

fun CharSequence?.isNumeric(): Boolean {
    return StringUtils.isNumeric(this)
}

fun CharSequence?.isNumericSpace(): Boolean {
    return StringUtils.isNumericSpace(this)
}

fun CharSequence?.isWhitespace(): Boolean {
    return StringUtils.isWhitespace(this)
}

/**
 * Note Minimum abbreviation width is 4.
 */
@JvmOverloads
fun CharSequence?.ellipses(maxLength: Int, offset: Int = 0): String {
    return StringUtils.abbreviate(this.toString(), offset, maxLength)
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

fun CharSequence.capitalize(): String {
    return StringUtils.capitalize(this.toString())
}

fun CharSequence.uncapitalize(): String {
    return StringUtils.uncapitalize(this.toString())
}

fun Array<out Any?>.toStringArray(): Array<String> {
    return this.map { it.toString() }.toTypedArray()
}

fun Array<out Any?>.toNullableStringArray(): Array<String?> {
    return this.map { it?.toString() }.toTypedArray()
}