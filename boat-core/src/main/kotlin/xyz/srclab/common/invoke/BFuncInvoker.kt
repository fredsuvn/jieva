package xyz.srclab.common.invoke

import xyz.srclab.common.base.asTyped

/**
 * Invoker used to invoke static method.
 */
interface BFuncInvoker {

    fun invoke(vararg args: Any?): Any?

    fun <T> invokeTyped(vararg args: Any?): T {
        return invoke(*args).asTyped()
    }
}