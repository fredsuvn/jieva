package xyz.srclab.common.invoke

import xyz.srclab.common.base.asAny
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.util.concurrent.Callable
import java.util.function.Function

/**
 * Invoker used to invoke static method.
 */
interface FuncInvoker<T> {

    fun invoke(vararg args: Any?): T

    companion object {

        @JvmName("from")
        @JvmOverloads
        @JvmStatic
        fun <T> Method.toFuncInvoker(force: Boolean = false, reflect: Boolean = true): FuncInvoker<T> {
            return if (reflect)
                ReflectedFuncInvoker(this, force)
            else {
                UnreflectedFuncInvoker(this, force)
            }
        }

        @JvmName("from")
        @JvmOverloads
        @JvmStatic
        fun <T> Constructor<T>.toFuncInvoker(force: Boolean = false, reflect: Boolean = true): FuncInvoker<T> {
            return if (reflect)
                ReflectedConstructorInvoker(this, force)
            else {
                UnreflectedConstructorInvoker(this, force)
            }
        }

        @JvmName("from")
        @JvmStatic
        fun Runnable.toFuncInvoker(): FuncInvoker<*> {
            return object : FuncInvoker<Any?> {
                override fun invoke(vararg args: Any?): Any? {
                    this@toFuncInvoker.run()
                    return null
                }
            }
        }

        @JvmName("from")
        @JvmStatic
        fun <T> Callable<T>.toFuncInvoker(): FuncInvoker<T> {
            return object : FuncInvoker<T> {
                override fun invoke(vararg args: Any?): T {
                    return this@toFuncInvoker.call()
                }
            }
        }

        @JvmName("from")
        @JvmStatic
        fun <T> Function<Array<out Any?>, T>.toFuncInvoker(): FuncInvoker<T> {
            return object : FuncInvoker<T> {
                override fun invoke(vararg args: Any?): T {
                    return this@toFuncInvoker.apply(args)
                }
            }
        }

        fun <T> from(func: (Array<out Any?>) -> T): FuncInvoker<T> {
            return object : FuncInvoker<T> {
                override fun invoke(vararg args: Any?): T {
                    return func(args)
                }
            }
        }

        private class ReflectedFuncInvoker<T>(
            private val method: Method,
            force: Boolean,
        ) : FuncInvoker<T> {

            init {
                if (force) {
                    method.isAccessible = true
                }
            }

            override fun invoke(vararg args: Any?): T {
                return method.invoke(null, *args).asAny()
            }
        }

        private class ReflectedConstructorInvoker<T>(
            private val constructor: Constructor<T>,
            force: Boolean,
        ) : FuncInvoker<T> {

            init {
                if (force) {
                    constructor.isAccessible = true
                }
            }

            override fun invoke(vararg args: Any?): T {
                return constructor.newInstance(*args)
            }
        }

        private class UnreflectedFuncInvoker<T>(
            method: Method,
            force: Boolean,
        ) : FuncInvoker<T> {

            private val handle: MethodHandle

            init {
                if (force) {
                    method.isAccessible = true
                }
                handle = MethodHandles.lookup().unreflect(method)
            }

            override fun invoke(vararg args: Any?): T {
                return handle.invokeWithArguments(*args).asAny()
            }
        }

        private class UnreflectedConstructorInvoker<T>(
            constructor: Constructor<T>,
            force: Boolean,
        ) : FuncInvoker<T> {

            private val handle: MethodHandle

            init {
                if (force) {
                    constructor.isAccessible = true
                }
                handle = MethodHandles.lookup().unreflectConstructor(constructor)
            }

            override fun invoke(vararg args: Any?): T {
                return handle.invokeWithArguments(*args).asAny()
            }
        }
    }
}