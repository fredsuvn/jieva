package xyz.srclab.common.convert

import xyz.srclab.common.base.*
import xyz.srclab.common.bean.BeanResolver
import xyz.srclab.common.collect.BaseIterableOps.Companion.toAnyArray
import xyz.srclab.common.collection.arrayAsList
import xyz.srclab.common.collection.arrayTypeToArray
import xyz.srclab.common.collection.componentType
import xyz.srclab.common.collect.resolveIterableSchemaOrNull
import xyz.srclab.common.reflect.*
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.lang.reflect.WildcardType
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

    @Suppress(INAPPLICABLE_JVM_NAME)
    val convertHandlers: List<ConvertHandler>
        @JvmName("convertHandlers") get

    @JvmDefault
    fun <T> convert(from: Any?, toType: Class<T>): T {
        for (handler in convertHandlers) {
            val result = handler.convert(from, toType, this)
            if (result === Default.NULL) {
                return null.asAny()
            }
            if (result !== null) {
                return result.asAny()
            }
        }
        throw UnsupportedOperationException("Cannot convert $from to $toType.")
    }

    @JvmDefault
    fun <T> convert(from: Any?, toType: Type): T {
        for (handler in convertHandlers) {
            val result = handler.convert(from, toType, this)
            if (result === Default.NULL) {
                return null.asAny()
            }
            if (result !== null) {
                return result.asAny()
            }
        }
        throw UnsupportedOperationException("Cannot convert $from to $toType.")
    }

    @JvmDefault
    fun <T> convert(from: Any?, toTypeRef: TypeRef<T>): T {
        return convert(from, toTypeRef.type)
    }

    @JvmDefault
    fun <T> convert(from: Any?, fromType: Type, toType: Type): T {
        for (handler in convertHandlers) {
            val result = handler.convert(from, fromType, toType, this)
            if (result === Default.NULL) {
                return null.asAny()
            }
            if (result !== null) {
                return result.asAny()
            }
        }
        throw UnsupportedOperationException("Cannot convert $fromType to $toType.")
    }

    @JvmDefault
    fun <T> convert(from: Any?, fromTypeRef: TypeRef<T>, toTypeRef: TypeRef<T>): T {
        return convert(from, fromTypeRef.type, toTypeRef.type)
    }

    @JvmDefault
    fun withPreConvertHandler(preConvertHandler: ConvertHandler): Converter {
        return newConverter(listOf(preConvertHandler).plus(convertHandlers))
    }

    companion object {

        @JvmField
        val DEFAULT: Converter = newConverter(ConvertHandler.DEFAULTS)

        @JvmField
        val EMPTY: Converter = newConverter(emptyList())

        @JvmField
        val NOP: Converter = newConverter(listOf(NopConvertHandler))

        @JvmStatic
        fun newConverter(convertHandlers: Iterable<ConvertHandler>): Converter {
            return object : Converter {
                override val convertHandlers = convertHandlers.toList()
            }
        }
    }
}

interface ConvertHandler {

    /**
     * Return null if [from] cannot be converted, return [Default.NULL] if result value is null.
     */
    fun convert(from: Any?, toType: Class<*>, converter: Converter): Any?

    /**
     * Return null if [from] cannot be converted, return [Default.NULL] if result value is null.
     */
    fun convert(from: Any?, toType: Type, converter: Converter): Any?

    /**
     * Return null if [from] cannot be converted, return [Default.NULL] if result value is null.
     */
    fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any?

    companion object {

        @JvmField
        val DEFAULTS: List<ConvertHandler> = listOf(
            NopConvertHandler,
            WildcardTypeConvertHandler,
            CharsConvertHandler,
            NumberAndPrimitiveConvertHandler,
            DateTimeConvertHandler.DEFAULT,
            IterableConvertHandler,
            BeanConvertHandler.DEFAULT,
        )
    }
}

object NopConvertHandler : ConvertHandler {

    override fun convert(from: Any?, toType: Class<*>, converter: Converter): Any? {
        if (toType == Any::class.java) {
            return from
        }
        if (from === null) {
            return Default.NULL
        }
        val fromClass = from.javaClass
        if (fromClass == toType || toType.isAssignableFrom(fromClass)) {
            return from
        }
        return null
    }

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        return if (toType is Class<*>) convert(from, toType, converter) else null
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        if (fromType == toType) {
            return from
        }
        if (from !== null && from.javaClass == fromType && toType is Class<*>) {
            return convert(from, toType, converter)
        }
        return null
    }
}

object WildcardTypeConvertHandler : ConvertHandler {

    override fun convert(from: Any?, toType: Class<*>, converter: Converter): Any? {
        return null
    }

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        if (toType is WildcardType) {
            return converter.convert(from, toType.upperBound)
        }
        return null
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        if (toType is WildcardType) {
            return converter.convert(from, toType.upperBound)
        }
        return null
    }
}

abstract class AbstractClassConvertHandler : ConvertHandler {

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        return if (toType is Class<*>) convert(from, toType, converter) else null
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        return if (toType is Class<*>) convert(from, toType, converter) else null
    }
}

object CharsConvertHandler : AbstractClassConvertHandler() {

    override fun convert(from: Any?, toType: Class<*>, converter: Converter): Any? {
        return when (toType) {
            String::class.java, CharSequence::class.java -> {
                if (from is ByteArray) from.toChars() else from.toString()
            }
            StringBuilder::class.java -> {
                if (from is ByteArray)
                    StringBuilder(from.toChars())
                else
                    StringBuilder(from.toString())
            }
            StringBuffer::class.java -> {
                if (from is ByteArray)
                    StringBuffer(from.toChars())
                else
                    StringBuffer(from.toString())
            }
            CharArray::class.java -> {
                if (from is ByteArray)
                    from.toChars().toCharArray()
                else
                    from.toString().toCharArray()
            }
            ByteArray::class.java -> {
                if (from is CharSequence)
                    from.toBytes()
                else
                    null
            }
            else -> null
        }
    }
}

object NumberAndPrimitiveConvertHandler : AbstractClassConvertHandler() {

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
            Number::class.java -> from.toNumber()
            else -> null
        }
    }
}

class DateTimeConvertHandler(
    private val dateFormat: DateFormat,
    private val instantFormatter: DateTimeFormatter,
    private val localDateTimeFormatter: DateTimeFormatter,
    private val zonedDateTimeFormatter: DateTimeFormatter,
    private val offsetDateTimeFormatter: DateTimeFormatter,
    private val localDateFormatter: DateTimeFormatter,
    private val localTimeFormatter: DateTimeFormatter,
) : AbstractClassConvertHandler() {

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
            Temporal::class.java, TemporalAdjuster::class.java -> from.toLocalDateTime(localDateTimeFormatter)
            else -> null
        }
    }

    companion object {

        @JvmField
        val DEFAULT: DateTimeConvertHandler = DateTimeConvertHandler()
    }
}

abstract class AbstractTypeConvertHandler : ConvertHandler {

    override fun convert(from: Any?, toType: Class<*>, converter: Converter): Any? {
        return convert(from, toType as Type, converter)
    }

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        if (from === null) {
            return convertNull(toType, converter)
        }
        return convertNotNull(from, from.javaClass, toType, converter)
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        if (from === null) {
            return convertNull(toType, converter)
        }
        return convertNotNull(from, fromType, toType, converter)
    }

    abstract fun convertNull(toType: Type, converter: Converter): Any?

    abstract fun convertNotNull(from: Any, fromType: Type, toType: Type, converter: Converter): Any?
}

object IterableConvertHandler : AbstractTypeConvertHandler() {

    override fun convertNull(toType: Type, converter: Converter): Any? {
        return Default.NULL
    }

    override fun convertNotNull(from: Any, fromType: Type, toType: Type, converter: Converter): Any? {
        return when (toType) {
            is Class<*> -> {
                val fromIterable = fromIterable(from)
                if (fromIterable === null) {
                    return null
                }
                toIterable(fromIterable, toType, converter)
            }
            is ParameterizedType -> {
                val fromIterable = fromIterable(from)
                if (fromIterable === null) {
                    return null
                }
                toIterable(fromIterable, toType, converter)
            }
            is GenericArrayType -> {
                val fromIterable = fromIterable(from)
                if (fromIterable === null) {
                    return null
                }
                toIterable(fromIterable, toType, converter)
            }
            else -> null
        }
    }

    private fun fromIterable(from: Any): Iterable<Any?>? {
        if (from is Iterable<*>) {
            return from
        }
        if (from.javaClass.isArray) {
            return from.arrayAsList<Any?>()
        }
        return null
    }

    private fun toIterable(iterable: Iterable<Any?>, toType: Class<*>, converter: Converter): Any? {
        if (toType.isArray) {
            val array = toType.arrayTypeToArray<Any?>(iterable.count())

        }
    }

    private fun toIterable(iterable: Iterable<Any?>, toType: ParameterizedType, converter: Converter): Any? {
    }

    private fun toIterable(iterable: Iterable<Any?>, toType: GenericArrayType, converter: Converter): Any? {
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

open class BeanConvertHandler(
    private val beanResolver: BeanResolver = BeanResolver.DEFAULT
) : AbstractTypeConvertHandler() {

    override fun doConvertNull(toType: Type, converter: Converter): Any? {
        return Default.NULL
    }

    override fun doConvertNotNull(from: Any, fromType: Type, toType: Type, converter: Converter): Any? {
        return when (toType) {
            is Class<*> -> return doConvert0(from, toType, toType.upperBound, converter)
            is ParameterizedType -> return doConvert0(from, toType.rawClass, toType.upperBound, converter)
            else -> null
        }
    }

    private fun doConvert0(from: Any, toRawClass: Class<*>, toType: Type, converter: Converter): Any? {
        return when (toRawClass) {
            Map::class.java -> beanResolver.copyProperties(
                from,
                HashMap<Any, Any?>(),
                from.javaClass,
                toType,
                converter
            ).toMap()
            MutableMap::class.java, LinkedHashMap::class.java -> beanResolver.copyProperties(
                from,
                LinkedHashMap(),
                from.javaClass,
                toType,
                converter
            )
            HashMap::class.java -> beanResolver.copyProperties(
                from,
                HashMap(),
                from.javaClass,
                toType,
                converter
            )
            TreeMap::class.java -> beanResolver.copyProperties(
                from,
                TreeMap(),
                from.javaClass,
                toType,
                converter
            )
            else -> {
                val toInstance = toRawClass.toInstance<Any>()
                return beanResolver.copyProperties(from, toInstance, from.javaClass, toType, converter)
            }
        }
    }

    companion object {

        @JvmField
        val DEFAULT: BeanTypeConvertHandler = BeanTypeConvertHandler()
    }
}