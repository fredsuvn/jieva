package xyz.srclab.common.convert

import xyz.srclab.common.collect.asToList
import xyz.srclab.common.collect.isEmpty
import xyz.srclab.common.collect.plusBefore
import xyz.srclab.common.convert.Converter.Companion.withPreConvertHandler
import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.asAny
import xyz.srclab.common.reflect.TypeRef
import java.lang.reflect.Type

/**
 * Interface for type conversion.
 *
 * By default this interface use a chain of [ConvertHandler] to do with conversion,
 * thus a simply way to custom a [Converter] is use [withPreConvertHandler] to add a custom [ConvertHandler].
 *
 * @see ConvertHandler
 */
interface Converter {

    @get:JvmName("convertHandlers")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val convertHandlers: List<ConvertHandler>

    /**
     * If [from] is null, from type will be seem as [Any].
     */
    @Throws(UnsupportedConvertException::class)
    fun <T> convert(from: Any?, toType: Class<T>): T {
        return convert(from, toType as Type)
    }

    /**
     * If [from] is null, from type will be seem as [Any].
     */
    @Throws(UnsupportedConvertException::class)
    fun <T> convert(from: Any?, toType: Type): T {
        return convert(from, from?.javaClass ?: Any::class.java, toType)
    }

    @Throws(UnsupportedConvertException::class)
    fun <T> convert(from: Any?, fromType: Type, toType: Type): T

    /**
     * If [from] is null, from type will be seem as [Any].
     */
    @Throws(UnsupportedConvertException::class)
    @JvmDefault
    fun <T> convert(from: Any?, toTypeRef: TypeRef<T>): T {
        return convert(from, toTypeRef.type)
    }

    @Throws(UnsupportedConvertException::class)
    @JvmDefault
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
            return ConverterImpl(convertHandlers.asToList())
        }

        /**
         * Returns a new [Converter] consists of [preConvertHandler] followed by [this]'s old [ConvertHandler]s.
         */
        @JvmStatic
        fun Converter.withPreConvertHandler(preConvertHandler: ConvertHandler): Converter {
            return newConverter(convertHandlers.plusBefore(0, preConvertHandler))
        }

        private class ConverterImpl(
            override val convertHandlers: List<ConvertHandler>,
        ) : Converter {

            override fun <T> convert(from: Any?, fromType: Type, toType: Type): T {
                val chain = ConvertChainImpl()
                return chain.next(from, fromType, toType).asAny()
            }

            private inner class ConvertChainImpl : ConvertChain {

                private var handlerIterator = convertHandlers.iterator()

                override val converter: Converter = this@ConverterImpl

                override fun next(from: Any?, fromType: Type, toType: Type): Any? {
                    if (!handlerIterator.hasNext()) {
                        throw UnsupportedConvertException("$fromType to $toType.")
                    }
                    val handler = handlerIterator.next()
                    return handler.convert(from, fromType, toType, this)
                }

                override fun restart(from: Any?, fromType: Type, toType: Type): Any? {
                    handlerIterator = convertHandlers.iterator()
                    return handlerIterator.next().convert(from, fromType, toType, this)
                }
            }
        }
    }
}

open class UnsupportedConvertException : RuntimeException {
    constructor() : super()
    constructor(message: CharSequence?) : super(message?.toString())
    constructor(message: CharSequence?, cause: Throwable?) : super(message?.toString(), cause)
    constructor(cause: Throwable?) : super(cause)
}