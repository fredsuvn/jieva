package xyz.srclab.common.run

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.MapAccessor

/**
 * Run context just like [ThreadLocal].
 */
interface RunContext : MapAccessor {

    /**
     * Attach contents of this context, current context as previously is returned.
     */
    @JvmDefault
    fun attach(): Attach {
        return object : Attach {
            override val contents: Map<Any, Any?> = this@RunContext.asMap()
        }
    }

    /**
     * Reverse an [attach], restoring the previous context.
     */
    fun detach(previous: Attach) {
        clear()
        asMap().putAll(previous.contents)
    }

    interface Attach {

        @get:JvmName("contents")
        @Suppress(INAPPLICABLE_JVM_NAME)
        val contents: Map<Any, Any?>
    }

    companion object {

        @JvmStatic
        fun current(): RunContext {
            return RunContextImpl(null)
        }

        private class RunContextImpl(attach: Attach?) : RunContext {

            private val threadLocal: ThreadLocal<MutableMap<Any, Any?>> =
                ThreadLocal.withInitial { HashMap(attach?.contents ?: emptyMap()) }

            override fun asMap(): MutableMap<Any, Any?> {
                return threadLocal.get()
            }
        }
    }
}