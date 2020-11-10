@file:JvmName("Num")
@file:JvmMultifileClass

package xyz.srclab.common.base

import org.apache.commons.lang3.StringUtils
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.text.toBigDecimal as toBigDecimalKt
import kotlin.text.toBigInteger as toBigIntegerKt
import kotlin.text.toByte as toByteKt
import kotlin.text.toDouble as toDoubleKt
import kotlin.text.toFloat as toFloatKt
import kotlin.text.toInt as toIntKt
import kotlin.text.toLong as toLongKt
import kotlin.text.toShort as toShortKt
import kotlin.toBigDecimal as numberToBigDecimalKt
import kotlin.toBigInteger as numberToBigIntegerKt

fun CharSequence?.isNumeric(): Boolean {
    return StringUtils.isNumeric(this)
}

fun CharSequence?.isNumericSpace(): Boolean {
    return StringUtils.isNumericSpace(this)
}

@JvmOverloads
fun CharSequence.toByte(radix: Int = Defaults.radix): Byte {
    return this.toString().toByteKt(radix)
}

@JvmOverloads
fun Any?.toByte(radix: Int = Defaults.radix): Byte {
    return when (this) {
        null -> 0
        is Number -> if (radix == 10) toByte() else toString().toByteKt(radix)
        false -> 0
        true -> 1
        is String -> toByteKt(radix)
        else -> toString().toByteKt(radix)
    }
}

@JvmOverloads
fun CharSequence.toShort(radix: Int = Defaults.radix): Short {
    return this.toString().toShortKt(radix)
}

@JvmOverloads
fun Any?.toShort(radix: Int = Defaults.radix): Short {
    return when (this) {
        null -> 0
        is Number -> if (radix == 10) toShort() else toString().toShortKt(radix)
        false -> 0
        true -> 1
        is String -> toShortKt(radix)
        else -> toString().toShortKt(radix)
    }
}

@JvmOverloads
fun CharSequence.toChar(radix: Int = Defaults.radix): Char {
    return this.toString().toIntKt(radix).toChar()
}

@JvmOverloads
fun Any?.toChar(radix: Int = Defaults.radix): Char {
    return when (this) {
        null -> 0.toChar()
        is Number -> if (radix == 10) toChar() else toString().toIntKt(radix).toChar()
        false -> 0.toChar()
        true -> 1.toChar()
        is String -> toIntKt(radix).toChar()
        else -> toString().toIntKt(radix).toChar()
    }
}

@JvmOverloads
fun CharSequence.toInt(radix: Int = Defaults.radix): Int {
    return this.toString().toIntKt(radix)
}

@JvmOverloads
fun Any?.toInt(radix: Int = Defaults.radix): Int {
    return when (this) {
        null -> 0
        is Number -> if (radix == 10) toInt() else toString().toIntKt(radix)
        false -> 0
        true -> 1
        is String -> toIntKt(radix)
        else -> toString().toIntKt(radix)
    }
}

@JvmOverloads
fun CharSequence.toLong(radix: Int = Defaults.radix): Long {
    return this.toString().toLongKt(radix)
}

@JvmOverloads
fun Any?.toLong(radix: Int = Defaults.radix): Long {
    return when (this) {
        null -> 0L
        is Number -> if (radix == 10) toLong() else toString().toLongKt(radix)
        false -> 0L
        true -> 1L
        is String -> toLongKt(radix)
        else -> toString().toLongKt(radix)
    }
}

fun CharSequence.toFloat(): Float {
    return this.toString().toFloatKt()
}

fun Any?.toFloat(): Float {
    return when (this) {
        null -> 0f
        is Number -> toFloat()
        false -> 0f
        true -> 1f
        is String -> toFloatKt()
        else -> toString().toFloatKt()
    }
}

fun CharSequence.toDouble(): Double {
    return this.toString().toDoubleKt()
}

fun Any?.toDouble(): Double {
    return when (this) {
        null -> 0.0
        is Number -> toDouble()
        false -> 0.0
        true -> 1.0
        is String -> toDoubleKt()
        else -> toString().toDoubleKt()
    }
}

@JvmOverloads
fun CharSequence.toBigInteger(radix: Int = Defaults.radix): BigInteger {
    if (radix == 10) {
        return when (this) {
            "", "0" -> BigInteger.ZERO
            "1" -> BigInteger.ONE
            "10" -> BigInteger.TEN
            else -> toString().toBigIntegerKt()
        }
    }
    return toString().toBigIntegerKt(radix)
}

@JvmOverloads
fun Any?.toBigInteger(radix: Int = Defaults.radix): BigInteger {
    if (radix == 10) {
        return when (this) {
            null -> BigInteger.ZERO
            is BigInteger -> this
            false -> BigInteger.ZERO
            true -> BigInteger.ONE
            is BigDecimal -> toBigInteger()
            is Number -> toLong().numberToBigIntegerKt()
            else -> toString().toBigIntegerKt()
        }
    }
    return when (this) {
        null -> BigInteger.ZERO
        is BigInteger -> this
        false -> BigInteger.ZERO
        true -> BigInteger.ONE
        is BigDecimal -> toBigInteger()
        else -> toString().toBigIntegerKt(radix)
    }
}

fun CharSequence.toBigDecimal(): BigDecimal {
    return when (this) {
        "", "0" -> BigDecimal.ZERO
        "1" -> BigDecimal.ONE
        "10" -> BigDecimal.TEN
        else -> toString().toBigDecimalKt()
    }
}

fun Any?.toBigDecimal(): BigDecimal {
    return when (this) {
        null -> BigDecimal.ZERO
        is BigDecimal -> this
        false -> BigDecimal.ZERO
        true -> BigDecimal.ONE
        is BigInteger -> numberToBigDecimalKt()
        is Int, Long, Byte, Short -> (this as Number).toLong().numberToBigDecimalKt()
        is Char -> toLong().numberToBigDecimalKt()
        is Float, Double -> (this as Number).toDouble().numberToBigDecimalKt()
        else -> toString().toBigDecimalKt()
    }
}