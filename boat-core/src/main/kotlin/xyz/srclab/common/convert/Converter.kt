package xyz.srclab.common.convert

import xyz.srclab.common.lang.Default
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.asAny
import xyz.srclab.common.collect.plusBefore
import xyz.srclab.common.reflect.TypeRef
import java.lang.reflect.Type

/**
 * Converter, global type convert interface, to convert object to another type.
 *
 * @see ConvertHandler
 * @see NopConvertHandler
 * @see WildcardTypeConvertHandler
 * @see CharsConvertHandler
 * @see NumberAndPrimitiveConvertHandler
 * @see DateTimeConvertHandler
 * @see IterableConvertHandler
 * @see BeanConvertHandler
 */
interface Converter {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val convertHandlers: List<ConvertHandler>
        @JvmName("convertHandlers") get

    @JvmDefault
    fun <T> convert(from: Any?, toType: Class<T>): T {
        for (handler in convertHandlers) {
            val result = handler.convert(from, toType, this)
            if (result === Default.NULL) {
                return null.asAny()
            }
            if (result !== null) {
                return result.asAny()
            }
        }
        throw UnsupportedOperationException("Cannot convert $from to $toType.")
    }

    @JvmDefault
    fun <T> convert(from: Any?, toType: Type): T {
        for (handler in convertHandlers) {
            val result = handler.convert(from, toType, this)
            if (result === Default.NULL) {
                return null.asAny()
            }
            if (result !== null) {
                return result.asAny()
            }
        }
        throw UnsupportedOperationException("Cannot convert $from to $toType.")
    }

    @JvmDefault
    fun <T> convert(from: Any?, toTypeRef: TypeRef<T>): T {
        return convert(from, toTypeRef.type)
    }

    @JvmDefault
    fun <T> convert(from: Any?, fromType: Type, toType: Type): T {
        for (handler in convertHandlers) {
            val result = handler.convert(from, fromType, toType, this)
            if (result === Default.NULL) {
                return null.asAny()
            }
            if (result !== null) {
                return result.asAny()
            }
        }
        throw UnsupportedOperationException("Cannot convert $fromType to $toType.")
    }

    @JvmDefault
    fun <T> convert(from: Any?, fromTypeRef: TypeRef<T>, toTypeRef: TypeRef<T>): T {
        return convert(from, fromTypeRef.type, toTypeRef.type)
    }

    @JvmDefault
    fun withPreConvertHandler(preConvertHandler: ConvertHandler): Converter {
        return newConverter(convertHandlers.plusBefore(0, preConvertHandler))
    }

    companion object {

        @JvmField
        val DEFAULT: Converter = newConverter(ConvertHandler.DEFAULTS)

        @JvmField
        val EMPTY: Converter = newConverter(emptyList())

        @JvmField
        val NOP: Converter = newConverter(listOf(NopConvertHandler))

        @JvmStatic
        fun newConverter(convertHandlers: Iterable<ConvertHandler>): Converter {
            return object : Converter {
                override val convertHandlers = convertHandlers.toList()
            }
        }
    }
}

