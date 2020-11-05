@file:JvmName("DateTime")

package xyz.srclab.common.base

import org.apache.commons.lang3.time.DateFormatUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.util.*

fun milliseconds(): Long {
    return System.currentTimeMillis()
}

fun nanoseconds(): Long {
    return System.nanoTime()
}

fun timestamp(): String {
    return Defaults.timestampFormatter.format(LocalDateTime.now())
}

@JvmField
val MIN_DATE = Date(0)

@JvmField
val MIN_ZONED_DATE_TIME: ZonedDateTime = OffsetDateTime.MIN.toZonedDateTime()

@JvmField
val ISO_DATE_FORMAT = DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.pattern

@JvmOverloads
fun dateFormat(pattern: String = ISO_DATE_FORMAT): DateFormat {
    return SimpleDateFormat(pattern)
}

fun Any?.toDate(datePattern: String): Date {
    return toDate(SimpleDateFormat(datePattern))
}

@JvmOverloads
fun Any?.toDate(dateFormat: DateFormat = dateFormat()): Date {
    return when (this) {
        null -> MIN_DATE
        is Date -> this
        is Instant -> Date.from(this)
        is LocalDateTime -> Date.from(toInstant(OffsetDateTime.now().offset))
        is ZonedDateTime -> Date.from(toInstant())
        is OffsetDateTime -> Date.from(toInstant())
        is LocalDate -> Date.from(ZonedDateTime.of(this, LocalTime.MIN, ZoneId.systemDefault()).toInstant())
        is LocalTime -> Date.from(ZonedDateTime.of(LocalDate.MIN, this, ZoneId.systemDefault()).toInstant())
        is Temporal -> Date.from(Instant.from(this))
        is Number -> when (val value = toLong()) {
            0L -> MIN_DATE
            else -> Date(value)
        }
        false -> MIN_DATE
        true -> Date()
        else -> dateFormat.parse(toString())
    }
}

fun Any?.toInstant(dateTimePattern: String): Instant {
    return toInstant(DateTimeFormatter.ofPattern(dateTimePattern))
}

@JvmOverloads
fun Any?.toInstant(dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT): Instant {
    return when (this) {
        null -> Instant.MIN
        is Instant -> this
        is Date -> toInstant()
        is LocalDateTime -> toInstant(ZonedDateTime.now().offset)
        is ZonedDateTime -> toInstant()
        is OffsetDateTime -> toInstant()
        is LocalDate -> ZonedDateTime.of(this, LocalTime.MIN, ZoneId.systemDefault()).toInstant()
        is LocalTime -> ZonedDateTime.of(LocalDate.MIN, this, ZoneId.systemDefault()).toInstant()
        is Temporal -> Instant.from(this)
        is Number -> when (val value = toLong()) {
            0L -> Instant.MIN
            else -> Instant.ofEpochMilli(value)
        }
        false -> Instant.MIN
        true -> Instant.now()
        else -> return Instant.from(dateTimeFormatter.parse(toString()))
    }
}

fun Any?.toLocalDateTime(dateTimePattern: String): LocalDateTime {
    return toLocalDateTime(DateTimeFormatter.ofPattern(dateTimePattern))
}

@JvmOverloads
fun Any?.toLocalDateTime(dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME): LocalDateTime {
    return when (this) {
        null -> LocalDateTime.MIN
        is LocalDateTime -> this
        is Date -> toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        is Instant -> atZone(ZoneId.systemDefault()).toLocalDateTime()
        is ZonedDateTime -> toLocalDateTime()
        is OffsetDateTime -> toLocalDateTime()
        is LocalDate -> LocalDateTime.of(this, LocalTime.MIN)
        is LocalTime -> LocalDateTime.of(LocalDate.MIN, this)
        is Temporal -> LocalDateTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> LocalDateTime.MIN
            else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
        false -> LocalDateTime.MIN
        true -> LocalDateTime.now()
        else -> return LocalDateTime.from(dateTimeFormatter.parse(toString()))
    }
}

fun Any?.toZonedDateTime(dateTimePattern: String): ZonedDateTime {
    return toZonedDateTime(DateTimeFormatter.ofPattern(dateTimePattern))
}

@JvmOverloads
fun Any?.toZonedDateTime(dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME): ZonedDateTime {
    return when (this) {
        null -> MIN_ZONED_DATE_TIME
        is ZonedDateTime -> this
        is Date -> toInstant().atZone(ZoneId.systemDefault())
        is Instant -> atZone(ZoneId.systemDefault())
        is LocalDateTime -> ZonedDateTime.of(this, ZoneId.systemDefault())
        is OffsetDateTime -> toZonedDateTime()
        is LocalDate -> ZonedDateTime.of(this, LocalTime.MIN, ZoneId.systemDefault())
        is LocalTime -> ZonedDateTime.of(LocalDate.MIN, this, ZoneId.systemDefault())
        is Temporal -> ZonedDateTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> MIN_ZONED_DATE_TIME
            else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault())
        }
        false -> MIN_ZONED_DATE_TIME
        true -> ZonedDateTime.now()
        else -> return ZonedDateTime.from(dateTimeFormatter.parse(toString()))
    }
}

fun Any?.toOffsetDateTime(dateTimePattern: String): OffsetDateTime {
    return toOffsetDateTime(DateTimeFormatter.ofPattern(dateTimePattern))
}

@JvmOverloads
fun Any?.toOffsetDateTime(dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME): OffsetDateTime {
    return when (this) {
        null -> OffsetDateTime.MIN
        is OffsetDateTime -> this
        is Date -> toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime()
        is Instant -> atZone(ZoneId.systemDefault()).toOffsetDateTime()
        is LocalDateTime -> ZonedDateTime.of(this, ZoneId.systemDefault()).toOffsetDateTime()
        is ZonedDateTime -> toOffsetDateTime()
        is LocalDate -> ZonedDateTime.of(this, LocalTime.MIN, ZoneId.systemDefault()).toOffsetDateTime()
        is LocalTime -> ZonedDateTime.of(LocalDate.MIN, this, ZoneId.systemDefault()).toOffsetDateTime()
        is Temporal -> OffsetDateTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> OffsetDateTime.MIN
            else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toOffsetDateTime()
        }
        false -> OffsetDateTime.MIN
        true -> OffsetDateTime.now()
        else -> return OffsetDateTime.from(dateTimeFormatter.parse(toString()))
    }
}

fun Any?.toLocalDate(dateTimePattern: String): LocalDate {
    return toLocalDate(DateTimeFormatter.ofPattern(dateTimePattern))
}

@JvmOverloads
fun Any?.toLocalDate(dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE): LocalDate {
    return when (this) {
        null -> LocalDate.MIN
        is LocalDate -> this
        is Date -> toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        is Instant -> atZone(ZoneId.systemDefault()).toLocalDate()
        is LocalDateTime -> ZonedDateTime.of(this, ZoneId.systemDefault()).toLocalDate()
        is ZonedDateTime -> toLocalDate()
        is OffsetDateTime -> toLocalDate()
        is LocalTime -> LocalDate.MIN
        is Temporal -> LocalDate.from(this)
        is Number -> when (val value = toLong()) {
            0L -> LocalDate.MIN
            else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate()
        }
        false -> LocalDate.MIN
        true -> LocalDate.now()
        else -> return LocalDate.from(dateTimeFormatter.parse(toString()))
    }
}

fun Any?.toLocalTime(dateTimePattern: String): LocalTime {
    return toLocalTime(DateTimeFormatter.ofPattern(dateTimePattern))
}

@JvmOverloads
fun Any?.toLocalTime(dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME): LocalTime {
    return when (this) {
        null -> LocalTime.MIN
        is LocalTime -> this
        is Date -> toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
        is Instant -> atZone(ZoneId.systemDefault()).toLocalTime()
        is LocalDateTime -> ZonedDateTime.of(this, ZoneId.systemDefault()).toLocalTime()
        is ZonedDateTime -> toLocalTime()
        is OffsetDateTime -> toLocalTime()
        is LocalDate -> LocalTime.MIN
        is Temporal -> LocalTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> LocalTime.MIN
            else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalTime()
        }
        false -> LocalTime.MIN
        true -> LocalTime.now()
        else -> return LocalTime.from(dateTimeFormatter.parse(toString()))
    }
}

fun Any?.toDuration(): Duration {
    return when (this) {
        null -> Duration.ZERO
        is Duration -> this
        is Number -> when (val value = toLong()) {
            0L -> Duration.ZERO
            else -> Duration.ofMillis(value)
        }
        false -> Duration.ZERO
        else -> Duration.parse(toString())
    }
}

fun Any?.toTimestamp(): String {
    return Defaults.timestampFormatter.format(this.toLocalDateTime())
}