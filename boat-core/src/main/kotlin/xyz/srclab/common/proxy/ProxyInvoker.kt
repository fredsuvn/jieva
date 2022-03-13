package xyz.srclab.common.proxy

import xyz.srclab.common.invoke.StaticInvoke
import java.lang.reflect.Method

/**
 * Represents an invoker to proxy target methods.
 */
interface ProxyInvoker {

    /**
     * Returns whether the [method] is target method to be proxied by this [ProxyInvoker].
     */
    fun isTarget(method: Method): Boolean

    /**
     * Invokes the proxied action for [sourceInst].[sourceMethod].
     *
     * @param sourceInst source instance which has [sourceMethod]
     * @param sourceMethod the method to be proxied
     * @param sourceInvoke the action of [sourceMethod]
     * @param args arguments of the proxy method (and [sourceMethod])
     */
    fun invokeProxy(
        sourceInst: Any,
        sourceMethod: Method,
        sourceInvoke: StaticInvoke,
        args: Array<out Any?>?
    ): Any?
}