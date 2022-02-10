@file:JvmName("BString")

package xyz.srclab.common.base

import com.google.common.base.CharMatcher
import org.apache.commons.lang3.StringUtils
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.function.Supplier
import kotlin.text.lines as linesKt
import kotlin.text.toCollection as toCollectionKt
import kotlin.text.toHashSet as toHashSetKt
import kotlin.text.toList as toListKt
import kotlin.text.toSet as toSetKt
import kotlin.text.toSortedSet as toSortedSetKt

/**
 * [CharMatcher] of pattern dot: `.`
 */
@JvmField
val DOT_MATCHER: CharMatcher = CharMatcher.`is`('.')

/**
 * [CharMatcher] of pattern underscore: `_`
 */
@JvmField
val UNDERSCORE_MATCHER: CharMatcher = CharMatcher.`is`('_')

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
 * [CharMatcher] of pattern `[0-9]`
 */
@JvmField
val NUMERIC_MATCHER: CharMatcher = CharMatcher.inRange('0', '9')

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
 * Returns whether [this] has a leading zero (assumed [this] is numeric).
 */
fun CharSequence?.numericWithLeadingZero(): Boolean {
    if (this === null || this.length < 2) {
        return false
    }
    return this[0] == '0'
}

fun anyEmpty(vararg charSeqs: CharSequence?): Boolean {
    for (charSeq in charSeqs) {
        if (charSeq.isEmpty()) {
            return true
        }
    }
    return false
}

fun anyEmpty(charSeqs: Iterable<CharSequence?>): Boolean {
    for (charSeq in charSeqs) {
        if (charSeq.isEmpty()) {
            return true
        }
    }
    return false
}

fun allEmpty(vararg charSeqs: CharSequence?): Boolean {
    for (charSeq in charSeqs) {
        if (!charSeq.isEmpty()) {
            return false
        }
    }
    return true
}

fun allEmpty(charSeqs: Iterable<CharSequence?>): Boolean {
    for (charSeq in charSeqs) {
        if (!charSeq.isEmpty()) {
            return false
        }
    }
    return true
}

fun anyBlank(vararg charSeqs: CharSequence?): Boolean {
    for (charSeq in charSeqs) {
        if (charSeq.isBlank()) {
            return true
        }
    }
    return false
}

fun anyBlank(charSeqs: Iterable<CharSequence?>): Boolean {
    for (charSeq in charSeqs) {
        if (charSeq.isBlank()) {
            return true
        }
    }
    return false
}

fun allBlank(vararg charSeqs: CharSequence?): Boolean {
    for (charSeq in charSeqs) {
        if (!charSeq.isBlank()) {
            return false
        }
    }
    return true
}

fun allBlank(charSeqs: Iterable<CharSequence?>): Boolean {
    for (charSeq in charSeqs) {
        if (!charSeq.isBlank()) {
            return false
        }
    }
    return true
}

fun CharSequence.lowerCase(): String {
    return this.toString().lowercase()
}

fun CharSequence.upperCase(): String {
    return this.toString().uppercase()
}

/**
 * Sets first character of given chars upper case.
 */
fun CharSequence.capitalize(): String {
    return StringUtils.capitalize(this.toString())
}

/**
 * Sets first character of given chars lower case.
 */
fun CharSequence.uncapitalize(): String {
    return StringUtils.uncapitalize(this.toString())
}

@JvmOverloads
fun CharSequence?.equals(other: CharSequence?, ignoreCase: Boolean = false): Boolean {
    return this.contentEquals(other, ignoreCase)
}

fun CharSequence?.equalsAny(vararg others: CharSequence?): Boolean {
    for (other in others) {
        if (this == other) {
            return true
        }
    }
    return false
}

fun CharSequence?.equalsAll(vararg others: CharSequence?): Boolean {
    for (other in others) {
        if (this != other) {
            return false
        }
    }
    return true
}

fun CharSequence?.equalsAnyIgnoreCase(vararg others: CharSequence?): Boolean {
    for (other in others) {
        if (this.equals(other, true)) {
            return true
        }
    }
    return false
}

fun CharSequence?.equalsAllIgnoreCase(vararg others: CharSequence?): Boolean {
    for (other in others) {
        if (this.equals(other, true)) {
            return false
        }
    }
    return true
}

@JvmOverloads
fun CharSequence?.equalsAny(others: Iterable<CharSequence?>, ignoreCase: Boolean = false): Boolean {
    for (other in others) {
        if (this.equals(other, ignoreCase)) {
            return true
        }
    }
    return false
}

@JvmOverloads
fun CharSequence?.equalsAll(others: Iterable<CharSequence?>, ignoreCase: Boolean = false): Boolean {
    for (other in others) {
        if (this.equals(other, ignoreCase)) {
            return false
        }
    }
    return true
}

fun CharSequence.charset(): Charset {
    return Charset.forName(this.toString())
}

fun CharSequence.lines(): List<String> {
    return this.linesKt()
}

@JvmOverloads
fun CharSequence.byteArray(charset: Charset = DEFAULT_CHARSET): ByteArray {
    return this.toString().toByteArray(charset)
}

@JvmOverloads
fun CharArray.byteArray(charset: Charset = DEFAULT_CHARSET): ByteArray {
    return String(this).toByteArray(charset)
}

@JvmOverloads
fun ByteArray.string(
    charset: Charset = DEFAULT_CHARSET,
    offset: Int = 0,
    length: Int = remainingLength(this.size, offset)
): String {
    return String(this, offset, length, charset)
}

@JvmOverloads
fun ByteArray.string(
    offset: Int,
    length: Int = remainingLength(this.size, offset)
): String {
    return string(DEFAULT_CHARSET, offset, length)
}

@JvmOverloads
fun ByteArray.charArray(
    charset: Charset = DEFAULT_CHARSET,
    offset: Int = 0,
    length: Int = remainingLength(this.size, offset)
): CharArray {
    return String(this, offset, length, charset).toCharArray()
}

@JvmOverloads
fun ByteArray.charArray(
    offset: Int,
    length: Int = remainingLength(this.size, offset)
): CharArray {
    return charArray(DEFAULT_CHARSET, offset, length)
}

fun CharSequence.charArray(): CharArray {
    val array = CharArray(this.length)
    for (c in this.withIndex()) {
        array[c.index] = c.value
    }
    return array
}

fun CharArray.string(): String {
    return String(this)
}

fun CharSequence.addIfNotStartWith(prefix: CharSequence): String {
    return if (this.startsWith(prefix)) {
        this.toString()
    } else {
        "$prefix$this"
    }
}

fun CharSequence.removeIfStartWith(prefix: CharSequence): String {
    return if (this.startsWith(prefix)) {
        this.substring(prefix.length)
    } else {
        this.toString()
    }
}

@JvmOverloads
fun CharSequence.to8BitBytes(offset: Int = 0, length: Int = remainingLength(this.length, offset)): ByteArray {
    checkRangeInBounds(offset, offset + length, 0, this.length)
    val array = ByteArray(length)
    var i = offset
    var j = 0
    while (i < offset + length) {
        array[j] = this[i].code.toByte()
        i++
        j++
    }
    return array
}

@JvmOverloads
fun CharArray.to8BitBytes(offset: Int = 0, length: Int = remainingLength(this.size, offset)): ByteArray {
    checkRangeInBounds(offset, offset + length, 0, this.size)
    val array = ByteArray(length)
    var i = offset
    var j = 0
    while (i < offset + length) {
        array[j] = this[i].code.toByte()
        i++
        j++
    }
    return array
}

@JvmOverloads
fun ByteArray.to8BitString(offset: Int = 0, length: Int = remainingLength(this.size, offset)): String {
    return String(this, offset, length, StandardCharsets.US_ASCII)
}

@JvmOverloads
fun ByteArray.to8BitChars(offset: Int = 0, length: Int = remainingLength(this.size, offset)): CharArray {
    checkRangeInBounds(offset, offset + length, 0, this.size)
    val array = CharArray(length)
    var i = offset
    var j = 0
    while (i < offset + length) {
        array[j] = this[i].toUnsignedInt().toChar()
        i++
        j++
    }
    return array
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

//Lazy:

@JvmSynthetic
fun lazyString(supplier: () -> String): LazyString {
    return LazyString.of(supplier.toSupplier())
}

/**
 * String reference, to refer to a range of [CharSequence] with indexes but not store the copy of data.
 */
interface StringRef : CharSequence {

    /**
     * Copies and returns a new String of current range.
     */
    fun copyOfRange(): String

    companion object {

        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun CharSequence.stringRef(startIndex: Int = 0, endIndex: Int = this.length): StringRef {
            checkRangeInBounds(startIndex, endIndex, 0, this.length)
            return CharSeqStringRef(this, startIndex, endIndex)
        }

        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun CharArray.stringRef(startIndex: Int = 0, endIndex: Int = this.size): StringRef {
            checkRangeInBounds(startIndex, endIndex, 0, this.size)
            return CharsStringRef(this, startIndex, endIndex)
        }

        @JvmName("offset")
        @JvmOverloads
        @JvmStatic
        fun CharSequence.stringRefByOffset(
            offset: Int = 0,
            length: Int = remainingLength(this.length, offset)
        ): StringRef {
            return stringRef(offset, offset + length)
        }

        @JvmName("offset")
        @JvmOverloads
        @JvmStatic
        fun CharArray.stringRefByOffset(offset: Int = 0, length: Int = remainingLength(this.size, offset)): StringRef {
            return stringRef(offset, offset + length)
        }

        private class CharSeqStringRef(
            private val chars: CharSequence,
            private val startIndex: Int,
            private val endIndex: Int
        ) : StringRef {

            override val length: Int = endIndex - startIndex

            override fun get(index: Int): Char {
                index.checkIndexInBounds(0, endIndex)
                return chars[index.actualIndex()]
            }

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                checkRangeInBounds(startIndex, endIndex, 0, this.endIndex)
                return CharSeqStringRef(chars, startIndex.actualIndex(), endIndex.actualIndex())
            }

            override fun copyOfRange(): String {
                return chars.subSequence(startIndex, endIndex).toString()
            }

            private fun Int.actualIndex() = this + startIndex

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is CharSeqStringRef) return false
                if (chars !== other.chars) return false
                if (startIndex != other.startIndex) return false
                if (endIndex != other.endIndex) return false
                return true
            }

            override fun hashCode(): Int {
                var result = chars.hashCode()
                result = 31 * result + startIndex
                result = 31 * result + endIndex
                return result
            }

            override fun toString(): String {
                return copyOfRange()
            }
        }

        private class CharsStringRef(
            private val chars: CharArray,
            private val startIndex: Int,
            private val endIndex: Int
        ) : StringRef {

            override val length: Int = endIndex - startIndex

            override fun get(index: Int): Char {
                index.checkIndexInBounds(0, endIndex)
                return chars[index.actualIndex()]
            }

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                checkRangeInBounds(startIndex, endIndex, 0, this.endIndex)
                return CharsStringRef(chars, startIndex.actualIndex(), endIndex.actualIndex())
            }

            override fun copyOfRange(): String {
                return String(chars, startIndex, remainingLength(chars.size, startIndex))
            }

            private fun Int.actualIndex() = this + startIndex

            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (other !is CharsStringRef) return false
                if (chars !== other.chars) return false
                if (startIndex != other.startIndex) return false
                if (endIndex != other.endIndex) return false
                return true
            }

            override fun hashCode(): Int {
                var result = chars.hashCode()
                result = 31 * result + startIndex
                result = 31 * result + endIndex
                return result
            }

            override fun toString(): String {
                return copyOfRange()
            }
        }
    }
}

/**
 * Represents a string with lazy initialization.
 *
 * This class is usually used for logging.
 */
interface LazyString : CharSequence {

    companion object {

        @JvmStatic
        fun of(supplier: Supplier<String>): LazyString {
            return LazyStringImpl(supplier)
        }

        private class LazyStringImpl(private val supplier: Supplier<String>) : LazyString {

            private val value: String by lazy { supplier.get() }

            override val length: Int
                get() = value.length

            override fun get(index: Int): Char {
                return value[index]
            }

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                return value.subSequence(startIndex, endIndex)
            }

            override fun toString(): String {
                return value
            }
        }
    }
}