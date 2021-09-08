@file:JvmName("Dates")

package xyz.srclab.common.base

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.util.*

const val TIMESTAMP_PATTERN = "yyyyMMddhhmmssSSS"

@JvmField
val LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd hh:mm:ss"

@JvmField
val ZONED_DATE_TIME_PATTERN = "yyyy-MM-dd hh:mm:ss ZZZZZ"

@JvmField
val EPOCH_DATE: Date = Date.from(Instant.EPOCH)

@JvmField
val EPOCH_LOCAL_DATE_TIME: LocalDateTime = LocalDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)

@JvmField
val EPOCH_LOCAL_DATE: LocalDate = EPOCH_LOCAL_DATE_TIME.toLocalDate()

@JvmField
val EPOCH_LOCAL_TIME: LocalTime = EPOCH_LOCAL_DATE_TIME.toLocalTime()

@JvmName("dateTimeFormatter")
fun CharSequence.toDateTimeFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern(this.toString())
}

@JvmName("dateFormat")
fun CharSequence.toDateFormat(): DateFormat {
    return SimpleDateFormat(this.toString())
}

fun CharSequence.toDate(pattern: String): Date {
    if (pattern == Defaults.dateTimePattern) {
        return Defaults.dateTimeFormat.parse(this.toString())
    }
    return pattern.toDateFormat().parse(this.toString())
}

fun Any?.toDate(datePattern: String): Date {
    return toDate(SimpleDateFormat(datePattern))
}

@JvmOverloads
fun Any?.toDate(dateFormat: DateFormat = Defaults.timestampFormat): Date {
    return when (this) {
        null -> EPOCH_DATE
        is Date -> this
        is Instant -> Date.from(this)
        is LocalDateTime -> Date.from(toInstant(OffsetDateTime.now().offset))
        is ZonedDateTime -> Date.from(toInstant())
        is OffsetDateTime -> Date.from(toInstant())
        is LocalDate -> Date.from(ZonedDateTime.of(this, EPOCH_LOCAL_TIME, ZoneId.systemDefault()).toInstant())
        is LocalTime -> Date.from(ZonedDateTime.of(EPOCH_LOCAL_DATE, this, ZoneId.systemDefault()).toInstant())
        is Temporal -> Date.from(Instant.from(this))
        is Number -> when (val value = toLong()) {
            0L -> EPOCH_DATE
            else -> Date(value)
        }
        false -> EPOCH_DATE
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
        null -> Instant.EPOCH
        is Instant -> this
        is Date -> toInstant()
        is LocalDateTime -> toInstant(ZonedDateTime.now().offset)
        is ZonedDateTime -> toInstant()
        is OffsetDateTime -> toInstant()
        is LocalDate -> ZonedDateTime.of(this, EPOCH_LOCAL_TIME, ZoneId.systemDefault()).toInstant()
        is LocalTime -> ZonedDateTime.of(EPOCH_LOCAL_DATE, this, ZoneId.systemDefault()).toInstant()
        is Temporal -> Instant.from(this)
        is Number -> when (val value = toLong()) {
            0L -> Instant.EPOCH
            else -> Instant.ofEpochMilli(value)
        }
        false -> Instant.EPOCH
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
        null -> EPOCH_LOCAL_DATE_TIME
        is LocalDateTime -> this
        is Date -> toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        is Instant -> atZone(ZoneId.systemDefault()).toLocalDateTime()
        is ZonedDateTime -> toLocalDateTime()
        is OffsetDateTime -> toLocalDateTime()
        is LocalDate -> LocalDateTime.of(this, EPOCH_LOCAL_TIME)
        is LocalTime -> LocalDateTime.of(EPOCH_LOCAL_DATE, this)
        is Temporal -> LocalDateTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> EPOCH_LOCAL_DATE_TIME
            else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDateTime()
        }
        false -> EPOCH_LOCAL_DATE_TIME
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
        null -> EPOCH_ZONED_DATE_TIME
        is ZonedDateTime -> this
        is Date -> toInstant().atZone(ZoneId.systemDefault())
        is Instant -> atZone(ZoneId.systemDefault())
        is LocalDateTime -> ZonedDateTime.of(this, ZoneId.systemDefault())
        is OffsetDateTime -> toZonedDateTime()
        is LocalDate -> ZonedDateTime.of(this, EPOCH_LOCAL_TIME, ZoneId.systemDefault())
        is LocalTime -> ZonedDateTime.of(EPOCH_LOCAL_DATE, this, ZoneId.systemDefault())
        is Temporal -> ZonedDateTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> EPOCH_ZONED_DATE_TIME
            else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault())
        }
        false -> EPOCH_ZONED_DATE_TIME
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
        null -> EPOCH_OFFSET_DATE_TIME
        is OffsetDateTime -> this
        is Date -> toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime()
        is Instant -> atZone(ZoneId.systemDefault()).toOffsetDateTime()
        is LocalDateTime -> ZonedDateTime.of(this, ZoneId.systemDefault()).toOffsetDateTime()
        is ZonedDateTime -> toOffsetDateTime()
        is LocalDate -> ZonedDateTime.of(this, EPOCH_LOCAL_TIME, ZoneId.systemDefault()).toOffsetDateTime()
        is LocalTime -> ZonedDateTime.of(EPOCH_LOCAL_DATE, this, ZoneId.systemDefault()).toOffsetDateTime()
        is Temporal -> OffsetDateTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> EPOCH_OFFSET_DATE_TIME
            else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toOffsetDateTime()
        }
        false -> EPOCH_OFFSET_DATE_TIME
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
        null -> EPOCH_LOCAL_DATE
        is LocalDate -> this
        is Date -> toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        is Instant -> atZone(ZoneId.systemDefault()).toLocalDate()
        is LocalDateTime -> ZonedDateTime.of(this, ZoneId.systemDefault()).toLocalDate()
        is ZonedDateTime -> toLocalDate()
        is OffsetDateTime -> toLocalDate()
        is LocalTime -> EPOCH_LOCAL_DATE
        is Temporal -> LocalDate.from(this)
        is Number -> when (val value = toLong()) {
            0L -> EPOCH_LOCAL_DATE
            else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate()
        }
        false -> EPOCH_LOCAL_DATE
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
        null -> EPOCH_LOCAL_TIME
        is LocalTime -> this
        is Date -> toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
        is Instant -> atZone(ZoneId.systemDefault()).toLocalTime()
        is LocalDateTime -> ZonedDateTime.of(this, ZoneId.systemDefault()).toLocalTime()
        is ZonedDateTime -> toLocalTime()
        is OffsetDateTime -> toLocalTime()
        is LocalDate -> EPOCH_LOCAL_TIME
        is Temporal -> LocalTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> EPOCH_LOCAL_TIME
            else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalTime()
        }
        false -> EPOCH_LOCAL_TIME
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
    return Defaults.timestampFormatter.format(toLocalDateTime())
}