package xyz.srclab.common.proxy

import java.lang.reflect.Method

/**
 * Represents a proxy method.
 *
 * @author sunqian
 */
interface ProxyMethod<T : Any> {

    /**
     * Returns whether proxy specified [method].
     */
    fun proxy(method: Method): Boolean

    /**
     * Proxy action
     */
    fun invoke(proxied: T, proxiedMethod: Method, superInvoke: SuperInvoke, args: Array<out Any?>): Any?
}