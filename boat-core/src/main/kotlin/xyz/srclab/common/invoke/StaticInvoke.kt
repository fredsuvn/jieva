package xyz.srclab.common.invoke

import xyz.srclab.common.base.asTyped
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Represents invocation operation which doesn't depend on an instance,
 * similar to [Method.invoke] of which `obj` parameter is null.
 */
interface StaticInvoke {

    /**
     * Do this invocation operation.
     *
     * @param args the arguments of this invocation
     */
    operator fun invoke(vararg args: Any?): Any?

    /**
     * Do this invocation operation, and casts the return value as type of [T].
     *
     * @param args the arguments of this invocation
     */
    fun <T> invokeTyped(vararg args: Any?): T {
        return this.invoke(*args).asTyped()
    }

    companion object {

        /**
         * Creates [StaticInvoke] from [this] method, using [InvokeProvider.defaultFactory].
         * @param force whether invoke forcibly if the method is not accessible.
         */
        @JvmName("of")
        @JvmOverloads
        fun Method.toStaticInvoke(force: Boolean = false): StaticInvoke {
            return InvokeProvider.defaultFactory().getStaticInvoke(this, force)
        }

        /**
         * Creates [StaticInvoke] from [this] constructor, using [InvokeProvider.defaultFactory].
         * @param force whether invoke forcibly if the constructor is not accessible.
         */
        @JvmName("of")
        @JvmOverloads
        fun Constructor<*>.toStaticInvoke(force: Boolean = false): StaticInvoke {
            return InvokeProvider.defaultFactory().getStaticInvoke(this, force)
        }
    }
}