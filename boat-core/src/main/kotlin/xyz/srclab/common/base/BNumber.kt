@file:JvmName("BNumber")

package xyz.srclab.common.base

import org.apache.commons.lang3.StringUtils
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import kotlin.text.toBigDecimal as toBigDecimalKt
import kotlin.text.toBigInteger as toBigIntegerKt
import kotlin.text.toByte as toByteKt
import kotlin.text.toDouble as toDoubleKt
import kotlin.text.toFloat as toFloatKt
import kotlin.text.toInt as toIntKt
import kotlin.text.toLong as toLongKt
import kotlin.text.toShort as toShortKt

/**
 * Returns 100 of which type is [BigInteger].
 */
fun hundredInt(): BigInteger = BNumberHolder.HUNDRED_INT

/**
 * Returns 1000 of which type is [BigInteger].
 */
fun thousandInt(): BigInteger = BNumberHolder.THOUSAND_INT

/**
 * Returns 100 of which type is [BigDecimal].
 */
fun hundredDecimal(): BigDecimal = BNumberHolder.HUNDRED_DECIMAL

/**
 * Returns 1000 of which type is [BigDecimal].
 */
fun thousandDecimal(): BigDecimal = BNumberHolder.THOUSAND_DECIMAL

/**
 * Converts chars to byte.
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toByte(radix: Int = defaultRadix()): Byte {
    return this.toString().toByteKt(radix)
}

/**
 * Converts chars to short.
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toShort(radix: Int = defaultRadix()): Short {
    return this.toString().toShortKt(radix)
}

/**
 * Converts chars to int then to char.
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toChar(radix: Int = defaultRadix()): Char {
    return this.toString().toIntKt(radix).toChar()
}

/**
 * Converts chars to int.
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toInt(radix: Int = defaultRadix()): Int {
    return this.toString().toIntKt(radix)
}

/**
 * Converts chars to long.
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toLong(radix: Int = defaultRadix()): Long {
    return this.toString().toLongKt(radix)
}

/**
 * Converts chars to float.
 */
@Throws(NumberFormatException::class)
fun CharSequence.toFloat(): Float {
    return this.toString().toFloatKt()
}

/**
 * Converts chars to double.
 */
@Throws(NumberFormatException::class)
fun CharSequence.toDouble(): Double {
    return this.toString().toDoubleKt()
}

/**
 * Converts chars to [BigInteger].
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toBigInteger(radix: Int = defaultRadix()): BigInteger {
    val str = this.toString()
    if (radix == 10) {
        return when (str) {
            "0" -> BigInteger.ZERO
            "1" -> BigInteger.ONE
            "10" -> BigInteger.TEN
            "100" -> hundredInt()
            "1000" -> thousandInt()
            else -> str.toBigIntegerKt()
        }
    }
    return str.toBigIntegerKt(radix)
}

/**
 * Converts chars to [BigDecimal].
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toBigDecimal(mathContext: MathContext = MathContext.UNLIMITED): BigDecimal {
    return when (val str = this.toString()) {
        "0" -> BigDecimal.ZERO
        "1" -> BigDecimal.ONE
        "10" -> BigDecimal.TEN
        "100" -> hundredDecimal()
        "1000" -> hundredDecimal()
        else -> str.toBigDecimalKt(mathContext)
    }
}

/**
 * Converts [this] to byte:
 *
 * * If this is null, return 0;
 * * If this is byte, return itself;
 * * If this is number, return byte value;
 * * If this is boolean ,return 1 for `true` or 0 for `false`;
 * * Else calls the [toCharSeq] then converts the char sequence to byte.
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun Any?.toByte(radix: Int = defaultRadix()): Byte {
    return when (this) {
        null -> 0
        is Byte -> this
        is Number -> this.toByte()
        is Boolean -> if (this) 1 else 0
        else -> this.toCharSeq().toByte(radix)
    }
}

/**
 * Converts [this] to short:
 *
 * * If this is null, return 0;
 * * If this is short, return itself;
 * * If this is number, return short value;
 * * If this is boolean ,return 1 for `true` or 0 for `false`;
 * * Else calls the [toCharSeq] then converts the char sequence to short.
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun Any?.toShort(radix: Int = defaultRadix()): Short {
    return when (this) {
        null -> 0
        is Short -> this
        is Number -> this.toShort()
        is Boolean -> if (this) 1 else 0
        else -> this.toCharSeq().toShort(radix)
    }
}

/**
 * Converts [this] to char:
 *
 * * If this is null, return 0;
 * * If this is char, return itself;
 * * If this is number, return int value to char;
 * * If this is boolean ,return 1 for `true` or 0 for `false`;
 * * Else calls the [toCharSeq] then converts the char sequence to char.
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun Any?.toChar(radix: Int = defaultRadix()): Char {
    return when (this) {
        null -> 0.toChar()
        is Char -> this
        is Number -> this.toInt().toChar()
        is Boolean -> if (this) 1.toChar() else 0.toChar()
        else -> this.toCharSeq().toChar(radix)
    }
}

/**
 * Converts [this] to int:
 *
 * * If this is null, return 0;
 * * If this is int, return itself;
 * * If this is number, return int value;
 * * If this is boolean ,return 1 for `true` or 0 for `false`;
 * * Else calls the [toCharSeq] then converts the char sequence to int.
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun Any?.toInt(radix: Int = defaultRadix()): Int {
    return when (this) {
        null -> 0
        is Int -> this
        is Number -> this.toInt()
        is Boolean -> if (this) 1 else 0
        else -> this.toCharSeq().toInt(radix)
    }
}

/**
 * Converts [this] to long:
 *
 * * If this is null, return 0;
 * * If this is long, return itself;
 * * If this is number, return long value;
 * * If this is boolean ,return 1 for `true` or 0 for `false`;
 * * Else calls the [toCharSeq] then converts the char sequence to long.
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun Any?.toLong(radix: Int = defaultRadix()): Long {
    return when (this) {
        null -> 0
        is Long -> this
        is Number -> this.toLong()
        is Boolean -> if (this) 1 else 0
        else -> this.toCharSeq().toLong(radix)
    }
}

/**
 * Converts [this] to float:
 *
 * * If this is null, return 0;
 * * If this is float, return itself;
 * * If this is number, return float value;
 * * If this is boolean ,return 1 for `true` or 0 for `false`;
 * * Else calls the [toCharSeq] then converts the char sequence to float.
 */
@Throws(NumberFormatException::class)
fun Any?.toFloat(): Float {
    return when (this) {
        null -> 0f
        is Float -> this
        is Number -> this.toFloat()
        is Boolean -> if (this) 1.0f else 0.0f
        else -> this.toCharSeq().toFloat()
    }
}

/**
 * Converts [this] to double:
 *
 * * If this is null, return 0;
 * * If this is double, return itself;
 * * If this is number, return double value;
 * * If this is boolean ,return 1 for `true` or 0 for `false`;
 * * Else calls the [toCharSeq] then converts the char sequence to double.
 */
@Throws(NumberFormatException::class)
fun Any?.toDouble(): Double {
    return when (this) {
        null -> 0.0
        is Double -> this
        is Number -> this.toDouble()
        is Boolean -> if (this) 1.0 else 0.0
        else -> this.toCharSeq().toDouble()
    }
}

/**
 * Converts [this] to [BigInteger]:
 *
 * * If this is null, return 0;
 * * If this is [BigInteger], return itself;
 * * If this is number, return big int value;
 * * If this is boolean ,return 1 for `true` or 0 for `false`;
 * * Else calls the [toCharSeq] then converts the char sequence to [BigInteger].
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun Any?.toBigInteger(radix: Int = defaultRadix()): BigInteger {
    return when (this) {
        null -> BigInteger.ZERO
        is BigInteger -> this
        is BigDecimal -> this.toBigInteger()
        is Boolean -> if (this) BigInteger.ONE else BigInteger.ZERO
        else -> this.toCharSeq().toBigInteger(radix)
    }
}

/**
 * Converts [this] to [BigDecimal]:
 *
 * * If this is null, return 0;
 * * If this is [BigDecimal], return itself;
 * * If this is number, return big decimal value;
 * * If this is boolean ,return 1 for `true` or 0 for `false`;
 * * Else calls the [toCharSeq] then converts the char sequence to [BigDecimal].
 */
@Throws(NumberFormatException::class)
@JvmOverloads
fun Any?.toBigDecimal(mathContext: MathContext = MathContext.UNLIMITED): BigDecimal {
    return when (this) {
        null -> BigDecimal.ZERO
        is BigDecimal -> this
        is Boolean -> if (this) BigDecimal.ONE else BigDecimal.ZERO
        else -> this.toCharSeq().toBigDecimal(mathContext)
    }
}

/**
 * Returns unsigned int.
 */
fun Byte.toUnsignedInt(): Int {
    return this.toInt() and 0x0000_00FF
}

/**
 * Returns unsigned long.
 */
fun Byte.toUnsignedLong(): Int {
    return this.toUnsignedInt() and 0x0000_00FF
}

/**
 * Returns unsigned int.
 */
fun Short.toUnsignedInt(): Int {
    return this.toInt() and 0x0000_FFFF
}

/**
 * Returns unsigned long.
 */
fun Short.toUnsignedLong(): Long {
    return this.toUnsignedInt().toLong() and 0x0000_FFFF
}

/**
 * Returns unsigned long.
 */
fun Int.toUnsignedLong(): Long {
    return this.toLong() and 0x0000_0000_FFFF_FFFF
}

/**
 * **Unsigned** int to binary string by [Integer.toBinaryString].
 * It will pad `0` before binary string if [size] > binary string's size, or no padding if [size] <= `0`.
 */
@JvmOverloads
fun Int.toBinaryString(size: Int = 32): String {
    return StringUtils.leftPad(Integer.toBinaryString(this), size, "0")
}

/**
 * **Unsigned** long to binary string by [java.lang.Long.toBinaryString].
 * It will pad `0` before binary string if [size] > binary string's size, or no padding if [size] <= `0`.
 */
@JvmOverloads
fun Long.toBinaryString(size: Int = 64): String {
    return StringUtils.leftPad(JavaLong.toBinaryString(this), size, "0")
}

/**
 * **Unsigned** int to hex string by [Integer.toHexString].
 * It will pad `0` before binary string if [size] > binary string's size, or no padding if [size] <= `0`.
 */
@JvmOverloads
fun Int.toHexString(size: Int = 8): String {
    return StringUtils.leftPad(Integer.toHexString(this), size, "0")
}

/**
 * **Unsigned** long to hex string by [java.lang.Long.toHexString].
 * It will pad `0` before binary string if [size] > binary string's size, or no padding if [size] <= `0`.
 */
@JvmOverloads
fun Long.toHexString(size: Int = 16): String {
    return StringUtils.leftPad(JavaLong.toHexString(this), size, "0")
}

/**
 * **Unsigned** int to octal string by [Integer.toOctalString].
 * It will pad `0` before binary string if [size] > binary string's size, or no padding if [size] <= `0`.
 */
@JvmOverloads
fun Int.toOctalString(size: Int = 11): String {
    return StringUtils.leftPad(Integer.toOctalString(this), size, "0")
}

/**
 * **Unsigned** long to octal string by [java.lang.Long.toOctalString].
 * It will pad `0` before binary string if [size] > binary string's size, or no padding if [size] <= `0`.
 */
@JvmOverloads
fun Long.toOctalString(size: Int = 22): String {
    return StringUtils.leftPad(JavaLong.toOctalString(this), size, "0")
}

/**
 * Parses given chars to int.
 *
 * Given chars may have a sign prefix (`+/-`) followed by a radix prefix (`0b/0B/0x/0X/0`):
 *
 * * 123456: positive decimal
 * * 0xffeecc: positive hex;
 * * -0xffeecc: negative hex;
 * * +0774411: positive octal;
 * * 0B001100: positive binary;
 */
fun CharSequence.parseInt(): Int {
    return ParsedChars(this).toInt()
}

/**
 * Parses given chars to int.
 *
 * Given chars may have a sign prefix (`+/-`) followed by a radix prefix (`0b/0B/0x/0X/0`):
 *
 * * 123456: positive decimal
 * * 0xffeecc: positive hex;
 * * -0xffeecc: negative hex;
 * * +0774411: positive octal;
 * * 0B001100: positive binary;
 */
fun CharSequence.parseLong(): Long {
    return ParsedChars(this).toLong()
}

/**
 * Parses given chars to [BigInteger].
 *
 * Given chars may have a sign prefix (`+/-`) followed by a radix prefix (`0b/0B/0x/0X/0`):
 *
 * * 123456: positive decimal
 * * 0xffeecc: positive hex;
 * * -0xffeecc: negative hex;
 * * +0774411: positive octal;
 * * 0B001100: positive binary;
 */
fun CharSequence.parseBigInteger(): BigInteger {
    return ParsedChars(this).toBigInteger()
}

private class ParsedChars(chars: CharSequence) {

    private val positive: Boolean
    private val radix: Int
    private val content: CharSequence

    init {
        var offset = 0
        if (chars.startsWith('-')) {
            positive = false
            offset = 1
        } else if (chars.startsWith('+')) {
            positive = true
            offset = 1
        } else {
            positive = true
            offset = 0
        }
        if (chars.startsWith("0x", offset) || chars.startsWith("0X", offset)) {
            radix = 16
            content = chars.subRef(offset + 2)
        } else if (chars.startsWith("0b", offset) || chars.startsWith("0B", offset)) {
            radix = 2
            content = chars.subRef(offset + 2)
        } else if (chars.startsWith("0", offset)) {
            radix = 8
            content = chars.subRef(offset + 1)
        } else {
            radix = 10
            content = chars.subRef(offset)
        }
    }

    fun toInt(): Int {
        val value = content.toInt(radix)
        return if (positive) value else -value
    }

    fun toLong(): Long {
        val value = content.toLong(radix)
        return if (positive) value else -value
    }

    fun toBigInteger(): BigInteger {
        val value = content.toBigInteger(radix)
        return if (positive) value else -value
    }
}

private object BNumberHolder {
    val HUNDRED_INT: BigInteger = BigInteger("100")
    val THOUSAND_INT: BigInteger = BigInteger("1000")
    val HUNDRED_DECIMAL: BigDecimal = BigDecimal("100")
    val THOUSAND_DECIMAL: BigDecimal = BigDecimal("1000")
}