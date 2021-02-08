package xyz.srclab.common.proxy

import xyz.srclab.common.base.asAny
import xyz.srclab.common.jvm.jvmDescriptor
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object JdkProxyClassFactory : ProxyClassFactory {

    override fun <T : Any> create(
        proxyClass: Class<T>,
        proxyMethods: Iterable<ProxyMethod<T>>,
        classLoader: ClassLoader
    ): ProxyClass<T> {
        return ProxyClassImpl(proxyClass, proxyMethods, classLoader)
    }

    private class ProxyClassImpl<T : Any>(
        private val proxyClass: Class<T>,
        private val proxyMethods: Iterable<ProxyMethod<T>>,
        private val classLoader: ClassLoader
    ) : ProxyClass<T> {

        override fun newInstance(): T {
            return Proxy.newProxyInstance(classLoader, arrayOf(proxyClass), ProxyMethodInterceptor(proxyMethods))
                .asAny()
        }

        override fun newInstance(parameterTypes: Array<Class<*>>?, args: Array<Any?>?): T {
            throw IllegalArgumentException("JDK proxy only support interface proxy.")
        }
    }

    private class ProxyMethodInterceptor<T : Any>(val proxyMethods: Iterable<ProxyMethod<T>>) : InvocationHandler {

        private val methodMap: Map<String, ProxyMethod<T>> by lazy {
            val map = HashMap<String, ProxyMethod<T>>()
            for (proxyMethod in proxyMethods) {
                map["${proxyMethod.name}${proxyMethod.parameterTypes.jvmDescriptor}"] = proxyMethod
            }
            map
        }

        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
            val proxyMethod = methodMap["${method.name}${method.parameterTypes.jvmDescriptor}"]
            if (proxyMethod === null) {
                throw IllegalStateException("Proxy method not found: $method")
            }
            return callProxyMethod(proxyMethod, proxy, method, args)
        }

        private fun callProxyMethod(
            proxyMethod: ProxyMethod<T>,
            proxy: Any,
            method: Method,
            args: Array<out Any>?
        ): Any? {
            return proxyMethod.invoke(proxy.asAny(), method, args, object : SuperInvoker {
                override fun invoke(args: Array<out Any?>?): Any? {
                    throw IllegalStateException("Cannot call a interface method: $method")
                }
            })
        }
    }
}