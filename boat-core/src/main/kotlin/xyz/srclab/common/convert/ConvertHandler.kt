package xyz.srclab.common.convert

import xyz.srclab.common.bean.BeanResolver
import xyz.srclab.common.bean.copyProperties
import xyz.srclab.common.collect.*
import xyz.srclab.common.collect.IterableType.Companion.toIterableType
import xyz.srclab.common.lang.*
import xyz.srclab.common.reflect.*
import java.lang.reflect.*
import java.math.BigDecimal
import java.math.BigInteger
import java.text.DateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.time.temporal.TemporalAdjuster
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Handler for [Converter].
 *
 * By default, a [Converter] uses a chain of [ConvertHandler]s to do convert action.
 * Use [ConvertChain.next] or [ConvertChain.restart] if current handler doesn't supports current convert.
 *
 * @see Converter
 * @see CompatibleConvertHandler
 * @see LowerBoundConvertHandler
 * @see CharsConvertHandler
 * @see NumberBooleanConvertHandler
 * @see DateTimeConvertHandler
 * @see IterableConvertHandler
 * @see BeanConvertHandler
 */
interface ConvertHandler {

    /**
     * Do convert from [from] to [to].
     *
     * Return [ConvertChain.next] or [ConvertChain.restart] if current handler doesn't supports current convert.
     */
    fun convert(from: Any?, fromType: Type, toType: Type, chain: ConvertChain): Any?

    companion object {

        @JvmField
        val DEFAULTS: List<ConvertHandler> = listOf(
            CompatibleConvertHandler,
            LowerBoundConvertHandler,
            CharsConvertHandler,
            NumberBooleanConvertHandler,
            DateTimeConvertHandler.DEFAULT,
            IterableConvertHandler,
            BeanConvertHandler.DEFAULT,
        )

        @JvmStatic
        fun defaultsWithDateTimeConvertHandler(
            dateTimeConvertHandler: DateTimeConvertHandler,
        ): List<ConvertHandler> {
            return listOf(
                CompatibleConvertHandler,
                LowerBoundConvertHandler,
                CharsConvertHandler,
                NumberBooleanConvertHandler,
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
                CompatibleConvertHandler,
                LowerBoundConvertHandler,
                CharsConvertHandler,
                NumberBooleanConvertHandler,
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
                CompatibleConvertHandler,
                LowerBoundConvertHandler,
                CharsConvertHandler,
                NumberBooleanConvertHandler,
                dateTimeConvertHandler,
                IterableConvertHandler,
                beanConvertHandler,
            )
        }
    }
}

/**
 * If `toType` is enum, this handler will invoke `toString()` of `fromType`
 * and try to find out same name enum value (case-ignored);
 * else use [Class.isAssignableFrom] to check whether from type can cast to to type;
 * else return [ConvertChain.next].
 */
object CompatibleConvertHandler : ConvertHandler {

    override fun convert(from: Any?, fromType: Type, toType: Type, chain: ConvertChain): Any? {
        if (fromType == toType || toType == Any::class.java) {
            return from
        }
        if (toType is Class<*>) {
            if (toType.isEnum) {
                val enumValue = toType.valueOfEnumIgnoreCaseOrNull<Enum<*>>(from.toString())
                return if (enumValue !== null) {
                    enumValue
                } else {
                    chain.next(from, fromType, toType)
                }
            }
            if (fromType is Class<*> && toType.isAssignableFrom(fromType)) {
                return from
            }
        }
        return chain.next(from, fromType, toType)
    }
}

/**
 * Returns lower bound of `toType` with [lowerBound] for [WildcardType] and [TypeVariable].
 */
object LowerBoundConvertHandler : ConvertHandler {

    override fun convert(from: Any?, fromType: Type, toType: Type, chain: ConvertChain): Any? {
        return when (toType) {
            is WildcardType -> {
                val lowerBound = toType.lowerBound
                if (lowerBound === null) {
                    chain.next(from, fromType, toType)
                } else {
                    chain.restart(from, fromType, lowerBound)
                }
            }
            else -> chain.next(from, fromType, toType)
        }
    }
}

/**
 * Supports convert `from` to types:
 *
 * * [String], [StringBuilder], [StringBuffer];
 * * [CharArray], [ByteArray], [Array<Char>], [Array<Byte>];
 */
object CharsConvertHandler : ConvertHandler {

    override fun convert(from: Any?, fromType: Type, toType: Type, chain: ConvertChain): Any? {
        if (toType !is Class<*>) {
            return chain.next(from, fromType, toType)
        }
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
            CharArray::class.java -> when (from) {
                is ByteArray -> from.toChars().toCharArray()
                else -> from.toString().toCharArray()
            }
            ByteArray::class.java -> when (from) {
                is CharSequence -> from.toBytes()
                is CharArray -> from.toBytes()
                else -> chain.next(from, fromType, toType)
            }
            else -> chain.next(from, fromType, toType)
        }
    }
}

/**
 * Supports convert `from` to number and boolean types:
 *
 * * [Boolean], [Byte], [Short], [Char], [Int], [Long], [Float], [Double] and their wrapper types;
 * * [BigInteger], [BigDecimal];
 * * [Number];
 */
object NumberBooleanConvertHandler : ConvertHandler {

    override fun convert(from: Any?, fromType: Type, toType: Type, chain: ConvertChain): Any? {
        if (toType !is Class<*>) {
            return chain.next(from, fromType, toType)
        }
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
            else -> chain.next(from, fromType, toType)
        }
    }
}

/**
 * Supports convert `from` to datetime types:
 *
 * * [Date];
 * * [Instant];
 * * [LocalDate], [LocalTime], [LocalDateTime], [ZonedDateTime], [OffsetDateTime];
 * * [Duration], [Temporal];
 */
open class DateTimeConvertHandler(
    private val dateFormat: DateFormat,
    private val instantFormatter: DateTimeFormatter,
    private val localDateTimeFormatter: DateTimeFormatter,
    private val zonedDateTimeFormatter: DateTimeFormatter,
    private val offsetDateTimeFormatter: DateTimeFormatter,
    private val localDateFormatter: DateTimeFormatter,
    private val localTimeFormatter: DateTimeFormatter,
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

    override fun convert(from: Any?, fromType: Type, toType: Type, chain: ConvertChain): Any? {
        if (toType !is Class<*>) {
            return chain.next(from, fromType, toType)
        }
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
            else -> chain.next(from, fromType, toType)
        }
    }

    companion object {

        @JvmField
        val DEFAULT: DateTimeConvertHandler = DateTimeConvertHandler()
    }
}

/**
 * Supports convert `from` to iterable types:
 *
 * * Array, [GenericArrayType];
 * * [Iterable], [Collection], [Set], [List];
 * * [HashSet], [LinkedHashSet], [TreeSet];
 */
object IterableConvertHandler : ConvertHandler {

    override fun convert(from: Any?, fromType: Type, toType: Type, chain: ConvertChain): Any? {
        if (from === null) {
            return chain.next(from, fromType, toType)
        }
        return when (toType) {
            is Class<*> -> {
                if (toType.isArray) {
                    val iterable = from.toIterable()
                    if (iterable === null) {
                        return chain.next(from, fromType, toType)
                    }
                    return toArray(iterable, toType, chain.converter)
                }
                if (Iterable::class.java.isAssignableFrom(toType)) {
                    val iterable = from.toIterable()
                    if (iterable === null) {
                        return chain.next(from, fromType, toType)
                    }
                    return toIterableOrNull(iterable, toType.toIterableType(), chain.converter)
                }
                return chain.next(from, fromType, toType)
            }
            is GenericArrayType -> convert(from, fromType, toType.rawClass, chain)
            is ParameterizedType -> {
                val rawClass = toType.rawClass
                if (Iterable::class.java.isAssignableFrom(rawClass)) {
                    val iterable = from.toIterable()
                    if (iterable === null) {
                        return chain.next(from, fromType, toType)
                    }
                    return toIterableOrNull(iterable, toType.toIterableType(), chain.converter)
                        ?: chain.next(from, fromType, toType)
                }
                chain.next(from, fromType, toType)
            }
            else -> chain.next(from, fromType, toType)
        }
    }

    private fun toArray(from: Iterable<*>, toType: Class<*>, converter: Converter): Any {
        return from.toAnyArray(toType.componentType) {
            converter.convert(it, toType.componentType)
        }
    }

    private fun toIterableOrNull(from: Iterable<*>, iterableType: IterableType, converter: Converter): Any? {
        return when (iterableType.rawClass) {
            Iterable::class.java, List::class.java, ArrayList::class.java ->
                toArrayList(from, iterableType.componentType, converter)
            LinkedList::class.java ->
                toLinkedList(from, iterableType.componentType, converter)
            Collection::class.java, Set::class.java, LinkedHashSet::class.java ->
                toLinkedHashSet(from, iterableType.componentType, converter)
            HashSet::class.java ->
                toHashSet(from, iterableType.componentType, converter)
            TreeSet::class.java ->
                toTreeSet(from, iterableType.componentType, converter)
            else -> null
        }
    }

    private fun toArrayList(iterable: Iterable<*>, componentType: Type, converter: Converter): Any {
        return iterable.mapTo(ArrayList<Any?>(iterable.count())) {
            converter.convert(it, componentType)
        }
    }

    private fun toLinkedList(iterable: Iterable<*>, componentType: Type, converter: Converter): Any {
        return iterable.mapTo(LinkedList<Any?>()) {
            converter.convert(it, componentType)
        }
    }

    private fun toLinkedHashSet(iterable: Iterable<*>, componentType: Type, converter: Converter): Any {
        return iterable.mapTo(LinkedHashSet<Any?>()) {
            converter.convert(it, componentType)
        }
    }

    private fun toHashSet(iterable: Iterable<*>, componentType: Type, converter: Converter): Any {
        return iterable.mapTo(HashSet<Any?>()) {
            converter.convert(it, componentType)
        }
    }

    private fun toTreeSet(iterable: Iterable<*>, componentType: Type, converter: Converter): Any {
        return iterable.mapTo(TreeSet<Any?>()) {
            converter.convert(it, componentType)
        }
    }

    private fun Any.toIterable(): Iterable<Any?>? {
        return when {
            this is Iterable<*> -> this
            this.javaClass.isArray -> this.arrayAsList()
            else -> null
        }
    }
}

/**
 * Supports convert `from` to bean or [Map] types:
 *
 * * Bean;
 * * [Map], [HashMap], [LinkedHashMap], [TreeMap];
 * * [ConcurrentHashMap];
 * * [SetMap], [MutableSetMap], [ListMap], [MutableListMap];
 */
open class BeanConvertHandler @JvmOverloads constructor(
    private val beanGenerator: BeanGenerator = BeanGenerator.DEFAULT,
    private val beanResolver: BeanResolver = BeanResolver.DEFAULT,
) : ConvertHandler {

    override fun convert(from: Any?, fromType: Type, toType: Type, chain: ConvertChain): Any? {
        if (from === null) {
            return chain.next(from, fromType, toType)
        }
        if (toType is GenericArrayType) {
            return chain.next(from, fromType, toType)
        }
        val toRawClass = toType.rawClassOrNull
        if (toRawClass === null) {
            return chain.next(from, fromType, toType)
        }
        if (toRawClass.isArray) {
            return chain.next(from, fromType, toType)
        }
        if (toRawClass.isEnum) {
            return chain.next(from, fromType, toType)
        }
        return when (toRawClass) {
            Map::class.java, LinkedHashMap::class.java ->
                toLinkedHashMap(from, toType, chain.converter)
            HashMap::class.java ->
                toHashMap(from, toType, chain.converter)
            TreeMap::class.java ->
                toTreeMap(from, toType, chain.converter)
            ConcurrentHashMap::class.java ->
                toConcurrentHashMap(from, toType, chain.converter)
            SetMap::class.java ->
                toSetMap(from, toType, chain.converter)
            MutableSetMap::class.java ->
                toMutableSetMap(from, toType, chain.converter)
            ListMap::class.java ->
                toListMap(from, toType, chain.converter)
            MutableListMap::class.java ->
                toMutableListMap(from, toType, chain.converter)
            else ->
                toObject(from, toType, chain.converter)
        }
    }

    private fun toLinkedHashMap(from: Any, toType: Type, converter: Converter): Any {
        return from.copyProperties(LinkedHashMap<Any, Any?>(), toType, beanResolver, converter)
    }

    private fun toHashMap(from: Any, toType: Type, converter: Converter): Any {
        return from.copyProperties(HashMap<Any, Any?>(), toType, beanResolver, converter)
    }

    private fun toTreeMap(from: Any, toType: Type, converter: Converter): Any {
        return from.copyProperties(TreeMap<Any, Any?>(), toType, beanResolver, converter)
    }

    private fun toConcurrentHashMap(from: Any, toType: Type, converter: Converter): Any {
        return from.copyProperties(ConcurrentHashMap<Any, Any?>(), toType, beanResolver, converter)
    }

    private fun toSetMap(from: Any, toType: Type, converter: Converter): Any {
        return toMutableSetMap(from, toType, converter).toSetMap()
    }

    private fun toMutableSetMap(from: Any, toType: Type, converter: Converter): MutableSetMap<Any, Any?> {
        return from.copyProperties(MutableSetMap.newMutableSetMap(), toType, beanResolver, converter)
    }

    private fun toListMap(from: Any, toType: Type, converter: Converter): Any {
        return toMutableListMap(from, toType, converter).toListMap()
    }

    private fun toMutableListMap(from: Any, toType: Type, converter: Converter): MutableListMap<Any, Any?> {
        return from.copyProperties(MutableListMap.newMutableListMap(), toType, beanResolver, converter)
    }

    private fun toObject(from: Any, toType: Type, converter: Converter): Any {
        val builder = beanGenerator.newBuilder(toType)
        //from.copyProperties(builder, newBeanProvider.builderType(builder, toType), beanResolver, converter)
        from.copyProperties(builder, beanResolver, converter)
        return beanGenerator.build(builder, toType)
    }

    /**
     * Bean generator to create target bean.
     */
    interface BeanGenerator {

        /**
         * Returns builder for bean of [type].
         */
        fun newBuilder(toType: Type): Any

        ///**
        // * Builder's type.
        // */
        //fun builderType(builder: Any, toType: Type): Type

        ///**
        // * Returns builder's type.
        // */
        //fun builderType(toType: Type): Type

        /**
         * Builds [builder] to bean.
         */
        fun build(builder: Any, toType: Type): Any

        companion object {

            /**
             * Default new bean operation, with `new Bean()` then `setXxx(value)` way.
             */
            val DEFAULT: BeanGenerator = object : BeanGenerator {

                override fun newBuilder(toType: Type): Any {
                    return toType.rawClass.newInstance()
                }

                //override fun builderType(toType: Type): Type {
                //    return toType
                //}

                override fun build(builder: Any, toType: Type): Any {
                    return builder
                }
            }
        }
    }

    companion object {

        @JvmField
        val DEFAULT: BeanConvertHandler = BeanConvertHandler()
    }
}