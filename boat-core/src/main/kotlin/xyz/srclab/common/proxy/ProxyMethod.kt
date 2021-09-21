package xyz.srclab.common.proxy

import java.lang.reflect.Method

/**
 * To describe proxy method info.
 *
 * @author sunqian
 */
interface ProxyMethod<T : Any> {

    /**
     * Returns whether this [ProxyMethod] is valid for given [method].
     */
    fun isProxy(method: Method): Boolean

    /**
     * Proxy action.
     */
    fun invoke(sourceObject: T, sourceMethod: Method, sourceInvoker: SourceInvoker, args: Array<out Any?>?): Any?
}