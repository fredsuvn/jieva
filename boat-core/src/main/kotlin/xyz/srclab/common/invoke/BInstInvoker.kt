package xyz.srclab.common.invoke

import xyz.srclab.common.base.asTyped

/**
 * Invoker used to invoke instance method.
 */
interface BInstInvoker {

    fun invoke(inst: Any, vararg args: Any?): Any?

    fun <T> invokeTyped(inst: Any, vararg args: Any?): T {
        return invoke(inst, *args).asTyped()
    }

    fun toFuncInvoker(inst: Any): BFuncInvoker {
        return object : BFuncInvoker {
            override fun invoke(vararg args: Any?): Any? {
                return this@BInstInvoker.invoke(inst, *args)
            }
        }
    }
}