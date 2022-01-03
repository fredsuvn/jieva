package xyz.srclab.common.run

import xyz.srclab.common.base.asTyped

/**
 * Context info of a running environment, just like [ThreadLocal].
 */
interface RunContext {

    fun get(key: Any): Any {
        return asMap()[key] ?: NoSuchElementException("$key")
    }

    fun getOrNull(key: Any): Any? {
        return asMap()[key]
    }

    fun <T : Any> getTyped(key: Any): T {
        return get(key).asTyped()
    }

    fun <T : Any> getTypedOrNull(key: Any): T? {
        return getOrNull(key).asTyped()
    }

    fun set(key: Any, value: Any?) {
        asMap()[key] = value
    }

    /**
     * Returns a [MutableMap] which is associated with this [RunContext], any change affects each other.
     */
    fun asMap(): MutableMap<Any, Any?>

    companion object {

        @JvmStatic
        fun current(): RunContext {
            return ThreadLocalRunContext
        }

        private object ThreadLocalRunContext : RunContext {

            private val threadLocal: ThreadLocal<MutableMap<Any, Any?>> =
                ThreadLocal.withInitial { HashMap() }

            override fun asMap(): MutableMap<Any, Any?> {
                return threadLocal.get()
            }
        }
    }
}