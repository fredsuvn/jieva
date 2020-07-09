package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.lang.ref.BooleanRef;

/**
 * @author sunqian
 */
final class CacheSupport {

    static Object DEFAULT_VALUE = new Object();

    static <K, V> CacheLoader<K, V> newContainsCacheLoader(BooleanRef containsFlag) {
        return new ContainsCacheLoader<>(containsFlag);
    }

    private static final class ContainsCacheLoader<K, V> implements CacheLoader<K, V> {

        private final BooleanRef containsFlag;

        private ContainsCacheLoader(BooleanRef containsFlag) {
            this.containsFlag = containsFlag;
        }

        @Override
        public @Nullable CacheValue<V> loadDetail(K key) {
            containsFlag.set(false);
            return null;
        }

        @Override
        public V load(K key) {
            containsFlag.set(false);
            return null;
        }
    }
}
