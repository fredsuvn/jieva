package xyz.srclab.common.invoke

import xyz.srclab.common.base.getJavaMajorVersion
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Provider to get [InstInvoke] and [StaticInvoke].
 */
interface InvokeProvider {

    /**
     * Returns [InstInvoke] from [method].
     */
    fun getInstInvoke(method: Method): InstInvoke = getInstInvoke(method, false)

    /**
     * Returns [InstInvoke] from [method].
     * @param force whether invoke forcibly if the method is not accessible.
     */
    fun getInstInvoke(method: Method, force: Boolean): InstInvoke

    /**
     * Returns [InstInvoke] from [method].
     */
    fun getStaticInvoke(method: Method): StaticInvoke = getStaticInvoke(method, false)

    /**
     * Returns [InstInvoke] from [method].
     * @param force whether invoke forcibly if the method is not accessible.
     */
    fun getStaticInvoke(method: Method, force: Boolean): StaticInvoke

    /**
     * Returns [InstInvoke] from [constructor].
     */
    fun getStaticInvoke(constructor: Constructor<*>): StaticInvoke = getStaticInvoke(constructor, false)

    /**
     * Returns [InstInvoke] from [constructor].
     * @param force whether invoke forcibly if the constructor is not accessible.
     */
    fun getStaticInvoke(constructor: Constructor<*>, force: Boolean): StaticInvoke

    companion object {

        private var defaultProvider: InvokeProvider = run {
            val javaVersion = getJavaMajorVersion()
            if (javaVersion <= 8) {
                return@run withReflected()
            }
            withUnreflected()
        }

        /**
         * Gets default [InvokeProvider].
         */
        @JvmStatic
        fun defaultProvider(): InvokeProvider {
            return defaultProvider
        }

        /**
         * Sets default [InvokeProvider].
         */
        @JvmStatic
        fun setDefaultProvider(provider: InvokeProvider) {
            this.defaultProvider = provider
        }

        /**
         * Returns the [InvokeProvider] which uses reflection to do invocation.
         */
        @JvmStatic
        fun withReflected(): InvokeProvider {
            return ReflectedInvokeProvider
        }

        /**
         * Returns the [InvokeProvider] which uses unreflected (by [MethodHandle]) to do invocation.
         */
        @JvmStatic
        fun withUnreflected(): InvokeProvider {
            return UnreflectedInvokeProvider
        }

        private object ReflectedInvokeProvider : InvokeProvider {

            override fun getInstInvoke(method: Method, force: Boolean): InstInvoke {
                return ReflectedInstInvoke(method, force)
            }

            override fun getStaticInvoke(method: Method, force: Boolean): StaticInvoke {
                return ReflectedStaticInvoke(method, force)
            }

            override fun getStaticInvoke(constructor: Constructor<*>, force: Boolean): StaticInvoke {
                return ReflectedConstructorInvoke(constructor, force)
            }

            private class ReflectedInstInvoke(
                private val method: Method,
                force: Boolean
            ) : InstInvoke {

                init {
                    if (force) {
                        method.isAccessible = true
                    }
                }

                override fun invoke(inst: Any, vararg args: Any?): Any? {
                    return method.invoke(inst, *args)
                }
            }

            private class ReflectedStaticInvoke(
                private val method: Method,
                force: Boolean
            ) : StaticInvoke {

                init {
                    if (force) {
                        method.isAccessible = true
                    }
                }

                override fun invoke(vararg args: Any?): Any? {
                    return method.invoke(null, *args)
                }
            }

            private class ReflectedConstructorInvoke(
                private val constructor: Constructor<*>,
                force: Boolean
            ) : StaticInvoke {

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

        private object UnreflectedInvokeProvider : InvokeProvider {

            override fun getInstInvoke(method: Method, force: Boolean): InstInvoke {
                return UnreflectedInstInvoke(method, force)
            }

            override fun getStaticInvoke(method: Method, force: Boolean): StaticInvoke {
                return UnreflectedStaticInvoke(method, force)
            }

            override fun getStaticInvoke(constructor: Constructor<*>, force: Boolean): StaticInvoke {
                return UnreflectedConstructorInvoke(constructor, force)
            }

            private class UnreflectedInstInvoke(method: Method, force: Boolean) : InstInvoke {

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

            private class UnreflectedStaticInvoke(method: Method, force: Boolean) : StaticInvoke {

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

            private class UnreflectedConstructorInvoke(constructor: Constructor<*>, force: Boolean) : StaticInvoke {

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