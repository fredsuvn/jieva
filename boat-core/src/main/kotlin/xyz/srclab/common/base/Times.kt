@file:JvmName("Times")

package xyz.srclab.common.base

import xyz.srclab.common.base.DatePattern.Companion.toDatePattern
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAccessor
import java.util.*

@JvmField
val TIMESTAMP_PATTERN: DatePattern = "yyyyMMddHHmmssSSS".toDatePattern()

fun epochSecond(): Long {
    return epochMilli() / 1000L
}

fun epochMilli(): Long {
    return System.currentTimeMillis()
}

@JvmOverloads
fun timestamp(pattern: DatePattern = TIMESTAMP_PATTERN, zoneId: ZoneId = ZoneId.systemDefault()): String {
    return TIMESTAMP_PATTERN.format(LocalDateTime.now(zoneId))
}

fun currentZoneOffset(): ZoneOffset {
    return LocalDateTime.now().getZoneOffset()
}

@JvmOverloads
fun LocalDateTime.getZoneOffset(zoneId: ZoneId = ZoneId.systemDefault()): ZoneOffset {
    return zoneId.rules.getOffset(this)
}

@JvmOverloads
fun Instant.getZoneOffset(zoneId: ZoneId = ZoneId.systemDefault()): ZoneOffset {
    return zoneId.rules.getOffset(this)
}

fun Date.toZonedDateTime(): ZonedDateTime {
    return this.toInstant().toZonedDateTime()
}

fun Date.toOffsetDateTime(): OffsetDateTime {
    return this.toInstant().toOffsetDateTime()
}

fun Date.toLocalDateTime(): LocalDateTime {
    return this.toInstant().toLocalDateTime()
}

fun Date.toLocalDate(): LocalDate {
    return this.toInstant().toLocalDate()
}

fun Date.toLocalTime(): LocalTime {
    return this.toInstant().toLocalTime()
}

fun TemporalAccessor.toDate(): Date {
    return toDateOrNull() ?: throw DateTimeException("toDate failed: $this")
}

fun TemporalAccessor.toInstant(): Instant {
    return toInstantOrNull() ?: throw DateTimeException("toInstant failed: $this")
}

fun TemporalAccessor.toZonedDateTime(): ZonedDateTime {
    return toZonedDateTimeOrNull() ?: throw DateTimeException("toZonedDateTime failed: $this")
}

fun TemporalAccessor.toOffsetDateTime(): OffsetDateTime {
    return toOffsetDateTimeOrNull() ?: throw DateTimeException("toOffsetDateTime failed: $this")
}

fun TemporalAccessor.toLocalDateTime(): LocalDateTime {
    return toLocalDateTimeOrNull() ?: throw DateTimeException("toLocalDateTime failed: $this")
}

fun TemporalAccessor.toLocalDate(): LocalDate {
    return toLocalDateOrNull() ?: throw DateTimeException("toLocalDate failed: $this")
}

fun TemporalAccessor.toLocalTime(): LocalTime {
    return toLocalTimeOrNull() ?: throw DateTimeException("toLocalTime failed: $this")
}

fun TemporalAccessor.toDateOrNull(): Date? {
    val instant = toInstantOrNull()
    if (instant === null) {
        return null
    }
    return Date.from(instant)
}

fun TemporalAccessor.toInstantOrNull(): Instant? {
    return when (this) {
        is Instant -> this
        is LocalDateTime -> this.toInstant(this.getZoneOffset())
        is ZonedDateTime -> this.toInstant()
        is OffsetDateTime -> this.toInstant()
        is LocalDate -> {
            val localDateTime = LocalDateTime.of(this, LocalTime.MIN)
            localDateTime.toInstant(localDateTime.getZoneOffset())
        }
        is LocalTime -> null
        else -> buildInstantOrNull()
    }
}

fun TemporalAccessor.toZonedDateTimeOrNull(): ZonedDateTime? {
    return when (this) {
        is Instant -> ZonedDateTime.ofInstant(this, ZoneId.systemDefault())
        is LocalDateTime -> this.atZone(ZoneId.systemDefault())
        is ZonedDateTime -> this
        is OffsetDateTime -> this.toZonedDateTime()
        is LocalDate -> ZonedDateTime.of(this, LocalTime.MIN, ZoneId.systemDefault())
        is LocalTime -> ZonedDateTime.of(LocalDate.MIN, this, ZoneId.systemDefault())
        else -> buildZonedDateTimeOrNull()
    }
}

fun TemporalAccessor.toOffsetDateTimeOrNull(): OffsetDateTime? {
    return when (this) {
        is Instant -> OffsetDateTime.ofInstant(this, ZoneId.systemDefault())
        is LocalDateTime -> this.atOffset(this.getZoneOffset())
        is ZonedDateTime -> this.toOffsetDateTime()
        is OffsetDateTime -> this
        is LocalDate -> OffsetDateTime.of(this, LocalTime.MIN, currentZoneOffset())
        is LocalTime -> OffsetDateTime.of(LocalDate.MIN, this, currentZoneOffset())
        else -> buildOffsetDateTimeOrNull()
    }
}

fun TemporalAccessor.toLocalDateTimeOrNull(): LocalDateTime? {
    return when (this) {
        is Instant -> LocalDateTime.ofInstant(this, ZoneId.systemDefault())
        is LocalDateTime -> this
        is ZonedDateTime -> this.toLocalDateTime()
        is OffsetDateTime -> this.toLocalDateTime()
        is LocalDate -> LocalDateTime.of(this, LocalTime.MIN)
        is LocalTime -> LocalDateTime.of(LocalDate.MIN, this)
        else -> buildLocalDateTimeOrNull()
    }
}

fun TemporalAccessor.toLocalDateOrNull(): LocalDate? {
    return when (this) {
        is Instant -> this.atZone(ZoneId.systemDefault()).toLocalDate()
        is LocalDateTime -> this.toLocalDate()
        is ZonedDateTime -> this.toLocalDate()
        is OffsetDateTime -> this.toLocalDate()
        is LocalDate -> this
        is LocalTime -> null
        else -> buildLocalDateOrNull()
    }
}

fun TemporalAccessor.toLocalTimeOrNull(): LocalTime? {
    return when (this) {
        is Instant -> this.atZone(ZoneId.systemDefault()).toLocalTime()
        is LocalDateTime -> this.toLocalTime()
        is ZonedDateTime -> this.toLocalTime()
        is OffsetDateTime -> this.toLocalTime()
        is LocalDate -> null
        is LocalTime -> this
        else -> buildLocalTimeOrNull()
    }
}

fun TemporalAccessor.buildInstantOrNull(): Instant? {
    if (this.isSupported(ChronoField.INSTANT_SECONDS)) {
        val second = this.getLong(ChronoField.INSTANT_SECONDS)
        val nano = getNanoOfSecond()
        return Instant.ofEpochSecond(second, nano.toLong())
    }
    val zonedDateTime = buildZonedDateTimeOrNull()
    if (zonedDateTime === null) {
        return null
    }
    return zonedDateTime.toInstant()
}

fun TemporalAccessor.buildZonedDateTimeOrNull(): ZonedDateTime? {
    return buildDateTimeOrNull { local, zoneId -> ZonedDateTime.of(local, zoneId) }
}

fun TemporalAccessor.buildOffsetDateTimeOrNull(): OffsetDateTime? {
    return buildDateTimeOrNull { local, zoneId -> OffsetDateTime.of(local, zoneId.rules.getOffset(local)) }
}

private inline fun <T> TemporalAccessor.buildDateTimeOrNull(
    generator: (local: LocalDateTime, zoneId: ZoneId) -> T?
): T? {
    val local = buildLocalDateTimeOrNull()
    if (local === null) {
        return null
    }
    val zoneId = if (this.isSupported(ChronoField.OFFSET_SECONDS)) {
        val offsetSecond = this.get(ChronoField.OFFSET_SECONDS)
        ZoneOffset.ofTotalSeconds(offsetSecond)
    } else {
        ZoneId.systemDefault()
    }
    return generator(local, zoneId)
}

fun TemporalAccessor.buildLocalDateTimeOrNull(): LocalDateTime? {
    val localDate = buildLocalDateOrNull()
    if (localDate === null) {
        return null
    }
    val localTime = buildLocalTimeOrNull()
    if (localTime === null) {
        return LocalDateTime.of(localDate, LocalTime.MIN)
    }
    return LocalDateTime.of(localDate, localTime)
}

fun TemporalAccessor.buildLocalDateOrNull(): LocalDate? {
    if (this.isSupported(ChronoField.EPOCH_DAY)) {
        return LocalDate.ofEpochDay(this.getLong(ChronoField.EPOCH_DAY))
    }

    val year = getYear()

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

fun TemporalAccessor.buildLocalTimeOrNull(): LocalTime? {
    if (this.isSupported(ChronoField.NANO_OF_DAY)) {
        return LocalTime.ofNanoOfDay(this.getLong(ChronoField.NANO_OF_DAY))
    }

    val nano = getNanoOfSecond()

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

    val hour = getHourOfSecond()

    var minute = 0
    if (this.isSupported(ChronoField.MINUTE_OF_HOUR)) {
        minute = this.get(ChronoField.MINUTE_OF_HOUR)
    }

    return LocalTime.of(hour, minute, second, nano)
}

fun TemporalAccessor.getNanoOfSecond(): Int {
    var nano = 0
    if (this.isSupported(ChronoField.NANO_OF_SECOND)) {
        nano = this.get(ChronoField.NANO_OF_SECOND)
    } else if (this.isSupported(ChronoField.MICRO_OF_SECOND)) {
        nano = this.get(ChronoField.MICRO_OF_SECOND) * 1000
    } else if (this.isSupported(ChronoField.MILLI_OF_SECOND)) {
        nano = this.get(ChronoField.MILLI_OF_SECOND) * 1000_000
    }
    return nano
}

fun TemporalAccessor.getHourOfSecond(): Int {
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
    return hour
}

fun TemporalAccessor.getYear(): Int {
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
    return year
}


/**
 * Pattern of datetime.
 */
interface DatePattern {

    val pattern: String

    fun toFormatter(): DateTimeFormatter

    fun toDateFormat(): DateFormat

    fun format(temporalAccessor: TemporalAccessor): String {
        return toFormatter().format(temporalAccessor)
    }

    fun format(date: Date): String {
        return toDateFormat().format(date)
    }

    fun parseDate(chars: CharSequence): Date {
        return toDateFormat().parse(chars.toString())
    }

    fun parseTemporalAccessor(chars: CharSequence): TemporalAccessor {
        return toFormatter().parse(chars)
    }

    fun parseTemporalAccessorOrNull(chars: CharSequence): TemporalAccessor? {
        return try {
            toFormatter().parse(chars)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    fun parseInstant(chars: CharSequence): Instant {
        return Instant.parse(chars)
    }

    fun parseZonedDateTime(chars: CharSequence): ZonedDateTime {
        return ZonedDateTime.parse(chars, toFormatter())
    }

    fun parseOffsetDateTime(chars: CharSequence): OffsetDateTime {
        return OffsetDateTime.parse(chars, toFormatter())
    }

    fun parseLocalDateTime(chars: CharSequence): LocalDateTime {
        return LocalDateTime.parse(chars, toFormatter())
    }

    fun parseLocalDate(chars: CharSequence): LocalDate {
        return LocalDate.parse(chars, toFormatter())
    }

    fun parseLocalTime(chars: CharSequence): LocalTime {
        return LocalTime.parse(chars, toFormatter())
    }

    fun buildInstant(chars: CharSequence): Instant {
        return parseTemporalAccessor(chars).toInstant()
    }

    fun buildZonedDateTime(chars: CharSequence): ZonedDateTime {
        return parseTemporalAccessor(chars).toZonedDateTime()
    }

    fun buildOffsetDateTime(chars: CharSequence): OffsetDateTime {
        return parseTemporalAccessor(chars).toOffsetDateTime()
    }

    fun buildLocalDateTime(chars: CharSequence): LocalDateTime {
        return parseTemporalAccessor(chars).toLocalDateTime()
    }

    fun buildLocalDate(chars: CharSequence): LocalDate {
        return parseTemporalAccessor(chars).toLocalDate()
    }

    fun buildLocalTime(chars: CharSequence): LocalTime {
        return parseTemporalAccessor(chars).toLocalTime()
    }

    fun buildInstantOrNull(chars: CharSequence): Instant? {
        return parseTemporalAccessorOrNull(chars)?.toInstantOrNull()
    }

    fun buildZonedDateTimeOrNull(chars: CharSequence): ZonedDateTime? {
        return parseTemporalAccessorOrNull(chars)?.toZonedDateTimeOrNull()
    }

    fun buildOffsetDateTimeOrNull(chars: CharSequence): OffsetDateTime? {
        return parseTemporalAccessorOrNull(chars)?.toOffsetDateTimeOrNull()
    }

    fun buildLocalDateTimeOrNull(chars: CharSequence): LocalDateTime? {
        return parseTemporalAccessorOrNull(chars)?.toLocalDateTimeOrNull()
    }

    fun buildLocalDateOrNull(chars: CharSequence): LocalDate? {
        return parseTemporalAccessorOrNull(chars)?.toLocalDateOrNull()
    }

    fun buildLocalTimeOrNull(chars: CharSequence): LocalTime? {
        return parseTemporalAccessorOrNull(chars)?.toLocalTimeOrNull()
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun CharSequence.toDatePattern(): DatePattern {
            return DatePatternImpl(this)
        }

        private class DatePatternImpl(
            pattern: CharSequence
        ) : DatePattern {

            override val pattern: String = pattern.toString()
            private val formatter = DateTimeFormatter.ofPattern(this.pattern)

            override fun toFormatter(): DateTimeFormatter = formatter

            override fun toDateFormat(): DateFormat {
                return SimpleDateFormat(pattern)
            }
        }
    }
}