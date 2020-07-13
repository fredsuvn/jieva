package xyz.srclab.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.*;
import com.google.common.util.concurrent.UncheckedExecutionException;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;
import xyz.srclab.common.cache.listener.CacheRemoveListener;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

/**
 * @author sunqian
 */
final class GuavaCacheSupport {

    static <K, V> xyz.srclab.common.cache.Cache<K, V> newGuavaCache(Cache<K, Object> guavaCache) {
        return new GuavaCache<>(guavaCache);
    }

    static <K, V> xyz.srclab.common.cache.Cache<K, V> newLoadingGuavaCache(LoadingCache<K, Object> loadingCache) {
        return new LoadingGuavaCache<>(loadingCache);
    }

    static <K, V> CacheBuilder<K, Object> toGuavaCacheBuilder(
            xyz.srclab.common.cache.CacheBuilder<K, V> builder) {
        CacheBuilder<K, Object> guavaBuilder = Cast.as(CacheBuilder.newBuilder());
        guavaBuilder.maximumSize(builder.maxSize());
        if (builder.expiry() != null) {
            CacheExpiry expiry = builder.expiry();
            @Nullable Duration expiryAfterCreate = expiry.expiryAfterCreate();
            @Nullable Duration expiryAfterRead = expiry.expiryAfterRead();
            @Nullable Duration expiryAfterUpdate = expiry.expiryAfterUpdate();
            expireAfterAccess(expiryAfterCreate, expiryAfterRead, expiryAfterUpdate, guavaBuilder);
            expireAfterWrite(expiryAfterCreate, expiryAfterUpdate, guavaBuilder);
        }
        if (builder.removeListener() != null) {
            CacheRemoveListener<? super K, ? super V> cacheRemoveListener = builder.removeListener();
            guavaBuilder.removalListener(new RemovalListenerImpl<>(cacheRemoveListener));
        }
        return guavaBuilder;
    }

    static <K, V> CacheLoader<K, Object> toGuavaCacheLoader(xyz.srclab.common.cache.CacheLoader<K, V> loader) {
        return new CacheLoader<K, Object>() {
            @Override
            public Object load(K k) {
                @Nullable xyz.srclab.common.cache.CacheLoader.Result<? extends V> result = loader.load(k);
                if (result == null) {
                    throw new NoResultException();
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
            CacheBuilder<K, Object> builder
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
            CacheBuilder<K, Object> builder
    ) {
        if (expiryAfterUpdate != null) {
            builder.expireAfterWrite(expiryAfterUpdate);
            return;
        }
        if (expiryAfterCreate != null) {
            builder.expireAfterWrite(expiryAfterCreate);
        }
    }

    private static class GuavaCache<K, V> implements FixedExpiryCache<K, V> {

        protected final Cache<K, Object> guavaCache;

        private GuavaCache(Cache<K, Object> guavaCache) {
            this.guavaCache = guavaCache;
        }

        @Override
        public boolean contains(K key) {
            return guavaCache.getIfPresent(key) != null;
        }

        @Override
        public V get(K key) {
            return CacheSupport.unmask(guavaCache.getIfPresent(key));
        }

        @Override
        public V get(K key, xyz.srclab.common.cache.CacheLoader<? super K, ? extends V> loader) {
            return CacheSupport.unmask(get0(key, loader));
        }

        @Nullable
        private Object get0(K key, xyz.srclab.common.cache.CacheLoader<? super K, ? extends V> loader) {
            try {
                return guavaCache.get(key, () -> {
                    @Nullable xyz.srclab.common.cache.CacheLoader.Result<? extends V> result = loader.load(key);
                    if (result == null) {
                        throw new NoResultException();
                    }
                    if (!result.needCache()) {
                        throw new NotCacheException(result.value());
                    }
                    return CacheSupport.mask(result.value());
                });
            } catch (ExecutionException e) {
                throw new IllegalStateException(e);
            } catch (UncheckedExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof NoResultException) {
                    return null;
                }
                if (cause instanceof NotCacheException) {
                    return ((NotCacheException) cause).getValue();
                }
                throw new IllegalStateException(e);
            }
        }

        @Override
        public void put(K key, @Nullable V value) {
            guavaCache.put(key, CacheSupport.mask(value));
        }

        @Override
        public void invalidate(K key) {
            guavaCache.invalidate(key);
        }

        @Override
        public void invalidateAll() {
            guavaCache.invalidateAll();
        }
    }

    private static final class LoadingGuavaCache<K, V> extends GuavaCache<K, V> {

        private LoadingGuavaCache(LoadingCache<K, Object> loadingCache) {
            super(loadingCache);
        }

        @Override
        public V get(K key) {
            try {
                return CacheSupport.unmask(((LoadingCache<K, Object>) guavaCache).get(key));
            } catch (ExecutionException e) {
                throw new IllegalStateException(e);
            } catch (UncheckedExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof NoResultException) {
                    return null;
                }
                if (cause instanceof NotCacheException) {
                    return CacheSupport.unmask(((NotCacheException) cause).getValue());
                }
                throw new IllegalStateException(e);
            }
        }
    }

    private static final class RemovalListenerImpl<K, V> implements RemovalListener<K, Object> {

        private final CacheRemoveListener<? super K, ? super V> listener;

        private RemovalListenerImpl(CacheRemoveListener<? super K, ? super V> listener) {
            this.listener = listener;
        }

        @Override
        public void onRemoval(RemovalNotification<K, Object> removalNotification) {
            @Nullable K key = removalNotification.getKey();
            @Nullable Object value = removalNotification.getValue();
            if (key == null) {
                throw new IllegalStateException("Null cached key");
            }
            if (value == null) {
                throw new IllegalStateException("Null cached value: " + key);
            }
            RemovalCause removalCause = removalNotification.getCause();
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
