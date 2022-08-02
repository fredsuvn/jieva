/**
 * Date and time utilities.
 */
@file:JvmName("BtDate")

package xyz.srclab.common.base

import xyz.srclab.common.base.DatePattern.Companion.toDatePattern
import java.io.Serializable
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
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
 * Returns current epoch milliseconds: [System.currentTimeMillis].
 */
fun currentMillis(): Long {
    return System.currentTimeMillis()
}

/**
 * Returns current system nanosecond: [System.nanoTime].
 */
fun currentNanos(): Long {
    return System.nanoTime()
}

/**
 * Returns current timestamp of which pattern is [BtProps.timestampPattern].
 */
@JvmOverloads
fun currentTimestamp(zoneId: ZoneId = ZoneId.systemDefault()): String {
    return BtProps.timestampPattern().format(ZonedDateTime.now(zoneId))
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
 * Converts to [ZonedDateTime].
 */
@JvmOverloads
fun Date.toZonedDateTime(zoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime {
    return ZonedDateTime.ofInstant(this.toInstant(), zoneId)
}

/**
 * Converts to [OffsetDateTime].
 */
@JvmOverloads
fun Date.toOffsetDateTime(zoneId: ZoneId = ZoneId.systemDefault()): OffsetDateTime {
    return OffsetDateTime.ofInstant(this.toInstant(), zoneId)
}

/**
 * Converts to [LocalDateTime].
 */
@JvmOverloads
fun Date.toLocalDateTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalDateTime {
    return LocalDateTime.ofInstant(this.toInstant(), zoneId)
}

/**
 * Converts to [LocalDate].
 */
@JvmOverloads
fun Date.toLocalDate(zoneId: ZoneId = ZoneId.systemDefault()): LocalDate {
    return toLocalDateTime(zoneId).toLocalDate()
}

/**
 * Converts to [LocalTime].
 */
@JvmOverloads
fun Date.toLocalTime(zoneId: ZoneId = ZoneId.systemDefault()): LocalTime {
    return toLocalDateTime(zoneId).toLocalTime()
}

/**
 * Converts to [Date], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute missing fields by other existing fields.
 */
@Throws(DateTimeException::class)
@JvmOverloads
fun TemporalAccessor.toDate(tryCompute: Boolean = true): Date {
    return toDateOrNull(tryCompute) ?: throw DateTimeException("toDate failed: $this")
}

/**
 * Converts to [Instant], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute missing fields by other existing fields.
 */
@Throws(DateTimeException::class)
@JvmOverloads
fun TemporalAccessor.toInstant(tryCompute: Boolean = true): Instant {
    return toInstantOrNull(tryCompute) ?: throw DateTimeException("toInstant failed: $this")
}

/**
 * Converts to [ZonedDateTime], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute missing fields by other existing fields.
 */
@Throws(DateTimeException::class)
@JvmOverloads
fun TemporalAccessor.toZonedDateTime(tryCompute: Boolean = true): ZonedDateTime {
    return toZonedDateTimeOrNull(tryCompute) ?: throw DateTimeException("toZonedDateTime failed: $this")
}

/**
 * Converts to [OffsetDateTime], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute missing fields by other existing fields.
 */
@Throws(DateTimeException::class)
@JvmOverloads
fun TemporalAccessor.toOffsetDateTime(tryCompute: Boolean = true): OffsetDateTime {
    return toOffsetDateTimeOrNull(tryCompute) ?: throw DateTimeException("toOffsetDateTime failed: $this")
}

/**
 * Converts to [LocalDateTime], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute missing fields by other existing fields.
 */
@Throws(DateTimeException::class)
@JvmOverloads
fun TemporalAccessor.toLocalDateTime(tryCompute: Boolean = true): LocalDateTime {
    return toLocalDateTimeOrNull(tryCompute) ?: throw DateTimeException("toLocalDateTime failed: $this")
}

/**
 * Converts to [LocalDate], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute missing fields by other existing fields.
 */
@Throws(DateTimeException::class)
@JvmOverloads
fun TemporalAccessor.toLocalDate(tryCompute: Boolean = true): LocalDate {
    return toLocalDateOrNull(tryCompute) ?: throw DateTimeException("toLocalDate failed: $this")
}

/**
 * Converts to [LocalTime], or throw [DateTimeException] if failed.
 *
 * @param tryCompute whether tries to compute missing fields by other existing fields.
 */
@Throws(DateTimeException::class)
@JvmOverloads
fun TemporalAccessor.toLocalTime(tryCompute: Boolean = true): LocalTime {
    return toLocalTimeOrNull(tryCompute) ?: throw DateTimeException("toLocalTime failed: $this")
}

/**
 * Converts to [Date], or null if failed.
 *
 * @param tryCompute whether tries to compute missing fields by other existing fields.
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
 * @param tryCompute whether tries to compute missing fields by other existing fields.
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
 * @param tryCompute whether tries to compute missing fields by other existing fields.
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
 * @param tryCompute whether tries to compute missing fields by other existing fields.
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
 * @param tryCompute whether tries to compute missing fields by other existing fields.
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
 * @param tryCompute whether tries to compute missing fields by other existing fields.
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
 * @param tryCompute whether tries to compute missing fields by other existing fields.
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
 * Computes [Instant] from [this] with existing fields, or returns null if failed.
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
 * Computes [ZonedDateTime] from [this] with existing fields, or returns null if failed.
 */
fun TemporalAccessor.computeZonedDateTime(): ZonedDateTime? {
    return computeDateTime { local, zoneId -> ZonedDateTime.of(local, zoneId) }
}

/**
 * Computes [OffsetDateTime] from [this] with existing fields, or returns null if failed.
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
 * Computes [LocalDateTime] from [this] with existing fields, or returns null if failed.
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
 * Computes [LocalDate] from [this] with existing fields, or returns null if failed.
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
 * Computes [LocalTime] from [this] with existing fields, or returns null if failed.
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
 * Computes nano of second value from [this] with existing fields.
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
 * Computes hour of second value from [this] with existing fields.
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
 * Computes year value from [this] with existing fields.
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
 * Pattern for date/time, used to format/parse between string and Date types.
 *
 * This pattern provides both [DateTimeFormatter] and [DateFormat] to compatible with JDK7-lower and JDK8-higher.
 */
interface DatePattern {

    /**
     * Pattern of date in [String], may be null (if based on [DateTimeFormatter]).
     */
    val pattern: String?

    /**
     * Returns a [DateTimeFormatter] to compatible with JDK8-higher, may be null.
     */
    fun formatter(): DateTimeFormatter?

    /**
     * Returns a [DateFormat] to compatible with JDK7-lower, may be null.
     */
    fun dateFormat(): DateFormat?

    /**
     * Formats [temporal] to string with [formatter].
     * If [formatter] is null, throw a [DateTimeException].
     */
    @Throws(DateTimeException::class)
    fun format(temporal: TemporalAccessor): String {
        return formatter()?.format(temporal) ?: throw DateTimeException("formatter is null")
    }

    /**
     * Formats [date] to string with [dateFormat].
     * If [dateFormat] is null, throw a [DateTimeException].
     */
    @Throws(DateTimeException::class)
    fun format(date: Date): String {
        return dateFormat()?.format(date) ?: throw DateTimeException("dateFormat is null")
    }

    /**
     * Parses [chars] to [TemporalAccessor] with [formatter].
     * If [formatter] is null, throw a [DateTimeException].
     */
    @Throws(DateTimeException::class, DateTimeParseException::class)
    fun parseTemporal(chars: CharSequence): TemporalAccessor {
        return formatter()?.parse(chars) ?: throw DateTimeException("formatter is null")
    }

    /**
     * Parses [chars] to [Date] with [dateFormat].
     * If [dateFormat] is null, throw a [IllegalStateException].
     */
    @Throws(DateTimeException::class, DateTimeParseException::class)
    fun parseDate(chars: CharSequence): Date {
        val dateFormat = dateFormat()
        if (dateFormat === null) {
            throw DateTimeException("dateFormat is null")
        }
        try {
            return dateFormat.parse(chars.toString())
        } catch (e: ParseException) {
            throw DateTimeParseException(e.message, chars, e.errorOffset, e)
        }
    }

    /**
     * Parses [chars] to [Instant] with [formatter].
     * If [formatter] is null, throw a [DateTimeException].
     */
    @Throws(DateTimeException::class, DateTimeParseException::class)
    fun parseInstant(chars: CharSequence): Instant {
        return formatter()?.parse(chars) { Instant.from(it) } ?: throw DateTimeException("formatter is null")
    }

    /**
     * Parses [chars] to [ZonedDateTime] with [formatter].
     * If [formatter] is null, throw a [DateTimeException].
     */
    @Throws(DateTimeException::class, DateTimeParseException::class)
    fun parseZonedDateTime(chars: CharSequence): ZonedDateTime {
        return formatter()?.parse(chars) { ZonedDateTime.from(it) } ?: throw DateTimeException("formatter is null")
    }

    /**
     * Parses [chars] to [OffsetDateTime] with [formatter].
     * If [formatter] is null, throw a [DateTimeException].
     */
    @Throws(DateTimeException::class, DateTimeParseException::class)
    fun parseOffsetDateTime(chars: CharSequence): OffsetDateTime {
        return formatter()?.parse(chars) { OffsetDateTime.from(it) } ?: throw DateTimeException("formatter is null")
    }

    /**
     * Parses [chars] to [LocalDateTime] with [formatter].
     * If [formatter] is null, throw a [DateTimeException].
     */
    @Throws(DateTimeException::class, DateTimeParseException::class)
    fun parseLocalDateTime(chars: CharSequence): LocalDateTime {
        return formatter()?.parse(chars) { LocalDateTime.from(it) } ?: throw DateTimeException("formatter is null")
    }

    /**
     * Parses [chars] to [LocalDate] with [formatter].
     * If [formatter] is null, throw a [DateTimeException].
     */
    @Throws(DateTimeException::class, DateTimeParseException::class)
    fun parseLocalDate(chars: CharSequence): LocalDate {
        return formatter()?.parse(chars) { LocalDate.from(it) } ?: throw DateTimeException("formatter is null")
    }

    /**
     * Parses [chars] to [LocalTime] with [formatter].
     * If [formatter] is null, throw a [DateTimeException].
     */
    @Throws(DateTimeException::class, DateTimeParseException::class)
    fun parseLocalTime(chars: CharSequence): LocalTime {
        return formatter()?.parse(chars) { LocalTime.from(it) } ?: throw DateTimeException("formatter is null")
    }

    companion object {

        /**
         * Builds a [DatePattern] with given string, [pattern], [formatter] and [dateFormat] are not null.
         */
        @JvmName("of")
        @JvmStatic
        fun CharSequence.toDatePattern(): DatePattern {
            return OfPattern(this)
        }

        /**
         * Builds a [DatePattern] with given [DateTimeFormatter], both [pattern] and [dateFormat] are null.
         */
        @JvmName("of")
        @JvmStatic
        fun DateTimeFormatter.toDatePattern(): DatePattern {
            return OfFormatter(this)
        }

        /**
         * Builds a [DatePattern] with given [chars] and [dateTimeFormatter],
         * [pattern], [formatter] and [dateFormat] are not null.
         */
        @JvmStatic
        fun of(chars: CharSequence, dateTimeFormatter: DateTimeFormatter): DatePattern {
            return OfPatternFormatter(chars, dateTimeFormatter)
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
            override val pattern: String? = null
            override fun formatter(): DateTimeFormatter = formatter
            override fun dateFormat(): DateFormat? = null
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

/**
 * Represents a point at time-line, like [Date], [Instant] and various [LocalDateTime].
 */
interface TimePoint {

    /**
     * Returns this time in milliseconds.
     */
    @Throws(DateTimeException::class)
    fun toMillis(): Long {
        return toInstant().toEpochMilli()
    }

    /**
     * Returns this time in seconds.
     */
    @Throws(DateTimeException::class)
    fun toSeconds(): Long {
        return toInstant().epochSecond
    }

    /**
     * Converts to [Date].
     */
    @Throws(DateTimeException::class)
    fun toDate(): Date

    /**
     * Converts to [Date], or null if failed.
     */
    @Throws(DateTimeException::class)
    fun toDateOrNull(): Date?

    /**
     * Converts to [TemporalAccessor].
     */
    @Throws(DateTimeException::class)
    fun toTemporal(): TemporalAccessor

    /**
     * Converts to [TemporalAccessor], or null if failed.
     */
    @Throws(DateTimeException::class)
    fun toTemporalOrNull(): TemporalAccessor?

    /**
     * Converts to [Instant].
     */
    @Throws(DateTimeException::class)
    fun toInstant(): Instant {
        return toTemporal().toInstant()
    }

    /**
     * Converts to [Instant], or null if failed.
     */
    @Throws(DateTimeException::class)
    fun toInstantOrNull(): Instant? {
        return toTemporalOrNull()?.toInstantOrNull()
    }

    /**
     * Converts to [ZonedDateTime].
     */
    @Throws(DateTimeException::class)
    fun toZonedDateTime(): ZonedDateTime {
        return toTemporal().toZonedDateTime()
    }

    /**
     * Converts to [ZonedDateTime], or null if failed.
     */
    @Throws(DateTimeException::class)
    fun toZonedDateTimeOrNull(): ZonedDateTime? {
        return toTemporalOrNull()?.toZonedDateTimeOrNull()
    }

    /**
     * Converts to [OffsetDateTime].
     */
    @Throws(DateTimeException::class)
    fun toOffsetDateTime(): OffsetDateTime {
        return toTemporal().toOffsetDateTime()
    }

    /**
     * Converts to [OffsetDateTime], or null if failed.
     */
    @Throws(DateTimeException::class)
    fun toOffsetDateTimeOrNull(): OffsetDateTime? {
        return toTemporalOrNull()?.toOffsetDateTimeOrNull()
    }

    /**
     * Converts to [LocalDateTime].
     */
    @Throws(DateTimeException::class)
    fun toLocalDateTime(): LocalDateTime {
        return toTemporal().toLocalDateTime()
    }

    /**
     * Converts to [LocalDateTime], or null if failed.
     */
    @Throws(DateTimeException::class)
    fun toLocalDateTimeOrNull(): LocalDateTime? {
        return toTemporalOrNull()?.toLocalDateTimeOrNull()
    }

    /**
     * Converts to [LocalDate].
     */
    @Throws(DateTimeException::class)
    fun toLocalDate(): LocalDate {
        return toTemporal().toLocalDate()
    }

    /**
     * Converts to [LocalDate], or null if failed.
     */
    @Throws(DateTimeException::class)
    fun toLocalDateOrNull(): LocalDate? {
        return toTemporalOrNull()?.toLocalDateOrNull()
    }

    /**
     * Converts to [LocalTime].
     */
    @Throws(DateTimeException::class)
    fun toLocalTime(): LocalTime {
        return toTemporal().toLocalTime()
    }

    /**
     * Converts to [LocalTime], or null if failed.
     */
    @Throws(DateTimeException::class)
    fun toLocalTimeOrNull(): LocalTime? {
        return toTemporalOrNull()?.toLocalTimeOrNull()
    }

    /**
     * Formats to string.
     */
    @Throws(DateTimeException::class)
    fun format(pattern: DatePattern): String

    companion object {

        /**
         * Return now [TimePoint].
         */
        fun now(): TimePoint {
            return Instant.now().toTimePoint()
        }

        /**
         * Parses and returns [TimePoint] from [this].
         */
        @JvmName("of")
        @JvmStatic
        fun Date.toTimePoint(): TimePoint {
            return OfDate(this)
        }

        /**
         * Parses and returns [TimePoint] from [this].
         */
        @JvmName("of")
        @JvmStatic
        fun TemporalAccessor.toTimePoint(): TimePoint {
            return OfTemporal(this)
        }

        /**
         * Parses and returns [TimePoint] from [this].
         */
        @JvmName("of")
        @JvmStatic
        fun CharSequence.toTimePoint(pattern: String): TimePoint {
            return OfChars(this, pattern.toDatePattern())
        }

        /**
         * Parses and returns [TimePoint] from [this].
         */
        @JvmName("of")
        @JvmOverloads
        @JvmStatic
        fun CharSequence.toTimePoint(pattern: DatePattern = BtProps.timestampPattern()): TimePoint {
            return OfChars(this, pattern)
        }

        private class OfDate(private val date: Date) : TimePoint, Serializable {

            override fun toMillis(): Long {
                return date.time
            }

            override fun toSeconds(): Long {
                return date.time / 1000
            }

            override fun toDate(): Date {
                return date
            }

            override fun toDateOrNull(): Date {
                return toDate()
            }

            override fun toTemporal(): TemporalAccessor {
                return date.toInstant()
            }

            override fun toTemporalOrNull(): TemporalAccessor {
                return toTemporal()
            }

            override fun format(pattern: DatePattern): String {
                return pattern.format(date)
            }
        }

        private class OfTemporal(private val temporal: TemporalAccessor) : TimePoint, Serializable {

            override fun toDate(): Date {
                return Date.from(temporal.toInstant())
            }

            override fun toDateOrNull(): Date? {
                val instant = temporal.toInstantOrNull()
                return if (instant === null) null else Date.from(instant)
            }

            override fun toTemporal(): TemporalAccessor {
                return temporal
            }

            override fun toTemporalOrNull(): TemporalAccessor {
                return toTemporal()
            }

            override fun format(pattern: DatePattern): String {
                return pattern.format(temporal)
            }
        }

        private class OfChars(chars: CharSequence, pattern: DatePattern) : TimePoint, Serializable {

            private val temporal: TemporalAccessor?
            private val date: Date?

            init {
                temporal = try {
                    pattern.parseTemporal(chars)
                } catch (e: Exception) {
                    null
                }
                date = try {
                    pattern.parseDate(chars)
                } catch (e: Exception) {
                    null
                }
                if (temporal === null && date === null) {
                    throw DateTimeException("Cannot parse $chars to pattern: $pattern")
                }
            }

            override fun toMillis(): Long {
                if (date !== null) {
                    return date.time
                }
                return temporal!!.toInstant().toEpochMilli()
            }

            override fun toSeconds(): Long {
                if (date !== null) {
                    return date.time / 1000
                }
                return temporal!!.toInstant().epochSecond
            }

            override fun toDate(): Date {
                if (date !== null) {
                    return date
                }
                return temporal!!.toDate()
            }

            override fun toDateOrNull(): Date? {
                if (date !== null) {
                    return date
                }
                return temporal?.toDateOrNull()
            }

            override fun toTemporal(): TemporalAccessor {
                if (temporal !== null) {
                    return temporal
                }
                return date!!.toInstant()
            }

            override fun toTemporalOrNull(): TemporalAccessor? {
                if (temporal !== null) {
                    return temporal
                }
                return date?.toInstant()
            }

            override fun format(pattern: DatePattern): String {
                if (temporal !== null) {
                    return pattern.format(temporal)
                }
                return pattern.format(date!!)
            }
        }
    }
}