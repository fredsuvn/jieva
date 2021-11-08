@file:JvmName("Chars")

package xyz.srclab.common.base

import com.google.common.base.CharMatcher
import org.apache.commons.lang3.StringUtils
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/**
 * Default charset: UTF-8.
 */
@JvmField
val DEFAULT_CHARSET: Charset = StandardCharsets.UTF_8

/**
 * [CharMatcher] of pattern dot: `.`
 */
@JvmField
val DOT_MATCHER: CharMatcher = CharMatcher.`is`('.')

/**
 * [CharMatcher] of pattern space: ` `
 */
@JvmField
val SPACE_MATCHER: CharMatcher = CharMatcher.`is`(' ')

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

/**
 * [CharMatcher] of pattern `[A-Z]`
 */
@JvmField
val UPPER_CASE_MATCHER: CharMatcher = CharMatcher.inRange('A', 'Z')

/**
 * [CharMatcher] of pattern `[a-z]`
 */
@JvmField
val LOWER_CASE_MATCHER: CharMatcher = CharMatcher.inRange('a', 'z')

/**
 * [CharMatcher] of pattern `[A-Z][a-z]`
 */
@JvmField
val LETTER_MATCHER: CharMatcher = UPPER_CASE_MATCHER.and(LOWER_CASE_MATCHER)

/**
 * [CharMatcher] of pattern underscore: `_`
 */
@JvmField
val UNDERSCORE_MATCHER: CharMatcher = CharMatcher.`is`('_')

/**
 * Checks if given chars is empty or null.
 */
fun CharSequence?.isEmpty(): Boolean {
    return this.isNullOrEmpty()
}

/**
 * Checks if given chars is empty or all blank.
 */
fun CharSequence?.isBlank(): Boolean {
    return this.isNullOrBlank()
}

/**
 * Checks if given chars is numeric by [Character.isDigit].
 */
fun CharSequence?.isNumeric(): Boolean {
    return StringUtils.isNumeric(this)
}

/**
 * Checks if given chars are all white space by [Character.isWhitespace].
 */
fun CharSequence?.isWhitespace(): Boolean {
    return StringUtils.isWhitespace(this)
}

/**
 * Abbreviates a String using ellipses. This will turn "Now is the time for all good men" into "...is the time for..."
 *
 * It allows you to specify a "left edge" [offset].
 * Note that this left edge is not necessarily going to be the leftmost character in the result,
 * or the first character following the ellipses, but it will appear somewhere in the result.
 *
 * In no case will it return a String of length greater than [length].
 *
 * ```
 * Chars.ellipses("", 4, 0)                  = ""
 * Chars.ellipses("abcdefghijklmno", 10, -1) = "abcdefg..."
 * Chars.ellipses("abcdefghijklmno", 10, 0)  = "abcdefg..."
 * Chars.ellipses("abcdefghijklmno", 10, 1)  = "abcdefg..."
 * Chars.ellipses("abcdefghijklmno", 10, 4)  = "abcdefg..."
 * Chars.ellipses("abcdefghijklmno", 10, 5)  = "...fghi..."
 * Chars.ellipses("abcdefghijklmno", 10, 6)  = "...ghij..."
 * Chars.ellipses("abcdefghijklmno", 10, 8)  = "...ijklmno"
 * Chars.ellipses("abcdefghijklmno", 10, 10) = "...ijklmno"
 * Chars.ellipses("abcdefghijklmno", 10, 12) = "...ijklmno"
 * Chars.ellipses("abcdefghij", 3, 0)        = IllegalArgumentException
 * Chars.ellipses("abcdefghij", 6, 5)        = IllegalArgumentException
 * ```
 */
@JvmOverloads
fun CharSequence.ellipses(length: Int = 6, offset: Int = 0): String {
    return StringUtils.abbreviate(this.toString(), offset, length)
}

/**
 * Capitalizes given chars.
 */
fun CharSequence.capitalize(): String {
    return StringUtils.capitalize(this.toString())
}

/**
 * Uncapitalize given chars.
 */
fun CharSequence.uncapitalize(): String {
    return StringUtils.uncapitalize(this.toString())
}

@JvmName("toString")
@JvmOverloads
fun ByteArray.toChars(charset: CharSequence, offset: Int = 0, length: Int = this.size - offset): String {
    return toChars(charset.toCharSet(), offset, length)
}

@JvmName("toString")
@JvmOverloads
fun ByteArray.toChars(charset: Charset = DEFAULT_CHARSET, offset: Int = 0, length: Int = this.size - offset): String {
    return String(this, offset, length, charset)
}

fun CharSequence.toBytes(charset: CharSequence): ByteArray {
    return toBytes(charset.toCharSet())
}

@JvmOverloads
fun CharSequence.toBytes(charset: Charset = DEFAULT_CHARSET): ByteArray {
    return this.toString().toByteArray(charset)
}

@JvmOverloads
fun CharArray.toBytes(charset: CharSequence, offset: Int = 0, length: Int = this.size - offset): ByteArray {
    return toBytes(charset.toCharSet(), offset, length)
}

@JvmOverloads
fun CharArray.toBytes(
    charset: Charset = DEFAULT_CHARSET, offset: Int = 0, length: Int = this.size - offset): ByteArray {
    return String(this, offset, length).toByteArray(charset)
}

@JvmName("charset")
fun CharSequence.toCharSet(): Charset {
    return Charset.forName(this.toString())
}

fun Array<out Any?>.toStringArray(): Array<String> {
    val result = arrayOfNulls<String>(this.size)
    for ((i, t) in this.withIndex()) {
        result[i] = t.toString()
    }
    return result.asTyped()
}

fun Array<out Any?>.toNullableStringArray(): Array<String?> {
    val result = arrayOfNulls<String>(this.size)
    for ((i, t) in this.withIndex()) {
        result[i] = t?.toString()
    }
    return result
}