package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.lang.ref.BooleanRef;

/**
 * @author sunqian
 */
final class NoResultCacheLoader<K, V> implements CacheLoader<K, V> {

    private final @Nullable BooleanRef flag;

    NoResultCacheLoader() {
        this.flag = null;
    }

    NoResultCacheLoader(BooleanRef flag) {
        this.flag = flag;
    }

    @Nullable
    @Override
    public Result<V> load(K key) {
        if (flag != null) {
            flag.set(false);
        }
        return null;
    }

    @Override
    public V simplyLoadValue(K key) {
        if (flag != null) {
            flag.set(false);
        }
        return null;
    }
}
