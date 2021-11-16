@file:JvmName("BNumbers")

package xyz.srclab.common.base

import com.google.common.primitives.UnsignedBytes
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
 * Default number value of null: 0
 */
const val DEFAULT_NULL_NUMBER = 0

/**
 * Default radix: 10.
 */
const val DEFAULT_RADIX: Int = 10

@JvmOverloads
fun Any?.toByte(defaultValue: Byte = DEFAULT_NULL_NUMBER.toByte(), radix: Int = DEFAULT_RADIX): Byte {
    return when (this) {
        null -> defaultValue
        is Number -> if (radix == 10) toByte() else toString().toByteKt(radix)
        false -> 0
        true -> 1
        else -> toString().toByteKt(radix)
    }
}

@JvmOverloads
fun Any?.toShort(defaultValue: Short = DEFAULT_NULL_NUMBER.toShort(), radix: Int = DEFAULT_RADIX): Short {
    return when (this) {
        null -> defaultValue
        is Number -> if (radix == 10) toShort() else toString().toShortKt(radix)
        false -> 0
        true -> 1
        else -> toString().toShortKt(radix)
    }
}

@JvmOverloads
fun Any?.toChar(defaultValue: Char = DEFAULT_NULL_NUMBER.toChar(), radix: Int = DEFAULT_RADIX): Char {
    return toInt(radix).toChar(defaultValue)
}

@JvmOverloads
fun Any?.toInt(defaultValue: Int = DEFAULT_NULL_NUMBER, radix: Int = DEFAULT_RADIX): Int {
    return when (this) {
        null -> defaultValue
        is Number -> if (radix == 10) toInt() else toString().toIntKt(radix)
        false -> 0
        true -> 1
        else -> toString().toIntKt(radix)
    }
}

@JvmOverloads
fun Any?.toLong(defaultValue: Long = DEFAULT_NULL_NUMBER.toLong(), radix: Int = DEFAULT_RADIX): Long {
    return when (this) {
        null -> defaultValue
        is Number -> if (radix == 10) toLong() else toString().toLongKt(radix)
        false -> 0L
        true -> 1L
        else -> toString().toLongKt(radix)
    }
}

@JvmOverloads
fun Any?.toFloat(defaultValue: Float = DEFAULT_NULL_NUMBER.toFloat()): Float {
    return when (this) {
        null -> defaultValue
        is Number -> toFloat()
        false -> 0f
        true -> 1f
        else -> toString().toFloatKt()
    }
}

@JvmOverloads
fun Any?.toDouble(defaultValue: Double = DEFAULT_NULL_NUMBER.toDouble()): Double {
    return when (this) {
        null -> defaultValue
        is Number -> toDouble()
        false -> 0.0
        true -> 1.0
        else -> toString().toDoubleKt()
    }
}

@JvmOverloads
fun Any?.toBigInteger(defaultValue: BigInteger = BigInteger.ZERO, radix: Int = DEFAULT_RADIX): BigInteger {
    if (this === null) {
        return defaultValue
    }
    if (this is BigInteger) {
        return this
    }
    val str = this.toString()
    if (str.isEmpty()) {
        return BigInteger.ZERO
    }
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

@JvmOverloads
fun Any?.toBigDecimal(
    defaultValue: BigDecimal = BigDecimal.ZERO,
    mathContext: MathContext = MathContext.UNLIMITED
): BigDecimal {
    if (this === null) {
        return defaultValue
    }
    if (this is BigDecimal) {
        return this
    }
    val str = this.toString()
    if (str.isEmpty()) {
        return BigDecimal.ZERO
    }
    return when (str) {
        "0" -> BigDecimal.ZERO
        "1" -> BigDecimal.ONE
        "10" -> BigDecimal.TEN
        else -> str.toBigDecimalKt(mathContext)
    }
}

fun Byte.toUnsignedInt(): Int {
    return UnsignedBytes.toInt(this)
}

fun Byte.toUnsignedLong(): Long {
    return this.toLong() and 0xFF
}

fun Short.toUnsignedInt(): Int {
    return this.toInt() and 0xFFFF
}

fun Short.toUnsignedLong(): Long {
    return this.toLong() and 0xFFFF
}

fun Int.toUnsignedLong(): Long {
    return this.toLong() and 0xFFFFFFFF
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
@JvmOverloads
fun CharSequence?.parseToBigInteger(defaultValue: BigInteger = BigInteger.ZERO): BigInteger {
    if (this.isNullOrBlank()) {
        return defaultValue
    }

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
 *
 * @see parseToBigInteger
 */
@JvmOverloads
fun CharSequence?.parseToInt(defaultValue: Int = DEFAULT_NULL_NUMBER): Int {
    if (this === null) {
        return defaultValue
    }
    return parseToBigInteger().toInt()
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
 *
 * @see parseToBigInteger
 */
@JvmOverloads
fun CharSequence?.parseToLong(defaultValue: Long = DEFAULT_NULL_NUMBER.toLong()): Long {
    if (this === null) {
        return defaultValue
    }
    return parseToBigInteger().toLong()
}