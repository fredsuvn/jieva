package xyz.srclab.common.proxy

import org.springframework.cglib.proxy.*
import xyz.srclab.common.base.asTyped
import xyz.srclab.common.func.StaticFunc
import java.lang.reflect.Method
import java.util.*

object SpringProxyClassGenerator : ProxyClassGenerator {

    override fun <T : Any> generate(
        sourceClass: Class<T>,
        proxyMethods: Iterable<ProxyMethod>,
        classLoader: ClassLoader
    ): ProxyClass<T> {
        val enhancer = Enhancer()
        enhancer.classLoader = classLoader
        if (sourceClass.isInterface) {
            enhancer.setInterfaces(arrayOf(sourceClass))
        } else {
            enhancer.setSuperclass(sourceClass)
        }

        val callbacks = LinkedList<Callback>()
        //index 0
        callbacks.add(NoOp.INSTANCE)
        //index 1..
        for (proxyMethod in proxyMethods) {
            callbacks.add(ProxyMethodInterceptor(proxyMethod))
        }
        val callbackFilter = CallbackFilter { method ->
            for ((i, callback) in callbacks.withIndex()) {
                if (callback is ProxyMethodInterceptor) {
                    if (callback.proxyMethod.isProxy(method)) {
                        return@CallbackFilter i
                    }
                }
            }
            0
        }

        enhancer.setCallbacks(callbacks.toTypedArray())
        enhancer.setCallbackFilter(callbackFilter)
        return ProxyClassImpl(enhancer)
    }

    private class ProxyMethodInterceptor(val proxyMethod: ProxyMethod) : MethodInterceptor {
        override fun intercept(obj: Any, method: Method, args: Array<out Any?>?, proxy: MethodProxy): Any? {
            val sourceInvoker = object : StaticFunc {
                override fun invoke(vararg args: Any?): Any? {
                    return proxy.invokeSuper(obj, args)
                }
            }
            return proxyMethod.invoke(obj, method, sourceInvoker, args)
        }
    }

    private class ProxyClassImpl<T : Any>(private val enhancer: Enhancer) : ProxyClass<T> {

        override fun instantiate(): T {
            return enhancer.create().asTyped()
        }

        override fun instantiate(parameterTypes: Array<Class<*>>, args: Array<Any?>): T {
            return enhancer.create(parameterTypes, args).asTyped()
        }
    }
}