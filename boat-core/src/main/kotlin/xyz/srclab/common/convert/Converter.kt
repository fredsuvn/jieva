package xyz.srclab.common.convert

import xyz.srclab.common.base.Val
import xyz.srclab.common.base.Val.Companion.toVal
import xyz.srclab.common.base.asType
import xyz.srclab.common.collect.asToList
import xyz.srclab.common.collect.isEmpty
import xyz.srclab.common.collect.plusBefore
import xyz.srclab.common.convert.Converter.Companion.extend
import xyz.srclab.common.reflect.TypeRef
import java.lang.reflect.Type

/**
 * Interface for type conversion.
 *
 * By default, this interface use a chain of [ConvertHandler] to do each conversion,
 * use [extend] to add a custom [ConvertHandler] before old handlers.
 *
 * @see ConvertHandler
 */
interface Converter {

    /**
     * Handlers of this converter.
     */
    val convertHandlers: List<ConvertHandler>

    /**
     * Converts [from] to [toType], the result is not null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convert(from: Any?, toType: Class<T>): T {
        return convertOrNull(from, toType) ?: throw ConvertException(from, toType)
    }

    /**
     * Converts [from] to [toType], the result is not null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convert(from: Any?, toType: Type): T {
        return convertOrNull(from, toType) ?: throw ConvertException(from, toType)
    }

    /**
     * Converts [from] to [toType], the result is not null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convert(from: Any?, toType: TypeRef<T>): T {
        return convertOrNull(from, toType) ?: throw ConvertException(from, toType.type)
    }

    /**
     * Converts [from] of which type is [fromType] to [toType], the result is not null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convert(from: Any?, fromType: Type, toType: Class<T>): T {
        return convertOrNull(from, fromType, toType) ?: throw ConvertException(fromType, toType)
    }

    /**
     * Converts [from] of which type is [fromType] to [toType], the result is not null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convert(from: Any?, fromType: Type, toType: Type): T {
        return convertOrNull(from, fromType, toType) ?: throw ConvertException(fromType, toType)
    }

    /**
     * Converts [from] of which type is [fromType] to [toType], the result is not null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convert(from: Any?, fromType: Type, toType: TypeRef<T>): T {
        return convertOrNull(from, fromType, toType) ?: throw ConvertException(fromType, toType.type)
    }

    /**
     * Converts [from] to type of [defaultValue], return [defaultValue] if the result is not null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convertOrDefault(from: Any?, defaultValue: T): T {
        return convertOrNull(from, defaultValue.javaClass) ?: defaultValue
    }

    /**
     * Converts [from] of which type is [fromType] to type of [defaultValue],
     * return [defaultValue] if the result is not null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convertOrDefault(from: Any?, fromType: Type, defaultValue: T): T {
        return convertOrNull(from, fromType, defaultValue.javaClass) ?: defaultValue
    }

    /**
     * Converts [from] to [toType], the result may be null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convertOrNull(from: Any?, toType: Class<T>): T? {
        return convertOrNull(from, from?.javaClass ?: Any::class.java, toType)
    }

    /**
     * Converts [from] to [toType], the result may be null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convertOrNull(from: Any?, toType: Type): T? {
        return convertOrNull(from, from?.javaClass ?: Any::class.java, toType)
    }

    /**
     * Converts [from] to [toType], the result may be null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convertOrNull(from: Any?, toType: TypeRef<T>): T? {
        return convertOrNull(from, toType.type)
    }

    /**
     * Converts [from] of which type is [fromType] to [toType], the result may be null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convertOrNull(from: Any?, fromType: Type, toType: Class<T>): T? {
        return convertOrNull(from, fromType, toType as Type)
    }

    /**
     * Converts [from] of which type is [fromType] to [toType], the result may be null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convertOrNull(from: Any?, fromType: Type, toType: Type): T? {
        return convertOrNull0(from, fromType, toType).asType()
    }

    /**
     * Converts [from] of which type is [fromType] to [toType], the result may be null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convertOrNull(from: Any?, fromType: Type, toType: TypeRef<T>): T? {
        return convertOrNull(from, fromType, toType.type)
    }

    /**
     * Converts [from] to [toType], the result is not null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convertVal(from: Any?, toType: Class<T>): Val<T?> {
        return convertOrNull(from, toType).toVal()
    }

    /**
     * Converts [from] to [toType], the result is not null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convertVal(from: Any?, toType: Type): Val<T?> {
        return convertOrNull<T>(from, toType).toVal()
    }

    /**
     * Converts [from] to [toType], the result is not null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convertVal(from: Any?, toType: TypeRef<T>): Val<T?> {
        return convertOrNull(from, toType).toVal()
    }

    /**
     * Converts [from] of which type is [fromType] to [toType], the result is not null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convertVal(from: Any?, fromType: Type, toType: Class<T>): Val<T?> {
        return convertOrNull(from, fromType, toType).toVal()
    }

    /**
     * Converts [from] of which type is [fromType] to [toType], the result is not null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convertVal(from: Any?, fromType: Type, toType: Type): Val<T?> {
        return convertOrNull<T>(from, fromType, toType).toVal()
    }

    /**
     * Converts [from] of which type is [fromType] to [toType], the result is not null.
     */
    @Throws(ConvertException::class)
    fun <T : Any> convertVal(from: Any?, fromType: Type, toType: TypeRef<T>): Val<T?> {
        return convertOrNull(from, fromType, toType).toVal()
    }

    private fun convertOrNull0(from: Any?, fromType: Type, toType: Type): Any? {
        for (convertHandler in convertHandlers) {
            val result = convertHandler.convert(from, fromType, toType, this)
            if (result === ConvertHandler.NULL) {
                return null
            }
            if (result !== null) {
                return result
            }
        }
        return null
    }

    companion object {

        /**
         * Creates a new [Converter] with given [convertHandlers].
         */
        @JvmOverloads
        @JvmStatic
        fun newConverter(
            convertHandlers: Iterable<ConvertHandler> = ConvertHandler.DEFAULT_HANDLERS
        ): Converter {
            if (convertHandlers.isEmpty()) {
                throw IllegalArgumentException("Convert handlers cannot be empty!")
            }
            return ConverterImpl(convertHandlers.asToList())
        }

        /**
         * Returns a new [Converter] consists of given [handler] followed by existing [ConvertHandler]s.
         */
        @JvmStatic
        fun Converter.extend(handler: ConvertHandler): Converter {
            return newConverter(this.convertHandlers.plusBefore(0, handler))
        }

        private class ConverterImpl(
            override val convertHandlers: List<ConvertHandler>
        ) : Converter
    }
}