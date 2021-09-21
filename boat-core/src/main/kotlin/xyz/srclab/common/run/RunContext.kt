package xyz.srclab.common.run

import xyz.srclab.common.base.MapAccessor

/**
 * Context info of a running environment, just like [ThreadLocal].
 */
interface RunContext : MapAccessor {

    /**
     * Returns a copy of current context's contents.
     */
    fun attach(): Map<Any, Any?> {
        return LinkedHashMap(asMap())
    }

    /**
     * Adds given [contents].
     */
    fun detach(contents: Map<Any, Any?>) {
        asMap().putAll(contents)
    }

    companion object {

        @JvmStatic
        fun current(): RunContext {
            return RunContextImpl
        }

        private object RunContextImpl : RunContext {

            private val threadLocal: ThreadLocal<MutableMap<Any, Any?>> =
                ThreadLocal.withInitial { HashMap() }

            override fun asMap(): MutableMap<Any, Any?> {
                return threadLocal.get()
            }
        }
    }
}