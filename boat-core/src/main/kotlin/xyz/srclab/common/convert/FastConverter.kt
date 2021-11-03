package xyz.srclab.common.convert

import xyz.srclab.common.invoke.Invoker.Companion.toInvoker
import xyz.srclab.common.reflect.TypeRef
import java.lang.reflect.Type

/**
 * Fast version of [Converter].
 *
 * By default, it uses a set of fast-convert-handlers -- which have methods annotated by [FastConvertMethod].
 * Each convert method has one parameter as `fromType`, and returns `toType` of value.
 *
 * @see FastConvertMethod
 */
interface FastConverter {

    /**
     * Convert from [from] to [R].
     */
    @Throws(UnsupportedConvertException::class)
    fun <T : Any, R : Any> convert(from: T, toType: Class<R>): R {
        return convert(from, toType as Type)
    }

    /**
     * Convert from [from] to [R].
     */
    @Throws(UnsupportedConvertException::class)
    fun <T : Any, R : Any> convert(from: T, toType: Type): R {
        return convert(from, from.javaClass, toType)
    }

    /**
     * Convert from [from] to [R].
     */
    @Throws(UnsupportedConvertException::class)
    fun <T : Any, R : Any> convert(from: T, fromType: Type, toType: Type): R

    /**
     * Convert from [from] to [R].
     */
    @Throws(UnsupportedConvertException::class)
    fun <T : Any, R : Any> convert(from: T, toType: TypeRef<R>): R {
        return convert(from, toType.type)
    }

    /**
     * Convert from [from] to [R].
     */
    @Throws(UnsupportedConvertException::class)
    fun <T : Any, R : Any> convert(from: T, fromType: TypeRef<T>, toType: TypeRef<R>): R {
        return convert(from, fromType.type, toType.type)
    }

    companion object {

        @JvmStatic
        fun newFastConverter(handler: Any): FastConverter {
            return newFastConverter(listOf(handler))
        }

        @JvmStatic
        fun newFastConverter(handlers: Iterable<Any>): FastConverter {
            return FastConverterImpl(handlers)
        }

        private class FastConverterImpl(
            handlers: Iterable<Any>
        ) : FastConverter {

            private val handlerMap: Map<Pair<Type, Type>, Invoke> by lazy {
                val map = HashMap<Pair<Type, Type>, Invoke>()
                for (handler in handlers) {
                    val methods = handler.javaClass.methods
                    for (method in methods) {
                        val annotation = method.getAnnotation(FastConvertMethod::class.java)
                        if (annotation === null || method.isBridge) {
                            continue
                        }
                        if (method.parameterCount != 1 || method.returnType == Void::class.java) {
                            throw IllegalArgumentException(
                                "Fast convert method must have only one parameter and a non-void type of return value."
                            )
                        }
                        map[method.genericParameterTypes[0] to method.genericReturnType] =
                            method.toInvoker().prepareFor(handler)
                    }
                }
                map
            }

            override fun <T : Any, R : Any> convert(from: T, fromType: Type, toType: Type): R {
                val invoke = handlerMap[fromType to toType]
                if (invoke === null) {
                    throw UnsupportedConvertException("$fromType to $toType.")
                }
                return invoke.start(from)
            }
        }
    }
}