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
 * By default, a [Converter] uses a group of [ConvertHandler]s to do convert action for each handler.
 * If a handler returns [Next.CONTINUE], the converter will call next handler;
 * if returns [Next.BREAK], the converter will fail to convert.
 *
 * @see Converter
 * @see CompatibleConvertHandler
 * @see LowerBoundConvertHandler
 * @see CharsConvertHandler
 * @see NumberBooleanConvertHandler
 * @see DateTimeConvertHandler
 * @see IterableConvertHandler
 * @see BeanConvertHandler
 * @see DefaultFailedConvertHandler
 */
interface ConvertHandler {

    /**
     * Fast hit for `toType`.
     *
     * Default [Converter] implementation will try to find associate handler by this, and ask for conversation first.
     */
    @get:JvmName("toTypeFastHit")
    @JvmDefault
    @Suppress(INAPPLICABLE_JVM_NAME)
    val toTypeFastHit: List<Type>
        get() {
            return emptyList()
        }

    /**
     * Do convert from [from] to [toType].
     *
     * Note [Next] type is a special type that cannot be supported.
     * Returns [Next.CONTINUE] means current conversation is unsupported but suggest to ask for next handler;
     * returns [Next.BREAK] means current conversation is unsupported and suggest to break immediately.
     */
    fun <T> convert(from: Any?, toType: Class<T>, converter: Converter): Any?

    /**
     * Do convert from [from] to [toType].
     *
     * Note [Next] type is a special type that cannot be supported.
     * Returns [Next.CONTINUE] means current conversation is unsupported but suggest to ask for next handler;
     * returns [Next.BREAK] means current conversation is unsupported and suggest to break immediately.
     */
    fun convert(from: Any?, toType: Type, converter: Converter): Any?

    /**
     * Do convert from [from] to [toType].
     *
     * Note [Next] type is a special type that cannot be supported.
     * Returns [Next.CONTINUE] means current conversation is unsupported but suggest to ask for next handler;
     * returns [Next.BREAK] means current conversation is unsupported and suggest to break immediately.
     */
    fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any?

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
 * else return [Next.CONTINUE].
 */
object CompatibleConvertHandler : ConvertHandler {

    override fun <T> convert(from: Any?, toType: Class<T>, converter: Converter): Any {
        if (from === null) {
            return Next.CONTINUE
        }
        if (toType == Any::class.java) {
            return from
        }
        if (toType.isEnum) {
            val enumValue = toType.valueOfEnumIgnoreCaseOrNull<Enum<*>>(from.toString())
            return if (enumValue !== null) {
                enumValue
            } else {
                Next.CONTINUE
            }
        }
        val fromClass = from.javaClass
        if (fromClass == toType || toType.isAssignableFrom(fromClass)) {
            return from
        }
        return Next.CONTINUE
    }

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        return if (toType is Class<*>) convert(from, toType, converter) else Next.CONTINUE
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        if (fromType == toType || toType == Any::class.java) {
            return from
        }
        if (toType is Class<*>) {
            return convert(from, toType, converter)
        }
        return Next.CONTINUE
    }
}

/**
 * Returns lower bound of `toType` with [lowerBound] for [WildcardType] and [TypeVariable].
 */
object LowerBoundConvertHandler : ConvertHandler {

    override fun <T> convert(from: Any?, toType: Class<T>, converter: Converter): Any {
        return Next.CONTINUE
    }

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        return when (toType) {
            is WildcardType -> {
                val lowerBound = toType.lowerBound
                return if (lowerBound === null) {
                    Next.CONTINUE
                } else {
                    converter.convert(from, lowerBound)
                }
            }
            else -> Next.CONTINUE
        }
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        return when (toType) {
            is WildcardType -> {
                val lowerBound = toType.lowerBound
                return if (lowerBound === null) {
                    Next.CONTINUE
                } else {
                    converter.convert(from, fromType, lowerBound)
                }
            }
            else -> Next.CONTINUE
        }
    }
}

/**
 * Base [ConvertHandler] of which `fromType` and `toType` are [Class], else return [Next.CONTINUE].
 */
abstract class AbstractClassConvertHandler : ConvertHandler {

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        return if (toType is Class<*>) convert(from, toType, converter) else Next.CONTINUE
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        return if (toType is Class<*>) convert(from, toType, converter) else Next.CONTINUE
    }
}

/**
 * Supports convert `from` to types:
 *
 * * [String], [StringBuilder], [StringBuffer];
 * * [CharArray], [ByteArray], [Array<Char>], [Array<Byte>];
 */
object CharsConvertHandler : AbstractClassConvertHandler() {

    override val toTypeFastHit: List<Type> = listOf(
        String::class.java,
        CharSequence::class.java,
        StringBuilder::class.java,
        StringBuffer::class.java,
        CharArray::class.java,
        ByteArray::class.java,
    )

    override fun <T> convert(from: Any?, toType: Class<T>, converter: Converter): Any {
        if (from !== null && from.javaClass == toType) {
            return from
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
                else -> Next.CONTINUE
            }
            else -> Next.CONTINUE
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
object NumberBooleanConvertHandler : AbstractClassConvertHandler() {

    override val toTypeFastHit: List<Type> = listOf(
        Boolean::class.javaPrimitiveType!!, Boolean::class.javaObjectType,
        Byte::class.javaPrimitiveType!!, Byte::class.javaObjectType,
        Short::class.javaPrimitiveType!!, Short::class.javaObjectType,
        Char::class.javaPrimitiveType!!, Char::class.javaObjectType,
        Int::class.javaPrimitiveType!!, Int::class.javaObjectType,
        Long::class.javaPrimitiveType!!, Long::class.javaObjectType,
        Float::class.javaPrimitiveType!!, Float::class.javaObjectType,
        Double::class.javaPrimitiveType!!, Double::class.javaObjectType,
        BigInteger::class.javaObjectType,
        BigDecimal::class.javaObjectType,
        Number::class.javaObjectType,
    )

    override fun <T> convert(from: Any?, toType: Class<T>, converter: Converter): Any {
        if (from !== null && from.javaClass == toType) {
            return from
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
            else -> Next.CONTINUE
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
) : AbstractClassConvertHandler() {

    override val toTypeFastHit: List<Type> = listOf(
        Date::class.java,
        Instant::class.java,
        LocalDate::class.java,
        LocalTime::class.java,
        LocalDateTime::class.java,
        ZonedDateTime::class.java,
        OffsetDateTime::class.java,
        Duration::class.java,
        Temporal::class.java,
    )

    constructor() : this(
        dateFormat(),
        DateTimeFormatter.ISO_INSTANT,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        DateTimeFormatter.ISO_ZONED_DATE_TIME,
        DateTimeFormatter.ISO_OFFSET_DATE_TIME,
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ISO_LOCAL_TIME
    )

    override fun <T> convert(from: Any?, toType: Class<T>, converter: Converter): Any {
        if (from !== null && from.javaClass == toType) {
            return from
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
            else -> Next.CONTINUE
        }
    }

    companion object {

        @JvmField
        val DEFAULT: DateTimeConvertHandler = DateTimeConvertHandler()
    }
}

/**
 * Base [ConvertHandler] of which `fromType` and `toType` are [Class] or [Type].
 */
abstract class AbstractTypeConvertHandler : ConvertHandler {

    override fun <T> convert(from: Any?, toType: Class<T>, converter: Converter): Any? {
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

/**
 * Supports convert `from` to iterable types:
 *
 * * Array, [GenericArrayType];
 * * [Iterable], [Collection], [Set], [List];
 * * [HashSet], [LinkedHashSet], [TreeSet];
 */
object IterableConvertHandler : AbstractTypeConvertHandler() {

    override val toTypeFastHit: List<Type> = listOf(
        Iterable::class.java,
        Collection::class.java,
        Set::class.java,
        List::class.java,
        HashSet::class.java,
        LinkedHashSet::class.java,
        TreeSet::class.java,
    )

    override fun convertNull(toType: Type, converter: Converter): Any {
        return Next.CONTINUE
    }

    override fun convertNotNull(from: Any, fromType: Type, toType: Type, converter: Converter): Any {
        if (from.javaClass == toType) {
            return from
        }
        return when (toType) {
            is Class<*> -> {
                if (toType.isArray) {
                    val iterable = from.toIterable()
                    if (iterable === null) {
                        return Next.CONTINUE
                    }
                    return toArray(iterable, toType, converter)
                }
                if (Iterable::class.java.isAssignableFrom(toType)) {
                    val iterable = from.toIterable()
                    if (iterable === null) {
                        return Next.CONTINUE
                    }
                    return toIterable(iterable, toType.toIterableType(), converter)
                }
                return Next.CONTINUE
            }
            is GenericArrayType -> convertNotNull(from, fromType, toType.rawClass, converter)
            is ParameterizedType -> {
                val rawClass = toType.rawClass
                if (Iterable::class.java.isAssignableFrom(rawClass)) {
                    val iterable = from.toIterable()
                    if (iterable === null) {
                        return Next.CONTINUE
                    }
                    return toIterable(iterable, toType.toIterableType(), converter)
                }
                Next.CONTINUE
            }
            else -> Next.CONTINUE
        }
    }

    private fun toArray(from: Iterable<*>, toType: Class<*>, converter: Converter): Any {
        return from.toAnyArray(toType.componentType) {
            converter.convert(it, toType.componentType)
        }
    }

    private fun toIterable(from: Iterable<*>, iterableType: IterableType, converter: Converter): Any {
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
            else -> Next.CONTINUE
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
    private val newBeanProvider: NewBeanProvider = NewBeanProvider.DEFAULT,
    private val beanResolver: BeanResolver = BeanResolver.DEFAULT,
) : AbstractTypeConvertHandler() {

    override val toTypeFastHit: List<Type> = listOf(
        Map::class.java,
        HashMap::class.java,
        LinkedHashMap::class.java,
        TreeMap::class.java,
        ConcurrentHashMap::class.java,
        SetMap::class.java,
        MutableSetMap::class.java,
        ListMap::class.java,
        MutableListMap::class.java,
    )

    override fun convertNull(toType: Type, converter: Converter): Any {
        return Next.CONTINUE
    }

    override fun convertNotNull(from: Any, fromType: Type, toType: Type, converter: Converter): Any {
        if (toType is GenericArrayType) {
            return Next.CONTINUE
        }
        val toRawClass = toType.rawClassOrNull
        if (toRawClass === null) {
            return Next.CONTINUE
        }
        if (toRawClass.isArray) {
            return Next.CONTINUE
        }
        if (toRawClass.isEnum) {
            return Next.CONTINUE
        }
        return when (toRawClass) {
            Map::class.java, LinkedHashMap::class.java ->
                toLinkedHashMap(from, toType, converter)
            HashMap::class.java ->
                toHashMap(from, toType, converter)
            TreeMap::class.java ->
                toTreeMap(from, toType, converter)
            ConcurrentHashMap::class.java ->
                toConcurrentHashMap(from, toType, converter)
            SetMap::class.java ->
                toSetMap(from, toType, converter)
            MutableSetMap::class.java ->
                toMutableSetMap(from, toType, converter)
            ListMap::class.java ->
                toListMap(from, toType, converter)
            MutableListMap::class.java ->
                toMutableListMap(from, toType, converter)
            else ->
                toObject(from, toType, converter)
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
        val builder = newBeanProvider.newBuilder(toType)
        from.copyProperties(builder, newBeanProvider.builderType(builder, toType), beanResolver, converter)
        return newBeanProvider.build(builder, toType)
    }

    /**
     * Provider for create target bean which is convert to.
     */
    interface NewBeanProvider {

        /**
         * Returns builder of target bean.
         */
        fun newBuilder(toType: Type): Any

        /**
         * Builder's type.
         */
        fun builderType(builder: Any, toType: Type): Type

        /**
         * Builds the target bean.
         */
        fun build(builder: Any, toType: Type): Any

        companion object {

            /**
             * Default new bean operation, with `new Bean()` then `setXxx(value)` way.
             */
            val DEFAULT: NewBeanProvider = object : NewBeanProvider {

                override fun newBuilder(toType: Type): Any {
                    return toType.rawClass.newInstance()
                }

                override fun builderType(builder: Any, toType: Type): Type {
                    return toType
                }

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

/**
 * Default [ConvertHandler] for `failed conversation`, see [Converter.newConverter].
 */
object DefaultFailedConvertHandler : ConvertHandler {

    override fun <T> convert(from: Any?, toType: Class<T>, converter: Converter): Any? {
        throw UnsupportedOperationException("from: $from, to: $toType")
    }

    override fun convert(from: Any?, toType: Type, converter: Converter): Any? {
        throw UnsupportedOperationException("from: $from, to: $toType")
    }

    override fun convert(from: Any?, fromType: Type, toType: Type, converter: Converter): Any? {
        throw UnsupportedOperationException("from: $from, fromType: $fromType, to: $toType")
    }
}