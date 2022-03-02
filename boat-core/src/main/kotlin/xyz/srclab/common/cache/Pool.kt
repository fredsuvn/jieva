package xyz.srclab.common.cache

import xyz.srclab.common.base.epochMillis
import java.util.*
import java.util.function.Supplier

/**
 * Object pool, it prepares initialized objects to ready to use as core set.
 * If the prepared objects was not enough, the pool will create new one and possibility keep it alive for some time
 * (which is called `idle time`).
 */
interface Pool<T : Any> {

    /**
     * Returns a ready idle node with value. If there is no idle node, return a new one.
     */
    fun get(): Node<T>

    /**
     * Returns a ready idle node with value. If there is no idle node, return null.
     */
    fun getIdle(): Node<T>?

    /**
     * Cleans up (**not clear**) this pool.
     *
     * Sometimes the pool will create extra value node when there is no idle node. These extra nodes may be kept alive
     * for some time, and this method will remove these nodes.
     */
    fun clean()

    /**
     * Node of [Pool], to store the value.
     */
    interface Node<T : Any> {

        /**
         * Returns actual value of this node.
         */
        val value: T

        /**
         * Releases this value back to the pool.
         */
        fun release()
    }

    companion object {

        /**
         * Returns a simple pool implementation, not thread safe.
         */
        @JvmStatic
        fun <T : Any> simplePool(
            coreSize: Int,
            maxSize: Int,
            keepAliveMillis: Long,
            loader: Supplier<T>,
        ): Pool<T> {
            return SimplePool(coreSize, maxSize, keepAliveMillis, loader)
        }

        /**
         * Returns a synchronized pool implementation, thread safe.
         */
        @JvmStatic
        fun <T : Any> synchronizedPool(
            coreSize: Int,
            maxSize: Int,
            keepAliveMillis: Long,
            loader: Supplier<T>,
        ): Pool<T> {
            val pool = simplePool(coreSize, maxSize, keepAliveMillis, loader)
            return pool.toSynchronized()
        }

        /**
         * Returns a synchronized pool implementation, thread safe.
         */
        @JvmName("synchronizedPool")
        @JvmStatic
        fun <T : Any> Pool<T>.toSynchronized(): Pool<T> {
            return SynchronizedPool(this)
        }

        private class SimplePool<T : Any>(
            private val coreSize: Int,
            private val maxSize: Int,
            private val keepAliveMillis: Long,
            private val loader: Supplier<T>,
        ) : Pool<T> {

            private val coreNodes: MutableList<CoreNode<T>> = ArrayList(coreSize)
            private val extNodes: Deque<ExtNode<T>> = LinkedList()
            private var corePointer = 0

            init {
                for (i in 1..coreSize) {
                    coreNodes.add(CoreNode(loader.get()))
                }
            }

            override fun get(): Node<T> {
                val node = getIdle()
                if (node !== null) {
                    return node
                }
                if (extNodes.size < maxSize - coreSize) {
                    val newNode = ExtNode(loader.get(), true)
                    extNodes.addLast(newNode)
                    return newNode
                }
                return NopNode(loader.get())
            }

            override fun getIdle(): Node<T>? {
                val coreNode = getCoreNode()
                if (coreNode !== null) {
                    return coreNode
                }
                return getExtNode()
            }

            private fun getCoreNode(): CoreNode<T>? {
                var count = 0
                while (count < coreSize) {
                    val i = (corePointer and 0x7f_ff_ff_ff) % coreSize
                    val node = coreNodes[i]
                    corePointer++
                    count++
                    if (!node.inUse) {
                        node.inUse = true
                        return node
                    }
                }
                return null
            }

            private fun getExtNode(): ExtNode<T>? {
                var result: ExtNode<T>? = null
                var count = 0
                val extSize = extNodes.size
                var findOne = false
                val now = epochMillis()
                while (count < extSize) {
                    val node = extNodes.pollFirst()
                    if (node === null) {
                        // means extNodes was empty
                        return result
                    }
                    if (node.inUse) {
                        count++
                        extNodes.addLast(node)
                        continue
                    }
                    // find unused node
                    if (!findOne) {
                        // if result is not set
                        findOne = true
                        node.inUse = true
                        result = node
                        extNodes.addLast(node)
                    } else {
                        // check expiration time
                        if (now - node.lastReleaseMillis <= keepAliveMillis) {
                            extNodes.addLast(node)
                        }
                    }
                    count++
                }
                return result
            }

            override fun clean() {
                extNodes.clear()
            }

            private data class CoreNode<T : Any>(
                override val value: T,
                var inUse: Boolean = false,
            ) : Node<T> {
                override fun release() {
                    inUse = false
                }
            }

            private data class ExtNode<T : Any>(
                override val value: T,
                var inUse: Boolean = false,
                var lastReleaseMillis: Long = 0,
            ) : Node<T> {
                override fun release() {
                    inUse = false
                    lastReleaseMillis = epochMillis()
                }
            }

            private data class NopNode<T : Any>(
                override val value: T,
            ) : Node<T> {
                override fun release() {
                }
            }
        }

        private class SynchronizedPool<T : Any>(
            private val pool: Pool<T>,
        ) : Pool<T> {

            @Synchronized
            override fun get(): Node<T> {
                return pool.get()
            }

            @Synchronized
            override fun getIdle(): Node<T>? {
                return pool.getIdle()
            }

            @Synchronized
            override fun clean() {
                pool.clean()
            }
        }
    }
}