package xyz.srclab.common.invoke

import xyz.srclab.common.base.asAny
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.util.concurrent.Callable
import java.util.function.Function

/**
 * Invoker used to invoke static method.
 */
interface FuncInvoker {

    fun invoke(vararg args: Any?): Any?

    fun <T> invokeTyped(vararg args: Any?): T {
        return invoke(*args).asAny()
    }

    companion object {

        @JvmOverloads
        @JvmStatic
        fun from(method: Method, force: Boolean = false, reflect: Boolean = true): FuncInvoker {
            return method.toFuncInvoker(force, reflect)
        }

        @JvmOverloads
        @JvmStatic
        fun from(constructor: Constructor<*>, force: Boolean = false, reflect: Boolean = true): FuncInvoker {
            return constructor.toFuncInvoker(force, reflect)
        }

        @JvmStatic
        fun from(runnable: Runnable): FuncInvoker {
            return runnable.toFuncInvoker()
        }

        @JvmStatic
        fun from(callable: Callable<*>): FuncInvoker {
            return callable.toFuncInvoker()
        }

        @JvmStatic
        fun from(func: Function<Array<out Any?>, *>): FuncInvoker {
            return func.toFuncInvoker()
        }

        @JvmStatic
        fun from(func: (Array<out Any?>) -> Any?): FuncInvoker {
            return funcInvoker(func)
        }
    }
}