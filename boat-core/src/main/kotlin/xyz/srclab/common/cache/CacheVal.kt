package xyz.srclab.common.cache

import xyz.srclab.common.base.Val

/**
 * [Val] for cache value.
 */
interface CacheVal<V : Any> : Val<V> {

    companion object {

        /**
         * Returns a [CacheVal] with [v].
         */
        @JvmStatic
        fun <V : Any> of(v: V): CacheVal<V> {
            return CacheValImpl(v)
        }

        private data class CacheValImpl<V : Any>(private val value: V?) : CacheVal<V> {
            override fun get(): V? = value
        }
    }
}