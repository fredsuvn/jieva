package xyz.srclab.common.run

import xyz.srclab.common.collect.toImmutableMap
import xyz.srclab.common.lang.MapAccessor

/**
 * Run context just like [ThreadLocal].
 */
interface RunContext : MapAccessor {

    /**
     * Attach contents of this context, current context as previously is returned.
     */
    @JvmDefault
    fun attach(): Map<Any, Any?> {
        return contents.toImmutableMap()
    }

    /**
     * Reverse an [attach], restoring the previous context.
     */
    fun detach(previous: Map<Any, Any?>) {
        clear()
        contents.putAll(previous)
    }

    companion object {

        @JvmStatic
        fun current(): RunContext {
            return RunContextImpl()
        }

        private class RunContextImpl : RunContext {

            private val threadLocal: ThreadLocal<MutableMap<Any, Any?>> =
                ThreadLocal.withInitial { HashMap() }

            override val contents: MutableMap<Any, Any?>
                get() = threadLocal.get()
        }
    }
}