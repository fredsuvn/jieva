package xyz.srclab.common.base

import java.util.*

/**
 * Singleton helper.
 */
interface BSingleton<K : Any, V : Any> {

    /**
     * Returns singleton of specified [key].
     */
    fun get(key: K, singletonSupplier: (key: K) -> V): V

    class Builder<K : Any, V : Any> {

        private var threadSafePolicy: BThreadSafePolicy = BThreadSafePolicy.THREAD_LOCAL
        private var containerGenerator: () -> MutableMap<K, V> = { WeakHashMap() }

        /**
         * Supports [BThreadSafePolicy.THREAD_LOCAL] or [BThreadSafePolicy.NONE].
         */
        fun threadSafePolicy(threadSafePolicy: BThreadSafePolicy) = apply {
            this.threadSafePolicy = threadSafePolicy
        }

        fun singletonMap(singletonMap: () -> MutableMap<K, V>) = apply {
            this.containerGenerator = singletonMap
        }

        fun build(): BSingleton<K, V> {
            return when (threadSafePolicy) {
                BThreadSafePolicy.THREAD_LOCAL -> ThreadLocalSingleton(containerGenerator)
                BThreadSafePolicy.NONE -> NoneThreadLocalSingleton(containerGenerator)
                else -> throw IllegalStateException("Unsupported thread-safe policy: $threadSafePolicy")
            }
        }

        private class ThreadLocalSingleton<K : Any, V : Any>(
            containerGenerator: () -> MutableMap<K, V>
        ) : BSingleton<K, V> {

            private val threadLocalContainer: ThreadLocal<MutableMap<K, V>> =
                ThreadLocal.withInitial(containerGenerator)

            override fun get(key: K, singletonSupplier: (key: K) -> V): V {
                return threadLocalContainer.get().computeIfAbsent(key, singletonSupplier)
            }
        }

        private class NoneThreadLocalSingleton<K : Any, V : Any>(
            containerGenerator: () -> MutableMap<K, V>
        ) : BSingleton<K, V> {

            private val container: MutableMap<K, V> = containerGenerator()

            override fun get(key: K, singletonSupplier: (key: K) -> V): V {
                return container.computeIfAbsent(key, singletonSupplier)
            }
        }
    }

    companion object {

        /**
         * With [BThreadSafePolicy.THREAD_LOCAL] and [WeakHashMap].
         */
        @JvmStatic
        fun <K : Any, V : Any> newSingleton(): BSingleton<K, V> {
            return newBuilder<K, V>().build()
        }

        @JvmStatic
        fun <K : Any, V : Any> newBuilder(): Builder<K, V> {
            return Builder()
        }
    }
}