package xyz.srclab.common.convert

import xyz.srclab.common.collect.ListMap
import xyz.srclab.common.collect.MutableListMap
import xyz.srclab.common.collect.asToList
import xyz.srclab.common.collect.plusBefore
import xyz.srclab.common.convert.Converter.Companion.withPreConvertHandler
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.Next
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
 */
interface Converter {

    @get:JvmName("convertHandlers")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val convertHandlers: List<ConvertHandler>

    @get:JvmName("failedHandler")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val failedHandler: ConvertHandler

    fun <T> convert(from: Any?, toType: Class<T>): T

    fun <T> convert(from: Any?, toType: Type): T

    fun <T> convert(from: Any?, fromType: Type, toType: Type): T

    @JvmDefault
    fun <T> convert(from: Any?, toTypeRef: TypeRef<T>): T {
        return convert(from, toTypeRef.type)
    }

    @JvmDefault
    fun <T> convert(from: Any?, fromTypeRef: TypeRef<T>, toTypeRef: TypeRef<T>): T {
        return convert(from, fromTypeRef.type, toTypeRef.type)
    }

    companion object {

        @JvmField
        val DEFAULT: Converter = newConverter(ConvertHandler.DEFAULTS)

        @JvmField
        val COMPATIBLE: Converter = newConverter(listOf(CompatibleConvertHandler))

        @JvmOverloads
        @JvmStatic
        fun newConverter(
            convertHandlers: Iterable<ConvertHandler>,
            failedHandler: ConvertHandler = DefaultFailedConvertHandler
        ): Converter {
            return ConverterImpl(convertHandlers.asToList(), failedHandler)
        }

        /**
         * Returns a new [Converter] consists of [preConvertHandler] followed by [this]'s old [ConvertHandler]s.
         */
        @JvmStatic
        fun Converter.withPreConvertHandler(preConvertHandler: ConvertHandler): Converter {
            return newConverter(convertHandlers.plusBefore(0, preConvertHandler), failedHandler)
        }

        private class ConverterImpl(
            override val convertHandlers: List<ConvertHandler>,
            override val failedHandler: ConvertHandler
        ) : Converter {

            private val toTypeFastHitMap: ListMap<Type, ConvertHandler> = run {
                val toTypeFastHitMap: MutableListMap<Type, ConvertHandler> = MutableListMap.newMutableListMap()
                for (handler in convertHandlers) {
                    for (type in handler.toTypeFastHit) {
                        toTypeFastHitMap.add(type, handler)
                    }
                }
                toTypeFastHitMap.toListMap()
            }

            override fun <T> convert(from: Any?, toType: Class<T>): T {
                return doConvert(toType) { it.convert(from, toType, this) }
            }

            override fun <T> convert(from: Any?, toType: Type): T {
                return doConvert(toType) { it.convert(from, toType, this) }
            }

            override fun <T> convert(from: Any?, fromType: Type, toType: Type): T {
                return doConvert(toType) { it.convert(from, fromType, toType, this) }
            }

            private inline fun <T> doConvert(toType: Type, action: (ConvertHandler) -> Any?): T {
                val hitHandlers = toTypeFastHitMap[toType] ?: emptyList()
                for (handler in hitHandlers) {
                    val result = action(handler)
                    if (result is Next) {
                        when (result) {
                            Next.CONTINUE -> continue
                            Next.BREAK -> return action(failedHandler).asAny()
                        }
                    }
                    return result.asAny()
                }
                for (handler in convertHandlers) {
                    if (hitHandlers.contains(handler)) {
                        continue
                    }
                    val result = action(handler)
                    if (result is Next) {
                        when (result) {
                            Next.CONTINUE -> continue
                            Next.BREAK -> return action(failedHandler).asAny()
                        }
                    }
                    return result.asAny()
                }
                return action(failedHandler).asAny()
            }
        }
    }
}