package xyz.srclab.common.proxy

import xyz.srclab.common.jvm.jvmDescriptor
import xyz.srclab.common.lang.asAny
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
            return Proxy.newProxyInstance(
                classLoader,
                arrayOf(proxyClass),
                ProxyMethodInterceptor(proxyClass, proxyMethods)
            )
                .asAny()
        }

        override fun newInstance(parameterTypes: Array<Class<*>>?, args: Array<Any?>?): T {
            throw IllegalArgumentException("JDK proxy only support interface proxy.")
        }
    }

    private class ProxyMethodInterceptor<T : Any>(
        proxyClass: Class<T>,
        val proxyMethods: Iterable<ProxyMethod<T>>
    ) : InvocationHandler {

        private val methodMap: Map<String, ProxyMethod<T>> by lazy {
            val map = HashMap<String, ProxyMethod<T>>()
            val methods = proxyClass.methods
            for (method in methods) {
                for (proxyMethod in proxyMethods) {
                    if (proxyMethod.proxy(method)) {
                        map["${method.name}${method.parameterTypes.jvmDescriptor}"] = proxyMethod
                    }
                }
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
            val superInvoke = object : SuperInvoke {
                override fun invoke(vararg args: Any?): Any? {
                    throw IllegalStateException("Cannot call a interface method: $method")
                }
            }
            return proxyMethod.invoke(proxy.asAny(), method, superInvoke, *args ?: emptyArray())
        }
    }
}