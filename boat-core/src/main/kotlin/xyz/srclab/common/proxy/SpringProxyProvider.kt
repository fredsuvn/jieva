package xyz.srclab.common.proxy

import org.springframework.cglib.proxy.*
import xyz.srclab.common.base.asTyped
import xyz.srclab.common.invoke.StaticInvoke
import java.lang.reflect.Method
import java.util.*

/**
 * Spring-cglib implementation for [ClassProxyProvider].
 */
object SpringProxyProvider : ClassProxyProvider {

    override fun <T : Any> getProxy(
        sourceClass: Class<T>,
        proxyInvoker: ProxyInvoker,
        classLoader: ClassLoader
    ): ClassProxy<T> {
        val enhancer = Enhancer()
        enhancer.classLoader = classLoader
        if (sourceClass.isInterface) {
            enhancer.setInterfaces(arrayOf(sourceClass))
        } else {
            enhancer.setSuperclass(sourceClass)
        }
        val callbacks = LinkedList<Callback>()
        val proxyInterceptor = ProxyMethodInterceptor(proxyInvoker)
        //index 0
        callbacks.add(NoOp.INSTANCE)
        //index 1
        callbacks.add(proxyInterceptor)
        val callbackFilter = CallbackFilter { method ->
            if (proxyInvoker.isTarget(method)) {
                return@CallbackFilter 1
            }
            0
        }
        enhancer.setCallbacks(callbacks.toTypedArray())
        enhancer.setCallbackFilter(callbackFilter)
        return ProxyClassImpl(enhancer)
    }

    private class ProxyMethodInterceptor(val proxyInvoker: ProxyInvoker) : MethodInterceptor {
        override fun intercept(obj: Any, method: Method, args: Array<out Any?>?, proxy: MethodProxy): Any? {
            val sourceInvoker = StaticInvoke {
                proxy.invokeSuper(obj, args)
            }
            return proxyInvoker.invokeProxy(obj, method, sourceInvoker, args)
        }
    }

    private class ProxyClassImpl<T : Any>(private val enhancer: Enhancer) : ClassProxy<T> {

        override fun newInst(): T {
            return enhancer.create().asTyped()
        }

        override fun newInst(parameterTypes: Array<Class<*>>, args: Array<Any?>): T {
            return enhancer.create(parameterTypes, args).asTyped()
        }
    }
}