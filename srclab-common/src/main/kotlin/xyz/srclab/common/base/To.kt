@file:JvmName("To")
@file:JvmMultifileClass

package xyz.srclab.common.base

import java.math.BigDecimal
import java.math.BigInteger
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.util.*
import kotlin.text.toBoolean as stringToBoolean
import kotlin.text.toByte as stringToByte
import kotlin.text.toDouble as stringToDouble
import kotlin.text.toFloat as stringToFloat
import kotlin.text.toInt as stringToInt
import kotlin.text.toLong as stringToLong
import kotlin.text.toShort as stringToShort
import kotlin.toBigDecimal as bigIntegerToBigDecimal
import kotlin.toString as defaultToString

fun Any?.toBoolean(): Boolean {
    return when (this) {
        null -> false
        is Boolean -> this
        is Number -> toInt() != 0
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
        null, false -> 0
        true -> 1
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
        null, false -> 0
        true -> 1
        is Number -> toShort()
        is String -> stringToShort()
        else -> toString().stringToShort()
    }
}

fun Number?.toChar(): Char {
    return this?.toChar() ?: 0.toChar()
}

fun CharSequence?.toChar(): Char {
    return (this?.toString()?.stringToInt() ?: 0).toChar()
}

fun Any?.toChar(): Char {
    return when (this) {
        null, false -> 0.toChar()
        true -> 1.toChar()
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
        null, false -> 0
        true -> 1
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
        null, false -> 0L
        true -> 1L
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
        null, false -> 0f
        true -> 1f
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
        null, false -> 0.0
        true -> 1.0
        is Number -> toDouble()
        is String -> stringToDouble()
        else -> toString().stringToDouble()
    }
}

fun Number?.toBigInteger(): BigInteger {
    return when (this) {
        null -> BigInteger.ZERO
        is BigInteger -> this
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
        null, false -> BigInteger.ZERO
        true -> BigInteger.ONE
        is BigInteger -> this
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
        null -> BigDecimal.ZERO
        is BigDecimal -> this
        is BigInteger -> bigIntegerToBigDecimal()
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
        null, false -> BigDecimal.ZERO
        true -> BigDecimal.ONE
        is BigDecimal -> this
        is BigInteger -> bigIntegerToBigDecimal()
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
    return defaultToString()
}

fun Any?.elementToString(): String {
    return when (this) {
        null -> defaultToString()
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
        null -> defaultToString()
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

fun Number?.toInstant(): Instant {
    return when (this) {
        null, 0, 0L, 0f, 0.0 -> Instant.MIN
        else -> Instant.ofEpochMilli(toLong())
    }
}

private val MIN_DATE = Date(0)
private val ISO_DATE_FORMAT = SimpleDateFormat(DateTimeFormatter.ISO_ZONED_DATE_TIME.toString())

fun Any?.toDate(): Date {
    return toDate(ISO_DATE_FORMAT)
}

fun Any?.toDate(datePattern: String): Date {
    return toDate(SimpleDateFormat(datePattern))
}

fun Any?.toDate(dateFormat: DateFormat): Date {
    return when (this) {
        null, false -> MIN_DATE
        is Date -> this
        is Instant -> Date.from(this)
        is Temporal -> Date.from(Instant.from(this))
        is Number -> when (val value = toLong()) {
            0L -> MIN_DATE
            else -> Date(value)
        }
        true -> Date()
        else -> dateFormat.parse(toString())
    }
}

fun Any?.toInstant(): Instant {
    return toInstant(DateTimeFormatter.ISO_INSTANT)
}

fun Any?.toInstant(dateTimePattern: String): Instant {
    return toInstant(DateTimeFormatter.ofPattern(dateTimePattern))
}

fun Any?.toInstant(dateTimeFormatter: DateTimeFormatter): Instant {
    return when (this) {
        null, false -> Instant.MIN
        is Instant -> this
        is Date -> Instant.ofEpochMilli(time)
        is Temporal -> Instant.from(this)
        is Number -> when (val value = toLong()) {
            0L -> Instant.MIN
            else -> Instant.ofEpochMilli(value)
        }
        true -> Instant.now()
        else -> {
            val temporal = dateTimeFormatter.parse(toString())
            return if (temporal is Instant) temporal else Instant.from(temporal)
        }
    }
}

fun Any?.toTimestamp(): Instant {
    return toInstant(Defaults.timestampFormatter)
}

fun Any?.toLocalDateTime(): LocalDateTime {
    return toLocalDateTime(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
}

fun Any?.toLocalDateTime(dateTimePattern: String): LocalDateTime {
    return toLocalDateTime(DateTimeFormatter.ofPattern(dateTimePattern))
}

fun Any?.toLocalDateTime(dateTimeFormatter: DateTimeFormatter): LocalDateTime {
    return LocalDateTime.ofInstant(toInstant(dateTimeFormatter), ZoneId.systemDefault())
}

fun Any?.toZonedDateTime(): ZonedDateTime {
    return toZonedDateTime(DateTimeFormatter.ISO_ZONED_DATE_TIME)
}

fun Any?.toZonedDateTime(dateTimePattern: String): ZonedDateTime {
    return toZonedDateTime(DateTimeFormatter.ofPattern(dateTimePattern))
}

fun Any?.toZonedDateTime(dateTimeFormatter: DateTimeFormatter): ZonedDateTime {
    return toInstant(dateTimeFormatter).atZone(ZoneId.systemDefault())
}

fun Any?.toOffsetDateTime(): OffsetDateTime {
    return toOffsetDateTime(DateTimeFormatter.ISO_ZONED_DATE_TIME)
}

fun Any?.toOffsetDateTime(dateTimePattern: String): OffsetDateTime {
    return toOffsetDateTime(DateTimeFormatter.ofPattern(dateTimePattern))
}

fun Any?.toOffsetDateTime(dateTimeFormatter: DateTimeFormatter): OffsetDateTime {
    return toZonedDateTime(dateTimeFormatter).toOffsetDateTime()
}

fun Any?.toDuration(): Duration {
    return when (this) {
        null, false -> Duration.ZERO
        is Duration -> this
        is Number -> when (val value = toLong()) {
            0L -> Duration.ZERO
            else -> Duration.ofMillis(value)
        }
        else -> Duration.parse(toString())
    }
}