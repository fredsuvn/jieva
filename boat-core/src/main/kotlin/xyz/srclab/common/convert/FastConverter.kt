package xyz.srclab.common.convert

import xyz.srclab.common.base.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.reflect.INHERITANCE_COMPARATOR

/**
 * Fast and narrowing version of [Converter], supports convert given object to type [R].
 *
 * @see FastConvertHandler
 */
interface FastConverter<R> {

    fun convert(from: Any): R

    companion object {

        @JvmStatic
        fun <R> newFastConverter(handlers: Iterable<FastConvertHandler<out R>>): FastConverter<R> {
            return FastConverterImpl(handlers)
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

private class FastConverterImpl<R>(handlers: Iterable<FastConvertHandler<out R>>) : FastConverter<R> {

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
        throw IllegalArgumentException("Cannot convert type: ${fromType.typeName}")
    }
}