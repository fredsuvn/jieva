package xyz.srclab.common.proxy

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import java.lang.reflect.Method

/**
 * @author sunqian
 */
interface ProxyMethod<T> {

    @Suppress(INAPPLICABLE_JVM_NAME)
    val name: String
        @JvmName("name") get

    @Suppress(INAPPLICABLE_JVM_NAME)
    val parameterTypes: Array<Class<*>>
        @JvmName("parameterTypes") get

    fun invoke(proxied: T, proxiedMethod: Method, args: Array<out Any?>?, superInvoker: SuperInvoker): Any?
}

interface SuperInvoker {

    fun invoke(args: Array<out Any?>?): Any?
}