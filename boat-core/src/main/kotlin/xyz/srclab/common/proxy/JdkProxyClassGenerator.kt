package xyz.srclab.common.proxy

import xyz.srclab.common.base.asTyped
import xyz.srclab.common.jvm.jvmDescriptor
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object JdkProxyClassGenerator : ProxyClassGenerator {

    override fun <T : Any> generate(
        sourceClass: Class<T>,
        proxyMethods: Iterable<ProxyMethod<T>>,
        classLoader: ClassLoader
    ): ProxyClass<T> {
        return ProxyClassImpl(sourceClass, proxyMethods, classLoader)
    }

    private class ProxyClassImpl<T : Any>(
        private val sourceClass: Class<T>,
        private val proxyMethods: Iterable<ProxyMethod<T>>,
        private val classLoader: ClassLoader
    ) : ProxyClass<T> {

        override fun instantiate(): T {
            return Proxy.newProxyInstance(
                classLoader,
                arrayOf(sourceClass),
                ProxyMethodInterceptor(sourceClass, proxyMethods)
            )
                .asTyped()
        }

        override fun instantiate(parameterTypes: Array<Class<*>>, args: Array<Any?>): T {
            throw IllegalArgumentException("JDK proxy only supports interface.")
        }
    }

    private class ProxyMethodInterceptor<T : Any>(
        sourceClass: Class<T>,
        private val proxyMethods: Iterable<ProxyMethod<T>>
    ) : InvocationHandler {

        private val methodMap: Map<String, ProxyMethod<T>> by lazy {
            val map = HashMap<String, ProxyMethod<T>>()
            val methods = sourceClass.methods
            for (method in methods) {
                for (proxyMethod in proxyMethods) {
                    if (proxyMethod.isProxy(method)) {
                        map["${method.name}${method.parameterTypes.jvmDescriptor}"] = proxyMethod
                        break
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
            val sourceInvoker = object : SourceInvoker {
                override fun invoke(args: Array<out Any?>?): Any? {
                    throw IllegalStateException("Cannot call a interface method: $method")
                }
            }
            return proxyMethod.invoke(proxy.asTyped(), method, sourceInvoker, args)
        }
    }
}