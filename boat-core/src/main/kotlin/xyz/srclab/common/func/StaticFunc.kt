package xyz.srclab.common.func

import xyz.srclab.common.base.asTyped
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * To handle a func of static.
 */
interface StaticFunc {

    operator fun invoke(vararg args: Any?): Any?

    fun <T> invokeTyped(vararg args: Any?): T {
        return invoke(*args).asTyped()
    }

    companion object {

        @JvmName("of")
        @JvmOverloads
        fun Method.toStaticFunc(force: Boolean = false): StaticFunc {
            return FuncFactory.DEFAULT.createStaticFunc(this, force)
        }

        @JvmName("of")
        @JvmOverloads
        fun Constructor<*>.toStaticFunc(force: Boolean = false): StaticFunc {
            return FuncFactory.DEFAULT.createStaticFunc(this, force)
        }
    }
}