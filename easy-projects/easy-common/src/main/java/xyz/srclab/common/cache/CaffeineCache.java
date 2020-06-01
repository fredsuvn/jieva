package xyz.srclab.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.base.Checker;
import xyz.srclab.common.base.Null;
import xyz.srclab.common.cache.listener.CacheRemoveListener;

import java.time.Duration;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class CaffeineCache<K, V> implements Cache<K, V> {

    private final com.github.benmanes.caffeine.cache.Cache<K, Object> caffeine;

    CaffeineCache(CacheBuilder<K, V> builder) {
        Caffeine<K, Object> caffeine = Cast.as(Caffeine.newBuilder());
        caffeine.maximumSize(builder.maxSize());
        if (builder.expiry() != null) {
            CacheExpiry expiry = builder.expiry();
            if (expiry.getExpiryAfterUpdate() != null) {
                caffeine.expireAfterWrite(expiry.getExpiryAfterUpdate());
            }
            if (expiry.getExpiryAfterCreate() != null) {
                caffeine.expireAfterAccess(expiry.getExpiryAfterCreate());
            }
        }
        if (builder.removeListener() != null) {
            CacheRemoveListener<K, V> cacheRemoveListener = builder.removeListener();
            caffeine.removalListener((key, value, cause) -> {
                if (key == null || value == null) {
                    throw new IllegalStateException("Unexpected entry: key = " + key + ", value = " + value);
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
                cacheRemoveListener.afterRemove(key, unmask(value), removeCause);
            });
        }
        if (builder.loader() == null) {
            this.caffeine = caffeine.build();
        } else {
            CacheLoader<? super K, @Nullable ? extends V> loader = builder.loader();
            this.caffeine = caffeine.build(key -> {
                CacheValue<@Nullable ? extends V> entry = loader.load(key);
                return mask(entry.value());
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
        return caffeine.getIfPresent(key) != null;
    }

    @Override
    public V get(K key) {
        @Nullable Object value = caffeine.getIfPresent(key);
        return value == null ? null : unmask(value);
    }

    @Override
    public V get(K key, Function<? super K, ? extends V> ifAbsent) {
        @Nullable Object value = caffeine.get(key, k -> {
            @Nullable V v = ifAbsent.apply(k);
            return mask(v);
        });
        checkEntry(value != null, key);
        return unmask(value);
    }

    @Override
    public V load(K key, CacheLoader<? super K, ? extends V> loader) {
        return get(key, k -> loader.load(k).value());
    }

    @Override
    public V getOrDefault(K key, @Nullable V defaultValue) {
        @Nullable Object value = caffeine.getIfPresent(key);
        return value == null ? defaultValue : unmask(value);
    }

    @Override
    public void put(K key, @Nullable V value) {
        caffeine.put(key, mask(value));
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
        caffeine.invalidate(key);
    }

    @Override
    public void invalidateAll(Iterable<? extends K> keys) {
        caffeine.invalidateAll(keys);
    }

    @Override
    public void invalidateAll() {
        caffeine.invalidateAll();
    }

    private void checkEntry(boolean expression, K key) {
        Checker.checkState(expression, "Unexpected value of key: " + key);
    }
}
