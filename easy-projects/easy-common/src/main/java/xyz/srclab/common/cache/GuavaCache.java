package xyz.srclab.common.cache;

import com.google.common.cache.RemovalCause;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Check;
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
            CacheRemoveListener<? super K, ? super V> cacheRemoveListener = builder.removeListener();
            guava.removalListener(removalNotification -> {
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
            });
        }
        if (builder.loader() == null) {
            this.guava = guava.build();
        } else {
            CacheLoader<? super K, @Nullable ? extends V> loader = builder.loader();
            this.guava = guava.build(new com.google.common.cache.CacheLoader<K, Object>() {
                @Override
                public Object load(K k) {
                    CacheValue<@Nullable ? extends V> entry = loader.load(k);
                    return mask(entry.value());
                }
            });
        }
    }

    GuavaCache(com.google.common.cache.Cache<K, Object> guavaCache) {
        this.guava = guavaCache;
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
    public V get(K key, Function<? super K, ? extends V> ifAbsent) {
        @Nullable Object value;
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
    public V getOrDefault(K key, @Nullable V defaultValue) {
        @Nullable Object value = guava.getIfPresent(key);
        return value == null ? defaultValue : unmask(value);
    }

    @Override
    public void put(K key, @Nullable V value) {
        guava.put(key, mask(value));
    }

    @Override
    public void put(K key, CacheValue<? extends V> entry) {
        put(key, entry.value());
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

    private Object mask(@Nullable V value) {
        return value == null ? Null.asObject() : value;
    }

    @Nullable
    private V unmask(Object value) {
        return Null.isNull(value) ? null : Cast.as(value);
    }

    private void checkEntry(boolean expression, K key) {
        Check.checkState(expression, "Unexpected value of key: " + key);
    }
}
