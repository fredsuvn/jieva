package xyz.srclab.common.invoke

import xyz.srclab.common.base.asTyped
import java.lang.reflect.Method

/**
 * Represents invocation operation which depends on an instance,
 * similar to [Method.invoke] of which `obj` parameter is not null.
 */
fun interface InstInvoke {

    /**
     * Do this invocation operation.
     *
     * @param inst the instance on which this invocation depends
     * @param args the arguments of this invocation
     */
    operator fun invoke(inst: Any, vararg args: Any?): Any?

    /**
     * Do this invocation operation, and casts the return value as type of [T].
     *
     * @param inst the instance on which this invocation depends
     * @param args the arguments of this invocation
     */
    fun <T> invokeTyped(inst: Any, vararg args: Any?): T {
        return this.invoke(inst, *args).asTyped()
    }

    /**
     * Returns a [StaticInvoke] of which invocation operation performs as
     * binding the [inst] on this [InstInvoke],
     * equivalent to `InstInvoke.invoke(inst, args)`.
     */
    fun asStaticInvoke(inst: Any): StaticInvoke {
        return InstInvokeAsStaticInvoke(inst, this)
    }

    companion object {

        /**
         * Creates [InstInvoke] from [this] method, using [InvokeProvider.defaultFactory].
         * @param force whether invoke forcibly if the method is not accessible.
         */
        @JvmName("of")
        @JvmOverloads
        fun Method.toInstInvoke(force: Boolean = false): InstInvoke {
            return InvokeProvider.defaultFactory().getInstInvoke(this, force)
        }

        private class InstInvokeAsStaticInvoke(
            private val inst: Any,
            private val instInvoke: InstInvoke,
        ) : StaticInvoke {
            override fun invoke(vararg args: Any?): Any? {
                return instInvoke(inst, *args)
            }
        }
    }
}