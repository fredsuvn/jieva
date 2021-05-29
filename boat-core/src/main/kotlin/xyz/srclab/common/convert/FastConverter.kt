package xyz.srclab.common.convert

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.asAny
import xyz.srclab.common.reflect.INHERITANCE_COMPARATOR
import xyz.srclab.common.reflect.TypeRef
import xyz.srclab.common.reflect.rawClass
import java.lang.reflect.Type

/**
 * Fast version of [Converter].
 *
 * It uses [FastConvertHandler] which must provide [FastConvertHandler.fromType] and [FastConvertHandler.toType]
 * to ensure supported `fromType` and `toType`.
 *
 * @see FastConvertHandler
 */
interface FastConverter {

    /**
     * Convert from [from] to [R].
     */
    @JvmDefault
    fun <T : Any, R : Any> convert(from: T, toType: Class<R>): R {
        return convert(from, toType as Type)
    }

    /**
     * Convert from [from] to [R].
     */
    @JvmDefault
    fun <T : Any, R : Any> convert(from: T, toType: Type): R {
        return convert(from, from.javaClass, toType)
    }

    /**
     * Convert from [from] to [R].
     */
    fun <T : Any, R : Any> convert(from: T, fromType: Type, toType: Type): R

    /**
     * Convert from [from] to [R].
     */
    @JvmDefault
    fun <T : Any, R : Any> convert(from: T, toType: TypeRef<R>): R {
        return convert(from, toType.type)
    }

    /**
     * Convert from [from] to [R].
     */
    @JvmDefault
    fun <T : Any, R : Any> convert(from: T, fromType: TypeRef<T>, toType: TypeRef<R>): R {
        return convert(from, fromType.type, toType.type)
    }

    companion object {

        @JvmOverloads
        @JvmStatic
        fun newFastConverter(
            handlers: Iterable<FastConvertHandler<*, *>>,
            failedHandler: FastConvertHandler<*, *> = DefaultFailedFastConvertHandler
        ): FastConverter {
            return FastConverterImpl(handlers, failedHandler)
        }

        private class FastConverterImpl(
            handlers: Iterable<FastConvertHandler<*, *>>,
            failedHandler: FastConvertHandler<*, *>
        ) : FastConverter {

            private val handlerMap: Map<Pair<Class<*>, Class<*>>, FastConvertHandler<*, *>> =
                handlers.associateBy { it.fromType to it.toType }
            private val handlerArray: Array<FastConvertHandler<*, *>> = handlerMap.values
                .sortedWith label@{ it1, it2 ->
                    val c1From = it1.fromType
                    val c2From = it2.fromType
                    val c1To = it1.toType
                    val c2To = it2.toType
                    val fromCompare = INHERITANCE_COMPARATOR.compare(c1From, c2From)
                    if (fromCompare != 0) {
                        return@label fromCompare
                    }
                    INHERITANCE_COMPARATOR.compare(c1To, c2To)
                }
                .toTypedArray()
            private val failedHandler: FastConvertHandler<Any, *> = failedHandler.asAny()

            override fun <T : Any, R : Any> convert(from: T, fromType: Type, toType: Type): R {
                val key = fromType to toType
                val mapHandler: FastConvertHandler<Any, *>? = handlerMap[key].asAny()
                if (mapHandler !== null) {
                    return mapHandler.convert(from).asAny()
                }
                for (fastConvertHandler in handlerArray) {
                    if (fastConvertHandler.fromType.isAssignableFrom(fromType.rawClass)
                        && toType.rawClass.isAssignableFrom(fastConvertHandler.toType)
                    ) {
                        val handler: FastConvertHandler<Any, *> = fastConvertHandler.asAny()
                        return handler.convert(from).asAny()
                    }
                }
                return failedHandler.convert(from).asAny()
            }
        }
    }
}

/**
 * Handler for [FastConverter], support convert [T] to [R].
 *
 * @see FastConverter
 */
interface FastConvertHandler<T : Any, R : Any> {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val fromType: Class<*>
        @JvmName("fromType") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val toType: Class<*>
        @JvmName("toType") get

    fun convert(from: T): R
}

object DefaultFailedFastConvertHandler : FastConvertHandler<Any, Nothing> {
    override val fromType: Class<*> = Any::class.java
    override val toType: Class<*> = Nothing::class.java
    override fun convert(from: Any): Nothing {
        throw UnsupportedOperationException("${from.javaClass}")
    }
}