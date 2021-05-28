package xyz.srclab.common.convert

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.asAny
import xyz.srclab.common.reflect.INHERITANCE_COMPARATOR

/**
 * Fast version of [Converter].
 *
 * It uses [FastConvertHandler] which must provide [FastConvertHandler.supportedType] to ensure supported `fromType`.
 *
 * @see FastConvertHandler
 */
interface FastConverter<R> {

    fun convert(from: Any): R

    companion object {

        @JvmOverloads
        @JvmStatic
        fun <R> newFastConverter(
            handlers: Iterable<FastConvertHandler<out R>>,
            failedHandler: FastConvertHandler<R> = DefaultFailedFastConvertHandler.asAny()
        ): FastConverter<R> {
            return FastConverterImpl(handlers, failedHandler)
        }
    }
}

/**
 * Handler for [FastConverter].
 *
 * @see FastConverter
 */
interface FastConvertHandler<R> {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val supportedType: Class<*>
        @JvmName("supportedType") get

    fun convert(from: Any): R
}

object DefaultFailedFastConvertHandler : FastConvertHandler<Nothing> {

    override val supportedType: Class<*> = Nothing::class.java

    override fun convert(from: Any): Nothing {
        throw UnsupportedOperationException("${from.javaClass}")
    }
}

private class FastConverterImpl<R>(
    handlers: Iterable<FastConvertHandler<out R>>,
    private val failedHandler: FastConvertHandler<R>
) : FastConverter<R> {

    private val handlerMap: Map<Class<*>, FastConvertHandler<out R>> = handlers.associateBy { it.supportedType }
    private val handlerArray: Array<FastConvertHandler<out R>> = handlerMap.values
        .sortedWith { it1, it2 ->
            val c1 = it1.supportedType
            val c2 = it2.supportedType
            INHERITANCE_COMPARATOR.compare(c1, c2)
        }
        .toTypedArray()

    override fun convert(from: Any): R {
        val fromType = from.javaClass
        val mapHandler = handlerMap[fromType]
        if (mapHandler !== null) {
            return mapHandler.convert(from)
        }
        for (arrayHandler in handlerArray) {
            if (arrayHandler.supportedType.isAssignableFrom(fromType)) {
                return arrayHandler.convert(from)
            }
        }
        return failedHandler.convert(from)
    }
}