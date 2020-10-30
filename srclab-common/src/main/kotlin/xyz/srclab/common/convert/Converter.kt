package xyz.srclab.common.convert

import xyz.srclab.common.base.*
import xyz.srclab.common.bean.BeanResolver
import xyz.srclab.common.bean.copyProperties
import xyz.srclab.common.collection.BaseIterableOps.Companion.toAnyArray
import xyz.srclab.common.collection.arrayAsList
import xyz.srclab.common.collection.componentType
import xyz.srclab.common.collection.resolveIterableSchemaOrNull
import xyz.srclab.common.reflect.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.time.temporal.TemporalAdjuster
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.collections.LinkedHashMap
import kotlin.collections.LinkedHashSet

interface Converter {

    fun <T> convert(from: Any?, toType: Class<T>): T

    fun <T> convert(from: Any?, toType: Type): T

    fun <T> convert(from: Any?, toTypeRef: TypeRef<T>): T {
        return convert(from, toTypeRef.type)
    }

    fun <T> convert(from: Any?, fromType: Type, toType: Type): T

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
    }
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
        val DEFAULTS: List<ConvertHandler> = listOf(
            NopConvertHandler,
            CharsConvertHandler,
            NumberAndPrimitiveConvertHandler,
            DateTimeConvertHandler.DEFAULT,
            UpperBoundConvertHandler,
            IterableConvertHandler,
            BeanConvertHandler.DEFAULT,
        )
    }
}

abstract class AbstractConvertHandler : ConvertHandler {

    override fun convert(from: Any?, toType: Class<*>, converter: Converter): Any? {
        if (from === null) {
            return null
        }
        return doConvert(from, from.javaClass, toType, converter)
    }

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        if (from === null) {
            return null
        }
        return doConvert(from, from.javaClass, toType, converter)
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        if (from === null) {
            return null
        }
        return doConvert(from, fromType, toType, converter)
    }

    protected abstract fun doConvert(from: Any, fromType: Type, toType: Type, converter: Converter): Any?
}

object NopConvertHandler : AbstractConvertHandler() {

    override fun doConvert(from: Any, fromType: Type, toType: Type, converter: Converter): Any? {
        return when {
            fromType == toType -> from
            fromType is Class<*> && toType is Class<*> && toType.isAssignableFrom(fromType) -> from
            else -> null
        }
    }
}

object CharsConvertHandler : AbstractConvertHandler() {

    override fun doConvert(from: Any, fromType: Type, toType: Type, converter: Converter): Any? {
        return when (toType) {
            String::class.java, CharSequence::class.java -> from.toString()
            StringBuilder::class.java -> StringBuilder(from.toString())
            StringBuffer::class.java -> StringBuffer(from.toString())
            CharArray::class.java -> from.toString().toCharArray()
            else -> null
        }
    }
}

object NumberAndPrimitiveConvertHandler : AbstractConvertHandler() {

    override fun doConvert(from: Any, fromType: Type, toType: Type, converter: Converter): Any? {
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
            Number::class.java -> from.toDouble()
            else -> null
        }
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
) : AbstractConvertHandler() {

    constructor() : this(
        dateFormat(),
        DateTimeFormatter.ISO_INSTANT,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_ZONED_DATE_TIME,
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ISO_LOCAL_TIME
    )

    override fun doConvert(from: Any, fromType: Type, toType: Type, converter: Converter): Any? {
        return when (toType) {
            Date::class.java -> from.toDate(dateFormat)
            Instant::class.java -> from.toInstant(instantFormatter)
            LocalDateTime::class.java -> from.toLocalDateTime(localDateTimeFormatter)
            ZonedDateTime::class.java -> from.toZonedDateTime(zonedDateTimeFormatter)
            OffsetDateTime::class.java -> from.toOffsetDateTime(offsetDateTimeFormatter)
            LocalDate::class.java -> from.toLocalDate(localDateFormatter)
            LocalTime::class.java -> from.toLocalTime(localTimeFormatter)
            Duration::class.java -> from.toDuration()
            Temporal::class.java, TemporalAdjuster::class.java -> from.toLocalDateTime(localDateTimeFormatter)
            else -> null
        }
    }

    companion object {

        @JvmField
        val DEFAULT: DateTimeConvertHandler = DateTimeConvertHandler()
    }
}

object UpperBoundConvertHandler : AbstractConvertHandler() {

    override fun doConvert(from: Any, fromType: Type, toType: Type, converter: Converter): Any? {
        return converter.convert(from, fromType.upperBound, toType.upperBound)
    }
}

object IterableConvertHandler : AbstractConvertHandler() {

    override fun doConvert(from: Any, fromType: Type, toType: Type, converter: Converter): Any? {
        if (from is Iterable<*>) {
            return iterableToType(from as Iterable<Any?>, toType, converter)
        }
        val fromClass = from.javaClass
        if (fromClass.isArray) {
            return iterableToType(from.arrayAsList(), toType, converter)
        }
        return null
    }

    private fun iterableToType(iterable: Iterable<Any?>, toType: Type, converter: Converter): Any? {
        val toComponentType = toType.componentType
        if (toComponentType !== null) {
            val upperComponentType = toComponentType.upperBound
            return iterable
                .map { converter.convert<Any?>(it, upperComponentType) }
                .toAnyArray(upperComponentType.upperClass)
        }
        val iterableSchema = toType.resolveIterableSchemaOrNull()
        if (iterableSchema === null) {
            return null
        }
        return if (iterable is Collection<*>)
            collectionMapTo(iterable, iterableSchema.rawClass, iterableSchema.componentType.upperBound, converter)
        else
            iterableMapTo(iterable, iterableSchema.rawClass, iterableSchema.componentType.upperBound, converter)
    }

    private fun iterableMapTo(
        iterable: Iterable<Any?>,
        iterableClass: Class<*>,
        componentType: Type,
        converter: Converter
    ): Iterable<Any?>? {
        return when (iterableClass) {
            List::class.java -> iterable.mapTo(LinkedList<Any?>()) {
                converter.convert(it, componentType)
            }.toList()
            LinkedList::class.java -> iterable.mapTo(LinkedList()) {
                converter.convert(it, componentType)
            }
            ArrayList::class.java -> iterable.mapTo(ArrayList()) {
                converter.convert(it, componentType)
            }
            Collection::class.java, Set::class.java -> iterable.mapTo(LinkedHashSet<Any?>()) {
                converter.convert(it, componentType)
            }.toSet()
            LinkedHashSet::class.java -> iterable.mapTo(LinkedHashSet()) {
                converter.convert(it, componentType)
            }
            HashSet::class.java -> iterable.mapTo(HashSet()) {
                converter.convert(it, componentType)
            }
            TreeSet::class.java -> iterable.mapTo(TreeSet()) {
                converter.convert(it, componentType)
            }
            else -> null
        }
    }

    private fun collectionMapTo(
        collection: Collection<Any?>,
        iterableClass: Class<*>,
        componentType: Type,
        converter: Converter
    ): Iterable<Any?>? {
        return when (iterableClass) {
            List::class.java -> collection.mapTo(ArrayList<Any?>(collection.size)) {
                converter.convert(it, componentType)
            }.toList()
            LinkedList::class.java -> collection.mapTo(LinkedList()) {
                converter.convert(it, componentType)
            }
            ArrayList::class.java -> collection.mapTo(ArrayList(collection.size)) {
                converter.convert(it, componentType)
            }
            Collection::class.java, Set::class.java -> collection.mapTo(LinkedHashSet<Any?>(collection.size)) {
                converter.convert(it, componentType)
            }.toSet()
            LinkedHashSet::class.java -> collection.mapTo(LinkedHashSet(collection.size)) {
                converter.convert(it, componentType)
            }
            HashSet::class.java -> collection.mapTo(HashSet(collection.size)) {
                converter.convert(it, componentType)
            }
            TreeSet::class.java -> collection.mapTo(TreeSet()) {
                converter.convert(it, componentType)
            }
            else -> null
        }
    }
}

open class BeanConvertHandler(protected val beanResolver: BeanResolver) : AbstractConvertHandler() {

    override fun doConvert(from: Any, fromType: Type, toType: Type, converter: Converter): Any? {
        return when (toType) {
            is Class<*> -> return doConvert0(from, toType, toType.upperBound, converter)
            is ParameterizedType -> return doConvert0(from, toType.rawClass, toType.upperBound, converter)
            else -> null
        }
    }

    private fun doConvert0(from: Any, toRawClass: Class<*>, toType: Type, converter: Converter): Any? {
        return when (toRawClass) {
            Map::class.java -> from.copyProperties(
                HashMap<Any, Any?>(),
                from.javaClass,
                toType,
                converter
            ).toMap()
            MutableMap::class.java, LinkedHashMap::class.java -> from.copyProperties(
                LinkedHashMap(),
                from.javaClass,
                toType,
                converter
            )
            HashMap::class.java -> from.copyProperties(
                HashMap(),
                from.javaClass,
                toType,
                converter
            )
            TreeMap::class.java -> from.copyProperties(
                TreeMap(),
                from.javaClass,
                toType,
                converter
            )
            else -> {
                val toInstance = toRawClass.toInstance<Any>()
                return from.copyProperties(toInstance, from.javaClass, toType, converter)
            }
        }
    }

    companion object {

        @JvmField
        val DEFAULT: BeanConvertHandler = BeanConvertHandler(BeanResolver.DEFAULT)
    }
}