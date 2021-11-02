package xyz.srclab.common.invoke

import xyz.srclab.common.base.asAny
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method

/**
 * Invoker used to invoke instance method.
 */
interface InstInvoker<T> {

    fun invoke(inst: Any, vararg args: Any?): T

    fun toFuncInvoker(inst: Any): FuncInvoker<T> {
        return object : FuncInvoker<T> {
            override fun invoke(vararg args: Any?): T {
                return this@InstInvoker.invoke(inst, *args)
            }
        }
    }

    companion object {

        @JvmName("from")
        @JvmOverloads
        @JvmStatic
        fun <T> Method.toInstInvoker(force: Boolean = false, reflect: Boolean = true): InstInvoker<T> {
            return if (reflect)
                ReflectedInstInvoker(this, force)
            else {
                UnreflectedInstInvoker(this, force)
            }
        }

        private class ReflectedInstInvoker<T>(
            private val method: Method,
            force: Boolean,
        ) : InstInvoker<T> {

            init {
                if (force) {
                    method.isAccessible = true
                }
            }

            override fun invoke(inst: Any, vararg args: Any?): T {
                return method.invoke(inst, *args).asAny()
            }
        }

        private class UnreflectedInstInvoker<T>(
            method: Method,
            force: Boolean,
        ) : InstInvoker<T> {

            private val handle: MethodHandle

            init {
                if (force) {
                    method.isAccessible = true
                }
                handle = MethodHandles.lookup().unreflect(method)
            }

            override fun invoke(inst: Any, vararg args: Any?): T {
                val arguments = arrayOfNulls<Any?>(args.size + 1)
                arguments[0] = inst
                System.arraycopy(args, 0, arguments, 1, args.size)
                return handle.invokeWithArguments(*arguments).asAny()
            }
        }
    }
}