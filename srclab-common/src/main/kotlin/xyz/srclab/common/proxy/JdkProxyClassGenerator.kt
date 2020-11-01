package xyz.srclab.common.proxy

import xyz.srclab.common.base.asAny
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object JdkProxyClassGenerator : ProxyClassGenerator {

    override fun <T : Any> generate(
        classLoader: ClassLoader,
        proxyClass: Class<T>,
        proxyMethods: Iterable<ProxyMethod<T>>
    ): ProxyClass<T> {
        return Proxy.newProxyInstance(classLoader, arrayOf(proxyClass), ProxyMethodInterceptor(proxyMethods)).asAny()
    }

    private class ProxyMethodInterceptor<T : Any>(val proxyMethods: Iterable<ProxyMethod<T>>) : InvocationHandler {

        private var methodMap: MutableMap<Method, ProxyMethod<T>>? = null

        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
            val proxyMethod = findProxyMethod(method)
            return callProxyMethod(proxyMethod, proxy, method, args)
        }

        private fun findProxyMethod(method: Method): ProxyMethod<T> {
            val tryFind = findFromMethodMap(method)
            if (tryFind !== null) {
                return tryFind
            }

            synchronized(this) {
                val proxyMethodFromMapSync = findFromMethodMap(method)
                if (proxyMethodFromMapSync !== null) {
                    return proxyMethodFromMapSync
                }
                val proxyMethodFromIterable = proxyMethods.find { pm ->
                    method.name == pm.name && method.parameterTypes.contentEquals(pm.parametersTypes)
                }
                if (proxyMethodFromIterable === null) {
                    throw IllegalStateException("Proxy method not found: $method")
                }

                //Using copy-on-write-way to update method map.
                val oldMap = methodMap
                val newMap = if (oldMap === null) HashMap() else HashMap(oldMap)
                newMap[method] = proxyMethodFromIterable
                methodMap = newMap
                return proxyMethodFromIterable
            }
        }

        private fun findFromMethodMap(method: Method): ProxyMethod<T>? {
            val methods = methodMap
            if (methods !== null) {
                val proxyMethodFromMap = methods[method]
                if (proxyMethodFromMap !== null) {
                    return proxyMethodFromMap
                }
            }
            return null
        }

        private fun callProxyMethod(
            proxyMethod: ProxyMethod<T>,
            proxy: Any,
            method: Method,
            args: Array<out Any>?
        ): Any? {
            return proxyMethod.invoke(proxy.asAny(), method, args, object : SuperInvoker {
                override fun invoke(args: Array<out Any?>?): Any? {
                    throw IllegalStateException("Illegal method calling: $method")
                }
            })
        }
    }
}