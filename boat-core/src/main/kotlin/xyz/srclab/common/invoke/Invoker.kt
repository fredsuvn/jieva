package xyz.srclab.common.invoke

import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Represents a invokable instance.
 *
 * @see [InvokerProvider]
 * @see [ReflectedInvokerProvider]
 * @see [MethodHandlerInvokerProvider]
 */
interface Invoker {

    /**
     * Invokes this [Invoker]. [object] is owner of this [Invoker] and [args] are arguments.
     */
    fun <T> invoke(`object`: Any?, vararg args: Any?): T {
        return invokeWith(`object`, false, *args)
    }

    /**
     * Forcibly invokes.
     *
     * @see invoke
     */
    fun <T> enforce(`object`: Any?, vararg args: Any?): T {
        return invokeWith(`object`, true, *args)
    }

    /**
     * Invokes this [Invoker] with arguments.
     * [object] is owner of this [Invoker] and [args] are arguments, and [force] tell whether force to invoke.
     */
    fun <T> invokeWith(`object`: Any?, force: Boolean, vararg args: Any?): T

    /**
     * Invokes this [Invoker] with [object] -- owner of this [Invoker].
     */
    fun invokeWith(`object`: Any?): Invoke {
        return object : Invoke {
            override fun <T> startWith(force: Boolean, vararg args: Any?): T {
                return invokeWith(`object`, force, *args)
            }
        }
    }

    companion object : InvokerProvider {

        private val defaultProvider = ReflectedInvokerProvider

        @JvmStatic
        override fun forMethod(method: Method): Invoker {
            return defaultProvider.forMethod(method)
        }

        @JvmStatic
        override fun forMethod(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Invoker {
            return defaultProvider.forMethod(clazz, methodName, *parameterTypes)
        }

        @JvmStatic
        override fun forConstructor(constructor: Constructor<*>): Invoker {
            return defaultProvider.forConstructor(constructor)
        }

        @JvmStatic
        override fun forConstructor(clazz: Class<*>, vararg parameterTypes: Class<*>): Invoker {
            return defaultProvider.forConstructor(clazz, *parameterTypes)
        }

        @JvmStatic
        fun Method.toInvoker(): Invoker {
            return forMethod(this)
        }

        @JvmStatic
        fun Constructor<*>.toInvoker(): Invoker {
            return forConstructor(this)
        }
    }
}