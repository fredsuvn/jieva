package xyz.srclab.common.func

import xyz.srclab.common.base.getJavaMajorVersion
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Provider to generate [InstFunc] and [StaticFunc].
 */
interface FuncProvider {

    /**
     * Returns [InstFunc] from [method].
     */
    fun getInstFunc(method: Method): InstFunc = getInstFunc(method, false)

    /**
     * Returns [InstFunc] from [method].
     *
     * @param force whether invoke forcibly if the method is not accessible.
     */
    fun getInstFunc(method: Method, force: Boolean): InstFunc

    /**
     * Returns [InstFunc] from [method].
     */
    fun getStaticFunc(method: Method): StaticFunc = getStaticFunc(method, false)

    /**
     * Returns [InstFunc] from [method].
     *
     * @param force whether invoke forcibly if the method is not accessible.
     */
    fun getStaticFunc(method: Method, force: Boolean): StaticFunc

    /**
     * Returns [InstFunc] from [constructor].
     */
    fun getStaticFunc(constructor: Constructor<*>): StaticFunc = getStaticFunc(constructor, false)

    /**
     * Returns [InstFunc] from [constructor].
     *
     * @param force whether invoke forcibly if the constructor is not accessible.
     */
    fun getStaticFunc(constructor: Constructor<*>, force: Boolean): StaticFunc

    companion object {

        private var defaultProvider: FuncProvider = run {
            val javaVersion = getJavaMajorVersion()
            if (javaVersion <= 8) {
                return@run withReflected()
            }
            withUnreflected()
        }

        /**
         * Gets default [FuncProvider].
         */
        @JvmStatic
        fun defaultProvider(): FuncProvider {
            return defaultProvider
        }

        /**
         * Sets default [FuncProvider].
         */
        @JvmStatic
        fun setDefaultProvider(provider: FuncProvider) {
            this.defaultProvider = provider
        }

        /**
         * Returns the [FuncProvider] which uses reflection to do invocation.
         */
        @JvmStatic
        fun withReflected(): FuncProvider {
            return ReflectedFuncProvider
        }

        /**
         * Returns the [FuncProvider] which uses unreflected (by [MethodHandle]) to do invocation.
         */
        @JvmStatic
        fun withUnreflected(): FuncProvider {
            return UnreflectedFuncProvider
        }

        private object ReflectedFuncProvider : FuncProvider {

            override fun getInstFunc(method: Method, force: Boolean): InstFunc {
                return ReflectedInstFunc(method, force)
            }

            override fun getStaticFunc(method: Method, force: Boolean): StaticFunc {
                return ReflectedStaticFunc(method, force)
            }

            override fun getStaticFunc(constructor: Constructor<*>, force: Boolean): StaticFunc {
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

        private object UnreflectedFuncProvider : FuncProvider {

            override fun getInstFunc(method: Method, force: Boolean): InstFunc {
                return UnreflectedInstFunc(method, force)
            }

            override fun getStaticFunc(method: Method, force: Boolean): StaticFunc {
                return UnreflectedStaticFunc(method, force)
            }

            override fun getStaticFunc(constructor: Constructor<*>, force: Boolean): StaticFunc {
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
                        0 -> handle.invoke(inst)
                        1 -> handle.invoke(inst, args[0])
                        2 -> handle.invoke(inst, args[0], args[1])
                        3 -> handle.invoke(inst, args[0], args[1], args[2])
                        4 -> handle.invoke(inst, args[0], args[1], args[2], args[3])
                        5 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4])
                        6 -> handle.invoke(inst, args[0], args[1], args[2], args[3], args[4], args[5])
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
                    0 -> handle.invoke()
                    1 -> handle.invoke(args[0])
                    2 -> handle.invoke(args[0], args[1])
                    3 -> handle.invoke(args[0], args[1], args[2])
                    4 -> handle.invoke(args[0], args[1], args[2], args[3])
                    5 -> handle.invoke(args[0], args[1], args[2], args[3], args[4])
                    6 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5])
                    7 -> handle.invoke(args[0], args[1], args[2], args[3], args[4], args[5], args[6])
                    else -> handle.invokeWithArguments(*args)
                }
            }
        }
    }
}