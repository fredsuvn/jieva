package xyz.srclab.common.cache

import xyz.srclab.common.base.currentMillis
import java.util.*
import java.util.function.Supplier

/**
 * Simple implementation for [Pool], not thread-safe.
 *
 * This implementation has a [coreSize] to specify min number of nodes to keep alive forever,
 * a [maxSize] to specify max number of nodes.
 * The nodes in range of [maxSize] - [coreSize] are called `ext` nodes,
 * [keepAliveMillis] is used to set time to keep alive in millis for them.
 *
 * @param coreSize core nodes number
 * @param maxSize max nodes number
 * @param keepAliveMillis time for `ext` nodes to keep alive in millis
 * @param loader value loader when this pool needs to create a new node
 */
open class SimplePool<T : Any>(
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

    override fun get(): PoolNode<T> {
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

    override fun getIdle(): PoolNode<T>? {
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
        val now = currentMillis()
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

    override fun cleanUp() {
        extNodes.clear()
    }

    /**
     * Returns synchronized version of this simple pool.
     */
    fun asSynchronized(): SynchronizedSimplePool<T> {
        return SynchronizedSimplePool(this)
    }

    private data class CoreNode<T : Any>(
        override val value: T,
        var inUse: Boolean = false,
    ) : PoolNode<T> {
        override fun release() {
            inUse = false
        }
    }

    private data class ExtNode<T : Any>(
        override val value: T,
        var inUse: Boolean = false,
        var lastReleaseMillis: Long = 0,
    ) : PoolNode<T> {
        override fun release() {
            inUse = false
            lastReleaseMillis = currentMillis()
        }
    }

    private data class NopNode<T : Any>(
        override val value: T,
    ) : PoolNode<T> {
        override fun release() {
        }
    }
}

/**
 * Synchronized version for [SimplePool], thread-safe.
 *
 * @see SimplePool
 */
open class SynchronizedSimplePool<T : Any>(
    private val pool: SimplePool<T>,
) : Pool<T> {

    @Synchronized
    override fun get(): PoolNode<T> {
        return pool.get()
    }

    @Synchronized
    override fun getIdle(): PoolNode<T>? {
        return pool.getIdle()
    }

    @Synchronized
    override fun cleanUp() {
        pool.cleanUp()
    }
}