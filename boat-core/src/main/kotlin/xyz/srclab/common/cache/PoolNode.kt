package xyz.srclab.common.cache

import xyz.srclab.common.base.Val

/**
 * Node of [Pool], to store or release the value.
 */
interface PoolNode<V> : Val<V> {

    /**
     * Releases current value.
     */
    fun release()
}