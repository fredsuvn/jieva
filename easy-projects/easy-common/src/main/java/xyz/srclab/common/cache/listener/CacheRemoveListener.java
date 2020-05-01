package xyz.srclab.common.cache.listener;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface CacheRemoveListener<K, V> extends CacheListener<K, V> {

    void beforeRemove(K key, @Nullable V value, Cause cause);

    void afterRemove(K key, @Nullable V value, Cause cause);

    enum Cause {

        /**
         * The entry was manually removed by the user. This can result from the user invoking any of the
         * following methods on the cache or map view.
         */
        EXPLICIT {
            @Override
            public boolean isEvicted() {
                return false;
            }
        },

        /**
         * The entry itself was not actually removed, but its value was replaced by the user. This can
         * result from the user invoking any of the following methods on the cache or map view.
         */
        REPLACED {
            @Override
            public boolean isEvicted() {
                return false;
            }
        },

        /**
         * The entry was removed automatically because its key or value was garbage-collected.
         */
        COLLECTED {
            @Override
            public boolean isEvicted() {
                return true;
            }
        },

        /**
         * The entry's expiration timestamp has passed.
         */
        EXPIRED {
            @Override
            public boolean isEvicted() {
                return true;
            }
        },

        /**
         * The entry was evicted due to size constraints.
         */
        SIZE {
            @Override
            public boolean isEvicted() {
                return true;
            }
        };

        /**
         * Returns {@code true} if there was an automatic removal due to eviction (the cause is neither
         * {@link #EXPLICIT} nor {@link #REPLACED}).
         *
         * @return if the entry was automatically removed due to eviction
         */
        public abstract boolean isEvicted();
    }
}
