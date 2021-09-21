package xyz.srclab.common.invoke

import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Represents a invokable instance.
 *
 * @see [InvokerGenerator]
 * @see [ReflectInvokerGenerator]
 * @see [MethodHandlerInvokerGenerator]
 */
interface Invoker {

    /**
     * Does invoke, not forcibly.
     */
    fun <T> invoke(obj: Any?, vararg args: Any?): T {
        return doInvoke(obj, false, *args)
    }

    /**
     * Forcibly invokes.
     *
     * @see invoke
     */
    fun <T> enforce(obj: Any?, vararg args: Any?): T {
        return doInvoke(obj, true, *args)
    }

    /**
     * Does invoke with [obj] -- specifies invoking object, and [force] -- specifies whether forcibly invoke.
     */
    fun <T> doInvoke(obj: Any?, force: Boolean, vararg args: Any?): T

    /**
     * Prepares an invoking, not forcibly.
     */
    fun prepare(obj: Any?): Invoke {
        return prepare(obj, false)
    }

    /**
     * Prepares an invoking.
     */
    fun prepare(obj: Any?, force: Boolean): Invoke {
        return object : Invoke {
            override fun <T> start(vararg args: Any?): T {
                return doInvoke(obj, force, *args)
            }
        }
    }

    companion object {

        private val defaultProvider = ReflectInvokerGenerator

        @JvmName("ofMethod")
        @JvmStatic
        fun Method.toInvoker(): Invoker {
            return defaultProvider.ofMethod(this)
        }

        @JvmStatic
        fun ofMethod(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Invoker {
            return defaultProvider.ofMethod(clazz, methodName, *parameterTypes)
        }

        @JvmName("ofConstructor")
        @JvmStatic
        fun Constructor<*>.toInvoker(): Invoker {
            return defaultProvider.ofConstructor(this)
        }

        @JvmStatic
        fun ofConstructor(clazz: Class<*>, vararg parameterTypes: Class<*>): Invoker {
            return defaultProvider.ofConstructor(clazz, *parameterTypes)
        }
    }
}