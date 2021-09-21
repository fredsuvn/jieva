package xyz.srclab.common.invoke

import xyz.srclab.common.base.asAny
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * [InvokerGenerator] by reflect.
 */
object ReflectInvokerGenerator : InvokerGenerator {

    override fun ofMethod(method: Method): Invoker {
        return ReflectedMethodInvoker(method)
    }

    override fun ofConstructor(constructor: Constructor<*>): Invoker {
        return ReflectedConstructorInvoker(constructor)
    }

    private class ReflectedMethodInvoker(private val method: Method) : Invoker {
        override fun <T> doInvoke(obj: Any?, force: Boolean, vararg args: Any?): T {
            if (force) {
                method.isAccessible = true
            }
            return method.invoke(obj, *args).asAny()
        }
    }

    private class ReflectedConstructorInvoker(private val constructor: Constructor<*>) : Invoker {
        override fun <T> doInvoke(obj: Any?, force: Boolean, vararg args: Any?): T {
            if (force) {
                constructor.isAccessible = true
            }
            return constructor.newInstance(*args).asAny()
        }
    }
}