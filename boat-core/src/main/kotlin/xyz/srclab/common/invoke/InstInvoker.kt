package xyz.srclab.common.invoke

import xyz.srclab.common.base.asAny

/**
 * Invoker used to invoke instance method.
 */
interface InstInvoker {

    fun invoke(inst: Any, vararg args: Any?): Any?

    fun <T> invokeTyped(inst: Any, vararg args: Any?): T {
        return invoke(inst, *args).asAny()
    }

    fun toFuncInvoker(inst: Any): FuncInvoker {
        return object : FuncInvoker {
            override fun invoke(vararg args: Any?): Any? {
                return this@InstInvoker.invoke(inst, *args)
            }
        }
    }
}