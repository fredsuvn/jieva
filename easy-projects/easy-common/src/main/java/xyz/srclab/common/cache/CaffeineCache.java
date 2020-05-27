package xyz.srclab.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Null;
import xyz.srclab.common.cache.listener.CacheRemoveListener;

import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class CaffeineCache<K, V> implements Cache<K, V> {

    private final com.github.benmanes.caffeine.cache.Cache<K, Object> caffeine;

    CaffeineCache(CacheBuilder<K, V> builder) {
        Caffeine<K, Object> caffeine = (Caffeine<K, Object>) Caffeine.newBuilder();
        caffeine.maximumSize(builder.getMaxSize());
        if (builder.getExpiry() != null) {
            CacheExpiry expiry = builder.getExpiry();
            if (expiry.getExpiryAfterUpdate() != null) {
                caffeine.expireAfterWrite(expiry.getExpiryAfterUpdate());
            }
            if (expiry.getExpiryAfterCreate() != null) {
                caffeine.expireAfterAccess(expiry.getExpiryAfterCreate());
            }
        }
        if (builder.getRemoveListener() != null) {
            CacheRemoveListener<K, V> cacheRemoveListener = builder.getRemoveListener();
            caffeine.removalListener((key, value, cause) -> {
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
                cacheRemoveListener.afterRemove(key, unmask(value), removeCause);
            });
        }
        if (builder.getLoader() == null) {
            this.caffeine = caffeine.build();
        } else {
            CacheLoader<? super K, @Nullable ? extends V> loader = builder.getLoader();
            this.caffeine = caffeine.build(new com.github.benmanes.caffeine.cache.CacheLoader<K, Object>() {
                @org.checkerframework.checker.nullness.qual.Nullable
                @Override
                public Object load(@NonNull K key) throws Exception {
                    @Nullable V value = loader.load(key);
                    return value == null ? Null.asObject() : value;
                }
            });
        }
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
    public V get(K key, @Nullable V defaultValue) {
        @Nullable Object value = caffeine.getIfPresent(key);
        return value == null ? defaultValue : unmask(value);
    }

    @Override
    public V get(K key, Function<? super K, ? extends V> ifAbsent) {
        Object value = caffeine.get(key, ifAbsent::apply);
        return null;
    }

    @Override
    public V get(K key, CacheLoader<? super K, ? extends V> loader) {
        return null;
    }

    @Override
    public @Immutable Map<K, V> getPresent(Iterable<? extends K> keys) {
        return null;
    }

    @Override
    public void put(K key, @Nullable V value) {

    }

    @Override
    public void put(K key, CacheLoader<? super K, ? extends V> loader) {

    }

    @Override
    public void expire(K key, Duration duration) {

    }

    @Override
    public void remove(K key) {

    }

    @Override
    public void removeAll() {

    }

    private Object mask(@Nullable V value) {
        return value == null ? Null.asObject() : value;
    }

    @Nullable
    private V unmask(Object value) {
        return Null.isNull(value) ? null : (V) value;
    }
}
