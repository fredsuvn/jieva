package xyz.srclab.common.cache

import java.util.function.Supplier

/**
 * A simple pool interface, prepares reusable objects in advance to ready to use.
 * Default implementation:
 *
 * * [SimplePool];
 * * [SynchronizedSimplePool];
 */
interface Pool<T : Any> {

    /**
     * Returns a ready idle node. If there is no idle node, return a new one.
     */
    fun get(): PoolNode<T>

    /**
     * Returns a ready idle node. If there is no idle node, return null.
     */
    fun getIdle(): PoolNode<T>?

    /**
     * Cleans up (**not clear**) this pool.
     *
     * Sometimes the pool will create extra value node when there is no idle node. These extra nodes may be kept alive
     * for some time, and this method will remove these nodes.
     */
    fun cleanUp()

    companion object {

        /**
         * Returns a simple pool implementation, not thread safe.
         *
         * @see SimplePool
         */
        @JvmStatic
        fun <T : Any> simplePool(
            coreSize: Int,
            maxSize: Int,
            keepAliveMillis: Long,
            loader: Supplier<T>,
        ): SimplePool<T> {
            return SimplePool(coreSize, maxSize, keepAliveMillis, loader)
        }

        /**
         * Returns a synchronized simple pool implementation, thread safe.
         *
         * @see SynchronizedSimplePool
         */
        @JvmStatic
        fun <T : Any> synchronizedPool(
            coreSize: Int,
            maxSize: Int,
            keepAliveMillis: Long,
            loader: Supplier<T>,
        ): SynchronizedSimplePool<T> {
            val pool = SimplePool(coreSize, maxSize, keepAliveMillis, loader)
            return SynchronizedSimplePool(pool)
        }
    }
}