package xyz.srclab.common.convert

import xyz.srclab.common.base.*
import xyz.srclab.common.bean.BeanBuilder
import xyz.srclab.common.bean.BeanResolver
import xyz.srclab.common.bean.copyProperties
import xyz.srclab.common.collect.*
import xyz.srclab.common.collect.IterableType.Companion.toIterableType
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
     * Does convert from [from] to [to],
     * returns null if unsupported,
     * returns [NULL] if result explicitly is `null`.
     */
    fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any?

    companion object {

        @JvmField
        val NULL: Any = "(╯°Д°)╯︵ ┻━┻"

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
    }
}

/**
 * [ConvertHandler] which is used to convert compatible type:
 *
 * * Returns `from` itself if `fromType` == `toType` or `toType` is compatible with `from`;
 * * If `toType` is enum, invoke `from.toString()` and try to find out same name enum value (case-ignored);
 */
object CompatibleConvertHandler : ConvertHandler {
    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (from === null || fromType == toType || toType == Any::class.java) {
            return from
        }
        if (toType is Class<*>) {
            val fromClass = from.javaClass
            if (toType.isAssignableFrom(fromClass)) {
                return from
            }
            if (fromClass.toWrapperClass() == toType.toWrapperClass()) {
                return from
            }
            if (toType.isEnum) {
                return toType.enumValueIgnoreCaseOrNull<Any>(from.toString())
            }
        }
        return null
    }
}

/**
 * Returns lower bound of `toType` with [lowerBound] for [WildcardType] and [TypeVariable].
 */
object LowerBoundConvertHandler : ConvertHandler {
    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        return when (toType) {
            is WildcardType -> {
                val lowerBound = toType.lowerBound
                if (lowerBound === null) {
                    null
                } else {
                    context.converter.convert<Any?>(from, fromType, lowerBound)
                }
            }
            else -> null
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
    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (toType !is Class<*>) {
            return null
        }
        fun fromToCharSeq(): CharSequence {
            return when (from) {
                is ByteArray -> from.toChars()
                is CharArray -> String(from)
                else -> from.toCharSeq()
            }
        }
        return when (toType) {
            CharSequence::class.java -> fromToCharSeq()
            String::class.java -> fromToCharSeq().toString()
            StringBuilder::class.java -> StringBuilder(fromToCharSeq())
            StringBuffer::class.java -> StringBuffer(fromToCharSeq())
            CharArray::class.java -> fromToCharSeq().toString().toCharArray()
            ByteArray::class.java -> when (from) {
                is CharSequence -> from.toBytes()
                is CharArray -> String(from).toBytes()
                else -> null
            }
            else -> null
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
    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (toType !is Class<*>) {
            return null
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
            toType == Number::class.java -> from.toInt()
            else -> null
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
open class DateTimeConvertHandler @JvmOverloads constructor(
    private val dateFormat: DateFormat? = null,
    private val dateTimeFormatter: DateTimeFormatter? = null,
) : ConvertHandler {

    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (toType !is Class<*>) {
            return null
        }
        return when (toType) {
            Date::class.java -> from.toDate(dateFormat)
            Instant::class.java -> from.toInstant(dateTimeFormatter)
            LocalDateTime::class.java -> from.toLocalDateTime(dateTimeFormatter)
            ZonedDateTime::class.java -> from.toZonedDateTime(dateTimeFormatter)
            OffsetDateTime::class.java -> from.toOffsetDateTime(dateTimeFormatter)
            LocalDate::class.java -> from.toLocalDate(dateTimeFormatter)
            LocalTime::class.java -> from.toLocalTime(dateTimeFormatter)
            Duration::class.java -> from.toDuration()
            Temporal::class.java, TemporalAdjuster::class.java -> from.toLocalDateTime(dateTimeFormatter)
            else -> null
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

    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (from === null) {
            return null
        }
        if (toType is GenericArrayType) {
            return convert(from, fromType, toType.rawClass, context)
        }
        val fromComponentType = getFromComponentType(fromType)
        if (fromComponentType === null) {
            return null
        }
        val iterable = fromToIterableOrNull(from)
        if (iterable === null) {
            return null
        }
        return when (toType) {
            is Class<*> -> {
                if (toType.isArray) {
                    return iterableToArray(iterable, fromComponentType, toType, context.converter)
                }
                if (Iterable::class.java.isAssignableFrom(toType)) {
                    return iterableToIterable(iterable, fromComponentType, toType, Any::class.java, context.converter)
                }
                return null
            }
            is ParameterizedType -> {
                val toClass = toType.rawClass
                if (Iterable::class.java.isAssignableFrom(toClass)) {
                    val toComponentType = getToComponentType(toType)
                    if (toComponentType === null) {
                        return null
                    }
                    return iterableToIterable(iterable, fromComponentType, toClass, toComponentType, context.converter)
                }
                return null
            }
            else -> null
        }
    }

    private fun getFromComponentType(fromType: Type): Type? {
        val fromClass = fromType.rawClassOrNull ?: Any::class.java
        if (fromClass.isArray) {
            return fromClass.componentType
        }
        return try {
            val iterableParameterizedType = fromType.getTypeSignature(Iterable::class.java)
            iterableParameterizedType.toIterableType().componentType
        } catch (e: Exception) {
            null
        }
    }

    private fun getToComponentType(toType: ParameterizedType): Type? {
        return try {
            val iterableParameterizedType = toType.getTypeSignature(Iterable::class.java)
            iterableParameterizedType.toIterableType().componentType
        } catch (e: Exception) {
            null
        }
    }

    private fun fromToIterableOrNull(from: Any): Iterable<Any?>? {
        if (from is Iterable<Any?>) {
            return from
        }
        if (from.javaClass.isArray) {
            return from.arrayAsList()
        }
        return null
    }

    private fun iterableToArray(
        from: Iterable<Any?>,
        fromComponentType: Type,
        toType: Class<*>,
        converter: Converter
    ): Any {
        val toComponentType = toType.componentType
        return from.toAnyArray(toComponentType) {
            converter.convert<Any>(it, fromComponentType, toComponentType)
        }
    }

    private fun iterableToIterable(
        from: Iterable<Any?>,
        fromComponentType: Type,
        toClass: Class<*>,
        toComponentType: Type,
        converter: Converter
    ): Iterable<Any?>? {
        return when (toClass) {
            Iterable::class.java, List::class.java, ArrayList::class.java ->
                from.mapTo(ArrayList(from.count())) {
                    converter.convert(it, fromComponentType, toComponentType)
                }
            LinkedList::class.java ->
                from.mapTo(LinkedList()) {
                    converter.convert(it, fromComponentType, toComponentType)
                }
            Collection::class.java, Set::class.java, LinkedHashSet::class.java ->
                from.mapTo(LinkedHashSet()) {
                    converter.convert(it, fromComponentType, toComponentType)
                }
            HashSet::class.java ->
                from.mapTo(HashSet()) {
                    converter.convert(it, fromComponentType, toComponentType)
                }
            TreeSet::class.java ->
                from.mapTo(TreeSet()) {
                    converter.convert(it, fromComponentType, toComponentType)
                }
            else -> null
        }
    }
}

/**
 * Supports convert `from` to `bean` or [Map] types:
 *
 * * Bean;
 * * [Map], [HashMap], [LinkedHashMap], [TreeMap];
 * * [ConcurrentHashMap];
 * * [SetMap], [ListMap];
 */
open class BeanConvertHandler @JvmOverloads constructor(
    private val beanBuilder: BeanBuilder = BeanBuilder.DEFAULT,
    private val beanResolver: BeanResolver = BeanResolver.DEFAULT,
) : ConvertHandler {

    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (from === null) {
            return null
        }
        if (toType is GenericArrayType) {
            return null
        }
        val toRawClass = toType.rawClassOrNull
        if (toRawClass === null) {
            return null
        }
        if (toRawClass.isArray) {
            return null
        }
        if (toRawClass.isEnum) {
            return null
        }
        return when (toRawClass) {
            Map::class.java, LinkedHashMap::class.java ->
                toLinkedHashMap(from, toType, context.converter)
            HashMap::class.java ->
                toHashMap(from, toType, context.converter)
            TreeMap::class.java ->
                toTreeMap(from, toType, context.converter)
            ConcurrentHashMap::class.java ->
                toConcurrentHashMap(from, toType, context.converter)
            SetMap::class.java ->
                toSetMap(from, toType, context.converter)
            ListMap::class.java ->
                toListMap(from, toType, context.converter)
            else ->
                toObject(from, toType, context.converter)
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
        return from.copyProperties(SetMap<Any, Any?>(), toType, beanResolver, converter)
    }

    private fun toListMap(from: Any, toType: Type, converter: Converter): Any {
        return from.copyProperties(ListMap<Any, Any?>(), toType, beanResolver, converter)
    }

    private fun toObject(from: Any, toType: Type, converter: Converter): Any? {
        if (toType !is Class<*>) {
            return null
        }
        val builder = beanBuilder.newBuilder(toType)
        //from.copyProperties(builder, newBeanProvider.builderType(builder, toType), beanResolver, converter)
        from.copyProperties(builder, beanResolver, converter)
        return beanBuilder.build(builder)
    }

    companion object {
        @JvmField
        val DEFAULT: BeanConvertHandler = BeanConvertHandler()
    }
}