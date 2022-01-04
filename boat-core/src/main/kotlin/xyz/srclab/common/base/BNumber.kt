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
 * Default radix: 10.
 */
const val DEFAULT_RADIX: Int = 10

@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toByte(radix: Int = DEFAULT_RADIX): Byte {
    return this.toString().toByteKt(radix)
}

@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toShort(radix: Int = DEFAULT_RADIX): Short {
    return this.toString().toShortKt(radix)
}

@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toChar(radix: Int = DEFAULT_RADIX): Char {
    return this.toString().toIntKt(radix).toChar()
}

@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toInt(radix: Int = DEFAULT_RADIX): Int {
    return this.toString().toIntKt(radix)
}

@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toLong(radix: Int = DEFAULT_RADIX): Long {
    return this.toString().toLongKt(radix)
}

@Throws(NumberFormatException::class)
fun CharSequence.toFloat(): Float {
    return this.toString().toFloatKt()
}

@Throws(NumberFormatException::class)
fun CharSequence.toDouble(): Double {
    return this.toString().toDoubleKt()
}

@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toBigInteger(radix: Int = DEFAULT_RADIX): BigInteger {
    val str = this.toString()
    if (radix == 10) {
        return when (str) {
            "0" -> BigInteger.ZERO
            "1" -> BigInteger.ONE
            "10" -> BigInteger.TEN
            else -> str.toBigIntegerKt()
        }
    }
    return str.toBigIntegerKt(radix)
}

@Throws(NumberFormatException::class)
@JvmOverloads
fun CharSequence.toBigDecimal(mathContext: MathContext = MathContext.UNLIMITED): BigDecimal {
    return when (val str = this.toString()) {
        "0" -> BigDecimal.ZERO
        "1" -> BigDecimal.ONE
        "10" -> BigDecimal.TEN
        else -> str.toBigDecimalKt(mathContext)
    }
}

@Throws(NumberFormatException::class)
@JvmOverloads
fun Any?.toByte(radix: Int = DEFAULT_RADIX): Byte {
    return when (this) {
        null -> 0
        is Byte -> this
        is Number -> this.toByte()
        else -> this.toCharSeq().toByte(radix)
    }
}

@Throws(NumberFormatException::class)
@JvmOverloads
fun Any?.toShort(radix: Int = DEFAULT_RADIX): Short {
    return when (this) {
        null -> 0
        is Short -> this
        is Number -> this.toShort()
        else -> this.toCharSeq().toShort(radix)
    }
}

@Throws(NumberFormatException::class)
@JvmOverloads
fun Any?.toChar(radix: Int = DEFAULT_RADIX): Char {
    return when (this) {
        null -> 0.toChar()
        is Char -> this
        is Number -> this.toInt().toChar()
        else -> this.toCharSeq().toChar(radix)
    }
}

@Throws(NumberFormatException::class)
@JvmOverloads
fun Any?.toInt(radix: Int = DEFAULT_RADIX): Int {
    return when (this) {
        null -> 0
        is Int -> this
        is Number -> this.toInt()
        else -> this.toCharSeq().toInt(radix)
    }
}

@Throws(NumberFormatException::class)
@JvmOverloads
fun Any?.toLong(radix: Int = DEFAULT_RADIX): Long {
    return when (this) {
        null -> 0
        is Long -> this
        is Number -> this.toLong()
        else -> this.toCharSeq().toLong(radix)
    }
}

@Throws(NumberFormatException::class)
fun Any?.toFloat(): Float {
    return when (this) {
        null -> 0f
        is Float -> this
        is Number -> this.toFloat()
        else -> this.toCharSeq().toFloat()
    }
}

@Throws(NumberFormatException::class)
fun Any?.toDouble(): Double {
    return when (this) {
        null -> 0.0
        is Double -> this
        is Number -> this.toDouble()
        else -> this.toCharSeq().toDouble()
    }
}

@Throws(NumberFormatException::class)
@JvmOverloads
fun Any?.toBigInteger(radix: Int = DEFAULT_RADIX): BigInteger {
    return when (this) {
        null -> BigInteger.ZERO
        is BigInteger -> this
        else -> this.toCharSeq().toBigInteger(radix)
    }
}

@Throws(NumberFormatException::class)
@JvmOverloads
fun Any?.toBigDecimal(mathContext: MathContext = MathContext.UNLIMITED): BigDecimal {
    return when (this) {
        null -> BigDecimal.ZERO
        is BigDecimal -> this
        else -> this.toCharSeq().toBigDecimal(mathContext)
    }
}

fun Byte.toUnsignedInt(): Int {
    return this.toInt() and 0x0000_00FF
}

fun Short.toUnsignedInt(): Int {
    return this.toInt() and 0x0000_FFFF
}

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
 * Given chars may have a sign prefix (`+/-`) followed by a radix prefix (`0b/0x/0`):
 *
 * * 123456: positive decimal
 * * 0xffeecc: positive hex;
 * * -0xffeecc: negative hex;
 * * +0774411: positive octal;
 * * 0b001100: positive binary;
 */
fun CharSequence.parseInt(): Int {

    fun parse(offset: Int): Int {
        if (this.startsWith("0x", offset)) {
            return this.substring(offset + 2).toIntKt(16)
        }
        if (this.startsWith("0b", offset)) {
            return this.substring(offset + 2).toIntKt(2)
        }
        if (this.startsWith("0", offset)) {
            return this.substring(offset + 1).toIntKt(8)
        }
        return this.substring(offset).toIntKt()
    }

    return if (this.startsWith("-")) {
        -parse(1)
    } else if (this.startsWith("+")) {
        parse(1)
    } else {
        parse(0)
    }
}

/**
 * Parses given chars to int.
 *
 * Given chars may have a sign prefix (`+/-`) followed by a radix prefix (`0b/0x/0`):
 *
 * * 123456: positive decimal
 * * 0xffeecc: positive hex;
 * * -0xffeecc: negative hex;
 * * +0774411: positive octal;
 * * 0b001100: positive binary;
 */
fun CharSequence.parseLong(): Long {

    fun parse(offset: Int): Long {
        if (this.startsWith("0x", offset)) {
            return this.substring(offset + 2).toLongKt(16)
        }
        if (this.startsWith("0b", offset)) {
            return this.substring(offset + 2).toLongKt(2)
        }
        if (this.startsWith("0", offset)) {
            return this.substring(offset + 1).toLongKt(8)
        }
        return this.substring(offset).toLongKt()
    }

    return if (this.startsWith("-")) {
        -parse(1)
    } else if (this.startsWith("+")) {
        parse(1)
    } else {
        parse(0)
    }
}

/**
 * Parses given chars to [BigInteger].
 *
 * Given chars may have a sign prefix (`+/-`) followed by a radix prefix (`0b/0x/0`):
 *
 * * 123456: positive decimal
 * * 0xffeecc: positive hex;
 * * -0xffeecc: negative hex;
 * * +0774411: positive octal;
 * * 0b001100: positive binary;
 */
fun CharSequence.parseBigInteger(): BigInteger {

    fun parse(offset: Int): BigInteger {
        if (this.startsWith("0x", offset)) {
            return BigInteger(this.substring(offset + 2), 16)
        }
        if (this.startsWith("0b", offset)) {
            return BigInteger(this.substring(offset + 2), 2)
        }
        if (this.startsWith("0", offset)) {
            return BigInteger(this.substring(offset + 1), 8)
        }
        return BigInteger(this.substring(offset))
    }

    return if (this.startsWith("-")) {
        parse(1).negate()
    } else if (this.startsWith("+")) {
        parse(1)
    } else {
        parse(0)
    }
}