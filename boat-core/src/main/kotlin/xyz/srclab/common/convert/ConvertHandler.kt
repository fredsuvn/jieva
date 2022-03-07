package xyz.srclab.common.convert

import xyz.srclab.common.base.*
import xyz.srclab.common.base.ParsedDate.Companion.toParsedDate
import xyz.srclab.common.bean.BeanCreator
import xyz.srclab.common.bean.BeanResolver
import xyz.srclab.common.bean.copyProperties
import xyz.srclab.common.collect.*
import xyz.srclab.common.collect.IterableType.Companion.toIterableType
import xyz.srclab.common.reflect.*
import java.lang.reflect.*
import java.lang.reflect.Array
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.charset.Charset
import java.time.*
import java.time.temporal.Temporal
import java.time.temporal.TemporalAccessor
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Handler for [Converter].
 *
 * @see Converter
 * @see CompatibleConvertHandler
 * @see LowerBoundConvertHandler
 * @see StringConvertHandler
 * @see NumberBooleanConvertHandler
 * @see DateTimeConvertHandler
 * @see CollectionConvertHandler
 * @see BeanConvertHandler
 * @see NullConvertHandler
 */
interface ConvertHandler {

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
         * Handler set for default [Converter].
         */
        @JvmField
        val DEFAULT_HANDLERS: List<ConvertHandler> = listOf(
            CompatibleConvertHandler,
            LowerBoundConvertHandler,
            StringConvertHandler(),
            NumberBooleanConvertHandler(),
            DateTimeConvertHandler(),
            CollectionConvertHandler,
            BeanConvertHandler(),
        )
    }
}

/**
 * [ConvertHandler] which is used to convert compatible type:
 *
 * * Returns `from` itself if `fromType` == `toType` or `toType` is compatible with `from`;
 * * If `from` is [String] and `toType` is enum, call `from.toString()` and find out the enum (case-ignored);
 * * If `from` is enum and `toType` is [String], return `from.toString()`;
 */
object CompatibleConvertHandler : ConvertHandler {
    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (from === null || fromType == toType || toType == Any::class.java) {
            return from
        }
        if (toType is Class<*>) {
            val fromClass = from.javaClass
            if (toType.canAssignedBy(fromClass)) {
                return from
            }
            if (fromClass.toWrapperClass() == toType.toWrapperClass()) {
                return from
            }
            if (fromClass == String::class.java && toType.isEnum) {
                return toType.enumValueOrNull<Any>(from.toString(), true)
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
object LowerBoundConvertHandler : ConvertHandler {
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
open class StringConvertHandler @JvmOverloads constructor(
    private val charset: Charset = defaultCharset()
) : ConvertHandler {
    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (toType !is Class<*>) {
            return null
        }
        fun fromToCharSeq(): CharSequence {
            return when (from) {
                is ByteArray -> from.bytesToString(charset)
                is CharArray -> String(from)
                else -> from.toCharSeq()
            }
        }
        return when (toType) {
            CharSequence::class.java -> fromToCharSeq()
            String::class.java -> fromToCharSeq().toString()
            StringBuilder::class.java -> StringBuilder(fromToCharSeq())
            StringBuffer::class.java -> StringBuffer(fromToCharSeq())
            CharArray::class.java -> when (from) {
                is CharSequence -> from.toCharArray()
                is ByteArray -> from.bytesToChars(charset)
                else -> null
            }
            ByteArray::class.java -> when (from) {
                is CharSequence -> from.charsToBytes(charset)
                is CharArray -> from.charsToBytes(charset)
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
open class NumberBooleanConvertHandler @JvmOverloads constructor(
    private val radix: Int = defaultRadix()
) : ConvertHandler {
    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (toType !is Class<*>) {
            return null
        }
        return when {
            toType.isBooleanType -> from.toBoolean()
            toType.isByteType -> from.toByte(radix)
            toType.isShortType -> from.toShort(radix)
            toType.isCharType -> from.toChar(radix)
            toType.isIntType -> from.toInt(radix)
            toType.isLongType -> from.toLong(radix)
            toType.isFloatType -> from.toFloat()
            toType.isDoubleType -> from.toDouble()
            toType == BigInteger::class.java -> from.toBigInteger(radix)
            toType == BigDecimal::class.java -> from.toBigDecimal()
            toType == Number::class.java -> from.toInt(radix)
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
    private val pattern: DatePattern = defaultTimestampPattern()
) : ConvertHandler {
    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (from === null) {
            return null
        }
        if (toType !is Class<*>) {
            return null
        }
        if (toType == Duration::class.java) {
            if (from is Duration) {
                return from
            }
            Duration.ofMillis(from.toLong())
        }
        val parsedDate = when (from) {
            is Date -> from.toParsedDate()
            is TemporalAccessor -> from.toParsedDate()
            else -> from.toString().toParsedDate(pattern)
        }
        return when (toType) {
            Date::class.java -> parsedDate.toDate()
            Instant::class.java -> parsedDate.toInstant()
            LocalDateTime::class.java -> parsedDate.toLocalDateTime()
            ZonedDateTime::class.java -> parsedDate.toZonedDateTime()
            OffsetDateTime::class.java -> parsedDate.toOffsetDateTime()
            LocalDate::class.java -> parsedDate.toLocalDate()
            LocalTime::class.java -> parsedDate.toLocalTime()
            TemporalAccessor::class.java -> parsedDate.toTemporal()
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
object CollectionConvertHandler : ConvertHandler {

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
        if (!toComponentType.isPrimitive) {
            return from.toArray(toComponentType.asTyped<Class<Any?>>()) {
                converter.convertOrNull(it, fromComponentType, toComponentType)
            }
        }
        val primitiveArray = toComponentType.newPrimitiveArray(from.count())
        for ((i, any) in from.withIndex()) {
            val element = any.convert(toComponentType)
            Array.set(primitiveArray, i, element)
        }
        return primitiveArray
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
                    converter.convertOrNull(it, fromComponentType, toComponentType)
                }
            LinkedList::class.java ->
                from.mapTo(LinkedList()) {
                    converter.convertOrNull(it, fromComponentType, toComponentType)
                }
            Collection::class.java, Set::class.java, LinkedHashSet::class.java ->
                from.mapTo(LinkedHashSet()) {
                    converter.convertOrNull(it, fromComponentType, toComponentType)
                }
            HashSet::class.java ->
                from.mapTo(HashSet()) {
                    converter.convertOrNull(it, fromComponentType, toComponentType)
                }
            TreeSet::class.java ->
                from.mapTo(TreeSet()) {
                    converter.convertOrNull(it, fromComponentType, toComponentType)
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
 * * [MutableSetMap], [MutableListMap];
 */
open class BeanConvertHandler @JvmOverloads constructor(
    private val beanCreator: BeanCreator = BeanCreator.DEFAULT,
    private val beanResolver: BeanResolver = BeanResolver.defaultResolver()
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
            Hashtable::class.java ->
                toHashtable(from, toType, context.converter)
            MutableSetMap::class.java ->
                toMutableSetMap(from, toType, context.converter)
            MutableListMap::class.java ->
                toMutableListMap(from, toType, context.converter)
            SetMap::class.java ->
                toSetMap(from, toType, context.converter)
            ListMap::class.java ->
                toListMap(from, toType, context.converter)
            else ->
                toObject(from, toType, context.converter)
        }
    }

    private fun toLinkedHashMap(from: Any, toType: Type, converter: Converter): LinkedHashMap<Any, Any?> {
        return from.copyProperties(LinkedHashMap(), toType, beanResolver, converter)
    }

    private fun toHashMap(from: Any, toType: Type, converter: Converter): HashMap<Any, Any?> {
        return from.copyProperties(HashMap(), toType, beanResolver, converter)
    }

    private fun toTreeMap(from: Any, toType: Type, converter: Converter): TreeMap<Any, Any?> {
        return from.copyProperties(TreeMap(), toType, beanResolver, converter)
    }

    private fun toConcurrentHashMap(from: Any, toType: Type, converter: Converter): ConcurrentHashMap<Any, Any?> {
        return from.copyProperties(ConcurrentHashMap(), toType, beanResolver, converter)
    }

    private fun toHashtable(from: Any, toType: Type, converter: Converter): Hashtable<Any, Any?> {
        return from.copyProperties(Hashtable(), toType, beanResolver, converter)
    }

    private fun toMutableSetMap(from: Any, toType: Type, converter: Converter): MutableSetMap<Any, Any?> {
        return from.copyProperties(newMutableSetMap(), toType, beanResolver, converter)
    }

    private fun toMutableListMap(from: Any, toType: Type, converter: Converter): MutableListMap<Any, Any?> {
        return from.copyProperties(mutableListMap(), toType, beanResolver, converter)
    }

    private fun toSetMap(from: Any, toType: Type, converter: Converter): SetMap<Any, Any?> {
        return toMutableSetMap(from, toType, converter).toSetMap()
    }

    private fun toListMap(from: Any, toType: Type, converter: Converter): ListMap<Any, Any?> {
        return toMutableListMap(from, toType, converter).toListMap()
    }

    private fun toObject(from: Any, toType: Type, converter: Converter): Any? {
        if (toType !is Class<*>) {
            return null
        }
        val builder = beanCreator.newBuilder(toType)
        from.copyProperties(builder, beanResolver, converter)
        return beanCreator.build(builder)
    }
}

open class NullConvertHandler(private val preventNull: Boolean) : ConvertHandler {
    override fun convert(from: Any?, fromType: Type, toType: Type, context: ConvertContext): Any? {
        if (from !== null) {
            return null
        }
        return if (preventNull) ConvertHandler.NULL else null
    }
}