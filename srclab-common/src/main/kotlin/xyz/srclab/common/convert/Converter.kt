package xyz.srclab.common.convert

import xyz.srclab.common.base.*
import xyz.srclab.common.reflect.TypeRef
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.util.*

interface Converter {

    @Throws(UnsupportedOperationException::class)
    fun <T> convert(from: Any?, to: Class<T>): T

    @Throws(UnsupportedOperationException::class)
    fun <T> convert(from: Any?, to: Type): T

    @Throws(UnsupportedOperationException::class)
    fun <T> convert(from: Any?, to: TypeRef<T>): T

    @Throws(UnsupportedOperationException::class)
    fun toString(any: Any?): String {
        return convert(any, String::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toBoolean(any: Any?): Boolean {
        return convert(any, Boolean::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toByte(any: Any?): Byte {
        return convert(any, Byte::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toShort(any: Any?): Short {
        return convert(any, Short::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toChar(any: Any?): Char {
        return convert(any, Char::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toInt(any: Any?): Int {
        return convert(any, Int::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toLong(any: Any?): Long {
        return convert(any, Long::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toFloat(any: Any?): Float {
        return convert(any, Float::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toDouble(any: Any?): Double {
        return convert(any, Double::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toBigInteger(any: Any?): BigInteger {
        return convert(any, BigInteger::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toBigDecimal(any: Any?): BigDecimal {
        return convert(any, BigDecimal::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toDate(any: Any?): Date {
        return convert(any, Date::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toInstant(any: Any?): Instant {
        return convert(any, Instant::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toLocalDateTime(any: Any?): LocalDateTime {
        return convert(any, LocalDateTime::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toZonedDateTime(any: Any?): ZonedDateTime {
        return convert(any, ZonedDateTime::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toOffsetDateTime(any: Any?): OffsetDateTime {
        return convert(any, OffsetDateTime::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toDuration(any: Any?): Duration {
        return convert(any, Duration::class.java)
    }

    class Builder : HandlersCachingProductBuilder<Converter, ConvertHandler, Builder>() {

        override fun buildNew(): Converter {
            return ConverterImpl(handlers())
        }

        private class ConverterImpl(private val handlers: List<ConvertHandler>) : Converter {

            override fun <T> convert(from: Any?, to: Class<T>): T {
                for (handler in handlers) {
                    val result = handler.convert(from, to, this)
                    if (result !== null) {
                        return result.asAny()
                    }
                }
                throw UnsupportedOperationException("Converting $from to $to doesn't supported.")
            }

            override fun <T> convert(from: Any?, to: Type): T {
                for (handler in handlers) {
                    val result = handler.convert(from, to, this)
                    if (result !== null) {
                        return result.asAny()
                    }
                }
                throw UnsupportedOperationException("Converting $from to $to doesn't supported.")
            }

            override fun <T> convert(from: Any?, to: TypeRef<T>): T {
                return convert(from, to.type)
            }
        }
    }

    companion object {

        @JvmStatic
        fun defaultConverter(): Converter? {
            return ConvertSupport.defaultConverter()
        }

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }
    }
}

interface ConvertHandler {

    /**
     * Return [NULL_VALUE] if final value is null.
     */
    fun convert(from: Any?, to: Class<*>, converter: Converter): Any?

    /**
     * Return [NULL_VALUE] if final value is null.
     */
    fun convert(from: Any?, to: Type, converter: Converter): Any?

    companion object {

        fun defaultHandlers(): List<ConvertHandler?>? {
            return ConvertHandlerSupport.defaultHandlers()
        }
    }
}

object NopConvertHandler : ConvertHandler {

    override fun convert(from: Any?, to: Class<*>, converter: Converter): Any? {
        if (from === null) {
            return null
        }
        return if (to.isAssignableFrom(from.javaClass)) {
            from
        } else null
    }

    override fun convert(from: Any?, to: Type, converter: Converter): Any? {
        if (from === null) {
            return null
        }
        return if (to is Class<*>) {
            convert(from, to, converter)
        } else null
    }
}

open class DateTimeConvertHandler(
    protected val dateFormat: DateFormat,
    protected val instantFormatter: DateTimeFormatter,
    protected val localDateTimeFormatter: DateTimeFormatter,
    protected val zonedDateTimeFormatter: DateTimeFormatter,
    protected val offsetDateTimeFormatter: DateTimeFormatter,
    protected val localDateFormatter: DateTimeFormatter,
    protected val localTimeFormatter: DateTimeFormatter,
) : ConvertHandler {

    constructor() : this(
        dateFormat(),
        DateTimeFormatter.ISO_INSTANT,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_ZONED_DATE_TIME,
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ISO_LOCAL_TIME
    )

    override fun convert(from: Any?, to: Class<*>, converter: Converter): Any? {
        if (from === null) {
            return null
        }
        return when (to) {
            Date::class.java -> toDate(from, converter)
            Instant::class.java -> toInstant(from, converter)
            LocalDateTime::class.java -> toLocalDateTime(from, converter)
            ZonedDateTime::class.java -> toZonedDateTime(from, converter)
            OffsetDateTime::class.java -> toOffsetDateTime(from, converter)
            LocalDate::class.java -> toLocalDate(from, converter)
            LocalTime::class.java -> toLocalTime(from, converter)
            Duration::class.java -> toDuration(from, converter)
            else -> null
        }
    }

    override fun convert(from: Any?, to: Type, converter: Converter): Any? {
        if (from === null) {
            return null
        }
        return if (to is Class<*>) {
            NopConvertHandler.convert(from, to, converter)
        } else null
    }

    private fun toDate(from: Any, converter: Converter): Date? {
        return when (from) {
            is Date -> from
            is CharSequence -> dateFormat.parse(toString())
            is Instant -> Date.from(from)
            is LocalDateTime -> Date.from(from.toInstant(OffsetDateTime.now().offset))
            is ZonedDateTime -> Date.from(from.toInstant())
            is OffsetDateTime -> Date.from(from.toInstant())
            is LocalDate -> Date.from(ZonedDateTime.of(from, LocalTime.MIN, ZoneId.systemDefault()).toInstant())
            is LocalTime -> Date.from(ZonedDateTime.of(LocalDate.MIN, from, ZoneId.systemDefault()).toInstant())
            is Temporal -> Date.from(Instant.from(from))
            is Number -> when (val value = toLong()) {
                0L -> MIN_DATE
                else -> Date(value)
            }
            else -> null
        }
    }

    private fun toInstant(from: Any, converter: Converter): Instant? {
        return when (from) {
            is Instant -> from
            is CharSequence -> Instant.from(instantFormatter.parse(toString()))
            is Date -> from.toInstant()
            is LocalDateTime -> from.toInstant(ZonedDateTime.now().offset)
            is ZonedDateTime -> from.toInstant()
            is OffsetDateTime -> from.toInstant()
            is LocalDate -> ZonedDateTime.of(from, LocalTime.MIN, ZoneId.systemDefault()).toInstant()
            is LocalTime -> ZonedDateTime.of(LocalDate.MIN, from, ZoneId.systemDefault()).toInstant()
            is Temporal -> Instant.from(from)
            is Number -> when (val value = toLong()) {
                0L -> Instant.MIN
                else -> Instant.ofEpochMilli(value)
            }
            else -> null
        }
    }

    private fun toLocalDateTime(from: Any, converter: Converter): LocalDateTime? {
        return when (from) {
            is LocalDateTime -> from
            is CharSequence -> return LocalDateTime.from(localDateTimeFormatter.parse(toString()))
            is Date -> from.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            is Instant -> from.atZone(ZoneId.systemDefault()).toLocalDateTime()
            is ZonedDateTime -> from.toLocalDateTime()
            is OffsetDateTime -> from.toLocalDateTime()
            is LocalDate -> LocalDateTime.of(from, LocalTime.MIN)
            is LocalTime -> LocalDateTime.of(LocalDate.MIN, from)
            is Temporal -> LocalDateTime.from(from)
            is Number -> when (val value = toLong()) {
                0L -> LocalDateTime.MIN
                else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDateTime()
            }
            else -> null
        }
    }

    private fun toZonedDateTime(from: Any, converter: Converter): ZonedDateTime? {
        return when (from) {
            is ZonedDateTime -> from
            is CharSequence -> return ZonedDateTime.from(zonedDateTimeFormatter.parse(toString()))
            is Date -> from.toInstant().atZone(ZoneId.systemDefault())
            is Instant -> from.atZone(ZoneId.systemDefault())
            is LocalDateTime -> ZonedDateTime.of(from, ZoneId.systemDefault())
            is OffsetDateTime -> from.toZonedDateTime()
            is LocalDate -> ZonedDateTime.of(from, LocalTime.MIN, ZoneId.systemDefault())
            is LocalTime -> ZonedDateTime.of(LocalDate.MIN, from, ZoneId.systemDefault())
            is Temporal -> ZonedDateTime.from(from)
            is Number -> when (val value = toLong()) {
                0L -> MIN_ZONED_DATE_TIME
                else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault())
            }
            else -> null
        }
    }

    private fun toOffsetDateTime(from: Any, converter: Converter): OffsetDateTime? {
        return when (from) {
            is OffsetDateTime -> from
            is CharSequence -> return OffsetDateTime.from(offsetDateTimeFormatter.parse(toString()))
            is Date -> from.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime()
            is Instant -> from.atZone(ZoneId.systemDefault()).toOffsetDateTime()
            is LocalDateTime -> ZonedDateTime.of(from, ZoneId.systemDefault()).toOffsetDateTime()
            is ZonedDateTime -> from.toOffsetDateTime()
            is LocalDate -> ZonedDateTime.of(from, LocalTime.MIN, ZoneId.systemDefault()).toOffsetDateTime()
            is LocalTime -> ZonedDateTime.of(LocalDate.MIN, from, ZoneId.systemDefault()).toOffsetDateTime()
            is Temporal -> OffsetDateTime.from(from)
            is Number -> when (val value = toLong()) {
                0L -> OffsetDateTime.MIN
                else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toOffsetDateTime()
            }
            else -> null
        }
    }

    private fun toLocalDate(from: Any, converter: Converter): LocalDate? {
        return when (from) {
            is LocalDate -> from
            is CharSequence -> return LocalDate.from(localDateFormatter.parse(toString()))
            is Date -> from.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            is Instant -> from.atZone(ZoneId.systemDefault()).toLocalDate()
            is LocalDateTime -> ZonedDateTime.of(from, ZoneId.systemDefault()).toLocalDate()
            is ZonedDateTime -> from.toLocalDate()
            is OffsetDateTime -> from.toLocalDate()
            is LocalTime -> LocalDate.MIN
            is Temporal -> LocalDate.from(from)
            is Number -> when (val value = toLong()) {
                0L -> LocalDate.MIN
                else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDate()
            }
            else -> null
        }
    }

    private fun toLocalTime(from: Any, converter: Converter): LocalTime? {
        return when (from) {
            is LocalTime -> from
            is CharSequence -> return LocalTime.from(localTimeFormatter.parse(toString()))
            is Date -> from.toInstant().atZone(ZoneId.systemDefault()).toLocalTime()
            is Instant -> from.atZone(ZoneId.systemDefault()).toLocalTime()
            is LocalDateTime -> ZonedDateTime.of(from, ZoneId.systemDefault()).toLocalTime()
            is ZonedDateTime -> from.toLocalTime()
            is OffsetDateTime -> from.toLocalTime()
            is LocalDate -> LocalTime.MIN
            is Temporal -> LocalTime.from(from)
            is Number -> when (val value = toLong()) {
                0L -> LocalTime.MIN
                else -> Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalTime()
            }
            else -> null
        }
    }

    private fun toDuration(from: Any, converter: Converter): Duration? {
        return when (from) {
            is Duration -> from
            is CharSequence -> Duration.parse(toString())
            is Number -> when (val value = toLong()) {
                0L -> Duration.ZERO
                else -> Duration.ofMillis(value)
            }
            else -> null
        }
    }

    companion object {

        @JvmField
        val INSTANCE = DateTimeConvertHandler()
    }
}

open class PrivateAndNumberConvertHandler:ConvertHandler{

    override fun convert(from: Any?, to: Class<*>, converter: Converter): Any? {
        if (from === null) {
            return null
        }
        return when (to) {
            Date::class.java -> toDate(from, converter)
            Instant::class.java -> toInstant(from, converter)
            LocalDateTime::class.java -> toLocalDateTime(from, converter)
            ZonedDateTime::class.java -> toZonedDateTime(from, converter)
            OffsetDateTime::class.java -> toOffsetDateTime(from, converter)
            LocalDate::class.java -> toLocalDate(from, converter)
            LocalTime::class.java -> toLocalTime(from, converter)
            Duration::class.java -> toDuration(from, converter)
            else -> null
        }
    }

    override fun convert(from: Any?, to: Type, converter: Converter): Any? {
        if (from === null) {
            return null
        }
        return if (to is Class<*>) {
            NopConvertHandler.convert(from, to, converter)
        } else null
    }

    private fun to
}