package xyz.srclab.common.func

import xyz.srclab.common.base.asTyped
import java.lang.reflect.Method

/**
 * To handle a func of instant.
 */
interface InstFunc {

    operator fun invoke(inst: Any, vararg args: Any?): Any?

    fun <T> invokeTyped(inst: Any, vararg args: Any?): T {
        return invoke(inst, *args).asTyped()
    }

    fun asStaticFunc(inst: Any): StaticFunc {
        return object : StaticFunc {
            override fun invoke(vararg args: Any?): Any? {
                return this@InstFunc(inst, *args)
            }
        }
    }

    companion object {

        @JvmName("of")
        @JvmOverloads
        fun Method.toInstFunc(force: Boolean = false): InstFunc {
            return FuncFactory.defaultFactory().createInstFunc(this, force)
        }
    }
}