@file:JvmName("BTime")

package xyz.srclab.common.base

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAccessor
import java.util.*

/**
 * Returns current epoch seconds.
 */
fun currentSeconds(): Long {
    return currentMillis() / 1000L
}

/**
 * Returns current epoch milliseconds.
 */
fun currentMillis(): Long {
    return System.currentTimeMillis()
}

/**
 * Returns system nanosecond.
 */
fun systemNanos(): Long {
    return System.nanoTime()
}

/**
 * Returns current timestamp pattern.
 */
@JvmOverloads
fun currentTimestamp(zoneId: ZoneId = ZoneId.systemDefault()): String {
    return defaultTimestampPattern().format(LocalDateTime.now(zoneId))
}

/**
 * Returns current zone offset.
 */
fun currentZoneOffset(): ZoneOffset {
    return LocalDateTime.now().getZoneOffset()
}

/**
 * Returns zone offset of [this] local date time.
 */
@JvmOverloads
fun LocalDateTime.getZoneOffset(zoneId: ZoneId = ZoneId.systemDefault()): ZoneOffset {
    return zoneId.rules.getOffset(this)
}

/**
 * Returns zone offset of [this] instant.
 */
@JvmOverloads
fun Instant.getZoneOffset(zoneId: ZoneId = ZoneId.systemDefault()): ZoneOffset {
    return zoneId.rules.getOffset(this)
}

/**
 * Converts to [ZonedDateTime], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun Date.toZonedDateTime(tryCompute: Boolean = true): ZonedDateTime {
    return this.toInstant().toZonedDateTime(tryCompute)
}

/**
 * Converts to [OffsetDateTime], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun Date.toOffsetDateTime(tryCompute: Boolean = true): OffsetDateTime {
    return this.toInstant().toOffsetDateTime(tryCompute)
}

/**
 * Converts to [LocalDateTime], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun Date.toLocalDateTime(tryCompute: Boolean = true): LocalDateTime {
    return this.toInstant().toLocalDateTime(tryCompute)
}

/**
 * Converts to [LocalDate], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun Date.toLocalDate(tryCompute: Boolean = true): LocalDate {
    return this.toInstant().toLocalDate(tryCompute)
}

/**
 * Converts to [LocalTime], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun Date.toLocalTime(tryCompute: Boolean = true): LocalTime {
    return this.toInstant().toLocalTime(tryCompute)
}

/**
 * Converts to [Date], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun TemporalAccessor.toDate(tryCompute: Boolean = true): Date {
    return toDateOrNull(tryCompute) ?: throw DateTimeException("toDate failed: $this")
}

/**
 * Converts to [Instant], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun TemporalAccessor.toInstant(tryCompute: Boolean = true): Instant {
    return toInstantOrNull(tryCompute) ?: throw DateTimeException("toInstant failed: $this")
}

/**
 * Converts to [ZonedDateTime], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun TemporalAccessor.toZonedDateTime(tryCompute: Boolean = true): ZonedDateTime {
    return toZonedDateTimeOrNull(tryCompute) ?: throw DateTimeException("toZonedDateTime failed: $this")
}

/**
 * Converts to [OffsetDateTime], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun TemporalAccessor.toOffsetDateTime(tryCompute: Boolean = true): OffsetDateTime {
    return toOffsetDateTimeOrNull(tryCompute) ?: throw DateTimeException("toOffsetDateTime failed: $this")
}

/**
 * Converts to [LocalDateTime], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun TemporalAccessor.toLocalDateTime(tryCompute: Boolean = true): LocalDateTime {
    return toLocalDateTimeOrNull(tryCompute) ?: throw DateTimeException("toLocalDateTime failed: $this")
}

/**
 * Converts to [LocalDate], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun TemporalAccessor.toLocalDate(tryCompute: Boolean = true): LocalDate {
    return toLocalDateOrNull(tryCompute) ?: throw DateTimeException("toLocalDate failed: $this")
}

/**
 * Converts to [LocalTime], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun TemporalAccessor.toLocalTime(tryCompute: Boolean = true): LocalTime {
    return toLocalTimeOrNull(tryCompute) ?: throw DateTimeException("toLocalTime failed: $this")
}

/**
 * Converts to [Date], or null if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun TemporalAccessor.toDateOrNull(tryCompute: Boolean = true): Date? {
    val instant = toInstantOrNull(tryCompute)
    if (instant === null) {
        return null
    }
    return Date.from(instant)
}

/**
 * Converts to [Instant], or null if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun TemporalAccessor.toInstantOrNull(tryCompute: Boolean = true): Instant? {
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
        else -> {
            if (!tryCompute) {
                return null
            }
            computeInstant()
        }
    }
}

/**
 * Converts to [ZonedDateTime], or null if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun TemporalAccessor.toZonedDateTimeOrNull(tryCompute: Boolean = true): ZonedDateTime? {
    return when (this) {
        is Instant -> ZonedDateTime.ofInstant(this, ZoneId.systemDefault())
        is LocalDateTime -> this.atZone(ZoneId.systemDefault())
        is ZonedDateTime -> this
        is OffsetDateTime -> this.toZonedDateTime()
        is LocalDate -> ZonedDateTime.of(this, LocalTime.MIN, ZoneId.systemDefault())
        is LocalTime -> ZonedDateTime.of(LocalDate.MIN, this, ZoneId.systemDefault())
        else -> {
            if (!tryCompute) {
                return null
            }
            computeZonedDateTime()
        }
    }
}

/**
 * Converts to [OffsetDateTime], or null if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun TemporalAccessor.toOffsetDateTimeOrNull(tryCompute: Boolean = true): OffsetDateTime? {
    return when (this) {
        is Instant -> OffsetDateTime.ofInstant(this, ZoneId.systemDefault())
        is LocalDateTime -> this.atOffset(this.getZoneOffset())
        is ZonedDateTime -> this.toOffsetDateTime()
        is OffsetDateTime -> this
        is LocalDate -> OffsetDateTime.of(this, LocalTime.MIN, currentZoneOffset())
        is LocalTime -> OffsetDateTime.of(LocalDate.MIN, this, currentZoneOffset())
        else -> {
            if (!tryCompute) {
                return null
            }
            computeOffsetDateTime()
        }
    }
}

/**
 * Converts to [LocalDateTime], or null if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun TemporalAccessor.toLocalDateTimeOrNull(tryCompute: Boolean = true): LocalDateTime? {
    return when (this) {
        is Instant -> LocalDateTime.ofInstant(this, ZoneId.systemDefault())
        is LocalDateTime -> this
        is ZonedDateTime -> this.toLocalDateTime()
        is OffsetDateTime -> this.toLocalDateTime()
        is LocalDate -> LocalDateTime.of(this, LocalTime.MIN)
        is LocalTime -> LocalDateTime.of(LocalDate.MIN, this)
        else -> {
            if (!tryCompute) {
                return null
            }
            computeLocalDateTime()
        }
    }
}

/**
 * Converts to [LocalDate], or null if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun TemporalAccessor.toLocalDateOrNull(tryCompute: Boolean = true): LocalDate? {
    return when (this) {
        is Instant -> this.atZone(ZoneId.systemDefault()).toLocalDate()
        is LocalDateTime -> this.toLocalDate()
        is ZonedDateTime -> this.toLocalDate()
        is OffsetDateTime -> this.toLocalDate()
        is LocalDate -> this
        is LocalTime -> null
        else -> {
            if (!tryCompute) {
                return null
            }
            computeLocalDate()
        }
    }
}

/**
 * Converts to [LocalTime], or null if failed.
 *
 * @param tryCompute whether tries to compute methods.
 */
@JvmOverloads
fun TemporalAccessor.toLocalTimeOrNull(tryCompute: Boolean = true): LocalTime? {
    return when (this) {
        is Instant -> this.atZone(ZoneId.systemDefault()).toLocalTime()
        is LocalDateTime -> this.toLocalTime()
        is ZonedDateTime -> this.toLocalTime()
        is OffsetDateTime -> this.toLocalTime()
        is LocalDate -> null
        is LocalTime -> this
        else -> {
            if (!tryCompute) {
                return null
            }
            computeLocalTime()
        }
    }
}

/**
 * Computes [Instant] from [this], or returns null if failed.
 */
fun TemporalAccessor.computeInstant(): Instant? {
    if (this.isSupported(ChronoField.INSTANT_SECONDS)) {
        val second = this.getLong(ChronoField.INSTANT_SECONDS)
        val nano = computeNanoOfSecond()
        return Instant.ofEpochSecond(second, nano.toLong())
    }
    val zonedDateTime = computeZonedDateTime()
    if (zonedDateTime === null) {
        return null
    }
    return zonedDateTime.toInstant()
}

/**
 * Computes [ZonedDateTime] from [this], or returns null if failed.
 */
fun TemporalAccessor.computeZonedDateTime(): ZonedDateTime? {
    return computeDateTime { local, zoneId -> ZonedDateTime.of(local, zoneId) }
}

/**
 * Computes [OffsetDateTime] from [this], or returns null if failed.
 */
fun TemporalAccessor.computeOffsetDateTime(): OffsetDateTime? {
    return computeDateTime { local, zoneId -> OffsetDateTime.of(local, zoneId.rules.getOffset(local)) }
}

private inline fun <T> TemporalAccessor.computeDateTime(
    generator: (local: LocalDateTime, zoneId: ZoneId) -> T?
): T? {
    val local = computeLocalDateTime()
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

/**
 * Computes [LocalDateTime] from [this], or returns null if failed.
 */
fun TemporalAccessor.computeLocalDateTime(): LocalDateTime? {
    val localDate = computeLocalDate()
    if (localDate === null) {
        return null
    }
    val localTime = computeLocalTime()
    if (localTime === null) {
        return LocalDateTime.of(localDate, LocalTime.MIN)
    }
    return LocalDateTime.of(localDate, localTime)
}

/**
 * Computes [LocalDate] from [this], or returns null if failed.
 */
fun TemporalAccessor.computeLocalDate(): LocalDate? {
    if (this.isSupported(ChronoField.EPOCH_DAY)) {
        return LocalDate.ofEpochDay(this.getLong(ChronoField.EPOCH_DAY))
    }

    val year = computeYear()

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

/**
 * Computes [LocalTime] from [this], or returns null if failed.
 */
fun TemporalAccessor.computeLocalTime(): LocalTime? {
    if (this.isSupported(ChronoField.NANO_OF_DAY)) {
        return LocalTime.ofNanoOfDay(this.getLong(ChronoField.NANO_OF_DAY))
    }

    val nano = computeNanoOfSecond()

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

    val hour = computeHourOfSecond()

    var minute = 0
    if (this.isSupported(ChronoField.MINUTE_OF_HOUR)) {
        minute = this.get(ChronoField.MINUTE_OF_HOUR)
    }

    return LocalTime.of(hour, minute, second, nano)
}

/**
 * Computes nano of second value from [this].
 */
fun TemporalAccessor.computeNanoOfSecond(): Int {
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

/**
 * Computes hour of second value from [this].
 */
fun TemporalAccessor.computeHourOfSecond(): Int {

    fun getAmPm(): Int {
        return try {
            this.get(ChronoField.AMPM_OF_DAY)
        } catch (e: Exception) {
            0
        }
    }

    var hour = 0
    if (this.isSupported(ChronoField.HOUR_OF_DAY)) {
        hour = this.get(ChronoField.HOUR_OF_DAY)
    } else if (this.isSupported(ChronoField.CLOCK_HOUR_OF_DAY)) {
        hour = this.get(ChronoField.CLOCK_HOUR_OF_DAY) - 1
    } else if (this.isSupported(ChronoField.HOUR_OF_AMPM)) {
        val ampm = getAmPm()
        hour = this.get(ChronoField.HOUR_OF_AMPM) + ampm * 12
    } else if (this.isSupported(ChronoField.CLOCK_HOUR_OF_AMPM)) {
        val ampm = getAmPm()
        hour = this.get(ChronoField.CLOCK_HOUR_OF_AMPM) - 1 + ampm * 12
    }
    return hour
}

/**
 * Computes year value from [this].
 */
fun TemporalAccessor.computeYear(): Int {
    var year = 0
    if (this.isSupported(ChronoField.YEAR)) {
        year = this.get(ChronoField.YEAR)
    } else if (this.isSupported(ChronoField.YEAR_OF_ERA)) {
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
 * Pattern for date/time, used to format/parse between char sequence and Date types.
 *
 * This pattern provides both [DateTimeFormatter] and [DateFormat] to compatible with JDK7-lower and JDK8-higher.
 */
interface DatePattern {

    /**
     * Returns pattern as string.
     *
     * Note sometimes this is unsupported.
     */
    val pattern: String

    /**
     * Returns a [DateTimeFormatter] to compatible with JDK8-higher.
     */
    fun formatter(): DateTimeFormatter

    /**
     * Returns a [DateFormat] to compatible with JDK7-lower.
     */
    fun dateFormat(): DateFormat

    /**
     * Formats [temporal] to string with [formatter].
     */
    fun format(temporal: TemporalAccessor): String {
        return formatter().format(temporal)
    }

    /**
     * Formats [date] to string with [dateFormat].
     */
    fun format(date: Date): String {
        return dateFormat().format(date)
    }

    /**
     * Parses [chars] to [TemporalAccessor] with [formatter].
     */
    fun parseTemporal(chars: CharSequence): TemporalAccessor {
        return formatter().parse(chars)
    }

    /**
     * Parses [chars] to [Date] with [dateFormat].
     */
    fun parseDate(chars: CharSequence): Date {
        return dateFormat().parse(chars.toString())
    }

    /**
     * Parses [chars] to [Instant] with [formatter].
     */
    fun parseInstant(chars: CharSequence): Instant {
        return formatter().parse(chars) { Instant.from(it) }
    }

    /**
     * Parses [chars] to [ZonedDateTime] with [formatter].
     */
    fun parseZonedDateTime(chars: CharSequence): ZonedDateTime {
        return formatter().parse(chars) { ZonedDateTime.from(it) }
    }

    /**
     * Parses [chars] to [OffsetDateTime] with [formatter].
     */
    fun parseOffsetDateTime(chars: CharSequence): OffsetDateTime {
        return formatter().parse(chars) { OffsetDateTime.from(it) }
    }

    /**
     * Parses [chars] to [LocalDateTime] with [formatter].
     */
    fun parseLocalDateTime(chars: CharSequence): LocalDateTime {
        return formatter().parse(chars) { LocalDateTime.from(it) }
    }

    /**
     * Parses [chars] to [LocalDate] with [formatter].
     */
    fun parseLocalDate(chars: CharSequence): LocalDate {
        return formatter().parse(chars) { LocalDate.from(it) }
    }

    /**
     * Parses [chars] to [LocalTime] with [formatter].
     */
    fun parseLocalTime(chars: CharSequence): LocalTime {
        return formatter().parse(chars) { LocalTime.from(it) }
    }

    companion object {

        /**
         * Returns a [DatePattern] of [this] pattern,
         * both [formatter()][formatter] and [dateFormat()][dateFormat] use this pattern.
         */
        @JvmName("of")
        @JvmStatic
        fun CharSequence.toDatePattern(): DatePattern {
            return OfPattern(this)
        }

        /**
         * Returns a [DatePattern] which only provides [formatter()][formatter],
         * [dateFormat()][dateFormat] and [pattern] were unsupported.
         */
        @JvmName("of")
        @JvmStatic
        fun DateTimeFormatter.toDatePattern(): DatePattern {
            return OfFormatter(this)
        }

        /**
         * Returns a [DatePattern] which pattern and dateFormat() use [pattern], and formatter() use [formatter].
         */
        @JvmStatic
        fun of(pattern: CharSequence, formatter: DateTimeFormatter): DatePattern {
            return OfPatternFormatter(pattern, formatter)
        }

        private class OfPattern(
            pattern: CharSequence
        ) : DatePattern {
            override val pattern: String = pattern.toString()
            private val formatter = DateTimeFormatter.ofPattern(this.pattern)
            override fun formatter(): DateTimeFormatter = formatter
            override fun dateFormat(): DateFormat {
                return SimpleDateFormat(pattern)
            }
        }

        private class OfFormatter(
            private val formatter: DateTimeFormatter
        ) : DatePattern {
            override val pattern: String = throw UnsupportedOperationException("")
            override fun formatter(): DateTimeFormatter = formatter
            override fun dateFormat(): DateFormat {
                throw UnsupportedOperationException("")
            }
        }

        private class OfPatternFormatter(
            pattern: CharSequence,
            private val formatter: DateTimeFormatter
        ) : DatePattern {
            override val pattern: String = pattern.toString()
            override fun formatter(): DateTimeFormatter = formatter
            override fun dateFormat(): DateFormat {
                return SimpleDateFormat(pattern)
            }
        }
    }
}