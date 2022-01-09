package xyz.srclab.common.func

import xyz.srclab.common.base.getJavaMajorVersion
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Factory to create [InstFunc] and [StaticFunc].
 */
interface FuncFactory {

    fun createInstFunc(method: Method): InstFunc = createInstFunc(method, false)

    fun createInstFunc(method: Method, force: Boolean): InstFunc

    fun createStaticFunc(method: Method): StaticFunc = createStaticFunc(method, false)

    fun createStaticFunc(method: Method, force: Boolean): StaticFunc

    fun createStaticFunc(constructor: Constructor<*>): StaticFunc = createStaticFunc(constructor, false)

    fun createStaticFunc(constructor: Constructor<*>, force: Boolean): StaticFunc

    companion object {

        private var defaultFactory: FuncFactory = run {
            val javaVersion = getJavaMajorVersion()
            if (javaVersion <= 8) {
                return@run reflected()
            }
            unreflected()
        }

        @JvmStatic
        fun defaultFactory(): FuncFactory {
            return defaultFactory
        }

        @JvmStatic
        fun setDefaultFactory(defaultFactory: FuncFactory) {
            this.defaultFactory = defaultFactory
        }

        @JvmStatic
        fun reflected(): FuncFactory {
            return ReflectedFuncFactory
        }

        @JvmStatic
        fun unreflected(): FuncFactory {
            return UnreflectedFuncFactory
        }

        private object ReflectedFuncFactory : FuncFactory {

            override fun createInstFunc(method: Method, force: Boolean): InstFunc {
                return ReflectedInstFunc(method, force)
            }

            override fun createStaticFunc(method: Method, force: Boolean): StaticFunc {
                return ReflectedStaticFunc(method, force)
            }

            override fun createStaticFunc(constructor: Constructor<*>, force: Boolean): StaticFunc {
                return ReflectedConstructorFunc(constructor, force)
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

            private class ReflectedStaticFunc(
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

            private class ReflectedConstructorFunc(
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
        }

        private object UnreflectedFuncFactory : FuncFactory {

            override fun createInstFunc(method: Method, force: Boolean): InstFunc {
                return UnreflectedInstFunc(method, force)
            }

            override fun createStaticFunc(method: Method, force: Boolean): StaticFunc {
                return UnreflectedStaticFunc(method, force)
            }

            override fun createStaticFunc(constructor: Constructor<*>, force: Boolean): StaticFunc {
                return UnreflectedConstructorFunc(constructor, force)
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
                        else -> {
                            val actualArgs = arrayOfNulls<Any?>(args.size + 1)
                            actualArgs[0] = inst
                            System.arraycopy(args, 0, actualArgs, 1, args.size)
                            handle.invokeWithArguments(*actualArgs)
                        }
                    }
                }
            }

            private class UnreflectedStaticFunc(method: Method, force: Boolean) : StaticFunc {

                private val handle: MethodHandle

                init {
                    if (force) {
                        method.isAccessible = true
                    }
                    handle = MethodHandles.lookup().unreflect(method)
                }

                override fun invoke(vararg args: Any?): Any? {
                    return invokeWithArguments(handle, *args)
                }
            }

            private class UnreflectedConstructorFunc(constructor: Constructor<*>, force: Boolean) : StaticFunc {

                private val handle: MethodHandle

                init {
                    if (force) {
                        constructor.isAccessible = true
                    }
                    handle = MethodHandles.lookup().unreflectConstructor(constructor)
                }

                override fun invoke(vararg args: Any?): Any? {
                    return invokeWithArguments(handle, *args)
                }
            }

            private fun invokeWithArguments(handle: MethodHandle, vararg args: Any?): Any? {
                return when (args.size) {
                    0 -> handle.invokeWithArguments()
                    1 -> handle.invokeWithArguments(args[0])
                    2 -> handle.invokeWithArguments(args[0], args[1])
                    3 -> handle.invokeWithArguments(args[0], args[1], args[2])
                    4 -> handle.invokeWithArguments(args[0], args[1], args[2], args[3])
                    5 -> handle.invokeWithArguments(args[0], args[1], args[2], args[3], args[4])
                    6 -> handle.invokeWithArguments(args[0], args[1], args[2], args[3], args[4], args[5])
                    7 -> handle.invokeWithArguments(args[0], args[1], args[2], args[3], args[4], args[5], args[6])
                    else -> handle.invokeWithArguments(*args)
                }
            }
        }
    }
}