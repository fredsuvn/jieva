@file:JvmName("BChars")

package xyz.srclab.common.base

import com.google.common.base.CharMatcher
import org.apache.commons.lang3.StringUtils
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.text.toCollection as toCollectionKt
import kotlin.text.toHashSet as toHashSetKt
import kotlin.text.toList as toListKt
import kotlin.text.toSet as toSetKt
import kotlin.text.toSortedSet as toSortedSetKt

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

//Utils:

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

@JvmOverloads
fun CharSequence?.equals(other: CharSequence?, ignoreCase: Boolean = false): Boolean {
    return this.contentEquals(other, ignoreCase)
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
 * BChars.ellipses("", 4, 0)                  = ""
 * BChars.ellipses("abcdefghijklmno", 10, -1) = "abcdefg..."
 * BChars.ellipses("abcdefghijklmno", 10, 0)  = "abcdefg..."
 * BChars.ellipses("abcdefghijklmno", 10, 1)  = "abcdefg..."
 * BChars.ellipses("abcdefghijklmno", 10, 4)  = "abcdefg..."
 * BChars.ellipses("abcdefghijklmno", 10, 5)  = "...fghi..."
 * BChars.ellipses("abcdefghijklmno", 10, 6)  = "...ghij..."
 * BChars.ellipses("abcdefghijklmno", 10, 8)  = "...ijklmno"
 * BChars.ellipses("abcdefghijklmno", 10, 10) = "...ijklmno"
 * BChars.ellipses("abcdefghijklmno", 10, 12) = "...ijklmno"
 * BChars.ellipses("abcdefghij", 3, 0)        = IllegalArgumentException
 * BChars.ellipses("abcdefghij", 6, 5)        = IllegalArgumentException
 * ```
 */
@JvmOverloads
fun CharSequence.ellipses(length: Int = 6, offset: Int = 0): String {
    return StringUtils.abbreviate(this.toString(), offset, length)
}

//Refs:

@JvmOverloads
fun CharSequence.refOfRange(startIndex: Int = 0, endIndex: Int = this.length): CharSequence {
    return CharSequenceRef(this, startIndex, endIndex)
}

@JvmOverloads
fun CharSequence.refOfOffset(offset: Int = 0, length: Int = this.length - offset): CharSequence {
    return CharSequenceRef(this, offset, offset + length)
}

@JvmOverloads
fun CharArray.charsRefOfRange(startIndex: Int = 0, endIndex: Int = this.size): CharSequence {
    return CharArrayRef(this, startIndex, endIndex)
}

@JvmOverloads
fun CharArray.charsRefOfOffset(offset: Int = 0, length: Int = this.size - offset): CharSequence {
    return CharArrayRef(this, offset, offset + length)
}

//Encode:

@JvmName("toString")
@JvmOverloads
fun ByteArray.encodeToString(charset: CharSequence, offset: Int = 0, length: Int = this.size - offset): String {
    return encodeToString(charset.toCharSet(), offset, length)
}

@JvmName("toString")
@JvmOverloads
fun ByteArray.encodeToString(
    charset: Charset = DEFAULT_CHARSET,
    offset: Int = 0,
    length: Int = this.size - offset
): String {
    return String(this, offset, length, charset)
}

@JvmName("toBytes")
fun CharSequence.decodeToBytes(charset: CharSequence): ByteArray {
    return decodeToBytes(charset.toCharSet())
}

@JvmName("toBytes")
@JvmOverloads
fun CharSequence.decodeToBytes(charset: Charset = DEFAULT_CHARSET): ByteArray {
    return this.toString().toByteArray(charset)
}

@JvmName("toBytes")
@JvmOverloads
fun CharArray.decodeToBytes(charset: CharSequence, offset: Int = 0, length: Int = this.size - offset): ByteArray {
    return decodeToBytes(charset.toCharSet(), offset, length)
}

@JvmName("toBytes")
@JvmOverloads
fun CharArray.decodeToBytes(
    charset: Charset = DEFAULT_CHARSET, offset: Int = 0, length: Int = this.size - offset
): ByteArray {
    return String(this, offset, length).toByteArray(charset)
}

fun CharSequence.toCharSet(): Charset {
    return Charset.forName(this.toString())
}

//toCollection:

fun <C : MutableCollection<in Char>> CharSequence.toCollection(destination: C): C {
    return this.toCollectionKt(destination)
}

fun CharSequence.toSet(): Set<Char> {
    return this.toSetKt()
}

fun CharSequence.toHashSet(): HashSet<Char> {
    return this.toHashSetKt()
}

fun CharSequence.toSortedSet(): SortedSet<Char> {
    return this.toSortedSetKt()
}

fun CharSequence.toList(): List<Char> {
    return this.toListKt()
}

private class CharSequenceRef(
    private val chars: CharSequence,
    private val startIndex: Int,
    private val endIndex: Int
) : CharSequenceHashAndEquals() {

    override val length: Int = endIndex - startIndex

    override fun get(index: Int): Char {
        index.checkIndexInBounds(startIndex, endIndex)
        return chars[startIndex + index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        checkRangeInBounds(startIndex, endIndex, 0, length)
        if (startIndex == endIndex) {
            return ""
        }
        return CharSequenceRef(chars, this.startIndex + startIndex, this.startIndex + endIndex)
    }
}

private class CharArrayRef(
    private val chars: CharArray,
    private val startIndex: Int,
    private val endIndex: Int
) : CharSequenceHashAndEquals() {

    override val length: Int = endIndex - startIndex

    override fun get(index: Int): Char {
        index.checkIndexInBounds(startIndex, endIndex)
        return chars[startIndex + index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        checkRangeInBounds(startIndex, endIndex, 0, length)
        if (startIndex == endIndex) {
            return ""
        }
        return CharArrayRef(chars, this.startIndex + startIndex, this.startIndex + endIndex)
    }
}

private abstract class CharSequenceHashAndEquals : CharSequence {

    private val chars: String by lazy {
        val result = CharArray(this.length)
        var i = 0
        for (c in this) {
            result[i++] = c
        }
        String(result)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CharSequence) return false
        return this.contentEquals(other)
    }

    override fun hashCode(): Int {
        return chars.hashCode()
    }

    override fun toString(): String {
        return chars
    }
}