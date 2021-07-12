package xyz.srclab.common.convert

import xyz.srclab.common.invoke.Invoke
import xyz.srclab.common.invoke.Invoker
import xyz.srclab.common.reflect.TypeRef
import java.lang.reflect.Type

/**
 * Fast version of [Converter].
 *
 * By default, it compute `fromType` and `toType`'s hash codes to find exactly matched [FastConvertHandler].
 *
 * @see FastConvertHandler
 */
interface FastConverter {

    /**
     * Convert from [from] to [R].
     */
    @Throws(UnsupportedConvertException::class)
    @JvmDefault
    fun <T : Any, R : Any> convert(from: T, toType: Class<R>): R {
        return convert(from, toType as Type)
    }

    /**
     * Convert from [from] to [R].
     */
    @Throws(UnsupportedConvertException::class)
    @JvmDefault
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
    @JvmDefault
    fun <T : Any, R : Any> convert(from: T, toType: TypeRef<R>): R {
        return convert(from, toType.type)
    }

    /**
     * Convert from [from] to [R].
     */
    @Throws(UnsupportedConvertException::class)
    @JvmDefault
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

            private val handlerMap: Map<Pair<Type, Type>, Invoke> = run {
                val map = HashMap<Pair<Type, Type>, Invoke>()
                for (handler in handlers) {
                    val methods = handler.javaClass.methods
                    for (method in methods) {
                        val annotation = method.getAnnotation(FastConvertHandler::class.java)
                        if (annotation === null) {
                            continue
                        }
                        if (method.parameterCount != 1 || method.returnType == Void::class.java) {
                            continue
                        }
                        map[method.genericParameterTypes[0] to method.genericReturnType] =
                                Invoker.forMethod(method).invokeWith(handler)
                    }
                }
                map
            }

            override fun <T : Any, R : Any> convert(from: T, fromType: Type, toType: Type): R {
                val invoke = handlerMap[fromType to toType]
                if (invoke === null) {
                    throw UnsupportedConvertException("$fromType to $toType")
                }
                return invoke.start(from)
            }
        }
    }
}