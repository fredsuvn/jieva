@file:JvmName("To")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.math.BigDecimal
import java.math.BigInteger
import java.util.*
import kotlin.text.toBoolean as toBooleanKt
import kotlin.text.toByte as toByteKt
import kotlin.text.toDouble as toDoubleKt
import kotlin.text.toFloat as toFloatKt
import kotlin.text.toInt as toIntKt
import kotlin.text.toLong as toLongKt
import kotlin.text.toShort as toShortKt
import kotlin.toBigDecimal as toBigDecimalKt
import kotlin.toString as toStringKt

fun Any?.toBoolean(): Boolean {
    return when (this) {
        is Boolean -> this
        null -> false
        is Number -> toInt() != 0
        else -> toString().toBooleanKt()
    }
}

fun CharSequence?.toByte(): Byte {
    return this?.toString()?.toByteKt() ?: 0
}

fun Any?.toByte(): Byte {
    return when (this) {
        null, false -> 0
        true -> 1
        is Number -> toByte()
        is String -> toByteKt()
        else -> toString().toByteKt()
    }
}

fun Number?.toShort(): Short {
    return this?.toShort() ?: 0
}

fun CharSequence?.toShort(): Short {
    return this?.toString()?.toShortKt() ?: 0
}

fun Any?.toShort(): Short {
    return when (this) {
        null, false -> 0
        true -> 1
        is Number -> toShort()
        is String -> toShortKt()
        else -> toString().toShortKt()
    }
}

fun Number?.toChar(): Char {
    return this?.toChar() ?: 0.toChar()
}

fun CharSequence?.toChar(): Char {
    return (this?.toString()?.toIntKt() ?: 0).toChar()
}

fun Any?.toChar(): Char {
    return when (this) {
        null, false -> 0.toChar()
        true -> 1.toChar()
        is Number -> toChar()
        is String -> toIntKt().toChar()
        else -> toString().toIntKt().toChar()
    }
}

fun Number?.toInt(): Int {
    return this?.toInt() ?: 0
}

fun CharSequence?.toInt(): Int {
    return this?.toString()?.toIntKt() ?: 0
}

fun Any?.toInt(): Int {
    return when (this) {
        null, false -> 0
        true -> 1
        is Number -> toInt()
        is String -> toIntKt()
        else -> toString().toIntKt()
    }
}

fun Number?.toLong(): Long {
    return this?.toLong() ?: 0
}

fun CharSequence?.toLong(): Long {
    return this?.toString()?.toLongKt() ?: 0
}

fun Any?.toLong(): Long {
    return when (this) {
        null, false -> 0L
        true -> 1L
        is Number -> toLong()
        is String -> toLongKt()
        else -> toString().toLongKt()
    }
}

fun Number?.toFloat(): Float {
    return this?.toFloat() ?: 0f
}

fun CharSequence?.toFloat(): Float {
    return this?.toString()?.toFloatKt() ?: 0f
}

fun Any?.toFloat(): Float {
    return when (this) {
        null, false -> 0f
        true -> 1f
        is Number -> toFloat()
        is String -> toFloatKt()
        else -> toString().toFloatKt()
    }
}

fun Number?.toDouble(): Double {
    return this?.toDouble() ?: 0.0
}

fun CharSequence?.toDouble(): Double {
    return this?.toString()?.toDoubleKt() ?: 0.0
}

fun Any?.toDouble(): Double {
    return when (this) {
        null, false -> 0.0
        true -> 1.0
        is Number -> toDouble()
        is String -> toDoubleKt()
        else -> toString().toDoubleKt()
    }
}

fun Number?.toBigInteger(): BigInteger {
    return when (this) {
        is BigInteger -> this
        null -> BigInteger.ZERO
        is BigDecimal -> toBigInteger()
        else -> when (toInt()) {
            0 -> BigInteger.ZERO
            1 -> BigInteger.ONE
            10 -> BigInteger.TEN
            else -> BigInteger(toString())
        }
    }
}

fun CharSequence?.toBigInteger(): BigInteger {
    return when (this) {
        null, "", "0" -> BigInteger.ZERO
        "1" -> BigInteger.ONE
        "10" -> BigInteger.TEN
        else -> BigInteger(toString())
    }
}

fun Any?.toBigInteger(): BigInteger {
    return when (this) {
        is BigInteger -> this
        null, false -> BigInteger.ZERO
        true -> BigInteger.ONE
        is BigDecimal -> toBigInteger()
        is Number -> when (toInt()) {
            0 -> BigInteger.ZERO
            1 -> BigInteger.ONE
            10 -> BigInteger.TEN
            else -> BigInteger(toString())
        }
        else -> BigInteger(toString())
    }
}

fun Number?.toBigDecimal(): BigDecimal {
    return when (this) {
        is BigDecimal -> this
        null -> BigDecimal.ZERO
        is BigInteger -> toBigDecimalKt()
        else -> when (toInt()) {
            0 -> BigDecimal.ZERO
            1 -> BigDecimal.ONE
            10 -> BigDecimal.TEN
            else -> BigDecimal(toString())
        }
    }
}

fun CharSequence?.toBigDecimal(): BigDecimal {
    return when (this) {
        null, "", "0" -> BigDecimal.ZERO
        "1" -> BigDecimal.ONE
        "10" -> BigDecimal.TEN
        else -> BigDecimal(toString())
    }
}

fun Any?.toBigDecimal(): BigDecimal {
    return when (this) {
        is BigDecimal -> this
        null, false -> BigDecimal.ZERO
        true -> BigDecimal.ONE
        is BigInteger -> toBigDecimalKt()
        is Number -> when (toInt()) {
            0 -> BigDecimal.ZERO
            1 -> BigDecimal.ONE
            10 -> BigDecimal.TEN
            else -> BigDecimal(toString())
        }
        else -> BigDecimal(toString())
    }
}

fun CharArray.toChars(): String {
    return String(this)
}

fun ByteArray.toChars(): String {
    return String(this, Defaults.charset)
}

fun CharArray.toBytes(): ByteArray {
    return toChars().toByteArray(Defaults.charset)
}

fun CharSequence.toBytes(): ByteArray {
    return toString().toByteArray(Defaults.charset)
}

fun Any?.toString(): String {
    return toStringKt()
}

fun Any?.elementToString(): String {
    return when (this) {
        null -> toStringKt()
        is Array<*> -> Arrays.toString(this)
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

fun Any?.elementDeepToString(): String {
    return when (this) {
        null -> toStringKt()
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