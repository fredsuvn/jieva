@file:JvmName("BDates")

package xyz.srclab.common.base

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.*
import java.util.*

/**
 * Format of date, used to format or parse between string and date objects.
 */
interface BDateFormat {

    /**
     * Pattern of format.
     */
    val pattern: String

    /**
     * Converts to [DateTimeFormatter].
     */
    fun toFormatter(): DateTimeFormatter

    /**
     * Formats given [temporalAccessor].
     */
    fun format(temporalAccessor: TemporalAccessor): String {
        return toFormatter().format(temporalAccessor)
    }

    /**
     * Formats given [date].
     */
    fun format(date: Date): String {
        val instant = date.toInstant()
        return toFormatter().format(instant)
    }

    /**
     * Parse to [Instant].
     */
    fun parseInstant(chars: CharSequence): Instant {
        val temporal = toFormatter().parse(chars)
    }
}

fun TemporalAccessor.toLocalDateTimeOrNull(): LocalDateTime? {
    if (this is LocalDateTime) {
        return this
    }
    if (this is ZonedDateTime) {
        return this.toLocalDateTime()
    }
    if (this is OffsetDateTime) {
        return this.toLocalDateTime()
    }
    //LocalDateTime.
    val localDate = this.toLocalDateOrNull()
}

fun TemporalAccessor.toLocalDateOrNull(): LocalDate? {
    if (this is LocalDate) {
        return this
    }

    if (this.isSupported(ChronoField.EPOCH_DAY)) {
        return LocalDate.ofEpochDay(this.getLong(ChronoField.EPOCH_DAY))
    }

    var year = 0
    if (this.isSupported(ChronoField.YEAR)) {
        year = this.get(ChronoField.YEAR)
    } else if (this.isSupported(ChronoField.YEAR_OF_ERA) && this.isSupported(ChronoField.ERA)) {
        val era = this.get(ChronoField.ERA)
        if (era == 1) {
            year = this.get(ChronoField.YEAR_OF_ERA)
        } else if (era == 0) {
            year = -this.get(ChronoField.YEAR_OF_ERA) + 1
        }
    }

    if (this.isSupported(ChronoField.DAY_OF_YEAR)) {
        return LocalDate.ofYearDay(year, this.get(ChronoField.DAY_OF_YEAR))
    }

    var month = 1
    if (this.isSupported(ChronoField.MONTH_OF_YEAR)) {
        month = this.get(ChronoField.MONTH_OF_YEAR)
    }
    var day = 1
    if (this.isSupported(ChronoField.DAY_OF_MONTH)) {
        day = this.get(ChronoField.DAY_OF_MONTH)
    }
    return LocalDate.of(year, month, day)
}

fun TemporalAccessor.toLocalTimeOrNull(): LocalTime? {
    if (this is LocalTime) {
        return this
    }

    if (this.isSupported(ChronoField.NANO_OF_DAY)) {
        return LocalTime.ofNanoOfDay(this.getLong(ChronoField.NANO_OF_DAY))
    }

    var nano = 0
    if (this.isSupported(ChronoField.NANO_OF_SECOND)) {
        nano = this.get(ChronoField.NANO_OF_SECOND)
    }

    if (this.isSupported(ChronoField.SECOND_OF_DAY)) {
        val secondOfDay = this.getLong(ChronoField.SECOND_OF_DAY)
        return LocalTime.ofNanoOfDay(secondOfDay + nano)
    }

    var second = 0
    if (this.isSupported(ChronoField.SECOND_OF_MINUTE)) {
        second = this.get(ChronoField.SECOND_OF_MINUTE)
    }

    if (this.isSupported(ChronoField.MINUTE_OF_DAY)) {
        val minuteOfDay = this.getLong(ChronoField.MINUTE_OF_DAY)
        val secondOfDay = minuteOfDay * 60
        return LocalTime.ofNanoOfDay(secondOfDay + nano)
    }

    var hour = 0
    if (this.isSupported(ChronoField.HOUR_OF_DAY)) {
        hour = this.get(ChronoField.HOUR_OF_DAY)
    } else if (this.isSupported(ChronoField.CLOCK_HOUR_OF_DAY)) {
        hour = this.get(ChronoField.CLOCK_HOUR_OF_DAY) - 1
    } else if (this.isSupported(ChronoField.AMPM_OF_DAY)) {
        if (this.isSupported(ChronoField.HOUR_OF_AMPM)) {
            val ampm = this.get(ChronoField.AMPM_OF_DAY)
            hour = this.get(ChronoField.HOUR_OF_AMPM) + ampm * 12
        } else if (this.isSupported(ChronoField.CLOCK_HOUR_OF_AMPM)) {
            val ampm = this.get(ChronoField.AMPM_OF_DAY)
            hour = this.get(ChronoField.CLOCK_HOUR_OF_AMPM) - 1 + ampm * 12
        }
    }

    var minute = 0
    if (this.isSupported(ChronoField.MINUTE_OF_HOUR)) {
        minute = this.get(ChronoField.MINUTE_OF_HOUR)
    }

    return LocalTime.of(hour, minute, second, nano)
}

const val TIMESTAMP_PATTERN = "yyyyMMddHHmmssSSS"

const val SIMPLE_LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss"

const val SIMPLE_OFFSET_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss XXX"

const val ISO_LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"

const val ISO_OFFSET_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX"

const val ISO_ZONED_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'"

@JvmField
val TIMESTAMP_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN)

@JvmField
val SIMPLE_LOCAL_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(SIMPLE_LOCAL_DATE_TIME_PATTERN)

@JvmField
val SIMPLE_OFFSET_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(SIMPLE_OFFSET_DATE_TIME_PATTERN)

@JvmField
val ISO_LOCAL_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(ISO_LOCAL_DATE_TIME_PATTERN)

@JvmField
val ISO_OFFSET_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(ISO_OFFSET_DATE_TIME_PATTERN)

@JvmField
val ISO_ZONED_DATE_TIME_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern(ISO_ZONED_DATE_TIME_PATTERN)

@JvmField
val EPOCH_DATE: Date = Date.from(Instant.EPOCH)

@JvmField
val EPOCH_LOCAL_DATE_TIME: LocalDateTime = LocalDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC)

@JvmField
val EPOCH_LOCAL_DATE: LocalDate = EPOCH_LOCAL_DATE_TIME.toLocalDate()

@JvmField
val EPOCH_LOCAL_TIME: LocalTime = EPOCH_LOCAL_DATE_TIME.toLocalTime()

@JvmField
val EPOCH_OFFSET_DATE_TIME: OffsetDateTime = EPOCH_LOCAL_DATE_TIME.atOffset(ZoneOffset.UTC)

@JvmField
val EPOCH_ZONED_DATE_TIME: ZonedDateTime = EPOCH_LOCAL_DATE_TIME.atZone(ZoneOffset.UTC)

@JvmField
val EPOCH_TIMESTAMP: String = EPOCH_LOCAL_DATE_TIME.format(TIMESTAMP_FORMATTER)

@JvmName("dateTimeFormatter")
fun CharSequence.toDateTimeFormatter(): DateTimeFormatter {
    return DateTimeFormatter.ofPattern(this.toString())
}

@JvmName("dateFormat")
fun CharSequence.toDateFormat(): DateFormat {
    return SimpleDateFormat(this.toString())
}

/**
 * Guesses given date time string's pattern:
 *
 * * [SIMPLE_LOCAL_DATE_TIME_PATTERN] if string like `2021-09-16 03:00:18`;
 * * [SIMPLE_OFFSET_DATE_TIME_PATTERN] else if string like `2021-09-16 03:00:18 +08:00`;
 * * [ISO_LOCAL_DATE_TIME_PATTERN] else if string like `2021-09-16T03:00:18`;
 * * [ISO_OFFSET_DATE_TIME_PATTERN] else if string like `2021-09-16T03:00:18+08:00`;
 * * [ISO_ZONED_DATE_TIME_PATTERN] else if string like `2011-12-03T10:15:30+01:00[Europe/Paris]`;
 * * [TIMESTAMP_PATTERN] else if string is numeric;
 *
 * If no matched, return null.
 */
fun CharSequence.guessDateTimePatternOrNull(): String? {
    val localPatternLength = "2021-09-16 03:00:18".length
    if (this.length == localPatternLength) {
        if (this.contains(' ')) {
            return SIMPLE_LOCAL_DATE_TIME_PATTERN
        }
        if (this.contains('T')) {
            return ISO_LOCAL_DATE_TIME_PATTERN
        }
    }
    val simpleOffsetPatternLength = "2021-09-16 03:00:18 +08:00".length
    if (this.length == simpleOffsetPatternLength && this.contains(' ')) {
        return SIMPLE_OFFSET_DATE_TIME_PATTERN
    }
    val isoOffsetPatternLength = "2021-09-16T03:00:18+08:00".length
    if (this.length == isoOffsetPatternLength && this.contains('T')) {
        return ISO_OFFSET_DATE_TIME_PATTERN
    }
    val isoZonedPatternLength = "2011-12-03T10:15:30+01:00[]".length
    if (this.length > isoZonedPatternLength && this.endsWith(']')) {
        return ISO_ZONED_DATE_TIME_PATTERN
    }
    if (this.length == TIMESTAMP_PATTERN.length && this.isNumeric()) {
        return TIMESTAMP_PATTERN
    }
    return null
}

/**
 * Guesses given date time string's formatter:
 *
 * * [SIMPLE_LOCAL_DATE_TIME_FORMATTER] if string like `2021-09-16 03:00:18`;
 * * [SIMPLE_OFFSET_DATE_TIME_FORMATTER] else if string like `2021-09-16 03:00:18 +08:00`;
 * * [ISO_LOCAL_DATE_TIME_FORMATTER] else if string like `2021-09-16T03:00:18`;
 * * [ISO_OFFSET_DATE_TIME_FORMATTER] else if string like `2021-09-16T03:00:18+08:00`;
 * * [ISO_ZONED_DATE_TIME_FORMATTER] else if string like `2011-12-03T10:15:30+01:00[Europe/Paris]`;
 * * [TIMESTAMP_FORMATTER] else if string is numeric;
 *
 * If no matched, return null.
 */
fun CharSequence.guessDateTimeFormatterOrNull(): DateTimeFormatter? {
    val localPatternLength = "2021-09-16 03:00:18".length
    if (this.length == localPatternLength) {
        if (this.contains(' ')) {
            return SIMPLE_LOCAL_DATE_TIME_FORMATTER
        }
        if (this.contains('T')) {
            return ISO_LOCAL_DATE_TIME_FORMATTER
        }
    }
    val simpleOffsetPatternLength = "2021-09-16 03:00:18 +08:00".length
    if (this.length == simpleOffsetPatternLength && this.contains(' ')) {
        return SIMPLE_OFFSET_DATE_TIME_FORMATTER
    }
    val isoOffsetPatternLength = "2021-09-16T03:00:18+08:00".length
    if (this.length == isoOffsetPatternLength && this.contains('T')) {
        return ISO_OFFSET_DATE_TIME_FORMATTER
    }
    val isoZonedPatternLength = "2011-12-03T10:15:30+01:00[]".length
    if (this.length > isoZonedPatternLength && this.endsWith(']')) {
        return ISO_ZONED_DATE_TIME_FORMATTER
    }
    if (this.length == TIMESTAMP_PATTERN.length && this.isNumeric()) {
        return TIMESTAMP_FORMATTER
    }
    return null
}

@JvmOverloads
fun Any?.toDate(pattern: CharSequence, defaultValue: Date = EPOCH_DATE): Date {
    return toDate(pattern.toDateFormat(), defaultValue)
}

@JvmOverloads
fun Any?.toDate(dateFormat: DateFormat? = null, defaultValue: Date = EPOCH_DATE): Date {

    fun String.getDateFormat(): DateFormat {
        if (dateFormat !== null) {
            return dateFormat
        }
        return (this.guessDateTimePatternOrNull()
            ?: throw IllegalArgumentException("Unknown datetime formatter: $this.")).toDateFormat()
    }

    return when (this) {
        null -> defaultValue
        is Date -> this
        is Instant -> Date.from(this)
        is ZonedDateTime -> Date.from(toInstant())
        is OffsetDateTime -> Date.from(toInstant())
        is LocalDateTime -> Date.from(toInstant(OffsetDateTime.now().offset))
        is LocalDate -> Date.from(toLocalDateTime().toInstant(OffsetDateTime.now().offset))
        is LocalTime -> Date.from(toLocalDateTime().toInstant(OffsetDateTime.now().offset))
        is Temporal -> Date.from(Instant.from(this))
        is Number -> when (val value = toLong()) {
            0L -> EPOCH_DATE
            else -> Date(value)
        }
        false -> EPOCH_DATE
        true -> Date()
        else -> {
            val dateString = this.toString()
            dateString.getDateFormat().parse(dateString)
        }
    }
}

@JvmOverloads
fun Any?.toInstant(dateTimePattern: CharSequence, defaultValue: Instant = Instant.EPOCH): Instant {
    return toInstant(dateTimePattern.toDateTimeFormatter(), defaultValue)
}

@JvmOverloads
fun Any?.toInstant(dateTimeFormatter: DateTimeFormatter? = null, defaultValue: Instant = Instant.EPOCH): Instant {
    return when (this) {
        null -> defaultValue
        is Instant -> this
        is Date -> toInstant()
        is ZonedDateTime -> toInstant()
        is OffsetDateTime -> toInstant()
        is LocalDateTime -> toInstant(OffsetDateTime.now().offset)
        is LocalDate -> toLocalDateTime().toInstant(OffsetDateTime.now().offset)
        is LocalTime -> toLocalDateTime().toInstant(OffsetDateTime.now().offset)
        is Temporal -> Instant.from(this)
        is Number -> when (val value = toLong()) {
            0L -> Instant.EPOCH
            else -> Instant.ofEpochMilli(value)
        }
        false -> Instant.EPOCH
        true -> Instant.now()
        else -> {
            val dateString = this.toString()
            Instant.from(dateString.getDateTimeFormatter(dateTimeFormatter).parse(dateString).wrap())
        }
    }
}

@JvmOverloads
fun Any?.toZonedDateTime(dateTimePattern: CharSequence, defaultValue: ZonedDateTime = EPOCH_ZONED_DATE_TIME): ZonedDateTime {
    return toZonedDateTime(dateTimePattern.toDateTimeFormatter(), defaultValue)
}

@JvmOverloads
fun Any?.toZonedDateTime(dateTimeFormatter: DateTimeFormatter? = null, defaultValue: ZonedDateTime = EPOCH_ZONED_DATE_TIME): ZonedDateTime {
    return when (this) {
        null -> defaultValue
        is ZonedDateTime -> this
        is Date -> toInstant().atZone(ZoneOffset.UTC)
        is Instant -> atZone(ZoneOffset.UTC)
        is OffsetDateTime -> toZonedDateTime()
        is LocalDateTime -> atZone(ZoneId.systemDefault())
        is LocalDate -> ZonedDateTime.of(this, EPOCH_LOCAL_TIME, ZoneId.systemDefault())
        is LocalTime -> ZonedDateTime.of(EPOCH_LOCAL_DATE, this, ZoneId.systemDefault())
        is Temporal -> ZonedDateTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> EPOCH_ZONED_DATE_TIME
            else -> Instant.ofEpochMilli(value).atZone(ZoneOffset.UTC)
        }
        false -> EPOCH_ZONED_DATE_TIME
        true -> ZonedDateTime.now()
        else -> {
            val dateString = this.toString()
            val formatter = dateString.getDateTimeFormatter(dateTimeFormatter)
            val temporal = formatter.parse(dateString).wrap()
            return if (formatter === ISO_ZONED_DATE_TIME_FORMATTER) {
                val local = LocalDateTime.from(formatter.parse(dateString))
                val zoneIdStartIndex = dateString.indexOf('[') + 1
                val zoneIdEndIndex = dateString.length - 1
                val zoneIdString = dateString.subSequence(zoneIdStartIndex, zoneIdEndIndex)
                val zoneId = ZoneId.of(zoneIdString.toString())
                local.atZone(zoneId)
            } else {
                ZonedDateTime.from(temporal.wrap())
            }
        }
    }
}

@JvmOverloads
fun Any?.toOffsetDateTime(dateTimePattern: CharSequence, defaultValue: OffsetDateTime = EPOCH_OFFSET_DATE_TIME): OffsetDateTime {
    return toOffsetDateTime(dateTimePattern.toDateTimeFormatter(), defaultValue)
}

@JvmOverloads
fun Any?.toOffsetDateTime(dateTimeFormatter: DateTimeFormatter? = null, defaultValue: OffsetDateTime = EPOCH_OFFSET_DATE_TIME): OffsetDateTime {
    return when (this) {
        null -> defaultValue
        is OffsetDateTime -> this
        is Date -> toInstant().atOffset(ZoneOffset.UTC)
        is Instant -> atOffset(ZoneOffset.UTC)
        is ZonedDateTime -> toOffsetDateTime()
        is LocalDateTime -> toZonedDateTime().toOffsetDateTime()
        is LocalDate -> toZonedDateTime().toOffsetDateTime()
        is LocalTime -> toZonedDateTime().toOffsetDateTime()
        is Temporal -> OffsetDateTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> EPOCH_OFFSET_DATE_TIME
            else -> Instant.ofEpochMilli(value).atOffset(ZoneOffset.UTC)
        }
        false -> EPOCH_OFFSET_DATE_TIME
        true -> OffsetDateTime.now()
        else -> {
            val dateString = this.toString()
            return OffsetDateTime.from(dateString.getDateTimeFormatter(dateTimeFormatter).parse(dateString).wrap())
        }
    }
}

@JvmOverloads
fun Any?.toLocalDateTime(dateTimePattern: CharSequence, defaultValue: LocalDateTime = EPOCH_LOCAL_DATE_TIME): LocalDateTime {
    return toLocalDateTime(dateTimePattern.toDateTimeFormatter(), defaultValue)
}

@JvmOverloads
fun Any?.toLocalDateTime(dateTimeFormatter: DateTimeFormatter? = null, defaultValue: LocalDateTime = EPOCH_LOCAL_DATE_TIME): LocalDateTime {
    return when (this) {
        null -> defaultValue
        is LocalDateTime -> this
        is Date -> LocalDateTime.ofInstant(toInstant(), ZoneOffset.UTC)
        is Instant -> LocalDateTime.ofInstant(this, ZoneOffset.UTC)
        is ZonedDateTime -> toLocalDateTime()
        is OffsetDateTime -> toLocalDateTime()
        is LocalDate -> LocalDateTime.of(this, EPOCH_LOCAL_TIME)
        is LocalTime -> LocalDateTime.of(EPOCH_LOCAL_DATE, this)
        is Temporal -> LocalDateTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> EPOCH_LOCAL_DATE_TIME
            else -> LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC)
        }
        false -> EPOCH_LOCAL_DATE_TIME
        true -> LocalDateTime.now()
        else -> {
            val dateString = this.toString()
            LocalDateTime.from(dateString.getDateTimeFormatter(dateTimeFormatter).parse(dateString).wrap())
        }
    }
}

@JvmOverloads
fun Any?.toLocalDate(dateTimePattern: CharSequence, defaultValue: LocalDate = EPOCH_LOCAL_DATE): LocalDate {
    return toLocalDate(dateTimePattern.toDateTimeFormatter(), defaultValue)
}

@JvmOverloads
fun Any?.toLocalDate(dateTimeFormatter: DateTimeFormatter? = null, defaultValue: LocalDate = EPOCH_LOCAL_DATE): LocalDate {
    return when (this) {
        null -> defaultValue
        is LocalDate -> this
        is Date -> toInstant().atOffset(ZoneOffset.UTC).toLocalDate()
        is Instant -> atOffset(ZoneOffset.UTC).toLocalDate()
        is ZonedDateTime -> toLocalDate()
        is OffsetDateTime -> toLocalDate()
        is LocalDateTime -> toLocalDate()
        is LocalTime -> EPOCH_LOCAL_DATE
        is Temporal -> LocalDate.from(this)
        is Number -> when (val value = toLong()) {
            0L -> EPOCH_LOCAL_DATE
            else -> Instant.ofEpochMilli(value).atOffset(ZoneOffset.UTC).toLocalDate()
        }
        false -> EPOCH_LOCAL_DATE
        true -> LocalDate.now()
        else -> {
            val dateString = this.toString()
            LocalDate.from(dateString.getDateTimeFormatter(dateTimeFormatter).parse(dateString).wrap())
        }
    }
}

@JvmOverloads
fun Any?.toLocalTime(dateTimePattern: CharSequence, defaultValue: LocalTime = EPOCH_LOCAL_TIME): LocalTime {
    return toLocalTime(dateTimePattern.toDateTimeFormatter(), defaultValue)
}

@JvmOverloads
fun Any?.toLocalTime(dateTimeFormatter: DateTimeFormatter? = null, defaultValue: LocalTime = EPOCH_LOCAL_TIME): LocalTime {
    return when (this) {
        null -> defaultValue
        is LocalTime -> this
        is Date -> toInstant().atOffset(ZoneOffset.UTC).toLocalTime()
        is Instant -> atOffset(ZoneOffset.UTC).toLocalTime()
        is ZonedDateTime -> toLocalTime()
        is OffsetDateTime -> toLocalTime()
        is LocalDateTime -> toLocalTime()
        is LocalDate -> EPOCH_LOCAL_TIME
        is Temporal -> LocalTime.from(this)
        is Number -> when (val value = toLong()) {
            0L -> EPOCH_LOCAL_TIME
            else -> Instant.ofEpochMilli(value).atOffset(ZoneOffset.UTC).toLocalTime()
        }
        false -> EPOCH_LOCAL_TIME
        true -> LocalTime.now()
        else -> {
            val dateString = this.toString()
            LocalTime.from(dateString.getDateTimeFormatter(dateTimeFormatter).parse(dateString).wrap())
        }
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

private fun String.getDateTimeFormatter(dateTimeFormatter: DateTimeFormatter?): DateTimeFormatter {
    if (dateTimeFormatter !== null) {
        return dateTimeFormatter
    }
    return this.guessDateTimeFormatterOrNull() ?: throw IllegalArgumentException("Unknown datetime formatter: $this.")
}

private fun TemporalAccessor.wrap(): TemporalAccessor {
    return TemporalAccessorWrapper(this)
}

private class TemporalAccessorWrapper(private val temporalAccessor: TemporalAccessor) : TemporalAccessor {

    override fun isSupported(field: TemporalField): Boolean {
        val actual = temporalAccessor.isSupported(field)
        if (!actual) {
            if (field == ChronoField.NANO_OF_DAY || field == ChronoField.NANO_OF_SECOND) {
                return true
            }
        }
        return actual
    }

    override fun getLong(field: TemporalField): Long {
        return try {
            temporalAccessor.getLong(field)
        } catch (e: Exception) {
            0
        }
    }

    override fun <R> query(query: TemporalQuery<R>): R {
        if (query == TemporalQueries.localTime()) {
            return super.query(LOCAL_TIME).asTyped()
        }
        return super.query(query)
    }

    companion object {
        private val LOCAL_TIME = TemporalQuery<LocalTime> { temporal: TemporalAccessor ->
            if (temporal.isSupported(ChronoField.NANO_OF_DAY)) {
                var nanos = temporal.getLong(ChronoField.NANO_OF_DAY)
                if (nanos == 0L) {
                    val hours = temporal.getLong(ChronoField.HOUR_OF_DAY)
                    val minutes = temporal.getLong(ChronoField.MINUTE_OF_HOUR)
                    val seconds = temporal.getLong(ChronoField.SECOND_OF_MINUTE)
                    val millis = temporal.getLong(ChronoField.MILLI_OF_SECOND)
                    nanos = ((3600 * hours + 60 * minutes + seconds) * 1000 + millis) * 1000
                }
                LocalTime.ofNanoOfDay(nanos)
            } else {
                null
            }
        }
    }
}