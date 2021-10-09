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
    fun prepareFor(obj: Any?): Invoke {
        return prepareFor(obj, false)
    }

    /**
     * Prepares an invoking.
     */
    fun prepareFor(obj: Any?, force: Boolean): Invoke {
        return object : Invoke {
            override fun <T> start(vararg args: Any?): T {
                return doInvoke(obj, force, *args)
            }
        }
    }

    companion object {

        @JvmField
        val DEFAULT_INVOKER_GENERATOR = ReflectInvokerGenerator

        @JvmName("ofMethod")
        @JvmStatic
        fun Method.toInvoker(): Invoker {
            return DEFAULT_INVOKER_GENERATOR.ofMethod(this)
        }

        @JvmStatic
        fun ofMethod(clazz: Class<*>, methodName: String, vararg parameterTypes: Class<*>): Invoker {
            return DEFAULT_INVOKER_GENERATOR.ofMethod(clazz, methodName, *parameterTypes)
        }

        @JvmName("ofConstructor")
        @JvmStatic
        fun Constructor<*>.toInvoker(): Invoker {
            return DEFAULT_INVOKER_GENERATOR.ofConstructor(this)
        }

        @JvmStatic
        fun ofConstructor(clazz: Class<*>, vararg parameterTypes: Class<*>): Invoker {
            return DEFAULT_INVOKER_GENERATOR.ofConstructor(clazz, *parameterTypes)
        }
    }
}