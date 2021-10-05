package xyz.srclab.common.convert

import xyz.srclab.common.base.asAny
import xyz.srclab.common.collect.asToList
import xyz.srclab.common.collect.isEmpty
import xyz.srclab.common.collect.plusBefore
import xyz.srclab.common.convert.Converter.Companion.extend
import xyz.srclab.common.reflect.TypeRef
import java.lang.reflect.Type

/**
 * Interface for type conversion.
 *
 * This interface use a chain of [ConvertHandler] to do each conversion,
 * use [extend] to add a custom [ConvertHandler] before old handlers.
 *
 * @see ConvertHandler
 */
interface Converter {

    val convertHandlers: List<ConvertHandler>

    /**
     * Converts [from] to [toType].
     *
     * If [from] is null, type of [from] will be seen as [Any].
     */
    @Throws(UnsupportedConvertException::class)
    fun <T> convert(from: Any?, toType: Class<T>): T {
        return convert(from, toType as Type)
    }

    /**
     * Converts [from] to [toType].
     *
     * If [from] is null, type of [from] will be seen as [Any].
     */
    @Throws(UnsupportedConvertException::class)
    fun <T> convert(from: Any?, toType: Type): T {
        return convert(from, from?.javaClass ?: Any::class.java, toType)
    }

    /**
     * Converts [from] (as type of [fromType]) to [toType].
     */
    @Throws(UnsupportedConvertException::class)
    fun <T> convert(from: Any?, fromType: Type, toType: Type): T {

        class ConvertContextImpl : ConvertContext {
            override val converter: Converter = this@Converter
        }

        val context = ConvertContextImpl()
        for (convertHandler in convertHandlers) {
            val result = convertHandler.convert(from, fromType, toType, context)
            if (result === ConvertHandler.NULL) {
                return null.asAny()
            }
            if (result !== null) {
                return result.asAny()
            }
        }
        throw UnsupportedConvertException("Unsupported converting: $from to $toType.")
    }

    /**
     * Converts [from] to type from [toTypeRef].
     *
     * If [from] is null, type of [from] will be seen as [Any].
     */
    @Throws(UnsupportedConvertException::class)
    fun <T> convert(from: Any?, toTypeRef: TypeRef<T>): T {
        return convert(from, toTypeRef.type)
    }

    /**
     * Converts [from] (as type from [fromTypeRef]) to type from [toTypeRef].
     */
    @Throws(UnsupportedConvertException::class)
    fun <F, R> convert(from: Any?, fromTypeRef: TypeRef<F>, toTypeRef: TypeRef<R>): R {
        return convert(from, fromTypeRef.type, toTypeRef.type)
    }

    companion object {

        @JvmField
        val DEFAULT: Converter = newConverter(ConvertHandler.DEFAULTS)

        @JvmField
        val COMPATIBLE: Converter = newConverter(listOf(CompatibleConvertHandler))

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