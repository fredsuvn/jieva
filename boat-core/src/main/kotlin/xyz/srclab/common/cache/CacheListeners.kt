package xyz.srclab.common.cache

interface CacheCreateListener<K, V> {
    fun beforeCreate(key: K)
    fun afterCreate(key: K, value: V)
}

interface CacheReadListener<K, V> {
    fun beforeRead(key: K)
    fun onHit(key: K, value: V)
    fun onMiss(key: K)
}

interface CacheUpdateListener<K, V> {
    fun beforeUpdate(key: K, oldValue: V)
    fun afterUpdate(key: K, oldValue: V, newValue: V)
}

interface CacheRemoveListener<K, V> {
    fun beforeRemove(key: K, value: V, cause: Cause)
    fun afterRemove(key: K, value: V, cause: Cause)

    enum class Cause {

        /**
         * The entry was manually removed by the user. This can result from the user invoking any of the
         * following methods on the cache or map view.
         */
        EXPLICIT {
            override val isEvicted: Boolean
                get() {
                    return false
                }
        },

        /**
         * The entry itself was not actually removed, but its value was replaced by the user. This can
         * result from the user invoking any of the following methods on the cache or map view.
         */
        REPLACED {
            override val isEvicted: Boolean
                get() {
                    return false
                }
        },

        /**
         * The entry was removed automatically because its key or value was garbage-collected.
         */
        COLLECTED {
            override val isEvicted: Boolean
                get() {
                    return true
                }
        },

        /**
         * The entry's expiration timestamp has passed.
         */
        EXPIRED {
            override val isEvicted: Boolean
                get() {
                    return true
                }
        },

        /**
         * The entry was evicted due to size constraints.
         */
        SIZE {
            override val isEvicted: Boolean
                get() {
                    return true
                }
        };

        /**
         * Returns `true` if there was an automatic removal due to eviction (the cause is neither
         * [EXPLICIT] nor [REPLACED]).
         *
         * @return if the entry was automatically removed due to eviction
         */
        abstract val isEvicted: Boolean
    }
}