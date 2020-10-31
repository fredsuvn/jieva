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
        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
            val proxyMethod = proxyMethods.find { pm ->
                method.name == pm.name && method.parameterTypes.contentEquals(pm.parametersTypes)
            }
            if (proxyMethod === null) {
                throw IllegalStateException("Proxy method not found: $method")
            }
            return proxyMethod.invoke(proxy.asAny(), method, args, object : SuperInvoker {
                override fun invoke(args: Array<out Any?>?): Any? {
                    throw IllegalStateException("Illegal method calling: $method")
                }
            })
        }
    }
}