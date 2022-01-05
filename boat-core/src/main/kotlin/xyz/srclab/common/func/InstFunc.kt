package xyz.srclab.common.func

import xyz.srclab.common.base.asTyped
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Method

/**
 * To handle a func of instant.
 */
interface InstFunc {

    operator fun invoke(inst: Any, vararg args: Any?): Any?

    fun <T> invokeTyped(inst: Any, vararg args: Any?): T {
        return invoke(inst, *args).asTyped()
    }

    fun toStaticFunc(inst: Any): StaticFunc {
        return object : StaticFunc {
            override fun invoke(vararg args: Any?): Any? {
                return this@InstFunc(inst, *args)
            }
        }
    }

    companion object {

        @JvmName("of")
        @JvmOverloads
        fun Method.toInstFunc(force: Boolean = false, useReflect: Boolean = true): InstFunc {
            return if (useReflect)
                ReflectedInstFunc(this, force)
            else {
                UnreflectedInstFunc(this, force)
            }
        }

        private class ReflectedInstFunc(
            private val method: Method,
            force: Boolean
        ) : InstFunc {

            init {
                if (force) {
                    method.isAccessible = true
                }
            }

            override fun invoke(inst: Any, vararg args: Any?): Any? {
                return method.invoke(inst, *args)
            }
        }

        private class UnreflectedInstFunc(method: Method, force: Boolean) : InstFunc {

            private val handle: MethodHandle

            init {
                if (force) {
                    method.isAccessible = true
                }
                handle = MethodHandles.lookup().unreflect(method)
            }

            override fun invoke(inst: Any, vararg args: Any?): Any? {
                return when (args.size) {
                    0 -> handle.invokeWithArguments(inst)
                    1 -> handle.invokeWithArguments(inst, args[0])
                    2 -> handle.invokeWithArguments(inst, args[0], args[1])
                    3 -> handle.invokeWithArguments(inst, args[0], args[1], args[2])
                    4 -> handle.invokeWithArguments(inst, args[0], args[1], args[2], args[3])
                    5 -> handle.invokeWithArguments(inst, args[0], args[1], args[2], args[3], args[4])
                    6 -> handle.invokeWithArguments(inst, args[0], args[1], args[2], args[3], args[4], args[5])
                    7 -> handle.invokeWithArguments(inst, args[0], args[1], args[2], args[3], args[4], args[5], args[6])
                    else -> {
                        val actualArgs = arrayOfNulls<Any?>(args.size + 1)
                        actualArgs[0] = inst
                        System.arraycopy(args, 0, actualArgs, 1, args.size)
                        handle.invokeWithArguments(*actualArgs)
                    }
                }
            }
        }
    }
}