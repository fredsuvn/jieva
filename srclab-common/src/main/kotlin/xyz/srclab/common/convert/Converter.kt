package xyz.srclab.common.convert

import xyz.srclab.common.base.HandlersCachingProductBuilder
import xyz.srclab.common.base.asAny
import xyz.srclab.common.reflect.TypeRef
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
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
     * Return [Parts.NULL_VALUE] if final value is null.
     */
    fun convert(from: Any?, to: Class<*>, converter: Converter): Any?

    /**
     * Return [Parts.NULL_VALUE] if final value is null.
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

open class DateTimeConvertHandler : ConvertHandler {

    private val dateTimeFormatter: DateTimeFormatter

    constructor(dateTimePattern: String) {
        dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern)
    }

    @JvmOverloads
    constructor(dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME) {
        this.dateTimeFormatter = dateTimeFormatter
    }

    override fun convert(from: Any?, to: Class<*>, converter: Converter): Any? {
        if (from === null) {
            return null
        }
        return when (from) {
            is Date -> dateToDateTime(from, to, converter)
            is String -> stringToDateTime(from, to, converter)
            is Number -> numberToDateTime(from, to, converter)
            is Temporal -> temporalToDateTime(from, to, converter)
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

    private fun dateToDateTime(from: Date, to: Class<*>, converter: Converter): Any? {
        return when(to){
            Date::class.java->from
            Instant::class.java->from
            LocalDateTime::class.java->from
            ZonedDateTime::class.java->from
            OffsetDateTime::class.java->from
            LocalDate::class.java->from
            LocalTime::class.java->from
            else->null
        }
    }

    private fun stringToDateTime(from: String, to: Class<*>, converter: Converter): Any? {
    }

    private fun numberToDateTime(from: Number, to: Class<*>, converter: Converter): Any? {
    }

    private fun temporalToDateTime(from: Temporal, to: Class<*>, converter: Converter): Any? {
    }

    private fun dateToDateTime(from: Temporal, to: Class<*>, converter: Converter): Any? {
    }

    private fun stringToDuration(from: String): Any? {
    }

    private fun numberToDuration(from: Number): Any? {
    }

    companion object {

        private val INSTANCE = DateTimeConvertHandler()
        fun instance(): DateTimeConvertHandler {
            return INSTANCE
        }
    }
}