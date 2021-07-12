package xyz.srclab.common.proxy

import org.springframework.cglib.proxy.*
import xyz.srclab.common.lang.asAny
import java.lang.reflect.Method
import java.util.*

object SpringProxyClassFactory : ProxyClassFactory {

    override fun <T : Any> create(
        proxyClass: Class<T>,
        proxyMethods: Iterable<ProxyMethod<T>>,
        classLoader: ClassLoader
    ): ProxyClass<T> {
        val enhancer = Enhancer()
        enhancer.classLoader = classLoader
        if (proxyClass.isInterface) {
            enhancer.setInterfaces(arrayOf(proxyClass))
        } else {
            enhancer.setSuperclass(proxyClass)
        }
        val callbacks = LinkedList<Callback>()
        callbacks.add(NoOp.INSTANCE)
        for (proxyMethod in proxyMethods) {
            callbacks.add(ProxyMethodInterceptor(proxyMethod))
        }
        val callbackFilter = CallbackFilter { method ->
            for (i in callbacks.indices) {
                val callback = callbacks[i]
                if (callback is ProxyMethodInterceptor<*>) {
                    if (callback.proxyMethod.proxy(method)) {
                        return@CallbackFilter i
                    } else {
                        return@CallbackFilter 0
                    }
                }
            }
            0
        }
        enhancer.setCallbacks(callbacks.toTypedArray())
        enhancer.setCallbackFilter(callbackFilter)
        return ProxyClassImpl(enhancer)
    }

    private class ProxyMethodInterceptor<T : Any>(val proxyMethod: ProxyMethod<T>) : MethodInterceptor {

        override fun intercept(obj: Any, method: Method, args: Array<out Any?>?, proxy: MethodProxy): Any? {
            val superInvoke = object : SuperInvoke {
                override fun invoke(vararg args: Any?): Any? {
                    return proxy.invokeSuper(obj, args)
                }
            }
            return proxyMethod.invoke(obj.asAny(), method, superInvoke, *args ?: emptyArray())
        }
    }

    private class ProxyClassImpl<T : Any>(private val enhancer: Enhancer) : ProxyClass<T> {

        override fun newInstance(): T {
            return enhancer.create().asAny()
        }

        override fun newInstance(parameterTypes: Array<Class<*>>?, args: Array<Any?>?): T {
            return enhancer.create(parameterTypes, args).asAny()
        }
    }
}