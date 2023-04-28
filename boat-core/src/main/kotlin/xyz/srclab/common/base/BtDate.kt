/**
 * Date and time utilities.
 */
@file:JvmName("BtDate")

package xyz.srclab.common.base

import java.io.Serializable
import java.text.DateFormat
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
fun epochSeconds(): Long {
    return epochMillis() / 1000L
}

/**
 * Returns current epoch milliseconds: [System.currentTimeMillis].
 */
fun epochMillis(): Long {
    return System.currentTimeMillis()
}

/**
 * Returns current nanosecond of [epochMillis].
 */
fun currentNanos(): Int {
    return Instant.now().nano
}

/**
 * Returns current system nanosecond: [System.nanoTime].
 */
fun systemNanos(): Long {
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
 * Returns current zone of system.
 */
fun currentZone(): ZoneId {
    return ZoneId.systemDefault()
}

/**
 * Returns current zone offset of system.
 */
fun currentZoneOffset(): ZoneOffset {
    return currentZone().rules.getOffset(Instant.now())
}

/**
 * Formats given date.
 */
fun Date.format(pattern: CharSequence): String {
    return SimpleDateFormat(pattern.toString()).format(this)
}

/**
 * Parses [date] to [Date] with [pattern].
 */
fun parseDate(pattern: CharSequence, date: CharSequence): Date {
    return SimpleDateFormat(pattern.toString()).parse(date.toString())
}

/**
 * Returns [Calendar] built from given date.
 */
@JvmOverloads
fun Date.toCalendar(zone: ZoneId = ZoneId.systemDefault()): Calendar {
    val calendar = Calendar.getInstance(TimeZone.getTimeZone(zone))
    calendar.time = this
    return calendar
}

/**
 * Adds or subtracts the specified amount of time to the given calendar field, based on the calendar's rules.
 * For example, to subtract 5 days from the current time of the calendar, you can achieve it by calling:
 *
 * ```
 * BtDate.add(date, Calendar.DAY_OF_MONTH, -5).
 * ```
 *
 * @param field field from [Calendar]
 * @param amount amount to be added
 * @param zone time zone
 */
@JvmOverloads
fun Date.add(field: Int, amount: Int, zone: ZoneId = ZoneId.systemDefault()): Date {
    val calendar = this.toCalendar(zone)
    calendar.add(field, amount)
    return calendar.time
}

/**
 * Sets the given calendar field to the given value.
 * The value is not interpreted by this method regardless of the leniency mode.
 *
 * @param field field from [Calendar]
 * @param amount amount to be added
 * @param zone time zone
 */
@JvmOverloads
fun Date.set(field: Int, amount: Int, zone: ZoneId = ZoneId.systemDefault()): Date {
    val calendar = this.toCalendar(zone)
    calendar.set(field, amount)
    return calendar.time
}

/**
 * Trims time part of given date, only keep the year, month and day of month. For example:
 *
 * ```
 * Date day1 = BtDate.trimDay(date)
 * // The day1 will be as 'yyyyMMdd'
 * ```
 */
@JvmOverloads
fun Date.trimTime(zone: ZoneId = ZoneId.systemDefault()): Date {
    val calendar = this.toCalendar(zone)
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.time
}

/**
 * Returns next [Date] of which day of month was added.
 *
 * @param day added number of day of month, default is 1
 * @param zone time zone
 */
@JvmOverloads
fun Date.nextDay(day: Int = 1, zone: ZoneId = ZoneId.systemDefault()): Date {
    return this.add(Calendar.DAY_OF_MONTH, day, zone)
}

/**
 * Converts given temporal to [Date].
 */
@JvmOverloads
fun TemporalAccessor.toDate(zone: ZoneId = ZoneId.systemDefault()): Date {
    return when (this) {
        is Instant -> Date.from(this)
        is ZonedDateTime -> Date.from(Instant.ofEpochSecond(this.toEpochSecond(), this.nano.toLong()))
        is OffsetDateTime -> Date.from(Instant.ofEpochSecond(this.toEpochSecond(), this.nano.toLong()))

        is LocalDateTime -> {
            val zonedDateTime = this.atZone(zone)
            Date.from(Instant.ofEpochSecond(zonedDateTime.toEpochSecond(), zonedDateTime.nano.toLong()))
        }

        is LocalDate -> {
            val zonedDateTime = ZonedDateTime.of(this, LocalTime.MIN, zone)
            Date.from(Instant.ofEpochSecond(zonedDateTime.toEpochSecond(), zonedDateTime.nano.toLong()))
        }

        is LocalTime -> {
            val zonedDateTime = ZonedDateTime.of(LocalDate.MIN, this, zone)
            Date.from(Instant.ofEpochSecond(zonedDateTime.toEpochSecond(), zonedDateTime.nano.toLong()))
        }

        else -> {
            val localDateTime = getLocalDateTime()
            val offset = getZoneOffset()
            if (offset === null) {
                val zonedDateTime = localDateTime.atZone(zone)
                Date.from(Instant.ofEpochSecond(zonedDateTime.toEpochSecond(), zonedDateTime.nano.toLong()))
            } else {
                val offsetDateTime = localDateTime.atOffset(offset)
                Date.from(Instant.ofEpochSecond(offsetDateTime.toEpochSecond(), offsetDateTime.nano.toLong()))
            }
        }
    }
}

/**
 * Converts to [Date].
 */
fun Duration.toDate(): Date {
    return Date.from(Instant.ofEpochSecond(this.seconds, this.nano.toLong()))
}

/**
 * Returns zone offset of given [TemporalAccessor].
 *
 * This method attempts the value through these one or more possible ways:
 * [ChronoField.OFFSET_SECONDS].
 * The result of those fields will be seen as 0 if it is failed to get.
 */
fun TemporalAccessor.getZoneOffset(): ZoneOffset? {
    if (this is ZoneOffset) {
        return this
    }
    if (this.isSupported(ChronoField.OFFSET_SECONDS)) {
        return ZoneOffset.ofTotalSeconds(this.get(ChronoField.OFFSET_SECONDS))
    }
    return null
}

/**
 * Returns instant of given [TemporalAccessor].
 *
 * This method attempts the value through these one or more possible ways:
 * [getNanoOfSecond],
 * [ChronoField.INSTANT_SECONDS].
 * The result of those fields will be seen as 0 if it is failed to get.
 */
fun TemporalAccessor.getInstant(): Instant? {
    if (this is Instant) {
        return this
    }
    if (this.isSupported(ChronoField.INSTANT_SECONDS)) {
        val second = this.getLong(ChronoField.INSTANT_SECONDS)
        val nano = getNanoOfSecond() ?: 0
        return Instant.ofEpochSecond(second, nano.toLong())
    }
    return null
}

/**
 * Returns zoned local date time of given [TemporalAccessor].
 *
 * This method attempts the value through these one or more possible ways:
 * [getZoneOffset], [getLocalDateTime].
 */
fun TemporalAccessor.getZonedDateTime(): ZonedDateTime? {
    if (this is ZonedDateTime) {
        return this
    }
    val zone = getZoneOffset()
    if (zone === null) {
        return null
    }
    return getLocalDateTime().atZone(zone)
}

/**
 * Returns offset local date time of given [TemporalAccessor].
 *
 * This method attempts the value through these one or more possible ways:
 * [getZoneOffset], [getLocalDateTime].
 */
fun TemporalAccessor.getOffsetLocalDateTime(): OffsetDateTime? {
    if (this is OffsetDateTime) {
        return this
    }
    val zone = getZoneOffset()
    if (zone === null) {
        return null
    }
    return getLocalDateTime().atOffset(zone)
}

/**
 * Returns local date time of given [TemporalAccessor].
 *
 * This method attempts the value through these one or more possible ways:
 * [getLocalDate], [getLocalTime].
 */
fun TemporalAccessor.getLocalDateTime(): LocalDateTime {
    if (this is LocalDateTime) {
        return this
    }
    return LocalDateTime.of(getLocalDate(), getLocalTime())
}

/**
 * Returns local date of given [TemporalAccessor].
 *
 * This method attempts the value through these one or more possible ways:
 * [getYear],
 * [ChronoField.EPOCH_DAY], [ChronoField.DAY_OF_YEAR], [ChronoField.MONTH_OF_YEAR],
 * [ChronoField.DAY_OF_MONTH].
 * The result of those fields will be seen as 0 if it is failed to get.
 */
fun TemporalAccessor.getLocalDate(): LocalDate {
    if (this is LocalDate) {
        return this
    }

    if (this.isSupported(ChronoField.EPOCH_DAY)) {
        return LocalDate.ofEpochDay(this.getLong(ChronoField.EPOCH_DAY))
    }

    val year = getYear() ?: 0

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
 * Returns local time of given [TemporalAccessor].
 *
 * This method attempts the value through these one or more possible ways:
 * [getNanoOfSecond], [getHourOfDay],
 * [ChronoField.NANO_OF_DAY], [ChronoField.SECOND_OF_DAY], [ChronoField.SECOND_OF_MINUTE],
 * [ChronoField.MINUTE_OF_DAY], [ChronoField.MINUTE_OF_HOUR].
 * The result of those fields will be seen as 0 if it is failed to get.
 */
fun TemporalAccessor.getLocalTime(): LocalTime {
    if (this is LocalTime) {
        return this
    }
    if (this.isSupported(ChronoField.NANO_OF_DAY)) {
        return LocalTime.ofNanoOfDay(this.getLong(ChronoField.NANO_OF_DAY))
    }

    val nano = getNanoOfSecond() ?: 0

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

    val hour = getHourOfDay() ?: 0

    var minute = 0
    if (this.isSupported(ChronoField.MINUTE_OF_HOUR)) {
        minute = this.get(ChronoField.MINUTE_OF_HOUR)
    }

    return LocalTime.of(hour, minute, second, nano)
}

/**
 * Returns nano of second of given [TemporalAccessor], or null if failed.
 *
 * This method attempts the value through these one or more possible ways:
 * [ChronoField.NANO_OF_SECOND], [ChronoField.MICRO_OF_SECOND], [ChronoField.MILLI_OF_SECOND].
 */
fun TemporalAccessor.getNanoOfSecond(): Int? {
    if (this.isSupported(ChronoField.NANO_OF_SECOND)) {
        return this.get(ChronoField.NANO_OF_SECOND)
    }
    return getNanoOfSecond0()
}

private fun TemporalAccessor.getNanoOfSecond0(): Int? {
    if (this.isSupported(ChronoField.MICRO_OF_SECOND)) {
        return this.get(ChronoField.MICRO_OF_SECOND) * 1000
    } else if (this.isSupported(ChronoField.MILLI_OF_SECOND)) {
        return this.get(ChronoField.MILLI_OF_SECOND) * 1000_000
    }
    return null
}

/**
 * Returns hour of day of given [TemporalAccessor], or null if failed.
 *
 * This method attempts the value through these one or more possible ways:
 * [ChronoField.HOUR_OF_DAY], [ChronoField.AMPM_OF_DAY], [ChronoField.CLOCK_HOUR_OF_DAY],
 * [ChronoField.HOUR_OF_AMPM], [ChronoField.CLOCK_HOUR_OF_AMPM].
 */
fun TemporalAccessor.getHourOfDay(): Int? {
    if (this.isSupported(ChronoField.HOUR_OF_DAY)) {
        return this.get(ChronoField.HOUR_OF_DAY)
    }
    return getHourOfDay0()
}

private fun TemporalAccessor.getHourOfDay0(): Int? {

    fun getAmPm(): Int? {
        if (this.isSupported(ChronoField.AMPM_OF_DAY)) {
            return this.get(ChronoField.AMPM_OF_DAY)
        }
        return null
    }

    if (this.isSupported(ChronoField.CLOCK_HOUR_OF_DAY)) {
        return this.get(ChronoField.CLOCK_HOUR_OF_DAY) - 1
    } else if (this.isSupported(ChronoField.HOUR_OF_AMPM)) {
        val ampm = getAmPm()
        if (ampm === null) {
            return null
        }
        return this.get(ChronoField.HOUR_OF_AMPM) + ampm * 12
    } else if (this.isSupported(ChronoField.CLOCK_HOUR_OF_AMPM)) {
        val ampm = getAmPm()
        if (ampm === null) {
            return null
        }
        return this.get(ChronoField.CLOCK_HOUR_OF_AMPM) - 1 + ampm * 12
    }
    return null
}

/**
 * Returns year of given [TemporalAccessor], or null if failed.
 *
 * This method attempts the value through these one or more possible ways:
 * [ChronoField.YEAR], [ChronoField.YEAR_OF_ERA], [ChronoField.ERA].
 */
fun TemporalAccessor.getYear(): Int? {
    if (this.isSupported(ChronoField.YEAR)) {
        return this.get(ChronoField.YEAR)
    }
    return getYear0()
}

private fun TemporalAccessor.getYear0(): Int? {
    if (this.isSupported(ChronoField.YEAR_OF_ERA)) {
        if (this.isSupported(ChronoField.ERA)) {
            val era = this.get(ChronoField.ERA)
            if (era == 1) {
                return this.get(ChronoField.YEAR_OF_ERA)
            } else if (era == 0) {
                return -this.get(ChronoField.YEAR_OF_ERA) + 1
            }
        }
    }
    return null
}

/**
 * Pattern for date time, used to format/parse between string and date types.
 *
 * This pattern provides both [DateTimeFormatter] and [DateFormat] to compatible with
 * JDK8 and higher and JDK7 and lower, and it uses suitable formatter to format/parse the date of different type.
 */
interface DatePattern {

    /**
     * Pattern of date in [String], may be null (if based on [DateTimeFormatter]).
     */
    val pattern: String?

    /**
     * Returns a [DateTimeFormatter] to compatible with JDK8 and higher, may be null.
     */
    fun formatter(): DateTimeFormatter?

    /**
     * Returns a [DateFormat] to compatible with JDK7 and lower, may be null.
     */
    fun dateFormat(): DateFormat?

    /**
     * Formats [date] to string.
     */
    @Throws(DateTimeException::class)
    fun format(date: Date): String {
        return dateFormat()?.format(date)
            ?: formatter()?.format(date.toInstant())
            ?: throw DateTimeException(ALL_FORMATTER_NULL)
    }

    /**
     * Formats [temporal] to string.
     */
    @Throws(DateTimeException::class)
    fun format(temporal: TemporalAccessor): String {
        return formatter()?.format(temporal)
            ?: dateFormat()?.format(temporal.toDate())
            ?: throw DateTimeException(ALL_FORMATTER_NULL)
    }

    /**
     * Parses [chars] to [Date].
     */
    @Throws(DateTimeException::class)
    fun parseDate(chars: CharSequence): Date {
        val df = dateFormat()
        if (df !== null) {
            return df.parse(chars.toString())
        }
        val t = formatter()?.parse(chars)
        if (t === null) {
            throw DateTimeException(ALL_FORMATTER_NULL)
        }
        val instant = t.getInstant()
        if (instant !== null) {
            return Date.from(instant)
        }
        val localDateTime = t.getLocalDateTime()
        val zone = t.getZoneOffset()
        return Date.from(localDateTime.atZone(zone ?: ZoneId.systemDefault()).toInstant())
    }

    /**
     * Parses [chars] to [TemporalAccessor].
     */
    @Throws(DateTimeException::class)
    fun parseTemporal(chars: CharSequence): TemporalAccessor {
        return formatter()?.parse(chars)
            ?: dateFormat()?.parse(chars.toString())?.toInstant()
            ?: throw DateTimeException(ALL_FORMATTER_NULL)
    }

    /**
     * Parses [chars] to [Instant].
     */
    @Throws(DateTimeException::class)
    fun parseInstant(chars: CharSequence): Instant {
        return formatter()?.parse(chars) { Instant.from(it) }
            ?: dateFormat()?.parse(chars.toString())?.toInstant()
            ?: throw DateTimeException(ALL_FORMATTER_NULL)
    }

    /**
     * Parses [chars] to [ZonedDateTime].
     */
    @Throws(DateTimeException::class)
    fun parseZonedDateTime(chars: CharSequence): ZonedDateTime {
        return formatter()?.parse(chars) { ZonedDateTime.from(it) }
            ?: dateFormat()?.parse(chars.toString())?.toInstant()?.atZone(ZoneId.systemDefault())
            ?: throw DateTimeException(ALL_FORMATTER_NULL)
    }

    /**
     * Parses [chars] to [OffsetDateTime].
     */
    @Throws(DateTimeException::class)
    fun parseOffsetDateTime(chars: CharSequence): OffsetDateTime {
        return formatter()?.parse(chars) { OffsetDateTime.from(it) }
            ?: dateFormat()?.parse(chars.toString())?.toInstant()?.atOffset(currentZoneOffset())
            ?: throw DateTimeException(ALL_FORMATTER_NULL)
    }

    /**
     * Parses [chars] to [LocalDateTime].
     */
    @Throws(DateTimeException::class, DateTimeParseException::class)
    fun parseLocalDateTime(chars: CharSequence): LocalDateTime {
        return formatter()?.parse(chars) { LocalDateTime.from(it) }
            ?: dateFormat()?.parse(chars.toString())?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDateTime()
            ?: throw DateTimeException(ALL_FORMATTER_NULL)
    }

    /**
     * Parses [chars] to [LocalDate].
     */
    @Throws(DateTimeException::class, DateTimeParseException::class)
    fun parseLocalDate(chars: CharSequence): LocalDate {
        return formatter()?.parse(chars) { LocalDate.from(it) }
            ?: dateFormat()?.parse(chars.toString())?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalDate()
            ?: throw DateTimeException(ALL_FORMATTER_NULL)
    }

    /**
     * Parses [chars] to [LocalTime].
     */
    @Throws(DateTimeException::class, DateTimeParseException::class)
    fun parseLocalTime(chars: CharSequence): LocalTime {
        return formatter()?.parse(chars) { LocalTime.from(it) }
            ?: dateFormat()?.parse(chars.toString())?.toInstant()?.atZone(ZoneId.systemDefault())?.toLocalTime()
            ?: throw DateTimeException(ALL_FORMATTER_NULL)
    }

    companion object {

        private const val ALL_FORMATTER_NULL = "All formatter are null."

        /**
         * Builds a [DatePattern] with given string, [pattern], [formatter] and [dateFormat] are not null.
         */
        @JvmStatic
        fun of(chars: CharSequence): DatePattern {
            return OfPattern(chars)
        }

        /**
         * Builds a [DatePattern] with given [DateTimeFormatter], both [pattern] and [dateFormat] are null.
         */
        @JvmStatic
        fun of(dateTimeFormatter: DateTimeFormatter): DatePattern {
            return OfFormatter(dateTimeFormatter)
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
     * Zone of this time.
     */
    val zone: ZoneId

    /**
     * Returns this time as epoch milliseconds.
     */
    @Throws(DateTimeException::class)
    fun epochMillis(): Long

    /**
     * Returns this time as epoch seconds.
     */
    @Throws(DateTimeException::class)
    fun epochSeconds(): Long

    /**
     * Returns nanoseconds of epoch second of this time.
     */
    @Throws(DateTimeException::class)
    fun nanoOfSecond(): Int

    /**
     * Converts to [Date].
     */
    @Throws(DateTimeException::class)
    fun toDate(): Date = Date.from(toInstant())

    /**
     * Converts to [TemporalAccessor].
     */
    @Throws(DateTimeException::class)
    fun toTemporal(): TemporalAccessor

    /**
     * Converts to [Instant].
     */
    @Throws(DateTimeException::class)
    fun toInstant(): Instant

    /**
     * Converts to [ZonedDateTime].
     */
    @Throws(DateTimeException::class)
    fun toZonedDateTime(): ZonedDateTime

    /**
     * Converts to [OffsetDateTime].
     */
    @Throws(DateTimeException::class)
    fun toOffsetDateTime(): OffsetDateTime

    /**
     * Converts to [LocalDateTime].
     */
    @Throws(DateTimeException::class)
    fun toLocalDateTime(): LocalDateTime {
        return toZonedDateTime().toLocalDateTime()
    }

    /**
     * Converts to [LocalDate].
     */
    @Throws(DateTimeException::class)
    fun toLocalDate(): LocalDate {
        return toLocalDateTime().toLocalDate()
    }

    /**
     * Converts to [LocalTime].
     */
    @Throws(DateTimeException::class)
    fun toLocalTime(): LocalTime {
        return toLocalDateTime().toLocalTime()
    }

    /**
     * Formats to string.
     */
    @Throws(DateTimeException::class)
    fun format(pattern: DatePattern): String

    /**
     * Formats to string.
     */
    @Throws(DateTimeException::class)
    fun format(pattern: CharSequence): String

    /**
     * Returns [TimePoint] for [zone] with same instant.
     */
    fun atZone(zone: ZoneId): TimePoint

    companion object {

        /**
         * Returns [TimePoint] from given temporal.
         */
        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun TemporalAccessor.toTimePoint(zone: ZoneId = ZoneId.systemDefault()): TimePoint {
            return when (this) {
                is Instant -> OfInstant(this, zone)
                is ZonedDateTime -> OfZonedDateTime(this, zone)
                is OffsetDateTime -> OfZonedDateTime(this.toZonedDateTime(), zone)
                is LocalDateTime -> OfZonedDateTime(this.atZone(zone), zone)
                is LocalDate -> OfZonedDateTime(this.atTime(LocalTime.MIN).atZone(zone), zone)
                is LocalTime -> OfZonedDateTime(this.atDate(LocalDate.MIN).atZone(zone), zone)
                else -> {
                    val zonedDateTime = this.getZonedDateTime()
                    if (zonedDateTime !== null) {
                        return OfZonedDateTime(zonedDateTime, zone)
                    }
                    OfZonedDateTime(this.getLocalDateTime().atZone(zone), zone)
                }
            }
        }

        /**
         * Returns [TimePoint] from given date.
         */
        @JvmName("of")
        @JvmStatic
        @JvmOverloads
        fun Date.toTimePoint(zone: ZoneId = ZoneId.systemDefault()): TimePoint {
            return OfDate(this, zone)
        }

        /**
         * Return [TimePoint] of now.
         */
        fun now(): TimePoint {
            return Instant.now().toTimePoint()
        }

        /**
         * Returns [TimePoint] from given params.
         */
        @JvmStatic
        @JvmOverloads
        fun of(
            year: Int,
            month: Int,
            dayOfMonth: Int,
            zone: ZoneId = ZoneId.systemDefault()
        ): TimePoint {
            return of(year, month, dayOfMonth, 0, 0, 0, 0, zone)
        }

        /**
         * Returns [TimePoint] from given params.
         */
        @JvmStatic
        @JvmOverloads
        fun of(
            year: Int,
            month: Int,
            dayOfMonth: Int,
            hour: Int,
            minute: Int,
            second: Int,
            nanoOfSecond: Int = 0,
            zone: ZoneId = ZoneId.systemDefault()
        ): TimePoint {
            return OfZonedDateTime(
                ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, nanoOfSecond, zone),
                zone
            )
        }

        private class OfDate(
            private val date: Date,
            override val zone: ZoneId
        ) : TimePoint, Serializable {

            override fun epochMillis(): Long = date.time

            override fun epochSeconds(): Long = epochMillis() / 1000

            override fun nanoOfSecond(): Int = 0

            override fun toDate(): Date = date

            override fun toTemporal(): TemporalAccessor = toInstant()

            override fun toInstant(): Instant = date.toInstant()

            override fun toZonedDateTime(): ZonedDateTime {
                return toInstant().atZone(zone)
            }

            override fun toOffsetDateTime(): OffsetDateTime {
                val instant = toInstant()
                return instant.atOffset(zone.rules.getOffset(instant))
            }

            override fun format(pattern: DatePattern): String {
                return pattern.format(date)
            }

            override fun format(pattern: CharSequence): String {
                return date.format(pattern)
            }

            override fun atZone(zone: ZoneId): TimePoint {
                if (this.zone == zone) {
                    return this
                }
                return OfDate(date, zone)
            }
        }

        private class OfInstant(
            private val instant: Instant,
            override val zone: ZoneId
        ) : TimePoint, Serializable {

            override fun epochMillis(): Long = instant.toEpochMilli()

            override fun epochSeconds(): Long = instant.epochSecond

            override fun nanoOfSecond(): Int = instant.nano

            override fun toTemporal(): TemporalAccessor = instant

            override fun toInstant(): Instant = instant

            override fun toZonedDateTime(): ZonedDateTime {
                return instant.atZone(zone)
            }

            override fun toOffsetDateTime(): OffsetDateTime {
                return instant.atOffset(zone.rules.getOffset(instant))
            }

            override fun format(pattern: DatePattern): String {
                return pattern.format(instant)
            }

            override fun format(pattern: CharSequence): String {
                return DateTimeFormatter.ofPattern(pattern.toString()).format(instant)
            }

            override fun atZone(zone: ZoneId): TimePoint {
                if (this.zone == zone) {
                    return this
                }
                return OfInstant(instant, zone)
            }
        }

        private class OfZonedDateTime(
            zonedDateTime: ZonedDateTime,
            override val zone: ZoneId
        ) : TimePoint, Serializable {

            private val dateTime = zonedDateTime.withZoneSameInstant(zone)

            override fun epochMillis(): Long =
                dateTime.toEpochSecond() * 1000 + dateTime.nano / 1000000

            override fun epochSeconds(): Long = dateTime.toEpochSecond()

            override fun nanoOfSecond(): Int = dateTime.nano

            override fun toTemporal(): TemporalAccessor = dateTime

            override fun toInstant(): Instant = dateTime.toInstant()

            override fun toZonedDateTime(): ZonedDateTime = dateTime

            override fun toOffsetDateTime(): OffsetDateTime {
                return dateTime.toOffsetDateTime()
            }

            override fun format(pattern: DatePattern): String {
                return pattern.format(dateTime)
            }

            override fun format(pattern: CharSequence): String {
                return DateTimeFormatter.ofPattern(pattern.toString()).format(dateTime)
            }

            override fun atZone(zone: ZoneId): TimePoint {
                if (this.zone == zone) {
                    return this
                }
                return OfZonedDateTime(dateTime, zone)
            }
        }
    }
}