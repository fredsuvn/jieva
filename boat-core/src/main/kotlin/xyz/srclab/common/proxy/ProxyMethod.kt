package xyz.srclab.common.proxy

import xyz.srclab.common.invoke.StaticInvoke
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
        sourceInvoke: StaticInvoke,
        args: Array<out Any?>?
    ): Any?
}