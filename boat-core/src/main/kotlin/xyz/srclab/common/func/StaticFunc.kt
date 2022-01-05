package xyz.srclab.common.func

import xyz.srclab.common.base.asTyped
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * To handle a func of static.
 */
interface StaticFunc {

    operator fun invoke(vararg args: Any?): Any?

    fun <T> invokeTyped(vararg args: Any?): T {
        return invoke(*args).asTyped()
    }

    companion object {

        @JvmName("of")
        @JvmOverloads
        fun Method.toStaticFunc(force: Boolean = false, useReflect: Boolean = true): StaticFunc {
            return if (useReflect)
                ReflectedFunc(this, force)
            else {
                UnreflectedFunc(this, force)
            }
        }

        @JvmName("of")
        @JvmOverloads
        fun Constructor<*>.toStaticFunc(force: Boolean = false, useReflect: Boolean = true): StaticFunc {
            return if (useReflect)
                ReflectedConstructor(this, force)
            else {
                UnreflectedConstructor(this, force)
            }
        }

        private class ReflectedFunc(
            private val method: Method,
            force: Boolean
        ) : StaticFunc {

            init {
                if (force) {
                    method.isAccessible = true
                }
            }

            override fun invoke(vararg args: Any?): Any? {
                return method.invoke(null, *args)
            }
        }

        private class ReflectedConstructor(
            private val constructor: Constructor<*>,
            force: Boolean
        ) : StaticFunc {

            init {
                if (force) {
                    constructor.isAccessible = true
                }
            }

            override fun invoke(vararg args: Any?): Any? {
                return constructor.newInstance(*args)
            }
        }

        private class UnreflectedFunc(method: Method, force: Boolean) : StaticFunc {

            private val handle: MethodHandle

            init {
                if (force) {
                    method.isAccessible = true
                }
                handle = MethodHandles.lookup().unreflect(method)
            }

            override fun invoke(vararg args: Any?): Any? {
                return handle.invokeWithArguments(*args)
            }
        }

        private class UnreflectedConstructor(constructor: Constructor<*>, force: Boolean) : StaticFunc {

            private val handle: MethodHandle

            init {
                if (force) {
                    constructor.isAccessible = true
                }
                handle = MethodHandles.lookup().unreflectConstructor(constructor)
            }

            override fun invoke(vararg args: Any?): Any? {
                return handle.invokeWithArguments(*args)
            }
        }
    }
}