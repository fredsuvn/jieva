package xyz.srclab.common.convert

import xyz.srclab.common.base.asTyped
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

    val convertHandlers: List<ConvertHandler>

    @Throws(ConvertException::class)
    fun <T : Any> convert(from: Any?, toType: Class<T>): T {
        return convertOrNull(from, toType) ?: throw ConvertException(from, toType)
    }

    @Throws(ConvertException::class)
    fun <T : Any> convert(from: Any?, toType: Type): T {
        return convertOrNull(from, toType) ?: throw ConvertException(from, toType)
    }

    @Throws(ConvertException::class)
    fun <T : Any> convert(from: Any?, toType: TypeRef<T>): T {
        return convert(from, toType.type)
    }

    @Throws(ConvertException::class)
    fun <T : Any> convert(from: Any?, fromType: Type, toType: Class<T>): T {
        return convertOrNull(from, fromType, toType) ?: throw ConvertException(fromType, toType)
    }

    @Throws(ConvertException::class)
    fun <T : Any> convert(from: Any?, fromType: Type, toType: Type): T {
        return convertOrNull(from, fromType, toType) ?: throw ConvertException(fromType, toType)
    }

    fun <T : Any> convertOrNull(from: Any?, toType: Class<T>): T? {
        return convertOrNull(from, from?.javaClass ?: Any::class.java, toType)
    }

    fun <T : Any> convertOrNull(from: Any?, toType: Type): T? {
        return convertOrNull(from, from?.javaClass ?: Any::class.java, toType)
    }

    fun <T : Any> convertOrNull(from: Any?, toType: TypeRef<T>): T? {
        return convertOrNull(from, toType.type)
    }

    fun <T : Any> convertOrNull(from: Any?, fromType: Type, toType: Class<T>): T? {
        return convertOrNull(from, fromType, toType as Type)
    }

    fun <T : Any> convertOrNull(from: Any?, fromType: Type, toType: Type): T? {
        val result: Any? = convertOrNull0(from, fromType, toType)
        if (result === ConvertHandler.NULL) {
            return null
        }
        return result.asTyped()
    }

    private fun convertOrNull0(from: Any?, fromType: Type, toType: Type): Any? {
        val context = ConvertContext(this)
        for (convertHandler in convertHandlers) {
            val result = convertHandler.convert(from, fromType, toType, context)
            if (result !== null) {
                return result
            }
        }
        return null
    }

    companion object {

        private var defaultConverter: Converter = newConverter(ConvertHandler.DEFAULT_HANDLERS)

        @JvmStatic
        fun defaultConverter(): Converter {
            return defaultConverter
        }

        @JvmStatic
        fun setDefaultConverter(converter: Converter) {
            this.defaultConverter = converter
        }

        @JvmStatic
        fun newConverter(
            convertHandlers: Iterable<ConvertHandler>
        ): Converter {
            if (convertHandlers.isEmpty()) {
                throw IllegalArgumentException("Convert handler list cannot be empty.")
            }
            return object : Converter {
                override val convertHandlers: List<ConvertHandler> = convertHandlers.asToList()
            }
        }

        /**
         * Returns a new [Converter] consists of given [handler] followed by existed [ConvertHandler]s.
         */
        @JvmStatic
        fun Converter.extend(handler: ConvertHandler): Converter {
            return newConverter(this.convertHandlers.plusBefore(0, handler))
        }
    }
}