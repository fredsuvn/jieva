package xyz.srclab.common.proxy

import xyz.srclab.common.func.StaticFunc
import java.lang.reflect.Method

/**
 * To describe proxy method info.
 */
interface ProxyMethod {

    /**
     * Returns whether this [ProxyMethod] is valid for given [method].
     */
    fun isProxy(method: Method): Boolean

    /**
     * Proxy action.
     */
    operator fun invoke(
        sourceObject: Any,
        sourceMethod: Method,
        sourceInvoke: StaticFunc,
        args: Array<out Any?>?
    ): Any?
}