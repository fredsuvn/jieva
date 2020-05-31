package xyz.srclab.common.cache;

import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.base.Null;
import xyz.srclab.common.cache.listener.CacheRemoveListener;
import xyz.srclab.common.exception.ExceptionWrapper;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class GuavaCache<K, V> implements Cache<K, V> {

    private static final Object NULL = new Object();

    private final com.google.common.cache.Cache<K, Object> guava;

    GuavaCache(CacheBuilder<K, V> builder) {
        com.google.common.cache.CacheBuilder<K, Object> guava =
                Cast.as(com.google.common.cache.CacheBuilder.newBuilder());
        guava.maximumSize(builder.maxSize());
        if (builder.expiry() != null) {
            CacheExpiry expiry = builder.expiry();
            if (expiry.getExpiryAfterUpdate() != null) {
                guava.expireAfterWrite(expiry.getExpiryAfterUpdate());
            }
            if (expiry.getExpiryAfterCreate() != null) {
                guava.expireAfterAccess(expiry.getExpiryAfterCreate());
            }
        }
        if (builder.removeListener() != null) {
            CacheRemoveListener<K, V> cacheRemoveListener = builder.removeListener();
            guava.removalListener(new RemovalListener<K, Object>() {
                @Override
                public void onRemoval(RemovalNotification<K, Object> removalNotification) {
                    @Nullable K key = removalNotification.getKey();
                    @Nullable Object value = removalNotification.getValue();
                    if (key == null || value == null) {
                        throw new IllegalStateException("Unexpected entry: key = " + key + ", value = " + value);
                    }
                    RemovalCause cause = removalNotification.getCause();
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
                    cacheRemoveListener.afterRemove(key, unmask(value), removeCause);
                }
            });
        }
        if (builder.loader() == null) {
            this.guava = guava.build();
        } else {
            CacheLoader<? super K, @Nullable ? extends V> loader = builder.loader();
            this.guava = guava.build(new com.google.common.cache.CacheLoader<K, Object>() {
                @Override
                public Object load(K k) {
                    CacheEntry<? super K, @Nullable ? extends V> entry = loader.load(k);
                    return mask(entry.value());
                }
            });
        }
    }

    private Object mask(@Nullable V value) {
        return value == null ? Null.asObject() : value;
    }

    @Nullable
    private V unmask(Object value) {
        return Null.isNull(value) ? null : Cast.as(value);
    }

    @Override
    public boolean contains(K key) {
        return guava.getIfPresent(key) != null;
    }

    @Override
    public V get(K key) {
        @Nullable Object value = guava.getIfPresent(key);
        return value == null ? null : unmask(value);
    }

    @Override
    public V get(K key, @Nullable V defaultValue) {
        @Nullable Object value = guava.getIfPresent(key);
        return value == null ? defaultValue : unmask(value);
    }

    @Override
    public V get(K key, Function<? super K, ? extends V> ifAbsent) {
        @Nullable Object value = null;
        try {
            value = guava.get(key, () -> {
                @Nullable V v = ifAbsent.apply(key);
                return mask(v);
            });
        } catch (ExecutionException e) {
            throw new ExceptionWrapper(e);
        }
        checkEntry(value != null, key);
        return unmask(value);
    }

    @Override
    public V load(K key, CacheLoader<? super K, ? extends V> loader) {
        return get(key, k -> loader.load(k).value());
    }

    @Override
    public void put(K key, @Nullable V value) {
        guava.put(key, mask(value));
    }

    @Override
    public void put(CacheEntry<? extends K, ? extends V> entry) {
        put(entry.key(), entry.value());
    }

    @Override
    public void expire(K key, long seconds) {
    }

    @Override
    public void expire(K key, Duration duration) {
    }

    @Override
    public void invalidate(K key) {
        guava.invalidate(key);
    }

    @Override
    public void invalidateAll(Iterable<? extends K> keys) {
        guava.invalidateAll(keys);
    }

    @Override
    public void invalidateAll() {
        guava.invalidateAll();
    }

    private void checkEntry(boolean expression, K key) {
        Checker.checkState(expression, "Unexpected value of key: " + key);
    }
}
