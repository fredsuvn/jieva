package xyz.srclab.common.convert

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import java.lang.reflect.Type

/**
 * Convert handler chain and context for each conversion of [Converter].
 */
interface ConvertChain {

    @get:JvmName("converter")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val converter: Converter

    /**
     * Continues to call next handler.
     */
    fun next(from: Any?, fromType: Type, toType: Type): Any?

    /**
     * Calls first handler of current conversation.
     */
    fun restart(from: Any?, fromType: Type, toType: Type): Any?
}