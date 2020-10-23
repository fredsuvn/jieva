package xyz.srclab.common.convert

import xyz.srclab.common.base.*
import xyz.srclab.common.bean.propertiesToBean
import xyz.srclab.common.bean.propertiesToMap
import xyz.srclab.common.reflect.TypeRef
import xyz.srclab.common.reflect.toInstance
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

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
    fun toLocalDate(any: Any?): LocalDate {
        return convert(any, LocalDate::class.java)
    }

    @Throws(UnsupportedOperationException::class)
    fun toLocalTime(any: Any?): LocalTime {
        return convert(any, LocalTime::class.java)
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
        fun defaultConverter(): Converter {
            return ConvertSupport.defaultConverter()
        }

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }

        @JvmStatic
        @Throws(UnsupportedOperationException::class)
        fun <T> convertTo(from: Any?, to: Class<T>): T {
            return defaultConverter().convert(from, to)
        }

        @JvmStatic
        @Throws(UnsupportedOperationException::class)
        fun <T> convertTo(from: Any?, to: Type): T {
            return defaultConverter().convert(from, to)
        }

        @JvmStatic
        @Throws(UnsupportedOperationException::class)
        fun <T> convertTo(from: Any?, to: TypeRef<T>): T {
            return defaultConverter().convert(from, to)
        }
    }
}

@Throws(UnsupportedOperationException::class)
fun <T> Any?.convertTo(to: Class<T>): T {
    return Converter.convertTo(this, to)
}

@Throws(UnsupportedOperationException::class)
fun <T> Any?.convertTo(to: Type): T {
    return Converter.convertTo(this, to)
}

@Throws(UnsupportedOperationException::class)
fun <T> Any?.convertTo(to: TypeRef<T>): T {
    return Converter.convertTo(this, to)
}

interface ConvertHandler {

    /**
     * Return null if [from] cannot be converted, return [NULL_VALUE] if result value is null.
     */
    fun convert(from: Any?, to: Class<*>, converter: Converter): Any?

    /**
     * Return null if [from] cannot be converted, return [NULL_VALUE] if result value is null.
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
        return when {
            from === null -> null
            to.isAssignableFrom(from.javaClass) -> from
            else -> null
        }
    }

    override fun convert(from: Any?, to: Type, converter: Converter): Any? {
        return if (to is Class<*>) {
            convert(from, to, converter)
        } else null
    }
}

object CharsConvertHandler : ConvertHandler {

    override fun convert(from: Any?, to: Class<*>, converter: Converter): Any? {
        return when (to) {
            String::class.java, CharSequence::class.java -> from.toString()
            StringBuilder::class.java -> StringBuilder(from.toString())
            StringBuffer::class.java -> StringBuffer(from.toString())
            else -> null
        }
    }

    override fun convert(from: Any?, to: Type, converter: Converter): Any? {
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
        return when (to) {
            Date::class.java -> from.toDate(dateFormat)
            Instant::class.java -> from.toInstant(instantFormatter)
            LocalDateTime::class.java -> from.toLocalDateTime(localDateTimeFormatter)
            ZonedDateTime::class.java -> from.toZonedDateTime(zonedDateTimeFormatter)
            OffsetDateTime::class.java -> from.toOffsetDateTime(offsetDateTimeFormatter)
            LocalDate::class.java -> from.toLocalDate(localDateFormatter)
            LocalTime::class.java -> from.toLocalTime(localTimeFormatter)
            Duration::class.java -> from.toDuration()
            else -> null
        }
    }

    override fun convert(from: Any?, to: Type, converter: Converter): Any? {
        return if (to is Class<*>) {
            convert(from, to, converter)
        } else null
    }

    companion object {

        @JvmField
        val INSTANCE = DateTimeConvertHandler()
    }
}

object NumberAndPrimitiveConvertHandler : ConvertHandler {

    override fun convert(from: Any?, to: Class<*>, converter: Converter): Any? {
        return when (to) {
            Boolean::class.javaPrimitiveType, Boolean::class.java -> from.toBoolean()
            Byte::class.javaPrimitiveType, Byte::class.java -> from.toByte()
            Short::class.javaPrimitiveType, Short::class.java -> from.toShort()
            Char::class.javaPrimitiveType, Char::class.java -> from.toChar()
            Int::class.javaPrimitiveType, Int::class.java -> from.toInt()
            Long::class.javaPrimitiveType, Long::class.java -> from.toLong()
            Float::class.javaPrimitiveType, Float::class.java -> from.toFloat()
            Double::class.javaPrimitiveType, Double::class.java -> from.toDouble()
            BigInteger::class.java -> from.toBigInteger()
            BigDecimal::class.java -> from.toBigDecimal()
            else -> null
        }
    }

    override fun convert(from: Any?, to: Type, converter: Converter): Any? {
        return if (to is Class<*>) {
            convert(from, to, converter)
        } else null
    }
}

object BeanConvertHandler : ConvertHandler {

    override fun convert(from: Any?, to: Class<*>, converter: Converter): Any? {
        if (from === null) {
            return null
        }
        return when (to) {
            is HashMap<*, *> -> return from.propertiesToMap(HashMap())
            is LinkedHashMap<*, *> -> return from.propertiesToMap(LinkedHashMap())
            is TreeMap<*, *> -> return from.propertiesToMap(TreeMap())
            is ConcurrentHashMap<*, *> -> return from.propertiesToMap(ConcurrentHashMap())
            else -> from.propertiesToBean(to.toInstance<Any>())
        }
    }

    override fun convert(from: Any?, to: Type, converter: Converter): Any? {
        return when (to) {
            is Class<*> -> return convert(from, to, converter)
            is ParameterizedType ->
            else -> null
        }
    }
}