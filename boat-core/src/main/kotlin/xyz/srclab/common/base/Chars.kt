@file:JvmName("Chars")

package xyz.srclab.common.base

import com.google.common.base.CharMatcher
import org.apache.commons.lang3.StringUtils
import java.nio.charset.Charset

/**
 * [CharMatcher] of pattern dot: `.`
 */
@JvmField
val DOT_MATCHER: CharMatcher = CharMatcher.`is`('.')

/**
 * [CharMatcher] of pattern hyphen: `-`
 */
@JvmField
val HYPHEN_MATCHER: CharMatcher = CharMatcher.`is`('-')

/**
 * [CharMatcher] of pattern plus sign: `+`
 */
@JvmField
val PLUS_MATCHER: CharMatcher = CharMatcher.`is`('+')

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
 * Abbreviates a String using ellipses:
 *
 * ```
 * Chars.ellipses("abcdefghijklmno", 5, 1) = "bcdef..."
 * ```
 *
 * Note min [length] of abbreviated string is 4.
 */
@JvmOverloads
fun CharSequence.ellipses(length: Int, offset: Int = 0): String {
    return StringUtils.abbreviate(this.toString(), offset, length)
}

fun CharSequence.capitalize(): String {
    return StringUtils.capitalize(this.toString())
}

fun CharSequence.uncapitalize(): String {
    return StringUtils.uncapitalize(this.toString())
}

@JvmName("toString")
fun ByteArray.encodeToString(charset: CharSequence): String {
    return encodeToString(charset.toCharSet())
}

@JvmName("toString")
@JvmOverloads
fun ByteArray.encodeToString(charset: Charset = Defaults.charset): String {
    return String(this, charset)
}

fun CharSequence.toBytes(charset: CharSequence): ByteArray {
    return toBytes(charset.toCharSet())
}

@JvmOverloads
fun CharSequence.toBytes(charset: Charset = Defaults.charset): ByteArray {
    return this.toString().toByteArray(charset)
}

fun CharArray.toBytes(charset: CharSequence): ByteArray {
    return toBytes(charset.toCharSet())
}

@JvmOverloads
fun CharArray.toBytes(charset: Charset = Defaults.charset): ByteArray {
    return String(this).toByteArray(charset)
}

@JvmName("charset")
fun CharSequence.toCharSet(): Charset {
    return Charset.forName(this.toString())
}

fun Array<out Any?>.toStringArray(): Array<String> {
    return this.map { it.toString() }.toTypedArray()
}

fun Array<out Any?>.toNullableStringArray(): Array<String?> {
    return this.map { it?.toString() }.toTypedArray()
}