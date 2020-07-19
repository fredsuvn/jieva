package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.NullObjects;
import xyz.srclab.common.lang.ref.BooleanRef;

/**
 * @author sunqian
 */
final class CacheSupport {

    static <K, V> CacheLoader<K, V> newContainsFlagCacheLoader(BooleanRef containsFlag) {
        return new ContainsFlagCacheLoader<>(containsFlag);
    }

    static Object mask(@Nullable Object value) {
        return value == null ? NullObjects.asObject() : value;
    }

    @Nullable
    static <V> V unmask(@Nullable Object value) {
        if (value == null) {
            return null;
        }
        return NullObjects.isNull(value) ? null : Cast.as(value);
    }

    private static final class ContainsFlagCacheLoader<K, V> implements CacheLoader<K, V> {

        private final BooleanRef flag;

        private ContainsFlagCacheLoader(BooleanRef flag) {
            this.flag = flag;
        }

        @Nullable
        @Override
        public Result<V> load(K key) {
            flag.set(false);
            return null;
        }

        @Override
        public V simplyLoadValue(K key) {
            flag.set(false);
            return null;
        }
    }
}
