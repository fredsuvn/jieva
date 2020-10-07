@file:JvmName("To")

package xyz.srclab.common.base

import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.text.toBoolean as stringToBoolean
import kotlin.text.toByte as stringToByte
import kotlin.text.toDouble as stringToDouble
import kotlin.text.toFloat as stringToFloat
import kotlin.text.toInt as stringToInt
import kotlin.text.toLong as stringToLong
import kotlin.text.toShort as stringToShort

fun Any?.toBoolean(): Boolean {
    return when (this) {
        null -> false
        is Boolean -> this
        else -> toString().stringToBoolean()
    }
}

fun Number?.toByte(): Byte {
    return this?.toByte() ?: 0
}

fun CharSequence?.toByte(): Byte {
    return this?.toString()?.stringToByte() ?: 0
}

fun Any?.toByte(): Byte {
    return when (this) {
        is Number -> toByte()
        is String -> stringToByte()
        else -> toString().stringToByte()
    }
}

fun Number?.toShort(): Short {
    return this?.toShort() ?: 0
}

fun CharSequence?.toShort(): Short {
    return this?.toString()?.stringToShort() ?: 0
}

fun Any?.toShort(): Short {
    return when (this) {
        is Number -> toShort()
        is String -> stringToShort()
        else -> toString().stringToShort()
    }
}

fun Number?.toChar(): Char {
    return this?.toChar() ?: 0.toChar()
}

fun CharSequence?.toChar(): Char {
    return this?.toString()?.stringToInt().toChar() ?: 0.toChar()
}

fun Any?.toChar(): Char {
    return when (this) {
        is Number -> toChar()
        is String -> stringToInt().toChar()
        else -> toString().stringToInt().toChar()
    }
}

fun Number?.toInt(): Int {
    return this?.toInt() ?: 0
}

fun CharSequence?.toInt(): Int {
    return this?.toString()?.stringToInt() ?: 0
}

fun Any?.toInt(): Int {
    return when (this) {
        is Number -> toInt()
        is String -> stringToInt()
        else -> toString().stringToInt()
    }
}

fun Number?.toLong(): Long {
    return this?.toLong() ?: 0
}

fun CharSequence?.toLong(): Long {
    return this?.toString()?.stringToLong() ?: 0
}

fun Any?.toLong(): Long {
    return when (this) {
        is Number -> toLong()
        is String -> stringToLong()
        else -> toString().stringToLong()
    }
}

fun Number?.toFloat(): Float {
    return this?.toFloat() ?: 0f
}

fun CharSequence?.toFloat(): Float {
    return this?.toString()?.stringToFloat() ?: 0f
}

fun Any?.toFloat(): Float {
    return when (this) {
        is Number -> toFloat()
        is String -> stringToFloat()
        else -> toString().stringToFloat()
    }
}

fun Number?.toDouble(): Double {
    return this?.toDouble() ?: 0.0
}

fun CharSequence?.toDouble(): Double {
    return this?.toString()?.stringToDouble() ?: 0.0
}

fun Any?.toDouble(): Double {
    return when (this) {
        is Number -> toDouble()
        is String -> stringToDouble()
        else -> toString().stringToDouble()
    }
}

fun Number?.toBigInteger(): BigInteger {
    return when (this) {
        null, 0, 0L, 0f, 0.0 -> BigInteger.ZERO
        1, 1L, 1f, 1.0 -> BigInteger.ONE
        10, 10L, 10f, 10.0 -> BigInteger.TEN
        is BigInteger -> this
        is BigDecimal -> toBigInteger()
        else -> BigInteger(toString())
    }
}

fun CharSequence?.toBigInteger(): BigInteger {
    return when (this) {
        null, "0" -> BigInteger.ZERO
        "1" -> BigInteger.ONE
        "10" -> BigInteger.TEN
        else -> BigInteger(toString())
    }
}

fun Any?.toBigInteger(): BigInteger {
    return when (this) {
        null -> BigInteger.ZERO
        is Number -> toBigInteger()
        is String -> toBigInteger()
        else -> toString().toBigInteger()
    }
}

fun Number?.toBigDecimal(): BigDecimal {
    return when (this) {
        null, 0, 0L, 0f, 0.0 -> BigDecimal.ZERO
        1, 1L, 1f, 1.0 -> BigDecimal.ONE
        10, 10L, 10f, 10.0 -> BigDecimal.TEN
        is BigInteger -> BigDecimal(this)
        is BigDecimal -> this
        else -> BigDecimal(toString())
    }
}

fun CharSequence?.toBigDecimal(): BigDecimal {
    return when (this) {
        null, "0" -> BigDecimal.ZERO
        "1" -> BigDecimal.ONE
        "10" -> BigDecimal.TEN
        else -> BigDecimal(toString())
    }
}

fun Any?.toBigDecimal(): BigDecimal {
    return when (this) {
        null -> BigDecimal.ZERO
        is Number -> toBigDecimal()
        is String -> toBigDecimal()
        else -> toString().toBigDecimal()
    }
}

fun CharArray.toChars(): String {
    return String(this)
}

fun ByteArray.toChars(): String {
    return String(this, Defaults.charset())
}

fun CharArray.toBytes(): ByteArray {
    return toChars().toByteArray(Defaults.charset())
}

fun CharSequence.toBytes(): ByteArray {
    return toString().toByteArray(Defaults.charset())
}

fun Any?.deepToString(): String {
    return when (this) {
        null -> toString()
        is Array<*> -> Arrays.deepToString(this)
        is BooleanArray -> Arrays.toString(this)
        is ByteArray -> Arrays.toString(this)
        is ShortArray -> Arrays.toString(this)
        is CharArray -> Arrays.toString(this)
        is IntArray -> Arrays.toString(this)
        is LongArray -> Arrays.toString(this)
        is FloatArray -> Arrays.toString(this)
        is DoubleArray -> Arrays.toString(this)
        else -> toString()
    }
}