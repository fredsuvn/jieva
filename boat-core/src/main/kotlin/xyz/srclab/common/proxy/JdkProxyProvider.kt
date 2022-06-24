package xyz.srclab.common.proxy

import xyz.srclab.common.base.asType
import xyz.srclab.common.invoke.StaticInvoke
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * JDK implementation for [ClassProxyProvider].
 */
object JdkProxyProvider : ClassProxyProvider {

    override fun <T : Any> getProxy(
        sourceClass: Class<T>,
        proxyInvoker: ProxyInvoker,
        classLoader: ClassLoader,
    ): ClassProxy<T> {
        return ProxyClassImpl(sourceClass, proxyInvoker, classLoader)
    }

    private class ProxyClassImpl<T : Any>(
        private val sourceClass: Class<T>,
        private val proxyInvoker: ProxyInvoker,
        private val classLoader: ClassLoader,
    ) : ClassProxy<T> {

        override fun newInst(): T {
            return Proxy.newProxyInstance(
                classLoader,
                arrayOf(sourceClass),
                ProxyMethodInterceptor(proxyInvoker)
            ).asType()
        }

        override fun newInst(parameterTypes: Array<Class<*>>, args: Array<Any?>): T {
            throw IllegalArgumentException("JDK proxy only supports interface.")
        }
    }

    private class ProxyMethodInterceptor(
        private val proxyInvoker: ProxyInvoker,
    ) : InvocationHandler {

        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
            if (proxyInvoker.isTarget(method)) {
                return proxyInvoker.invokeProxy(proxy, method, UnsupportedSourceInvoke, args)
            }
            throw IllegalStateException("Proxy method not found: $method")
        }
    }

    private object UnsupportedSourceInvoke : StaticInvoke {
        override fun invoke(vararg args: Any?): Any? {
            throw IllegalStateException("Interface's method is not implemented.")
        }
    }
}