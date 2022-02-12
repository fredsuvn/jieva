@file:JvmName("BTime")

package xyz.srclab.common.base

import xyz.srclab.common.base.DatePattern.Companion.toDatePattern
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAccessor
import java.util.*

const val TIMESTAMP_PATTERN_STRING = "yyyyMMddHHmmssSSS"

@JvmField
val TIMESTAMP_PATTERN: DatePattern = run {
    //Fuck jdk8
    //"yyyyMMddHHmmssSSS".toDatePattern()
    if (isJdk9OrHigher()) {
        return@run TIMESTAMP_PATTERN_STRING.toDatePattern()
    }
    val formatter: DateTimeFormatter = DateTimeFormatterBuilder() // date/time
        .appendPattern("yyyyMMddHHmmss") // milliseconds
        .appendValue(ChronoField.MILLI_OF_SECOND, 3) // create formatter
        .toFormatter()
    TIMESTAMP_PATTERN_STRING.toDatePattern(formatter)
}

fun epochSeconds(): Long {
    return epochMillis() / 1000L
}

fun epochMillis(): Long {
    return System.currentTimeMillis()
}

@JvmOverloads
fun timestamp(pattern: DatePattern = TIMESTAMP_PATTERN, zoneId: ZoneId = ZoneId.systemDefault()): String {
    return pattern.format(LocalDateTime.now(zoneId))
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

fun TemporalAccessor.getYear(): Int {
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

    fun parseTemporalAccessor(chars: CharSequence): TemporalAccessor {
        return toFormatter().parse(chars)
    }

    fun parseTemporalAccessor(obj: Any): TemporalAccessor {
        if (obj is TemporalAccessor) {
            return obj
        }
        if (obj is Date) {
            return obj.toInstant()
        }
        return parseTemporalAccessor(obj.toString())
    }

    fun parseTemporalAccessorOrNull(chars: CharSequence): TemporalAccessor? {
        return try {
            toFormatter().parse(chars)
        } catch (e: DateTimeParseException) {
            null
        }
    }

    fun parseTemporalAccessorOrNull(obj: Any): TemporalAccessor? {
        if (obj is TemporalAccessor) {
            return obj
        }
        if (obj is Date) {
            return obj.toInstant()
        }
        return parseTemporalAccessorOrNull(obj.toString())
    }

    fun parseDate(chars: CharSequence): Date {
        return toDateFormat().parse(chars.toString())
    }

    fun parseDate(obj: Any): Date {
        if (obj is Instant) {
            return Date.from(obj)
        }
        if (obj is Date) {
            return obj
        }
        if (obj is TemporalAccessor) {
            return Date.from(Instant.from(obj))
        }
        return parseDate(obj.toString())
    }

    fun parseInstant(chars: CharSequence): Instant {
        return Instant.parse(chars)
    }

    fun parseInstant(obj: Any): Instant {
        if (obj is Instant) {
            return obj
        }
        if (obj is Date) {
            return obj.toInstant()
        }
        if (obj is TemporalAccessor) {
            return Instant.from(obj)
        }
        return parseInstant(obj.toString())
    }

    fun parseZonedDateTime(chars: CharSequence): ZonedDateTime {
        return ZonedDateTime.parse(chars, toFormatter())
    }

    fun parseZonedDateTime(obj: Any): ZonedDateTime {
        if (obj is ZonedDateTime) {
            return obj
        }
        if (obj is Date) {
            return ZonedDateTime.ofInstant(obj.toInstant(), ZoneId.systemDefault())
        }
        if (obj is TemporalAccessor) {
            return ZonedDateTime.from(obj)
        }
        return parseZonedDateTime(obj.toString())
    }

    fun parseOffsetDateTime(chars: CharSequence): OffsetDateTime {
        return OffsetDateTime.parse(chars, toFormatter())
    }

    fun parseOffsetDateTime(obj: Any): OffsetDateTime {
        if (obj is OffsetDateTime) {
            return obj
        }
        if (obj is Date) {
            return OffsetDateTime.ofInstant(obj.toInstant(), ZoneId.systemDefault())
        }
        if (obj is TemporalAccessor) {
            return OffsetDateTime.from(obj)
        }
        return parseOffsetDateTime(obj.toString())
    }

    fun parseLocalDateTime(chars: CharSequence): LocalDateTime {
        return LocalDateTime.parse(chars, toFormatter())
    }

    fun parseLocalDateTime(obj: Any): LocalDateTime {
        if (obj is LocalDateTime) {
            return obj
        }
        if (obj is Date) {
            return LocalDateTime.ofInstant(obj.toInstant(), ZoneId.systemDefault())
        }
        if (obj is TemporalAccessor) {
            return LocalDateTime.from(obj)
        }
        return parseLocalDateTime(obj.toString())
    }

    fun parseLocalDate(chars: CharSequence): LocalDate {
        return LocalDate.parse(chars, toFormatter())
    }

    fun parseLocalDate(obj: Any): LocalDate {
        if (obj is LocalDate) {
            return obj
        }
        if (obj is Date) {
            return parseTemporalAccessor(obj).toLocalDate()
        }
        if (obj is TemporalAccessor) {
            return LocalDate.from(obj)
        }
        return parseLocalDate(obj.toString())
    }

    fun parseLocalTime(chars: CharSequence): LocalTime {
        return LocalTime.parse(chars, toFormatter())
    }

    fun parseLocalTime(obj: Any): LocalTime {
        if (obj is LocalTime) {
            return obj
        }
        if (obj is Date) {
            return parseTemporalAccessor(obj).toLocalTime()
        }
        if (obj is TemporalAccessor) {
            return LocalTime.from(obj)
        }
        return parseLocalTime(obj.toString())
    }

    fun buildInstant(chars: CharSequence): Instant {
        return parseTemporalAccessor(chars).toInstant()
    }

    fun buildInstant(obj: Any): Instant {
        if (obj is Instant) {
            return obj
        }
        if (obj is Date) {
            return obj.toInstant()
        }
        if (obj is TemporalAccessor) {
            return obj.toInstant()
        }
        return buildInstant(obj.toString())
    }

    fun buildZonedDateTime(chars: CharSequence): ZonedDateTime {
        return parseTemporalAccessor(chars).toZonedDateTime()
    }

    fun buildZonedDateTime(obj: Any): ZonedDateTime {
        if (obj is ZonedDateTime) {
            return obj
        }
        if (obj is Date) {
            return obj.toZonedDateTime()
        }
        if (obj is TemporalAccessor) {
            return obj.toZonedDateTime()
        }
        return buildZonedDateTime(obj.toString())
    }

    fun buildOffsetDateTime(chars: CharSequence): OffsetDateTime {
        return parseTemporalAccessor(chars).toOffsetDateTime()
    }

    fun buildOffsetDateTime(obj: Any): OffsetDateTime {
        if (obj is OffsetDateTime) {
            return obj
        }
        if (obj is Date) {
            return obj.toOffsetDateTime()
        }
        if (obj is TemporalAccessor) {
            return obj.toOffsetDateTime()
        }
        return buildOffsetDateTime(obj.toString())
    }

    fun buildLocalDateTime(chars: CharSequence): LocalDateTime {
        return parseTemporalAccessor(chars).toLocalDateTime()
    }

    fun buildLocalDateTime(obj: Any): LocalDateTime {
        if (obj is LocalDateTime) {
            return obj
        }
        if (obj is Date) {
            return obj.toLocalDateTime()
        }
        if (obj is TemporalAccessor) {
            return obj.toLocalDateTime()
        }
        return buildLocalDateTime(obj.toString())
    }

    fun buildLocalDate(chars: CharSequence): LocalDate {
        return parseTemporalAccessor(chars).toLocalDate()
    }

    fun buildLocalDate(obj: Any): LocalDate {
        if (obj is LocalDate) {
            return obj
        }
        if (obj is Date) {
            return obj.toLocalDate()
        }
        if (obj is TemporalAccessor) {
            return obj.toLocalDate()
        }
        return buildLocalDate(obj.toString())
    }

    fun buildLocalTime(chars: CharSequence): LocalTime {
        return parseTemporalAccessor(chars).toLocalTime()
    }

    fun buildLocalTime(obj: Any): LocalTime {
        if (obj is LocalTime) {
            return obj
        }
        if (obj is Date) {
            return obj.toLocalTime()
        }
        if (obj is TemporalAccessor) {
            return obj.toLocalTime()
        }
        return buildLocalTime(obj.toString())
    }

    fun buildInstantOrNull(chars: CharSequence): Instant? {
        return parseTemporalAccessorOrNull(chars)?.toInstantOrNull()
    }

    fun buildInstantOrNull(obj: Any): Instant? {
        if (obj is Instant) {
            return obj
        }
        if (obj is Date) {
            return obj.toInstant()
        }
        if (obj is TemporalAccessor) {
            return obj.toInstantOrNull()
        }
        return buildInstantOrNull(obj.toString())
    }

    fun buildZonedDateTimeOrNull(chars: CharSequence): ZonedDateTime? {
        return parseTemporalAccessorOrNull(chars)?.toZonedDateTimeOrNull()
    }

    fun buildZonedDateTimeOrNull(obj: Any): ZonedDateTime? {
        if (obj is ZonedDateTime) {
            return obj
        }
        if (obj is Date) {
            return obj.toZonedDateTime()
        }
        if (obj is TemporalAccessor) {
            return obj.toZonedDateTimeOrNull()
        }
        return buildZonedDateTimeOrNull(obj.toString())
    }

    fun buildOffsetDateTimeOrNull(chars: CharSequence): OffsetDateTime? {
        return parseTemporalAccessorOrNull(chars)?.toOffsetDateTimeOrNull()
    }

    fun buildOffsetDateTimeOrNull(obj: Any): OffsetDateTime? {
        if (obj is OffsetDateTime) {
            return obj
        }
        if (obj is Date) {
            return obj.toOffsetDateTime()
        }
        if (obj is TemporalAccessor) {
            return obj.toOffsetDateTimeOrNull()
        }
        return buildOffsetDateTimeOrNull(obj.toString())
    }

    fun buildLocalDateTimeOrNull(chars: CharSequence): LocalDateTime? {
        return parseTemporalAccessorOrNull(chars)?.toLocalDateTimeOrNull()
    }

    fun buildLocalDateTimeOrNull(obj: Any): LocalDateTime? {
        if (obj is LocalDateTime) {
            return obj
        }
        if (obj is Date) {
            return obj.toLocalDateTime()
        }
        if (obj is TemporalAccessor) {
            return obj.toLocalDateTimeOrNull()
        }
        return buildLocalDateTimeOrNull(obj.toString())
    }

    fun buildLocalDateOrNull(chars: CharSequence): LocalDate? {
        return parseTemporalAccessorOrNull(chars)?.toLocalDateOrNull()
    }

    fun buildLocalDateOrNull(obj: Any): LocalDate? {
        if (obj is LocalDate) {
            return obj
        }
        if (obj is Date) {
            return obj.toLocalDate()
        }
        if (obj is TemporalAccessor) {
            return obj.toLocalDateOrNull()
        }
        return buildLocalDateOrNull(obj.toString())
    }

    fun buildLocalTimeOrNull(chars: CharSequence): LocalTime? {
        return parseTemporalAccessorOrNull(chars)?.toLocalTimeOrNull()
    }

    fun buildLocalTimeOrNull(obj: Any): LocalTime? {
        if (obj is LocalTime) {
            return obj
        }
        if (obj is Date) {
            return obj.toLocalTime()
        }
        if (obj is TemporalAccessor) {
            return obj.toLocalTimeOrNull()
        }
        return buildLocalTimeOrNull(obj.toString())
    }

    companion object {

        @JvmName("of")
        @JvmStatic
        fun CharSequence.toDatePattern(): DatePattern {
            return OfPattern(this)
        }

        @JvmName("of")
        @JvmStatic
        fun CharSequence.toDatePattern(formatter: DateTimeFormatter): DatePattern {
            return OfPatternFormatter(this, formatter)
        }

        /**
         * Note `pattern` and `toDateFormat` is unsupported for returned [DatePattern].
         */
        @JvmName("of")
        @JvmStatic
        fun DateTimeFormatter.toDatePattern(): DatePattern {
            return OfFormatter(this)
        }

        private class OfPattern(
            pattern: CharSequence
        ) : DatePattern {

            override val pattern: String = pattern.toString()
            private val formatter = DateTimeFormatter.ofPattern(this.pattern)

            override fun toFormatter(): DateTimeFormatter = formatter

            override fun toDateFormat(): DateFormat {
                return SimpleDateFormat(pattern)
            }
        }

        private class OfFormatter(
            private val formatter: DateTimeFormatter
        ) : DatePattern {

            override val pattern: String = throw UnsupportedOperationException("")

            override fun toFormatter(): DateTimeFormatter = formatter

            override fun toDateFormat(): DateFormat {
                throw UnsupportedOperationException("")
            }
        }

        private class OfPatternFormatter(
            pattern: CharSequence,
            private val formatter: DateTimeFormatter
        ) : DatePattern {

            override val pattern: String = pattern.toString()

            override fun toFormatter(): DateTimeFormatter = formatter

            override fun toDateFormat(): DateFormat {
                return SimpleDateFormat(pattern)
            }
        }
    }
}