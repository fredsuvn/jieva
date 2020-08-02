package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.lang.ref.BooleanRef;

import java.util.Map;

/**
 * @author sunqian
 */
public class NoResultCacheLoader<K, V> implements CacheLoader<K, V> {

    private final @Nullable BooleanRef noResultFlag;

    NoResultCacheLoader() {
        this.noResultFlag = null;
    }

    NoResultCacheLoader(BooleanRef noResultFlag) {
        this.noResultFlag = noResultFlag;
    }

    @Nullable
    @Override
    public Result<V> load(K key) {
        if (noResultFlag != null) {
            noResultFlag.set(true);
        }
        return null;
    }

    @Override
    public Map<K, Result<V>> loadAll(Iterable<? extends K> keys) {
        return MapKit.empty();
    }
}
