/**
 * String utilities.
 */
@file:JvmName("BtString")

package xyz.srclab.common.base

import com.google.common.base.CharMatcher
import org.apache.commons.lang3.StringUtils
import xyz.srclab.common.asType
import xyz.srclab.common.remLength
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.function.Supplier
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.math.min
import kotlin.text.lines as linesKt
import kotlin.text.toCollection as toCollectionKt
import kotlin.text.toList as toListKt
import kotlin.text.toSet as toSetKt

/**
 * Charset of UTF-8.
 */
@JvmField
val UTF8: Charset = StandardCharsets.UTF_8

/**
 * [CharMatcher] for char dot: ".".
 */
@JvmField
val DOT_MATCHER: CharMatcher = CharMatcher.`is`('.')

/**
 * [CharMatcher] for char hyphen: "-".
 */
@JvmField
val HYPHEN_MATCHER: CharMatcher = CharMatcher.`is`('-')

/**
 * [CharMatcher] for char underscore: "_".
 */
@JvmField
val UNDERSCORE_MATCHER: CharMatcher = CharMatcher.`is`('_')

/**
 * Returns whether given chars is empty or null.
 */
@OptIn(ExperimentalContracts::class)
fun CharSequence?.isEmpty(): Boolean {
    contract {
        returns(false) implies (this@isEmpty !== null)
    }

    return this.isNullOrEmpty()
}

/**
 * Returns whether given chars is empty or blank.
 */
@OptIn(ExperimentalContracts::class)
fun CharSequence?.isBlank(): Boolean {
    contract {
        returns(false) implies (this@isBlank !== null)
    }

    return this.isNullOrBlank()
}

/**
 * Returns whether given chars is not empty and numeric with [Character.isDigit].
 */
@OptIn(ExperimentalContracts::class)
fun CharSequence?.isNumeric(): Boolean {
    contract {
        returns(true) implies (this@isNumeric !== null)
    }

    if (this.isNullOrEmpty()) {
        return false
    }
    for (c in this) {
        if (!Character.isDigit(c)) {
            return false
        }
    }
    return true
}

/**
 * Returns whether given chars is not empty and only contains white space with [Character.isWhitespace].
 */
@OptIn(ExperimentalContracts::class)
fun CharSequence?.isWhitespace(): Boolean {
    contract {
        returns(true) implies (this@isWhitespace !== null)
    }

    if (this.isNullOrEmpty()) {
        return false
    }
    for (c in this) {
        if (!Character.isWhitespace(c)) {
            return false
        }
    }
    return true
}

/**
 * Returns whether given chars is empty with [isNumeric], and has a leading zero.
 */
@OptIn(ExperimentalContracts::class)
fun CharSequence?.isNumericLeadingZero(): Boolean {
    contract {
        returns(true) implies (this@isNumericLeadingZero !== null)
    }

    if (!this.isNumeric()) {
        return false
    }
    return this.isLeadingZero()
}

/**
 * Returns whether given chars has a leading zero and its length is greater than 1.
 */
fun CharSequence.isLeadingZero(): Boolean {
    if (this.length < 2) {
        return false
    }
    return this[0] == '0'
}

/**
 * Returns true if any of given strings is empty with [isEmpty].
 */
fun anyEmpty(vararg strings: CharSequence?): Boolean {
    return anyPredicate({ str -> str.isEmpty() }, *strings)
}

/**
 * Returns true if any of given strings is blank with [isBlank].
 */
fun anyBlank(vararg strings: CharSequence?): Boolean {
    return anyPredicate({ str -> str.isBlank() }, *strings)
}

/**
 * Returns true if each of given strings is empty with [isEmpty].
 */
fun allEmpty(vararg strings: CharSequence?): Boolean {
    return allPredicate({ str -> str.isEmpty() }, *strings)
}

/**
 * Returns true if each given strings is blank with [isBlank].
 */
fun allBlank(vararg strings: CharSequence?): Boolean {
    return allPredicate({ str -> str.isBlank() }, *strings)
}

/**
 * Returns the [String] of which first char was capitalized.
 */
fun CharSequence.capitalize(): String {
    return StringUtils.capitalize(this.toString())
}

/**
 * Returns the [String] of which first char was uncapitalized.
 */
fun CharSequence.uncapitalize(): String {
    return StringUtils.uncapitalize(this.toString())
}

/**
 * Returns the [String] of which chars were converted to lower case.
 */
fun CharSequence.lowerCase(): String {
    return this.toString().lowercase(BtProps.locale())
}

/**
 * Returns the [String] of which chars were converted to upper case.
 */
fun CharSequence.upperCase(): String {
    return this.toString().uppercase(BtProps.locale())
}

/**
 * Returns whether char sequence of [this] equals to [other]'s.
 */
@JvmName("equals")
@JvmOverloads
fun CharSequence?.charsEquals(other: CharSequence?, ignoreCase: Boolean = false): Boolean {
    return this.contentEquals(other, ignoreCase)
}

/**
 * Returns true if any of [others]'s char sequence equals to [this]'s with [charsEquals].
 */
fun CharSequence?.anyEquals(vararg others: CharSequence?): Boolean {
    return this.anyEquals(false, *others)
}

/**
 * Returns true if each of [others]'s char sequence equals to [this]'s with [charsEquals].
 */
fun CharSequence?.allEquals(vararg others: CharSequence?): Boolean {
    return this.allEquals(false, *others)
}

/**
 * Returns true if any of [others]'s char sequence equals to [this]'s with [charsEquals].
 */
fun CharSequence?.anyEquals(ignoreCase: Boolean, vararg others: CharSequence?): Boolean {
    return anyPredicate({ str -> this.charsEquals(str, ignoreCase) }, *others)
}

/**
 * Returns true if each of [others]'s char sequence equals to [this]'s with [charsEquals].
 */
fun CharSequence?.allEquals(ignoreCase: Boolean, vararg others: CharSequence?): Boolean {
    return allPredicate({ str -> this.charsEquals(str, ignoreCase) }, *others)
}

/**
 * Splits this char sequence to a list of lines delimited by any of the following character sequences: CRLF, LF or CR.
 *
 * The lines returned do not include terminating line separators.
 */
fun CharSequence.lines(): List<String> {
    return this.linesKt()
}

/**
 * Converts chars to bytes with [charset].
 */
@JvmOverloads
fun CharSequence.getBytes(charset: Charset = BtProps.charset()): ByteArray {
    return this.toString().asJavaString().getBytes(charset)
}

/**
 * Converts bytes to String with [charset].
 */
@JvmName("toString")
@JvmOverloads
fun ByteArray.getString(
    charset: Charset = BtProps.charset(),
    offset: Int = 0,
    length: Int = remLength(this.size, offset),
): String {
    return String(this, offset, length, charset)
}

/**
 * Converts chars to bytes, each of char will be seen as an 8-bit byte.
 */
@JvmOverloads
fun CharSequence.getBytes8Bit(offset: Int = 0, length: Int = remLength(this.length, offset)): ByteArray {
    //checkRangeInBounds(offset, offset + length, 0, this.length)
    return getBytes8Bit(length) { this[it + offset] }
}

/**
 * Converts chars to bytes, each of char will be seen as an 8-bit byte.
 */
@JvmOverloads
fun CharArray.getBytes8Bit(offset: Int = 0, length: Int = remLength(this.size, offset)): ByteArray {
    //checkRangeInBounds(offset, offset + length, 0, this.size)
    return getBytes8Bit(length) { this[it + offset] }
}

private inline fun getBytes8Bit(
    length: Int,
    getChar: (Int) -> Char,
): ByteArray {
    val array = ByteArray(length)
    for (i in 0 until length) {
        val char = getChar(i)
        array[i] = char.code.toByte()
    }
    return array
}

/**
 * Converts bytes to String, each of char will be seen as an 8-bit byte.
 */
@JvmName("toString8Bit")
@JvmOverloads
fun ByteArray.getString8Bit(offset: Int = 0, length: Int = remLength(this.size, offset)): String {
    return String(this, offset, length, StandardCharsets.ISO_8859_1)
}

/**
 * Converts char sequence to char array.
 */
fun CharSequence.getChars(): CharArray {
    val array = CharArray(this.length)
    for (i in 0 until this.length) {
        array[i] = this[i]
    }
    return array
}

/**
 * Fills [dest] char array with [this] char sequence.
 */
@JvmOverloads
fun CharSequence.getChars(dest: CharArray, offset: Int = 0, length: Int = remLength(dest.size, offset)) {
    val minLen = min(this.length, length)
    if (this is JavaString) {
        this.getChars(0, minLen, dest, offset)
        return
    }
    for (i in 0 until minLen) {
        dest[offset + i] = this[i]
    }
}

/**
 * If [this] does not start with [prefix], add the [prefix] as prefix and return; else return itself to String.
 */
fun CharSequence.addIfNotStartWith(prefix: CharSequence): String {
    return if (this.startsWith(prefix)) {
        this.toString()
    } else {
        "$prefix$this"
    }
}

/**
 * If [this] starts with [prefix], remove the [prefix] and return; else return itself to String.
 */
fun CharSequence.removeIfStartWith(prefix: CharSequence): String {
    return if (this.startsWith(prefix)) {
        this.substring(prefix.length)
    } else {
        this.toString()
    }
}

/**
 * If [this] does not end with [suffix], add the [suffix] as suffix and return; else return itself to String.
 */
fun CharSequence.addIfNotEndWith(suffix: CharSequence): String {
    return if (this.endsWith(suffix)) {
        this.toString()
    } else {
        "$this$suffix"
    }
}

/**
 * If [this] ends with [suffix], remove the [suffix] and return; else return itself to String.
 */
fun CharSequence.removeIfEndWith(suffix: CharSequence): String {
    return if (this.endsWith(suffix)) {
        this.substring(0, this.length - suffix.length)
    } else {
        this.toString()
    }
}

/**
 * Returns sub char sequence reference of [this] from [startIndex] inclusive to [endIndex] exclusive.
 *
 * If [startIndex] is 0 and [endIndex] is length of this, return itself; else return a [CharsRef].
 */
@Throws(IndexOutOfBoundsException::class)
@JvmOverloads
fun CharSequence.subRef(startIndex: Int = 0, endIndex: Int = this.length): CharSequence {
    if (startIndex == 0 && endIndex == this.length) {
        return this
    }
    return CharsRef.of(this, startIndex, endIndex)
}

/**
 * Returns sub char sequence as [CharsRef] reference of [this] from [startIndex] inclusive to [endIndex] exclusive.
 *
 * If [startIndex] is 0 and [endIndex] is length of this, return itself; else return a [CharsRef].
 */
@Throws(IndexOutOfBoundsException::class)
@JvmOverloads
fun CharSequence.charsRef(startIndex: Int = 0, endIndex: Int = this.length): CharsRef {
    if (startIndex == 0 && endIndex == this.length && this is CharsRef) {
        return this
    }
    return CharsRef.of(this, startIndex, endIndex)
}

/**
 * Returns [CharsRef] of [this] from [startIndex] inclusive to [endIndex] exclusive.
 */
@Throws(IndexOutOfBoundsException::class)
@JvmOverloads
fun CharArray.charsRef(startIndex: Int = 0, endIndex: Int = this.size): CharsRef {
    return CharsRef.of(this, startIndex, endIndex)
}

/**
 * Puts and returns each char of [this] into a [Collection].
 */
fun <C : MutableCollection<in Char>> CharSequence.toCollection(destination: C): C {
    return this.toCollectionKt(destination)
}

/**
 * Returns a [Set] which contains all chars of [this].
 */
fun CharSequence.toSet(): Set<Char> {
    return this.toSetKt()
}

/**
 * Returns a [List] which contains all chars of [this].
 */
fun CharSequence.toList(): List<Char> {
    return this.toListKt()
}

/**
 * Returns a [LazyString].
 */
@JvmSynthetic
fun lazyString(supplier: Supplier<Any?>): LazyString {
    return LazyString.of(supplier)
}

/**
 * Returns this as [java.lang.String].
 */
@JvmSynthetic
fun String.asJavaString(): JavaString {
    return this.asType()
}



/**
 * Represents a string with lazy initialization.
 *
 * This class is usually used for logging.
 */
interface LazyString : CharSequence {

    companion object {

        /**
         * Returns a [LazyString] supplied by [supplier].
         */
        @JvmStatic
        fun of(supplier: Supplier<Any?>): LazyString {
            return LazyStringImpl(supplier)
        }

        private class LazyStringImpl(private val supplier: Supplier<Any?>) : LazyString, FinalClass() {

            override val length: Int
                get() = toString().length

            override fun get(index: Int): Char {
                return toString()[index]
            }

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                return toString().subSequence(startIndex, endIndex)
            }

            override fun hashCode0(): Int {
                return toString().hashCode()
            }

            override fun toString0(): String {
                return supplier.get().toString()
            }
        }
    }
}