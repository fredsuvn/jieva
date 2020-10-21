@file:JvmName("DateTime")

package xyz.srclab.common.base

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.util.*

@JvmField
val MIN_DATE = Date(0)

@JvmField
val ISO_ZONED_DATE_TIME_FORMAT: SimpleDateFormat = SimpleDateFormat(DateTimeFormatter.ISO_ZONED_DATE_TIME.toString())

@JvmField
val MIN_UTC_ZONED_DATE_TIME: ZonedDateTime = ZonedDateTime.ofInstant(Instant.MIN, ZoneOffset.UTC)

fun Number.toDate(): Date {
    return Date(toLong())
}

fun

fun Any?.toDate(datePattern: String): Date {
    return toDate(SimpleDateFormat(datePattern))
}

@JvmOverloads
fun Any?.toDate(dateFormat: DateFormat = ISO_ZONED_DATE_TIME_FORMAT): Date {
    return when (this) {
        is Date -> this
        null, false -> MIN_DATE
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

fun Number?.toInstant(): Instant {
    return when (val value = toLong()) {
        0L -> Instant.MIN
        else -> Instant.ofEpochMilli(value)
    }
}

fun Any?.toInstant(dateTimePattern: String): Instant {
    return toInstant(DateTimeFormatter.ofPattern(dateTimePattern))
}

@JvmOverloads
fun Any?.toInstant(dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT): Instant {
    return when (this) {
        is Instant -> this
        null, false -> Instant.MIN
        is Date -> this.toInstant()
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
fun Number?.toLocalDateTime(): LocalDateTime {
    return when (val value = toLong()) {
        0L -> LocalDateTime.MIN
        else -> LocalDateTime.ofEpochSecond(value)
    }
}

fun Any?.toLocalDateTime(dateTimePattern: String): LocalDateTime {
    return toLocalDateTime(DateTimeFormatter.ofPattern(dateTimePattern))
}

@JvmOverloads
fun Any?.toLocalDateTime(dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME): LocalDateTime {
    return when (this) {
        is LocalDateTime -> this
        null, false -> LocalDateTime.MIN
        is Date -> this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        is Instant -> this.atZone(ZoneId.systemDefault()).toLocalDateTime()
        is Temporal -> LocalDateTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> LocalDateTime.MIN
            else -> LocalDateTime.ofEpochSecond(value)
        }
        true -> LocalDateTime.now()
        else -> {
            val temporal = dateTimeFormatter.parse(toString())
            return if (temporal is LocalDateTime) temporal else LocalDateTime.from(temporal)
        }
    }
}

fun Any?.toZonedDateTime(dateTimePattern: String): ZonedDateTime {
    return toZonedDateTime(DateTimeFormatter.ofPattern(dateTimePattern))
}

@JvmOverloads
fun Any?.toZonedDateTime(dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME): ZonedDateTime {
    return when (this) {
        is ZonedDateTime -> this
        null, false -> MIN_UTC_ZONED_DATE_TIME
        is Date -> this.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        is Instant -> this.atZone(ZoneId.systemDefault()).toLocalDateTime()
        is Temporal -> LocalDateTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> ZonedDateTime.
            else -> LocalDateTime.ofEpochSecond(value)
        }
        true -> ZonedDateTime.now()
        else -> {
            val temporal = dateTimeFormatter.parse(toString())
            return if (temporal is ZonedDateTime) temporal else ZonedDateTime.from(temporal)
        }
    }
}

fun Any?.toOffsetDateTime(dateTimePattern: String): OffsetDateTime {
    return toOffsetDateTime(DateTimeFormatter.ofPattern(dateTimePattern))
}

@JvmOverloads
fun Any?.toOffsetDateTime(dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME): OffsetDateTime {
    return toZonedDateTime(dateTimeFormatter).toOffsetDateTime()
}

fun Any?.toLocalDate(dateTimePattern: String): LocalDate {
    return toLocalDate(DateTimeFormatter.ofPattern(dateTimePattern))
}

@JvmOverloads
fun Any?.toLocalDate(dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE): LocalDate {
    return when (this) {
        is LocalDate -> this
        null, false -> LocalDate.MIN
        is Date -> this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        is Instant -> this.atZone(ZoneId.systemDefault()).toLocalDate()
        is Temporal -> LocalDate.from(this)
        is Number -> when (val value = toLong()) {
            0L -> LocalDate.MIN
            else -> LocalDate.ofEpochDay(value)
        }
        true -> LocalDate.now()
        else -> {
            val temporal = dateTimeFormatter.parse(toString())
            return if (temporal is LocalDate) temporal else LocalDate.from(temporal)
        }
    }
}

fun Any?.toLocalTime(dateTimePattern: String): LocalTime {
    return toLocalTime(DateTimeFormatter.ofPattern(dateTimePattern))
}

@JvmOverloads
fun Any?.toLocalTime(dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME): LocalTime {
    return when (this) {
        is LocalTime -> this
        null, false -> LocalTime.MIN
        is Date -> this.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
        is Instant -> this.atZone(ZoneId.systemDefault()).toLocalTime()
        is Temporal -> LocalTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> LocalTime.MIN
            else -> LocalTime.ofSecondOfDay(value)
        }
        true -> LocalTime.now()
        else -> {
            val temporal = dateTimeFormatter.parse(toString())
            return if (temporal is LocalTime) temporal else LocalTime.from(temporal)
        }
    }
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

fun Any?.toTimestamp(): String {
    return Defaults.timestampFormatter.format(this.toZonedDateTime())
}