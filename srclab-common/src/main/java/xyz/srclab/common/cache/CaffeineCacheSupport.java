package xyz.srclab.common.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.*;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.As;
import xyz.srclab.common.cache.listener.CacheRemoveListener;
import xyz.srclab.common.collection.MapKit;

import java.time.Duration;
import java.util.Map;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class CaffeineCacheSupport {

    static <K, V> xyz.srclab.common.cache.Cache<K, V> newCaffeineCache(Cache<K, Object> caffeineCache) {
        return new CaffeineCache<>(caffeineCache);
    }

    static <K, V> xyz.srclab.common.cache.Cache<K, V> newLoadingCaffeineCache(LoadingCache<K, Object> loadingCache) {
        return new LoadingCaffeineCache<>(loadingCache);
    }

    static <K, V> Caffeine<K, Object> toCaffeineCacheBuilder(
            xyz.srclab.common.cache.CacheBuilder<K, V> builder) {
        Caffeine<K, Object> caffeineBuilder = As.notNull(Caffeine.newBuilder());
        caffeineBuilder.maximumSize(builder.maxSize());
        if (builder.expiry() != null) {
            CacheExpiry expiry = builder.expiry();
            @Nullable Duration expiryAfterCreate = expiry.expiryAfterCreate();
            @Nullable Duration expiryAfterRead = expiry.expiryAfterRead();
            @Nullable Duration expiryAfterUpdate = expiry.expiryAfterUpdate();
            expireAfterAccess(expiryAfterCreate, expiryAfterRead, expiryAfterUpdate, caffeineBuilder);
            expireAfterWrite(expiryAfterCreate, expiryAfterUpdate, caffeineBuilder);
        }
        if (builder.removeListener() != null) {
            CacheRemoveListener<? super K, ? super V> cacheRemoveListener = builder.removeListener();
            caffeineBuilder.removalListener(new RemovalListenerImpl<>(cacheRemoveListener));
        }
        return caffeineBuilder;
    }

    static <K, V> CacheLoader<K, Object> toCaffeineCacheLoader(xyz.srclab.common.cache.CacheLoader<K, V> loader) {
        return new CacheLoader<K, Object>() {
            @Nullable
            @Override
            public Object load(K key) {
                @Nullable xyz.srclab.common.cache.CacheLoader.Result<? extends V> result = loader.load(key);
                if (result == null) {
                    return null;
                }
                if (!result.needCache()) {
                    throw new NotCacheException(result.value());
                }
                return CacheSupport.mask(result.value());
            }
        };
    }

    private static <K> void expireAfterAccess(
            @Nullable Duration expiryAfterCreate,
            @Nullable Duration expiryAfterRead,
            @Nullable Duration expiryAfterUpdate,
            Caffeine<K, Object> builder
    ) {
        if (expiryAfterRead != null) {
            builder.expireAfterAccess(expiryAfterRead);
            return;
        }
        if (expiryAfterCreate != null) {
            builder.expireAfterAccess(expiryAfterCreate);
            return;
        }
        if (expiryAfterUpdate != null) {
            builder.expireAfterAccess(expiryAfterUpdate);
        }
    }

    private static <K> void expireAfterWrite(
            @Nullable Duration expiryAfterCreate,
            @Nullable Duration expiryAfterUpdate,
            Caffeine<K, Object> builder
    ) {
        if (expiryAfterUpdate != null) {
            builder.expireAfterWrite(expiryAfterUpdate);
            return;
        }
        if (expiryAfterCreate != null) {
            builder.expireAfterWrite(expiryAfterCreate);
        }
    }

    private static class CaffeineCache<K, V> extends AbstractCache<K, V> implements FixedExpiryCache<K, V> {

        protected final Cache<K, Object> caffeineCache;

        private CaffeineCache(Cache<K, Object> caffeineCache) {
            this.caffeineCache = caffeineCache;
        }

        @Override
        public V get(K key) {
            return CacheSupport.unmask(caffeineCache.getIfPresent(key));
        }

        @Override
        public V get(K key, Function<? super K, ? extends V> function) {
            return CacheSupport.unmask(caffeineCache.get(key, function));
        }

        @Override
        public @Immutable Map<K, V> getPresent(Iterable<? extends K> keys) {
            Map<K,Object> result = caffeineCache.getAllPresent(keys);
            return MapKit.map(result, k -> k, CacheSupport::unmask);
        }

        @Override
        public @Immutable Map<K, V> getAll(Iterable<? extends K> keys, Function<Iterable<? extends K>, Map<K, @Nullable V>> function) {
            return null;
        }

        @Override
        public void put(K key, @Nullable V value) {
            caffeineCache.put(key, CacheSupport.mask(value));
        }

        @Override
        public void invalidate(K key) {
            caffeineCache.invalidate(key);
        }

        @Override
        public void invalidateAll(Iterable<? extends K> keys) {
            caffeineCache.invalidate(keys);
        }

        @Override
        public void invalidateALL() {
            caffeineCache.invalidateAll();
        }
    }

    private static final class LoadingCaffeineCache<K, V> extends CaffeineCache<K, V> {

        private LoadingCaffeineCache(LoadingCache<K, Object> loadingCache) {
            super(loadingCache);
        }

        @Override
        public V get(K key) {
            try {
                return CacheSupport.unmask(((LoadingCache<K, Object>) caffeineCache).get(key));
            } catch (NoResultException e) {
                return null;
            } catch (NotCacheException e) {
                return CacheSupport.unmask(e.getValue());
            }
        }
    }

    private static final class RemovalListenerImpl<K, V> implements RemovalListener<K, Object> {

        private final CacheRemoveListener<? super K, ? super V> listener;

        private RemovalListenerImpl(CacheRemoveListener<? super K, ? super V> listener) {
            this.listener = listener;
        }

        @Override
        public void onRemoval(@Nullable K key, @Nullable Object value, RemovalCause removalCause) {
            if (key == null) {
                throw new IllegalStateException("Null cached key");
            }
            if (value == null) {
                throw new IllegalStateException("Null cached value: " + key);
            }
            CacheRemoveListener.Cause cause = parseCause(removalCause);
            listener.afterRemove(key, CacheSupport.unmask(value), cause);
        }

        private CacheRemoveListener.Cause parseCause(RemovalCause removalCause) {
            switch (removalCause) {
                case SIZE:
                    return CacheRemoveListener.Cause.SIZE;
                case EXPIRED:
                    return CacheRemoveListener.Cause.EXPIRED;
                case COLLECTED:
                    return CacheRemoveListener.Cause.COLLECTED;
                case EXPLICIT:
                    return CacheRemoveListener.Cause.EXPLICIT;
                case REPLACED:
                    return CacheRemoveListener.Cause.REPLACED;
                default:
                    throw new IllegalStateException("Unknown removal cause: " + removalCause);
            }
        }
    }
}
