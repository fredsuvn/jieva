package xyz.srclab.common.invoke

import xyz.srclab.common.base.asAny

/**
 * Invoker used to invoke static method.
 */
interface FuncInvoker {

    fun invoke(vararg args: Any?): Any?

    fun <T> invokeTyped(vararg args: Any?): T {
        return invoke(*args).asAny()
    }
}