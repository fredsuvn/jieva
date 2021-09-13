@file:JvmName("Nums")

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

@JvmOverloads
fun Any?.toByte(radix: Int = DEFAULT_RADIX): Byte {
    return when (this) {
        null -> 0
        is Number -> if (radix == 10) toByte() else toString().toByteKt(radix)
        false -> 0
        true -> 1
        else -> toString().toByteKt(radix)
    }
}

@JvmOverloads
fun Any?.toShort(radix: Int = DEFAULT_RADIX): Short {
    return when (this) {
        null -> 0
        is Number -> if (radix == 10) toShort() else toString().toShortKt(radix)
        false -> 0
        true -> 1
        else -> toString().toShortKt(radix)
    }
}

@JvmOverloads
fun Any?.toChar(radix: Int = DEFAULT_RADIX): Char {
    return when (this) {
        null -> 0.toChar()
        is Number -> if (radix == 10) toChar() else toString().toIntKt(radix).toChar()
        false -> 0.toChar()
        true -> 1.toChar()
        else -> toString().toIntKt(radix).toChar()
    }
}

@JvmOverloads
fun Any?.toInt(radix: Int = DEFAULT_RADIX): Int {
    return when (this) {
        null -> 0
        is Number -> if (radix == 10) toInt() else toString().toIntKt(radix)
        false -> 0
        true -> 1
        else -> toString().toIntKt(radix)
    }
}

@JvmOverloads
fun Any?.toLong(radix: Int = DEFAULT_RADIX): Long {
    return when (this) {
        null -> 0L
        is Number -> if (radix == 10) toLong() else toString().toLongKt(radix)
        false -> 0L
        true -> 1L
        else -> toString().toLongKt(radix)
    }
}

fun Any?.toFloat(): Float {
    return when (this) {
        null -> 0f
        is Number -> toFloat()
        false -> 0f
        true -> 1f
        else -> toString().toFloatKt()
    }
}

fun Any?.toDouble(): Double {
    return when (this) {
        null -> 0.0
        is Number -> toDouble()
        false -> 0.0
        true -> 1.0
        else -> toString().toDoubleKt()
    }
}

@JvmOverloads
fun Any?.toBigInteger(radix: Int = DEFAULT_RADIX): BigInteger {
    if (this === null) {
        return BigInteger.ZERO
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

fun Any?.toBigDecimal(mathContext: MathContext = MathContext.UNLIMITED): BigDecimal {
    if (this === null) {
        return BigDecimal.ZERO
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
    return StringUtils.leftPad(java.lang.Long.toBinaryString(this), size, "0")
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
    return StringUtils.leftPad(java.lang.Long.toHexString(this), size, "0")
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
    return StringUtils.leftPad(java.lang.Long.toOctalString(this), size, "0")
}

/**
 * Parses given chars to [BigInteger].
 *
 * Given chars must have two parts: `sign` and `body`:
 *
 * * Sign: may be `+` or `-` at first char, or none;
 * * Body: may be hex, binary, octal or decimal;
 *
 * For sign:
 *
 * * `+` if positive;
 * * `-` if negative;
 *
 * For body:
 *
 * * Hex if starts with `0x`;
 * * Binary if starts with `0b`;
 * * Octal if starts with `0`;
 *
 * Examples:
 *
 * * 0xffeecc: a positive hex chars;
 * * -0xffeecc: a negative hex chars;
 * * +0774411: a positive octal chars;
 * * 0b001100: a positive binary chars;
 */
fun CharSequence?.parseToBigInteger(): BigInteger {
    if (this.isNullOrBlank()) {
        return BigInteger.ZERO
    }

    fun parse(chars: CharSequence, offset: Int): BigInteger {
        if (chars.startsWith("0x", offset)) {
            return BigInteger(chars.substring(offset + 2), 16)
        }
        if (chars.startsWith("0b", offset)) {
            return BigInteger(chars.substring(offset + 2), 2)
        }
        if (chars.startsWith("0", offset)) {
            return BigInteger(chars.substring(offset + 1), 8)
        }
        return BigInteger(chars.substring(offset))
    }

    return if (this.startsWith("-")) {
        parse(this, 1).negate()
    } else if (this.startsWith("+")) {
        parse(this, 1)
    } else {
        parse(this, 0)
    }
}