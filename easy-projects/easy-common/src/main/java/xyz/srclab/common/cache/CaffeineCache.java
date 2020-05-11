package xyz.srclab.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.cache.listener.CacheRemoveListener;
import xyz.srclab.common.collection.IterableHelper;
import xyz.srclab.common.collection.MapHelper;
import xyz.srclab.common.lang.Ref;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class CaffeineCache<K, V> implements Cache<K, V> {

    private static final Object NULL = new Object();

    private final com.github.benmanes.caffeine.cache.Cache<K, Object> caffeine;

    private final CacheRemoveListener<K, V> cacheRemoveListener;

    CaffeineCache(CacheBuilder<K, V> builder) {
        this.cacheRemoveListener = builder.getRemoveListener();
        Caffeine<K, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(builder.getMaxSize())
                .expireAfterWrite(builder.getExpiryAfterUpdate())
                .expireAfterAccess(builder.getExpiryAfterCreate())
                .softValues()
                .removalListener((key, value, cause) -> {
                    if (key == null || value == null) {
                        return;
                    }
                    CacheRemoveListener.Cause removeCause;
                    switch (cause) {
                        case SIZE:
                            removeCause = CacheRemoveListener.Cause.SIZE;
                            break;
                        case EXPIRED:
                            removeCause = CacheRemoveListener.Cause.EXPIRED;
                            break;
                        case COLLECTED:
                            removeCause = CacheRemoveListener.Cause.COLLECTED;
                            break;
                        case EXPLICIT:
                            removeCause = CacheRemoveListener.Cause.EXPLICIT;
                            break;
                        case REPLACED:
                            removeCause = CacheRemoveListener.Cause.REPLACED;
                            break;
                        default:
                            throw new IllegalStateException("Unknown remove cause: " + cause);
                    }
                    cacheRemoveListener.afterRemove(key, unmaskValue(value), removeCause);
                });
        this.caffeine = caffeine.build();
    }

    @Override
    public boolean has(K key) {
        return caffeine.getIfPresent(key) != null;
    }

    @Override
    public boolean hasAll(Iterable<K> keys) {
        return caffeine.getAllPresent(keys).size() == IterableHelper.asCollection(keys).size();
    }

    @Override
    public boolean hasAny(Iterable<K> keys) {
        return !caffeine.getAllPresent(keys).isEmpty();
    }

    @Override
    public V get(K key) throws NoSuchElementException {
        @Nullable Object value = caffeine.getIfPresent(key);
        Checker.checkElementByKey(value != null, key);
        return unmaskValue(value);
    }

    @Override
    public @Nullable Ref<V> getIfPresent(K key) {
        @Nullable Object value = caffeine.getIfPresent(key);
        return value == null ?
                null : Ref.of(unmaskValue(value));
    }

    @Override
    public V get(K key, Function<K, @Nullable V> ifAbsent) {
        Object value = Objects.requireNonNull(caffeine.get(key, k -> maskValue(ifAbsent.apply(k))));
        return unmaskValue(value);
    }

    @Override
    public V get(K key, CacheValueFunction<K, @Nullable V> ifAbsent) {
        return get(key, (Function<K, @Nullable V>) ifAbsent);
    }

    @Override
    public Map<K, V> getAll(Iterable<K> keys) throws NoSuchElementException {
        Collection<K> keyCollection = IterableHelper.asCollection(keys);
        Map<K, Object> result = caffeine.getAllPresent(keys);
        Checker.checkElementByKey(result.size() == keyCollection.size(), keys);
        return MapHelper.map(result, k -> k, this::unmaskValue);
    }

    @Override
    public Map<K, V> getAll(Iterable<K> keys, Function<K, @Nullable V> ifAbsent) {
        Map<K, Object> result = caffeine.getAll(keys, ks -> {
            Map<K, Object> newValues = new LinkedHashMap<>();
            for (K k : ks) {
                newValues.put(k, maskValue(ifAbsent.apply(k)));
            }
            return newValues;
        });
        return MapHelper.map(result, k -> k, this::unmaskValue);
    }

    @Override
    public Map<K, V> getAll(Iterable<K> keys, CacheValueFunction<K, @Nullable V> ifAbsent) {
        return getAll(keys, (Function<K, @Nullable V>) ifAbsent);
    }

    @Override
    public Map<K, V> getPresent(Iterable<K> keys) {
        Map<K, Object> result = caffeine.getAllPresent(keys);
        return MapHelper.map(result, k -> k, this::unmaskValue);
    }

    @Override
    public void put(K key, @Nullable V value) {
        caffeine.put(key, maskValue(value));
    }

    @Override
    public void put(K key, Function<K, @Nullable V> valueFunction) {
        caffeine.put(key, maskValue(valueFunction.apply(key)));
    }

    @Override
    public void put(K key, CacheValueFunction<K, @Nullable V> valueFunction) {
        put(key, (Function<K, @Nullable V>) valueFunction);
    }

    @Override
    public void putAll(Map<K, @Nullable V> data) {
        caffeine.putAll(MapHelper.map(data, k -> k, this::maskValue));
    }

    @Override
    public void putAll(Iterable<K> keys, Function<K, @Nullable V> valueFunction) {
        Map<K, Object> data = new LinkedHashMap<>();
        for (K key : keys) {
            data.put(key, maskValue(valueFunction.apply(key)));
        }
        caffeine.putAll(data);
    }

    @Override
    public void putAll(Iterable<K> keys, CacheValueFunction<K, @Nullable V> valueFunction) {
        putAll(keys, (Function<K, @Nullable V>) valueFunction);
    }

    @Override
    public void expire(K key, long seconds) {
    }

    @Override
    public void expire(K key, Duration duration) {
    }

    @Override
    public void expire(K key, Function<K, Duration> kDurationFunction) {
    }

    @Override
    public void expireAll(Iterable<K> keys, long seconds) {
    }

    @Override
    public void expireAll(Iterable<K> keys, Duration duration) {
    }

    @Override
    public void expireAll(Iterable<K> keys, Function<K, Duration> kDurationFunction) {
    }

    @Override
    public void remove(K key) {
        caffeine.invalidate(key);
    }

    @Override
    public void removeAll(Iterable<K> keys) {
        caffeine.invalidateAll(keys);
    }

    @Override
    public void removeAll() {
        caffeine.invalidateAll();
    }

    private Object maskValue(@Nullable V value) {
        return value == null ? NULL : value;
    }

    @Nullable
    private V unmaskValue(Object value) {
        return value == NULL ? null : (V) value;
    }
}
