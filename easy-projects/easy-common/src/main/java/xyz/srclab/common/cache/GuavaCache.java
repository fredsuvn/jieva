package xyz.srclab.common.cache;

import com.google.common.cache.RemovalCause;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.cache.listener.CacheRemoveListener;
import xyz.srclab.common.collection.iterable.IterableHelper;
import xyz.srclab.common.collection.map.MapHelper;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class GuavaCache<K, V> implements Cache<K, V> {

    private static final Object NULL = new Object();

    private final com.google.common.cache.Cache<K, Object> guava;

    private final CacheRemoveListener<K, V> cacheRemoveListener;

    GuavaCache(CacheBuilder<K, V> builder) {
        this.cacheRemoveListener = builder.getRemoveListener();
        com.google.common.cache.CacheBuilder<K, Object> cacheBuilder = com.google.common.cache.CacheBuilder.newBuilder()
                .maximumSize(builder.getMaxSize())
                .expireAfterWrite(builder.getExpiryAfterUpdate())
                .expireAfterAccess(builder.getExpiryAfterCreate())
                .softValues()
                .removalListener(removalNotification -> {
                    @Nullable K key = removalNotification.getKey();
                    @Nullable Object value = removalNotification.getValue();
                    RemovalCause cause = removalNotification.getCause();
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
        this.guava = cacheBuilder.build();
    }

    @Override
    public boolean has(K key) {
        return guava.getIfPresent(key) != null;
    }

    @Override
    public boolean hasAll(Iterable<K> keys) {
        return guava.getAllPresent(keys).size() == IterableHelper.asCollection(keys).size();
    }

    @Override
    public boolean hasAny(Iterable<K> keys) {
        return !guava.getAllPresent(keys).isEmpty();
    }

    @Override
    public V get(K key) throws NoSuchElementException {
        @Nullable Object value = guava.getIfPresent(key);
        Checker.checkElementByKey(value != null, key);
        return unmaskValue(value);
    }

    @Override
    public V get(K key, Function<K, @Nullable V> ifAbsent) {
        try {
            Object value = Objects.requireNonNull(guava.get(key, () -> maskValue(ifAbsent.apply(key))));
            return unmaskValue(value);
        } catch (ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public V get(K key, CacheValueFunction<K, @Nullable V> ifAbsent) {
        return get(key, (Function<K, @Nullable V>) ifAbsent);
    }

    @Override
    public Map<K, V> getAll(Iterable<K> keys) throws NoSuchElementException {
        Collection<K> keyCollection = IterableHelper.asCollection(keys);
        Map<K, Object> result = guava.getAllPresent(keys);
        Checker.checkElementByKey(result.size() == keyCollection.size(), keys);
        return MapHelper.map(result, k -> k, this::unmaskValue);
    }

    @Override
    public Map<K, V> getAll(Iterable<K> keys, Function<K, @Nullable V> ifAbsent) {
        Collection<K> keyCollection = IterableHelper.asCollection(keys);
        Map<K, Object> present = guava.getAllPresent(keys);
        if (present.size() == keyCollection.size()) {
            return MapHelper.map(present, k -> k, this::unmaskValue);
        }
        Map<K, Object> result = new LinkedHashMap<>(present);
        for (K key : keys) {
            if (present.containsKey(key)) {
                continue;
            }
            result.put(key, get(key, ifAbsent));
        }
        return MapHelper.map(result, k -> k, this::unmaskValue);
    }

    @Override
    public Map<K, V> getAll(Iterable<K> keys, CacheValueFunction<K, @Nullable V> ifAbsent) {
        return getAll(keys, (Function<K, @Nullable V>) ifAbsent);
    }

    @Override
    public Map<K, V> getPresent(Iterable<K> keys) {
        Map<K, Object> result = guava.getAllPresent(keys);
        return MapHelper.map(result, k -> k, this::unmaskValue);
    }

    @Override
    public void put(K key, @Nullable V value) {
        guava.put(key, maskValue(value));
    }

    @Override
    public void put(K key, Function<K, @Nullable V> valueFunction) {
        guava.put(key, maskValue(valueFunction.apply(key)));
    }

    @Override
    public void put(K key, CacheValueFunction<K, @Nullable V> valueFunction) {
        put(key, (Function<K, @Nullable V>) valueFunction);
    }

    @Override
    public void putAll(Map<K, @Nullable V> data) {
        guava.putAll(MapHelper.map(data, k -> k, this::maskValue));
    }

    @Override
    public void putAll(Iterable<K> keys, Function<K, @Nullable V> valueFunction) {
        Map<K, Object> data = new LinkedHashMap<>();
        for (K key : keys) {
            data.put(key, maskValue(valueFunction.apply(key)));
        }
        guava.putAll(data);
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
        guava.invalidate(key);
    }

    @Override
    public void removeAll(Iterable<K> keys) {
        guava.invalidateAll(keys);
    }

    @Override
    public void removeAll() {
        guava.invalidateAll();
    }

    private Object maskValue(@Nullable V value) {
        return value == null ? NULL : value;
    }

    @Nullable
    private V unmaskValue(Object value) {
        return value == NULL ? null : (V) value;
    }
}
