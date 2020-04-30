package xyz.srclab.common.cache;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.cache.listener.*;
import xyz.srclab.common.time.TimeHelper;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * Not Thread Safe!
 *
 * @author sunqian
 */
final class AutoCleanMapCache<K, V> implements Cache<K, V> {

    private final Map<K, CacheValue<V>> autoCleanMap;

    private final long expiryAfterCreateMillis;
    private final long expiryAfterUpdateMillis;
    private final long expiryAfterReadMillis;

    private CacheCreateListener<K, V> cacheCreateListener;
    private CacheReadListener<K, V> cacheReadListener;
    private CacheUpdateListener<K, V> cacheUpdateListener;
    private CacheRemoveListener<K, V> cacheRemoveListener;

    AutoCleanMapCache(CacheBuilder<K, V> builder) {
        this.autoCleanMap = CacheSupport.newAutoCleanMap(builder.getConcurrencyLevel());
        this.expiryAfterCreateMillis = builder.getExpiryAfterCreate().toMillis();
        this.expiryAfterUpdateMillis = builder.getExpiryAfterUpdate().toMillis();
        this.expiryAfterReadMillis = builder.getExpiryAfterRead().toMillis();
        setListener(builder.getListeners());
    }

    private void setListener(List<CacheListener<K, V>> cacheListeners) {
        @Nullable CacheCreateListener<K, V> cacheCreateListener = null;
        @Nullable CacheReadListener<K, V> cacheReadListener = null;
        @Nullable CacheUpdateListener<K, V> cacheUpdateListener = null;
        @Nullable CacheRemoveListener<K, V> cacheRemoveListener = null;
        for (CacheListener<K, V> cacheListener : cacheListeners) {
            if (cacheListener instanceof CacheCreateListener) {
                cacheCreateListener = (CacheCreateListener<K, V>) cacheListener;
                continue;
            }
            if (cacheListener instanceof CacheReadListener) {
                cacheReadListener = (CacheReadListener<K, V>) cacheListener;
                continue;
            }
            if (cacheListener instanceof CacheUpdateListener) {
                cacheUpdateListener = (CacheUpdateListener<K, V>) cacheListener;
                continue;
            }
            if (cacheListener instanceof CacheRemoveListener) {
                cacheRemoveListener = (CacheRemoveListener<K, V>) cacheListener;
                continue;
            }
            throw new IllegalArgumentException("Unknown cache listener: " + cacheListener);
        }
        this.cacheCreateListener =
                cacheCreateListener == null ? CacheCreateListener.getDefault() : cacheCreateListener;
        this.cacheReadListener =
                cacheReadListener == null ? CacheReadListener.getDefault() : cacheReadListener;
        this.cacheUpdateListener =
                cacheUpdateListener == null ? CacheUpdateListener.getDefault() : cacheUpdateListener;
        this.cacheRemoveListener =
                cacheRemoveListener == null ? CacheRemoveListener.getDefault() : cacheRemoveListener;
    }

    @Override
    public boolean has(K key) {
        @Nullable CacheValue<V> cacheValue = autoCleanMap.get(key);
        if (cacheValue == null) {
            return false;
        }
        if (cacheValue.isExpired()) {
            cacheRemoveListener.beforeRemove(key, cacheValue.getValue());
            autoCleanMap.remove(key);
            cacheRemoveListener.afterRemove(key, cacheValue.getValue());
            return false;
        }
        return true;
    }

    @Override
    @Nullable
    public V get(K key) throws NoSuchElementException {
        cacheReadListener.beforeRead(key);
        @Nullable CacheValue<V> cacheValue = autoCleanMap.get(key);
        if (cacheValue == null) {
            cacheReadListener.onReadFailure(key);
            throw new NoSuchElementException("Key: " + key);
        }
        if (cacheValue.isExpired()) {
            cacheRemoveListener.beforeRemove(key, cacheValue.getValue());
            autoCleanMap.remove(key);
            cacheRemoveListener.afterRemove(key, cacheValue.getValue());
            cacheReadListener.onReadFailure(key);
            throw new NoSuchElementException("Key: " + key);
        }
        @Nullable V value = getFromCacheValue(cacheValue);
        cacheReadListener.onReadSuccess(key, value);
        return value;
    }

    @Override
    @Nullable
    public V get(K key, Function<K, @Nullable V> ifAbsent) {
        return getFromFunction(key, ifAbsent);
    }

    @Override
    @Nullable
    public V get(K key, CacheValueFunction<K, @Nullable V> ifAbsent) {
        return getFromFunction(key, ifAbsent);
    }

    @Nullable
    private V getFromFunction(K key, Function<K, @Nullable V> ifAbsent) {
        cacheReadListener.beforeRead(key);
        @Nullable CacheValue<V> cacheValue = autoCleanMap.get(key);
        if (cacheValue == null) {
            cacheReadListener.onReadFailure(key);
            cacheCreateListener.beforeCreate(key);
            @Nullable V newValue = ifAbsent.apply(key);
            cacheCreateListener.afterCreate(key, newValue);
            autoCleanMap.put(
                    key, new CacheValue<>(newValue, TimeHelper.nowMillis() + expiryAfterCreateMillis));
            return newValue;
        }
        if (cacheValue.isExpired()) {
            cacheRemoveListener.beforeRemove(key, cacheValue.getValue());
            cacheRemoveListener.afterRemove(key, cacheValue.getValue());
            cacheReadListener.onReadFailure(key);
            cacheCreateListener.beforeCreate(key);
            @Nullable V newValue = ifAbsent.apply(key);
            cacheCreateListener.afterCreate(key, newValue);

            if (ifAbsent instanceof CacheValueFunction) {
                CacheValueFunction<K, V> cacheValueFunction = (CacheValueFunction<K, V>) ifAbsent;
                long newExpiryAtMillis = TimeHelper.nowMillis() + cacheValueFunction.getExpiryAfterCreate().toMillis();
                CustomCacheValue<V> customCacheValue;
                if (cacheValue instanceof CustomCacheValue) {
                    cacheValue.setValue(newValue);
                    cacheValue.setExpiredAtMillis(newExpiryAtMillis);
                    customCacheValue = (CustomCacheValue<V>) cacheValue;
                } else {
                    customCacheValue = new CustomCacheValue<>(newValue, newExpiryAtMillis);
                    customCacheValue.setExpiryAfterUpdateMillis(cacheValueFunction.getExpiryAfterCreate().toMillis());
                    customCacheValue.setExpiryAfterReadMillis(cacheValueFunction.getExpiryAfterRead().toMillis());
                    customCacheValue.setExpiryAfterUpdateMillis(cacheValueFunction.getExpiryAfterUpdate().toMillis());
                    autoCleanMap.put(key, customCacheValue);
                }
            } else {
                cacheValue.setValue(newValue);
                if (cacheValue instanceof CustomCacheValue) {
                    ((CustomCacheValue<V>) cacheValue).resetExpiryMillis();
                }
            }

            cacheValue.setValue(newValue);
            cacheValue.setExpiredAtMillis(TimeHelper.nowMillis() + expiryAfterCreateMillis);
            if (cacheValue instanceof CustomCacheValue) {
                if (ifAbsent instanceof CacheValueFunction) {
                    CacheValueFunction<K, V> cacheValueFunction = (CacheValueFunction<K, V>) ifAbsent;
                    ((CustomCacheValue<V>) cacheValue)
                            .setExpiryAfterCreateMillis(cacheValueFunction.getExpiryAfterCreate().toMillis());
                    ((CustomCacheValue<V>) cacheValue)
                            .setExpiryAfterReadMillis((cacheValueFunction.getExpiryAfterRead().toMillis());
                    ((CustomCacheValue<V>) cacheValue)
                            .setExpiryAfterUpdateMillis(cacheValueFunction.getExpiryAfterUpdate().toMillis());
                } else {
                    ((CustomCacheValue<V>) cacheValue).resetExpiryMillis();
                }
            }
            return newValue;
        }
        @Nullable V value = getFromCacheValue(cacheValue);
        cacheReadListener.onReadSuccess(key, value);
        return value;
    }

    @Nullable
    private V getFromCacheValue(CacheValue<V> cacheValue) {
        @Nullable V value = cacheValue.getValue();
        if (cacheValue instanceof CustomCacheValue) {
            cacheValue.setExpiredAtMillis(
                    TimeHelper.nowMillis() + ((CustomCacheValue<V>) cacheValue).getExpiryAfterReadMillis());
        } else {
            cacheValue.setExpiredAtMillis(
                    TimeHelper.nowMillis() + expiryAfterReadMillis);
        }
        return value;
    }

    @Override
    public void put(K key, @Nullable V value) {

    }

    @Override
    public void put(K key, Function<K, @Nullable V> valueFunction) {

    }

    @Override
    public void put(K key, CacheValueFunction<K, @Nullable V> valueFunction) {

    }

    @Override
    public void expire(K key, Duration duration) {

    }

    @Override
    public void expire(K key, Function<K, Duration> durationFunction) {

    }

    @Override
    public void remove(K key) {
    }

    @Override
    public void removeAll() {
        autoCleanMap.clear();
    }

    private static class CacheValue<V> {

        private @Nullable V value;
        private long expiredAtMillis;

        private CacheValue() {
        }

        private CacheValue(@Nullable V value, long expiredAtMillis) {
            this.value = value;
            this.expiredAtMillis = expiredAtMillis;
        }

        public @Nullable V getValue() {
            return value;
        }

        public void setValue(@Nullable V value) {
            this.value = value;
        }

        public long getExpiredAtMillis() {
            return expiredAtMillis;
        }

        public void setExpiredAtMillis(long expiredAtMillis) {
            this.expiredAtMillis = expiredAtMillis;
        }

        public boolean isExpired() {
            return expiredAtMillis > 0 && TimeHelper.nowMillis() > expiredAtMillis;
        }
    }

    private final class CustomCacheValue<V> extends CacheValue<V> {

        private long expiryAfterCreateMillis;
        private long expiryAfterUpdateMillis;
        private long expiryAfterReadMillis;

        private CustomCacheValue() {
            super();
            resetExpiryMillis();
        }

        private CustomCacheValue(@Nullable V value, long expiredAtMillis) {
            super(value, expiredAtMillis);
            resetExpiryMillis();
        }

        public long getExpiryAfterCreateMillis() {
            return expiryAfterCreateMillis;
        }

        public void setExpiryAfterCreateMillis(long expiryAfterCreateMillis) {
            this.expiryAfterCreateMillis = expiryAfterCreateMillis;
        }

        public long getExpiryAfterUpdateMillis() {
            return expiryAfterUpdateMillis;
        }

        public void setExpiryAfterUpdateMillis(long expiryAfterUpdateMillis) {
            this.expiryAfterUpdateMillis = expiryAfterUpdateMillis;
        }

        public long getExpiryAfterReadMillis() {
            return expiryAfterReadMillis;
        }

        public void setExpiryAfterReadMillis(long expiryAfterReadMillis) {
            this.expiryAfterReadMillis = expiryAfterReadMillis;
        }

        public void resetExpiryMillis() {
            this.expiryAfterCreateMillis = AutoCleanMapCache.this.expiryAfterCreateMillis;
            this.expiryAfterReadMillis = AutoCleanMapCache.this.expiryAfterReadMillis;
            this.expiryAfterUpdateMillis = AutoCleanMapCache.this.expiryAfterUpdateMillis;
        }
    }
}
