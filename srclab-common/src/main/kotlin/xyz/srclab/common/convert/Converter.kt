package xyz.srclab.common.convert

import xyz.srclab.common.base.*
import xyz.srclab.common.bean.beanToMap
import xyz.srclab.common.bean.propertiesToBean
import xyz.srclab.common.bean.propertiesToMap
import xyz.srclab.common.bean.resolveBean
import xyz.srclab.common.collection.BaseIterableOps.Companion.asToList
import xyz.srclab.common.collection.IterableSchema
import xyz.srclab.common.collection.ListOps.Companion.asMutableList
import xyz.srclab.common.collection.ListOps.Companion.asMutableListOrNull
import xyz.srclab.common.collection.isArray
import xyz.srclab.common.collection.resolveIterableSchemaOrNull
import xyz.srclab.common.reflect.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.TypeVariable
import java.lang.reflect.WildcardType
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap

interface Converter {

    @Throws(UnsupportedOperationException::class)
    fun <T> convert(from: Any?, toType: Class<T>): T

    @Throws(UnsupportedOperationException::class)
    fun <T> convert(from: Any?, toType: Type): T

    @Throws(UnsupportedOperationException::class)
    fun <T> convert(from: Any?, toTypeRef: TypeRef<T>): T {
        return convert(from, toTypeRef.type)
    }

    @Throws(UnsupportedOperationException::class)
    fun <T> convert(from: Any?, fromType: Type, toType: Type): T

    @Throws(UnsupportedOperationException::class)
    fun <T> convert(from: Any?, fromTypeRef: TypeRef<T>, toTypeRef: TypeRef<T>): T {
        return convert(from, fromTypeRef.type, toTypeRef.type)
    }

    class Builder : HandlersCachingProductBuilder<Converter, ConvertHandler, Builder>() {

        override fun buildNew(): Converter {
            return ConverterImpl(handlers())
        }

        private class ConverterImpl(private val handlers: List<ConvertHandler>) : Converter {

            override fun <T> convert(from: Any?, toType: Class<T>): T {
                for (handler in handlers) {
                    val result = handler.convert(from, toType, this)
                    if (result === NULL_VALUE) {
                        return null as T
                    }
                    if (result !== null) {
                        return result.asAny()
                    }
                }
                throw UnsupportedOperationException("Cannot convert $from to $toType.")
            }

            override fun <T> convert(from: Any?, toType: Type): T {
                for (handler in handlers) {
                    val result = handler.convert(from, toType, this)
                    if (result === NULL_VALUE) {
                        return null as T
                    }
                    if (result !== null) {
                        return result.asAny()
                    }
                }
                throw UnsupportedOperationException("Cannot convert $from to $toType.")
            }

            override fun <T> convert(from: Any?, fromType: Type, toType: Type): T {
                for (handler in handlers) {
                    val result = handler.convert(from, fromType, toType, this)
                    if (result === NULL_VALUE) {
                        return null as T
                    }
                    if (result !== null) {
                        return result.asAny()
                    }
                }
                throw UnsupportedOperationException("Cannot convert $fromType to $toType.")
            }
        }
    }

    companion object {

        @JvmField
        val DEFAULT: Converter = newBuilder().addHandlers(ConvertHandler.DEFAULTS).build()

        @JvmStatic
        fun newBuilder(): Builder {
            return Builder()
        }

        @JvmStatic
        @Throws(UnsupportedOperationException::class)
        fun <T> convertTo(from: Any?, toType: Class<T>): T {
            return DEFAULT.convert(from, toType)
        }

        @JvmStatic
        @Throws(UnsupportedOperationException::class)
        fun <T> convertTo(from: Any?, toType: Type): T {
            return DEFAULT.convert(from, toType)
        }

        @JvmStatic
        @Throws(UnsupportedOperationException::class)
        fun <T> convertTo(from: Any?, toTypeRef: TypeRef<T>): T {
            return DEFAULT.convert(from, toTypeRef)
        }

        @JvmStatic
        @Throws(UnsupportedOperationException::class)
        fun <T> convertTo(from: Any?, fromType: Type, toType: Type): T {
            return DEFAULT.convert(from, fromType, toType)
        }

        @JvmStatic
        @Throws(UnsupportedOperationException::class)
        fun <T> convertTo(from: Any?, fromTypeRef: TypeRef<T>, toTypeRef: TypeRef<T>): T {
            return DEFAULT.convert(from, fromTypeRef, toTypeRef)
        }
    }
}

@Throws(UnsupportedOperationException::class)
fun <T> Any?.convertTo(toType: Class<T>): T {
    return Converter.convertTo(this, toType)
}

@Throws(UnsupportedOperationException::class)
fun <T> Any?.convertTo(toType: Type): T {
    return Converter.convertTo(this, toType)
}

@Throws(UnsupportedOperationException::class)
fun <T> Any?.convertTo(toTypeRef: TypeRef<T>): T {
    return Converter.convertTo(this, toTypeRef)
}

@Throws(UnsupportedOperationException::class)
fun <T> Any?.convertTo(fromType: Type, toType: Type): T {
    return Converter.convertTo(this, fromType, toType)
}

@Throws(UnsupportedOperationException::class)
fun <T> Any?.convertTo(fromTypeRef: TypeRef<T>, toTypeRef: TypeRef<T>): T {
    return Converter.convertTo(this, fromTypeRef, toTypeRef)
}

interface ConvertHandler {

    /**
     * Return null if [from] cannot be converted, return [NULL_VALUE] if result value is null.
     */
    fun convert(from: Any?, toType: Class<*>, converter: Converter): Any?

    /**
     * Return null if [from] cannot be converted, return [NULL_VALUE] if result value is null.
     */
    fun convert(from: Any?, toType: Type, converter: Converter): Any?

    /**
     * Return null if [from] cannot be converted, return [NULL_VALUE] if result value is null.
     */
    fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any?

    companion object {

        @JvmField
        val DEFAULTS: List<ConvertHandler> = ConvertHandlerSupport.defaultHandlers()
    }
}

object NopConvertHandler : ConvertHandler {

    override fun convert(from: Any?, toType: Class<*>, converter: Converter): Any? {
        return when {
            from === null -> null
            toType.isAssignableFrom(from.javaClass) -> from
            else -> null
        }
    }

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        return if (toType is Class<*>) {
            convert(from, toType, converter)
        } else null
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        return if (fromType == toType) {
            from
        } else null
    }
}

object CharsConvertHandler : ConvertHandler {

    override fun convert(from: Any?, toType: Class<*>, converter: Converter): Any? {
        return when (toType) {
            String::class.java, CharSequence::class.java -> from.toString()
            StringBuilder::class.java -> StringBuilder(from.toString())
            StringBuffer::class.java -> StringBuffer(from.toString())
            else -> null
        }
    }

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        return if (toType is Class<*>) {
            convert(from, toType, converter)
        } else null
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        return if (toType is Class<*>) {
            convert(from, toType, converter)
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

    override fun convert(from: Any?, toType: Class<*>, converter: Converter): Any? {
        return when (toType) {
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

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        return if (toType is Class<*>) {
            convert(from, toType, converter)
        } else null
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        return if (toType is Class<*>) {
            convert(from, toType, converter)
        } else null
    }

    companion object {

        @JvmField
        val INSTANCE = DateTimeConvertHandler()
    }
}

object NumberAndPrimitiveConvertHandler : ConvertHandler {

    override fun convert(from: Any?, toType: Class<*>, converter: Converter): Any? {
        return when (toType) {
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

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        return if (toType is Class<*>) {
            convert(from, toType, converter)
        } else null
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        return if (toType is Class<*>) {
            convert(from, toType, converter)
        } else null
    }
}

object BeanConvertHandler : ConvertHandler {

    override fun convert(from: Any?, toType: Class<*>, converter: Converter): Any? {
        if (from === null) {
            return null
        }
        return convert0(from, from.javaClass, toType, toType, converter)
    }

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        if (from === null) {
            return null
        }
        val fromType = from.javaClass
        return when (toType) {
            is Class<*> -> return convert0(from, from.javaClass, toType, toType, converter)
            is ParameterizedType -> return convert0(from, fromType, toType.rawClass, toType, converter)
            is TypeVariable<*>, is WildcardType -> return convert(from, fromType, toType.upperBound, converter)
            else -> null
        }
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        if (from === null) {
            return null
        }
        return when (toType) {
            is Class<*> -> return convert0(from, fromType, toType, toType, converter)
            is ParameterizedType -> return convert0(from, fromType, toType.rawClass, toType, converter)
            is TypeVariable<*>, is WildcardType -> return convert(from, fromType, toType.upperBound, converter)
            else -> null
        }
    }

    private fun convert0(from: Any, fromType: Type, toRawClass: Class<*>, toType: Type, converter: Converter): Any? {
        return when (toRawClass) {
            Map::class.java -> from.propertiesToMap(HashMap<Any, Any?>(), fromType, toType, converter).toMap()
            MutableMap::class.java, HashMap::class.java -> return from.propertiesToMap(
                HashMap<Any, Any?>(),
                fromType,
                toType,
                converter
            )
            LinkedHashMap::class.java -> return from.propertiesToMap(
                LinkedHashMap<Any, Any?>(),
                fromType,
                toType,
                converter
            )
            TreeMap::class.java -> return from.propertiesToMap(TreeMap<Any, Any?>(), fromType, toType, converter)
            ConcurrentHashMap::class.java -> return from.propertiesToMap(
                ConcurrentHashMap<Any, Any?>(),
                fromType,
                toType,
                converter
            )
            else -> {
                val toInstance = toRawClass.toInstance<Any>()
                return from.propertiesToBean(toInstance, fromType, toType, converter)
            }
        }
    }
}

object ListConvertHandler : ConvertHandler {

    override fun convert(from: Any?, toType: Class<*>, converter: Converter): Any? {
        if (from === null) {
            return null
        }
    }

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        TODO("Not yet implemented")
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        TODO("Not yet implemented")
    }

    private fun convert0(from: Any, fromType: Type, toType: Type, converter: Converter): Any? {
        val list:Iterable<Any?>? = when (fromType) {
            is Class<*> -> {
                if (fromType.isArray) {
                    return from.asMutableListOrNull<Any?>()
                }
                if (Iterable::class.java.isAssignableFrom(fromType)) {
                    return from as Iterable<Any?>
                }
                return null
            }
            else -> {
                val listSchema = fromType.resolveIterableSchemaOrNull()
                if (listSchema === null) {
                    return null
                }
                return from as Iterable<Any?>
            }
        }
    }
}