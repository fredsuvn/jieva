package xyz.srclab.common.cache

/**
 * Cache create listener.
 */
interface CacheCreateListener<K, V> {
    /**
     * Callback before creating new entry.
     */
    fun beforeCreate(key: K)

    /**
     * Callback after creating new entry.
     */
    fun afterCreate(key: K, value: V)
}

/**
 * Cache read listener.
 */
interface CacheReadListener<K, V> {
    /**
     * Callback before reading.
     */
    fun beforeRead(key: K)

    /**
     * Callback on hitting the key to read.
     */
    fun onHit(key: K, value: V)

    /**
     * Callback on missing the key to read.
     */
    fun onMiss(key: K)
}

/**
 * Cache update listener.
 */
interface CacheUpdateListener<K, V> {
    /**
     * Callback before updating the entry.
     */
    fun beforeUpdate(key: K, oldValue: V)

    /**
     * Callback after updating the entry.
     */
    fun afterUpdate(key: K, oldValue: V, newValue: V)
}

/**
 * Cache remove listener.
 */
interface CacheRemoveListener<K, V> {
    /**
     * Callback before removing the entry.
     */
    fun beforeRemove(key: K, value: V, cause: Cause)

    /**
     * Callback after removing the entry.
     */
    fun afterRemove(key: K, value: V, cause: Cause)

    /**
     * The cause why the entry was removed.
     */
    enum class Cause {

        /**
         * The entry was manually removed by the user.
         */
        EXPLICIT {
            override val isEvicted: Boolean
                get() {
                    return false
                }
        },

        /**
         * The entry itself was not actually removed, but its value was replaced by the user.
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
         * The entry is expired.
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