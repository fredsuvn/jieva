package xyz.srclab.common.convert

import org.apache.commons.lang3.ArrayUtils
import xyz.srclab.common.bean.BeanCopyOptions
import xyz.srclab.common.bean.copyProperties
import xyz.srclab.common.collect.*
import xyz.srclab.common.collect.IterableType.Companion.toIterableType
import xyz.srclab.common.lang.*
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
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.count
import kotlin.collections.mapTo

/**
 * Handler for [Converter].
 *
 * @see Converter
 */
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

        @JvmStatic
        fun defaultsWithDateTimeConvertHandler(
            dateTimeConvertHandler: DateTimeConvertHandler,
        ): List<ConvertHandler> {
            return listOf(
                NopConvertHandler,
                WildcardTypeConvertHandler,
                CharsConvertHandler,
                NumberAndPrimitiveConvertHandler,
                dateTimeConvertHandler,
                IterableConvertHandler,
                BeanConvertHandler.DEFAULT,
            )
        }

        @JvmStatic
        fun defaultsWithBeanConvertHandler(
            beanConvertHandler: BeanConvertHandler,
        ): List<ConvertHandler> {
            return listOf(
                NopConvertHandler,
                WildcardTypeConvertHandler,
                CharsConvertHandler,
                NumberAndPrimitiveConvertHandler,
                DateTimeConvertHandler.DEFAULT,
                IterableConvertHandler,
                beanConvertHandler,
            )
        }

        @JvmStatic
        fun defaultsWithDateTimeBeanConvertHandler(
            dateTimeConvertHandler: DateTimeConvertHandler,
            beanConvertHandler: BeanConvertHandler,
        ): List<ConvertHandler> {
            return listOf(
                NopConvertHandler,
                WildcardTypeConvertHandler,
                CharsConvertHandler,
                NumberAndPrimitiveConvertHandler,
                dateTimeConvertHandler,
                IterableConvertHandler,
                beanConvertHandler,
            )
        }
    }
}

object NopConvertHandler : ConvertHandler {

    override fun convert(from: Any?, toType: Class<*>, converter: Converter): Any? {
        if (toType == Any::class.java) {
            return from.replaceNull()
        }
        if (from === null) {
            return Default.NULL
        }
        val fromClass = from.javaClass
        if (fromClass == toType || toType.isAssignableFrom(fromClass)) {
            return from.replaceNull()
        }
        return null
    }

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        return if (toType is Class<*>) convert(from, toType, converter) else null
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        if (toType == Any::class.java || fromType == toType) {
            return from.replaceNull()
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
            String::class.java, CharSequence::class.java -> when (from) {
                is ByteArray -> from.toChars()
                is CharArray -> from.toChars()
                else -> from.toString()
            }
            StringBuilder::class.java -> when (from) {
                is ByteArray -> StringBuilder(from.toChars())
                is CharArray -> StringBuilder(from.toChars())
                else -> StringBuilder(from.toString())
            }
            StringBuffer::class.java -> when (from) {
                is ByteArray -> StringBuffer(from.toChars())
                is CharArray -> StringBuffer(from.toChars())
                else -> StringBuffer(from.toString())
            }
            CharArray::class.java -> {
                if (from is ByteArray)
                    from.toChars().toCharArray()
                else
                    from.toString().toCharArray()
            }
            Array<Char>::class.java -> {
                if (from is ByteArray)
                    ArrayUtils.toObject(from.toChars().toCharArray())
                else
                    ArrayUtils.toObject(from.toString().toCharArray())
            }
            ByteArray::class.java -> when (from) {
                is CharSequence -> from.toBytes()
                is CharArray -> from.toBytes()
                else -> from.toString()
            }
            Array<Byte>::class.java -> when (from) {
                is CharSequence -> ArrayUtils.toObject(from.toBytes())
                is CharArray -> ArrayUtils.toObject(from.toBytes())
                else -> from.toString()
            }
            else -> null
        }
    }
}

object NumberAndPrimitiveConvertHandler : AbstractClassConvertHandler() {

    override fun convert(from: Any?, toType: Class<*>, converter: Converter): Any? {
        return when {
            toType.isBooleanType -> from.toBoolean()
            toType.isByteType -> from.toByte()
            toType.isShortType -> from.toShort()
            toType.isCharType -> from.toChar()
            toType.isIntType -> from.toInt()
            toType.isLongType -> from.toLong()
            toType.isFloatType -> from.toFloat()
            toType.isDoubleType -> from.toDouble()
            toType == BigInteger::class.java -> from.toBigInteger()
            toType == BigDecimal::class.java -> from.toBigDecimal()
            toType == Number::class.java -> from.toNumber()
            else -> null
        }
    }
}

open class DateTimeConvertHandler(
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

    override fun convertNull(toType: Type, converter: Converter): Any {
        return Default.NULL
    }

    override fun convertNotNull(from: Any, fromType: Type, toType: Type, converter: Converter): Any? {
        return when (toType) {
            is Class<*> -> {
                if (toType.isArray) {
                    val iterable = fromToIterable(from)
                    return if (iterable === null) {
                        null
                    } else {
                        toArray(iterable, toType, converter)
                    }
                }
                if (Iterable::class.java.isAssignableFrom(toType)) {
                    return toIterableType(from, toType.toIterableType(), converter)
                }
                null
            }
            is GenericArrayType -> convertNotNull(from, fromType, toType.rawClass, converter)
            is ParameterizedType -> {
                val rawClass = toType.rawClass
                if (Iterable::class.java.isAssignableFrom(rawClass)) {
                    return toIterableType(from, toType.toIterableType(), converter)
                }
                null
            }
            else -> null
        }
    }

    private fun toArray(iterable: Iterable<Any?>, arrayClass: Class<*>, converter: Converter): Any {
        val componentType = arrayClass.componentType
        return when {
            componentType.isByteType -> iterable.toBooleanArray()
            componentType.isByteType -> iterable.toByteArray()
            componentType.isShortType -> iterable.toShortArray()
            componentType.isCharType -> iterable.toCharArray()
            componentType.isIntType -> iterable.toIntArray()
            componentType.isLongType -> iterable.toLongArray()
            componentType.isFloatType -> iterable.toFloatArray()
            componentType.isDoubleType -> iterable.toDoubleArray()
            else -> iterable.toArray(componentType) { converter.convert(it, componentType) }
        }
    }

    private fun toIterableType(from: Any, iterableType: IterableType, converter: Converter): Any? {
        return when (iterableType.rawClass) {
            List::class.java, ArrayList::class.java -> toArrayList(from, iterableType.componentType, converter)
            LinkedList::class.java -> toLinkedList(from, iterableType.componentType, converter)
            Collection::class.java, Set::class.java, LinkedHashSet::class.java ->
                toLinkedHashSet(from, iterableType.componentType, converter)
            HashSet::class.java -> toHashSet(from, iterableType.componentType, converter)
            TreeSet::class.java -> toTreeSet(from, iterableType.componentType, converter)
            else -> null
        }
    }

    private fun toArrayList(from: Any, componentType: Type, converter: Converter): Any? {
        val iterable = fromToIterable(from)
        if (iterable === null) {
            return null
        }
        return iterable.mapTo(ArrayList<Any?>(iterable.count())) {
            converter.convert(it, componentType)
        }
    }

    private fun toLinkedList(from: Any, componentType: Type, converter: Converter): Any? {
        val iterable = fromToIterable(from)
        if (iterable === null) {
            return null
        }
        return iterable.mapTo(LinkedList<Any?>()) {
            converter.convert(it, componentType)
        }
    }

    private fun toLinkedHashSet(from: Any, componentType: Type, converter: Converter): Any? {
        val iterable = fromToIterable(from)
        if (iterable === null) {
            return null
        }
        return iterable.mapTo(LinkedHashSet<Any?>()) {
            converter.convert(it, componentType)
        }
    }

    private fun toHashSet(from: Any, componentType: Type, converter: Converter): Any? {
        val iterable = fromToIterable(from)
        if (iterable === null) {
            return null
        }
        return iterable.mapTo(HashSet<Any?>()) {
            converter.convert(it, componentType)
        }
    }

    private fun toTreeSet(from: Any, componentType: Type, converter: Converter): Any? {
        val iterable = fromToIterable(from)
        if (iterable === null) {
            return null
        }
        return iterable.mapTo(TreeSet<Any?>()) {
            converter.convert(it, componentType)
        }
    }

    private fun fromToIterable(from: Any): Iterable<Any?>? {
        if (from is Iterable<*>) {
            return from.replaceNull().asAny()
        }
        if (from.javaClass.isArray) {
            return from.replaceNull().arrayAsList()
        }
        return null
    }
}

open class BeanConvertHandler @JvmOverloads constructor(
    private val copyOptions: BeanCopyOptions = BeanCopyOptions.DEFAULT,
    private val builderGenerator: (Class<*>) -> Any = { it.toInstance() },
    private val buildFunction: (builder: Any, toType: Class<*>) -> Any = { builder, _ -> builder },
) : AbstractTypeConvertHandler() {

    override fun convertNull(toType: Type, converter: Converter): Any {
        return Default.NULL
    }

    override fun convertNotNull(from: Any, fromType: Type, toType: Type, converter: Converter): Any? {
        return when (toType) {
            Map::class.java, LinkedHashMap::class.java -> toLinkedHashMap(from, fromType, toType, converter)
            HashMap::class.java -> toHashMap(from, fromType, toType, converter)
            TreeMap::class.java -> toTreeMap(from, fromType, toType, converter)
            ConcurrentHashMap::class.java -> toConcurrentHashMap(from, fromType, toType, converter)
            is Class<*> -> {
                if (toType.isArray) {
                    return null
                }
                return toObject(from, fromType, toType, converter)
            }
            is ParameterizedType -> {
                return when (val rawClass = toType.rawClass) {
                    Map::class.java, LinkedHashMap::class.java -> toLinkedHashMap(from, fromType, toType, converter)
                    HashMap::class.java -> toHashMap(from, fromType, toType, converter)
                    TreeMap::class.java -> toTreeMap(from, fromType, toType, converter)
                    else -> toObject(from, fromType, rawClass, converter)
                }
            }
            else -> null
        }
    }

    private fun toLinkedHashMap(from: Any, fromType: Type, toType: Type, converter: Converter): Any {
        return from.copyProperties(LinkedHashMap<Any?, Any?>(), currentCopyOptions(fromType, toType, converter))
    }

    private fun toHashMap(from: Any, fromType: Type, toType: Type, converter: Converter): Any {
        return from.copyProperties(HashMap<Any?, Any?>(), currentCopyOptions(fromType, toType, converter))
    }

    private fun toTreeMap(from: Any, fromType: Type, toType: Type, converter: Converter): Any {
        return from.copyProperties(TreeMap<Any?, Any?>(), currentCopyOptions(fromType, toType, converter))
    }

    private fun toConcurrentHashMap(from: Any, fromType: Type, toType: Type, converter: Converter): Any {
        return from.copyProperties(ConcurrentHashMap<Any?, Any?>(), currentCopyOptions(fromType, toType, converter))
    }

    private fun toObject(from: Any, fromType: Type, toType: Class<*>, converter: Converter): Any {
        val builder = builderGenerator(toType)
        from.copyProperties(builder, currentCopyOptions(fromType, builder.javaClass, converter))
        return buildFunction(builder, toType)
    }

    private fun currentCopyOptions(fromType: Type, toType: Type, converter: Converter): BeanCopyOptions {
        return copyOptions.toBuilder()
            .fromType(fromType)
            .toType(toType)
            .converter(converter)
            .build()
    }

    companion object {

        @JvmField
        val DEFAULT: BeanConvertHandler = BeanConvertHandler()
    }
}

private fun Any?.replaceNull(): Any {
    return if (this === null) Default.NULL else this
}