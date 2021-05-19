package xyz.srclab.common.convert

import xyz.srclab.common.collect.plusBefore
import xyz.srclab.common.convert.Converter.Companion.withPreConvertHandler
import xyz.srclab.common.lang.Defaults
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.asAny
import xyz.srclab.common.reflect.TypeRef
import java.lang.reflect.Type

/**
 * Interface for type conversion.
 *
 * By default this interface use a serials of [ConvertHandler] to do with conversion,
 * thus a simply way to custom a [Converter] is use [withPreConvertHandler] to add a custom [ConvertHandler].
 *
 * @see ConvertHandler
 * @see CompatibleConvertHandler
 * @see WildcardTypeConvertHandler
 * @see CharsConvertHandler
 * @see NumberAndPrimitiveConvertHandler
 * @see DateTimeConvertHandler
 * @see CollectionConvertHandler
 * @see BeanConvertHandler
 */
interface Converter {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val convertHandlers: List<ConvertHandler>
        @JvmName("convertHandlers") get

    @JvmDefault
    fun <T> convert(from: Any?, toType: Class<T>): T {
        val result = doConvert { it.convert(from, toType, this) }
        if (result === null) {
            throw UnsupportedOperationException("Cannot convert $from to $toType.")
        }
        return finalResult(result).asAny()
    }

    @JvmDefault
    fun <T> convert(from: Any?, toType: Type): T {
        val result = doConvert { it.convert(from, toType, this) }
        if (result === null) {
            throw UnsupportedOperationException("Cannot convert $from to $toType.")
        }
        return finalResult(result).asAny()
    }

    @JvmDefault
    fun <T> convert(from: Any?, fromType: Type, toType: Type): T {
        val result = doConvert { it.convert(from, fromType, toType, this) }
        if (result === null) {
            throw UnsupportedOperationException("Cannot convert $from as $fromType to $toType.")
        }
        return finalResult(result).asAny()
    }

    @JvmDefault
    fun <T> convert(from: Any?, toTypeRef: TypeRef<T>): T {
        return convert(from, toTypeRef.type)
    }

    @JvmDefault
    fun <T> convert(from: Any?, fromTypeRef: TypeRef<T>, toTypeRef: TypeRef<T>): T {
        return convert(from, fromTypeRef.type, toTypeRef.type)
    }

    private inline fun <T> doConvert(action: (ConvertHandler) -> T): T? {
        for (handler in convertHandlers) {
            val result = action(handler)
            if (result !== null) {
                return result.asAny()
            }
        }
        return null
    }

    private fun <T> finalResult(result: T): T {
        if (result === Defaults.NULL) {
            return null.asAny()
        }
        return result
    }

    companion object {

        @JvmField
        val DEFAULT: Converter = newConverter(ConvertHandler.DEFAULTS)

        @JvmField
        val COMPATIBLE: Converter = newConverter(listOf(CompatibleConvertHandler))

        @JvmStatic
        fun newConverter(convertHandlers: Iterable<ConvertHandler>): Converter {
            return object : Converter {
                override val convertHandlers = convertHandlers.toList()
            }
        }

        /**
         * Returns a new [Converter] consists of [preConvertHandler] followed by [this]'s old [ConvertHandler]s.
         */
        @JvmStatic
        fun Converter.withPreConvertHandler(preConvertHandler: ConvertHandler): Converter {
            return newConverter(convertHandlers.plusBefore(0, preConvertHandler))
        }
    }
}