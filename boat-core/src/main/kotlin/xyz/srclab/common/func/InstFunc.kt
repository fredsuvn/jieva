package xyz.srclab.common.func

import java.lang.reflect.Method

/**
 * Represents an instance function (method).
 */
fun interface InstFunc {

    /**
     * Invokes this function.
     *
     * @param inst the instance where this function in
     * @param args the arguments of this function
     */
    operator fun invoke(inst: Any, vararg args: Any?): Any?

    /**
     * Returns a [StaticFunc] from binding given [inst] on this [InstFunc],
     * equivalent to `InstInvoke.invoke(inst, args)`.
     */
    fun toStaticInvoke(inst: Any): StaticFunc {
        return InstInvokeAsStaticFunc(inst, this)
    }

    companion object {

        /**
         * Creates [InstFunc] from [this] method by [FuncProvider.defaultProvider].
         *
         * @param force whether invoke forcibly if the method is not accessible.
         */
        @JvmName("of")
        @JvmOverloads
        fun Method.toInstInvoke(force: Boolean = false): InstFunc {
            return FuncProvider.defaultProvider().getInstFunc(this, force)
        }

        private class InstInvokeAsStaticFunc(
            private val inst: Any,
            private val instFunc: InstFunc,
        ) : StaticFunc {
            override fun invoke(vararg args: Any?): Any? {
                return instFunc(inst, *args)
            }
        }
    }
}