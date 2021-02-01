package xyz.srclab.common.proxy

import net.sf.cglib.proxy.CallbackFilter
import net.sf.cglib.proxy.Enhancer
import net.sf.cglib.proxy.MethodInterceptor
import net.sf.cglib.proxy.MethodProxy
import xyz.srclab.common.base.asAny
import java.lang.reflect.Method
import java.util.*

object CglibProxyClassFactory : ProxyClassFactory {

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
        val callbacks = LinkedList<ProxyMethodInterceptor<T>>()
        for (proxyMethod in proxyMethods) {
            callbacks.add(ProxyMethodInterceptor(proxyMethod))
        }
        val callbackFilter = CallbackFilter { method ->
            callbacks.indexOfFirst { mi ->
                method.name == mi.proxyMethod.name
                        && method.parameterTypes.contentEquals(mi.proxyMethod.parametersTypes)
            }
        }
        enhancer.setCallbacks(callbacks.toTypedArray())
        enhancer.setCallbackFilter(callbackFilter)
        return ProxyClassImpl(enhancer)
    }

    private class ProxyMethodInterceptor<T : Any>(val proxyMethod: ProxyMethod<T>) : MethodInterceptor {
        override fun intercept(obj: Any, method: Method, args: Array<out Any?>?, proxy: MethodProxy): Any? {
            return proxyMethod.invoke(obj.asAny(), method, args, object : SuperInvoker {
                override fun invoke(args: Array<out Any?>?): Any? {
                    return proxy.invokeSuper(obj, args)
                }
            })
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