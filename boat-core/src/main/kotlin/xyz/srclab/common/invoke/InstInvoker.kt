package xyz.srclab.common.invoke

import xyz.srclab.common.base.asTyped
import java.lang.reflect.Method

/**
 * Invoker used to invoke instance method.
 */
interface InstInvoker {

    fun invoke(inst: Any, vararg args: Any?): Any?

    fun <T> invokeTyped(inst: Any, vararg args: Any?): T {
        return invoke(inst, *args).asTyped()
    }

    fun toFuncInvoker(inst: Any): FuncInvoker {
        return object : FuncInvoker {
            override fun invoke(vararg args: Any?): Any? {
                return this@InstInvoker.invoke(inst, *args)
            }
        }
    }

    companion object {

        @JvmOverloads
        @JvmStatic
        fun from(method: Method, force: Boolean = false, reflect: Boolean = true): InstInvoker {
            return method.toInstInvoker(force, reflect)
        }
    }
}