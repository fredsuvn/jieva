@file:JvmName("BConverts")
@file:JvmMultifileClass

package xyz.srclab.common.convert

import xyz.srclab.common.base.*
import xyz.srclab.common.bean.BeanCreator
import xyz.srclab.common.bean.BeanResolver
import xyz.srclab.common.bean.copyProperties
import xyz.srclab.common.collect.*
import xyz.srclab.common.reflect.*
import java.lang.reflect.*
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.charset.Charset
import java.time.*
import java.time.temporal.Temporal
import java.time.temporal.TemporalAdjuster
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@JvmOverloads
fun nullConvertHandler(preventNull: Boolean = true): NullConvertHandler {
    return NullConvertHandler(preventNull)
}

@JvmOverloads
fun charsConvertHandler(charset: Charset = DEFAULT_CHARSET): CharsConvertHandler {
    return CharsConvertHandler(charset)
}

@JvmOverloads
fun numberBooleanConvertHandler(radix: Int = DEFAULT_RADIX): NumberBooleanConvertHandler {
    return NumberBooleanConvertHandler(radix)
}

@JvmOverloads
fun beanConvertHandler(
    beanCreator: BeanCreator = BeanCreator.DEFAULT,
    beanResolver: BeanResolver = BeanResolver.DEFAULT,
): BeanConvertHandler {
    return BeanConvertHandler(beanCreator, beanResolver)
}

/**
 * Handler for [Converter].
 *
 * @see Converter
 * @see CompatibleConvertHandler
 * @see LowerBoundConvertHandler
 * @see CharsConvertHandler
 * @see NumberBooleanConvertHandler
 * @see DateTimeConvertHandler
 * @see CollectionConvertHandler
 * @see BeanConvertHandler
 */
interface BConvertHandler {

    /**
     * Converts from [from] to [toType]. Returns null if it is unsupported.
     *
     * If the result value is exactly `null`, return [NULL].
     */
    fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any?

    companion object {

        @JvmField
        val NULL: Any = "(╯°Д°)╯︵ ┻━┻"

        /**
         * Common handlers.
         */
        @JvmField
        val COMMON_HANDLERS: List<BConvertHandler> = listOf(
            CompatibleConvertHandler,
            LowerBoundConvertHandler,
            CharsConvertHandler,
            NumberBooleanConvertHandler,
            DateTimeConvertHandler.DEFAULT,
            CollectionConvertHandler,
            BeanConvertHandler.DEFAULT,
        )
    }
}

open class NullConvertHandler(private val preventNull: Boolean) : BConvertHandler {
    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (from !== null) {
            return null
        }
        return if (preventNull) BConvertHandler.NULL else null
    }
}

/**
 * [BConvertHandler] which is used to convert compatible type:
 *
 * * Returns `from` itself if `fromType` == `toType` or `toType` is compatible with `from`;
 * * If `from` is [String] and `toType` is enum, call `from.toString()` and find out the enum (case-ignored);
 * * If `from` is enum and `toType` is [String], return `from.toString()`;
 */
object CompatibleConvertHandler : BConvertHandler {
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
            if (fromClass == String::class.java && toType.isEnum) {
                return toType.enumValueIgnoreCaseOrNull<Any>(from.toString())
            }
            if (fromClass.isEnum && toType == String::class.java) {
                return from.toString()
            }
        }
        return null
    }
}

/**
 * Returns lower bound of `toType` with [lowerBound] for [WildcardType] and [TypeVariable].
 */
object LowerBoundConvertHandler : BConvertHandler {
    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        return when (toType) {
            is WildcardType -> {
                val lowerBound = toType.lowerBound
                if (lowerBound === null) {
                    null
                } else {
                    context.converter.convertOrNull<Any>(from, fromType, lowerBound)
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
open class CharsConvertHandler(private val charset: Charset) : BConvertHandler {
    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (toType !is Class<*>) {
            return null
        }
        fun fromToCharSeq(): CharSequence {
            return when (from) {
                is ByteArray -> from.encodeToString(charset)
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
                is CharSequence -> from.decodeToBytes(charset)
                is CharArray -> String(from).decodeToBytes(charset)
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
open class NumberBooleanConvertHandler(private val radix: Int) : BConvertHandler {
    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (toType !is Class<*>) {
            return null
        }
        return when {
            toType.isBooleanType -> from.toBoolean()
            toType.isByteType -> from.toByte(DEFAULT_NULL_NUMBER.toByte(), radix)
            toType.isShortType -> from.toShort(DEFAULT_NULL_NUMBER.toShort(), radix)
            toType.isCharType -> from.toChar(DEFAULT_NULL_NUMBER.toChar(), radix)
            toType.isIntType -> from.toInt(DEFAULT_NULL_NUMBER, radix)
            toType.isLongType -> from.toLong(DEFAULT_NULL_NUMBER.toLong(), radix)
            toType.isFloatType -> from.toFloat(DEFAULT_NULL_NUMBER.toFloat())
            toType.isDoubleType -> from.toDouble(DEFAULT_NULL_NUMBER.toDouble())
            toType == BigInteger::class.java -> from.toBigInteger(DEFAULT_NULL_NUMBER.toBigInteger(), radix)
            toType == BigDecimal::class.java -> from.toBigDecimal(DEFAULT_NULL_NUMBER.toBigDecimal())
            toType == Number::class.java -> from.toInt(DEFAULT_NULL_NUMBER, radix)
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
object DateTimeConvertHandler : BConvertHandler {
    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (toType !is Class<*>) {
            return null
        }
        return when (toType) {
            Date::class.java -> from.toDate()
            Instant::class.java -> from.toInstant()
            LocalDateTime::class.java -> from.toLocalDateTime()
            ZonedDateTime::class.java -> from.toZonedDateTime()
            OffsetDateTime::class.java -> from.toOffsetDateTime()
            LocalDate::class.java -> from.toLocalDate()
            LocalTime::class.java -> from.toLocalTime()
            Duration::class.java -> from.toDuration()
            Temporal::class.java, TemporalAdjuster::class.java -> from.toLocalDateTime()
            else -> null
        }
    }
}

/**
 * Supports convert `from` to collection types:
 *
 * * Array, [GenericArrayType];
 * * [Iterable], [Collection], [Set], [List];
 * * [HashSet], [LinkedHashSet], [TreeSet];
 */
object CollectionConvertHandler : BConvertHandler {

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
            iterableParameterizedType.toCollectType().componentType
        } catch (e: Exception) {
            null
        }
    }

    private fun getToComponentType(toType: ParameterizedType): Type? {
        return try {
            val iterableParameterizedType = toType.getTypeSignature(Iterable::class.java)
            iterableParameterizedType.toCollectType().componentType
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
 * * [BSetMap], [BListMap];
 */
open class BeanConvertHandler(
    private val beanCreator: BeanCreator,
    private val beanResolver: BeanResolver
) : BConvertHandler {

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
            BSetMap::class.java ->
                toSetMap(from, toType, context.converter)
            BListMap::class.java ->
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
        return from.copyProperties(setMap<Any, Any?>(), toType, beanResolver, converter)
    }

    private fun toListMap(from: Any, toType: Type, converter: Converter): Any {
        return from.copyProperties(listMap<Any, Any?>(), toType, beanResolver, converter)
    }

    private fun toObject(from: Any, toType: Type, converter: Converter): Any? {
        if (toType !is Class<*>) {
            return null
        }
        val builder = beanCreator.newBuilder(toType)
        //from.copyProperties(builder, newBeanProvider.builderType(builder, toType), beanResolver, converter)
        from.copyProperties(builder, beanResolver, converter)
        return beanCreator.build(builder)
    }
}