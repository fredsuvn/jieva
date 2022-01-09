package xyz.srclab.common.proxy

import xyz.srclab.common.base.asTyped
import xyz.srclab.common.func.StaticFunc
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object JdkProxyFactory : ClassProxyFactory {

    override fun <T : Any> generate(
        sourceClass: Class<T>,
        proxyMethods: Iterable<ProxyMethod>,
        classLoader: ClassLoader
    ): ClassProxy<T> {
        return ProxyClassImpl(sourceClass, proxyMethods, classLoader)
    }

    private class ProxyClassImpl<T : Any>(
        private val sourceClass: Class<T>,
        private val proxyMethods: Iterable<ProxyMethod>,
        private val classLoader: ClassLoader
    ) : ClassProxy<T> {

        override fun create(): T {
            return Proxy.newProxyInstance(
                classLoader,
                arrayOf(sourceClass),
                ProxyMethodInterceptor(proxyMethods)
            ).asTyped()
        }

        override fun create(parameterTypes: Array<Class<*>>, args: Array<Any?>): T {
            throw IllegalArgumentException("JDK proxy only supports interface.")
        }
    }

    private class ProxyMethodInterceptor(
        private val proxyMethods: Iterable<ProxyMethod>
    ) : InvocationHandler {

        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
            for (proxyMethod in proxyMethods) {
                if (proxyMethod.isProxy(method)) {
                    return proxyMethod.invoke(proxy, method, UnsupportedSourceInvoke, args)
                }
            }
            throw IllegalStateException("Proxy method not found: $method")
        }
    }

    private object UnsupportedSourceInvoke : StaticFunc {
        override fun invoke(vararg args: Any?): Any? {
            throw IllegalStateException("Interface's method is not implemented.")
        }
    }
}