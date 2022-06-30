package xyz.srclab.common.func

import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Represents a function (static method).
 */
fun interface StaticFunc {

    /**
     * Invokes this function.
     *
     * @param args the arguments of this function
     */
    operator fun invoke(vararg args: Any?): Any?

    companion object {

        /**
         * Creates [StaticFunc] from [this] method by [FuncProvider.defaultProvider].
         *
         * @param force whether invoke forcibly if the method is not accessible.
         */
        @JvmName("of")
        @JvmOverloads
        fun Method.toStaticInvoke(force: Boolean = false): StaticFunc {
            return FuncProvider.defaultProvider().getStaticFunc(this, force)
        }

        /**
         * Creates [StaticFunc] from [this] constructor by [FuncProvider.defaultProvider].
         *
         * @param force whether invoke forcibly if the constructor is not accessible.
         */
        @JvmName("of")
        @JvmOverloads
        fun Constructor<*>.toStaticInvoke(force: Boolean = false): StaticFunc {
            return FuncProvider.defaultProvider().getStaticFunc(this, force)
        }
    }
}