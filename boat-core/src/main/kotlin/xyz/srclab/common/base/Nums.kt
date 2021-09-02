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

@JvmOverloads
fun Any?.toByte(radix: Int = Defaults.radix): Byte {
    return when (this) {
        null -> 0
        is Number -> if (radix == 10) toByte() else toString().toByteKt(radix)
        false -> 0
        true -> 1
        else -> toString().toByteKt(radix)
    }
}

@JvmOverloads
fun Any?.toShort(radix: Int = Defaults.radix): Short {
    return when (this) {
        null -> 0
        is Number -> if (radix == 10) toShort() else toString().toShortKt(radix)
        false -> 0
        true -> 1
        else -> toString().toShortKt(radix)
    }
}

@JvmOverloads
fun Any?.toChar(radix: Int = Defaults.radix): Char {
    return when (this) {
        null -> 0.toChar()
        is Number -> if (radix == 10) toChar() else toString().toIntKt(radix).toChar()
        false -> 0.toChar()
        true -> 1.toChar()
        else -> toString().toIntKt(radix).toChar()
    }
}

@JvmOverloads
fun Any?.toInt(radix: Int = Defaults.radix): Int {
    return when (this) {
        null -> 0
        is Number -> if (radix == 10) toInt() else toString().toIntKt(radix)
        false -> 0
        true -> 1
        else -> toString().toIntKt(radix)
    }
}

@JvmOverloads
fun Any?.toLong(radix: Int = Defaults.radix): Long {
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
fun Any?.toBigInteger(radix: Int = Defaults.radix): BigInteger {
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
 * **Unsigned** int to binary string.
 * It will pad `0` before binary string if [size] > binary string's size, or no padding if [size] <= `0`.
 *
 * @see Integer.toBinaryString
 */
@JvmOverloads
fun Int.toBinaryString(size: Int = 32): String {
    return StringUtils.leftPad(Integer.toBinaryString(this), size, "0")
}

/**
 * **Unsigned** long to binary string.
 * It will pad `0` before binary string if [size] > binary string's size, or no padding if [size] <= `0`.
 *
 * @see java.lang.Long.toBinaryString
 */
@JvmOverloads
fun Long.toBinaryString(size: Int = 64): String {
    return StringUtils.leftPad(java.lang.Long.toBinaryString(this), size, "0")
}

/**
 * **Unsigned** int to hex string.
 * It will pad `0` before binary string if [size] > binary string's size, or no padding if [size] <= `0`.
 *
 * @see Integer.toHexString
 */
@JvmOverloads
fun Int.toHexString(size: Int = 8): String {
    return StringUtils.leftPad(Integer.toHexString(this), size, "0")
}

/**
 * **Unsigned** long to hex string.
 * It will pad `0` before binary string if [size] > binary string's size, or no padding if [size] <= `0`.
 *
 * @see java.lang.Long.toHexString
 */
@JvmOverloads
fun Long.toHexString(size: Int = 16): String {
    return StringUtils.leftPad(java.lang.Long.toHexString(this), size, "0")
}

/**
 * **Unsigned** int to octal string.
 * It will pad `0` before binary string if [size] > binary string's size, or no padding if [size] <= `0`.
 *
 * @see Integer.toOctalString
 */
@JvmOverloads
fun Int.toOctalString(size: Int = 11): String {
    return StringUtils.leftPad(Integer.toOctalString(this), size, "0")
}

/**
 * **Unsigned** long to octal string.
 * It will pad `0` before binary string if [size] > binary string's size, or no padding if [size] <= `0`.
 *
 * @see java.lang.Long.toOctalString
 */
@JvmOverloads
fun Long.toOctalString(size: Int = 22): String {
    return StringUtils.leftPad(java.lang.Long.toOctalString(this), size, "0")
}