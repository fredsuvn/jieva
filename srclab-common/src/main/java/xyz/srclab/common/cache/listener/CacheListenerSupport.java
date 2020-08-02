package xyz.srclab.common.cache.listener;

import xyz.srclab.annotation.Nullable;

final class CacheListenerSupport {

    static final CacheCreateListener<Object, Object> EMPTY_CREATE_LISTENER = new CacheCreateListener<Object, Object>() {

        @Override
        public void beforeCreate(Object key) {
        }

        @Override
        public void afterCreate(Object key, @Nullable Object value) {
        }
    };

    static final CacheReadListener<Object, Object> EMPTY_READ_LISTENER = new CacheReadListener<Object, Object>() {

        @Override
        public void beforeRead(Object key) {
        }

        @Override
        public void onHit(Object key, @Nullable Object value) {
        }

        @Override
        public void onMiss(Object key) {
        }
    };

    static final CacheUpdateListener<Object, Object> EMPTY_UPDATE_LISTENER = new CacheUpdateListener<Object, Object>() {

        @Override
        public void beforeUpdate(Object key, @Nullable Object oldValue) {
        }

        @Override
        public void afterUpdate(Object key, @Nullable Object oldValue, @Nullable Object newValue) {
        }
    };

    static final CacheRemoveListener<Object, Object> EMPTY_REMOVE_LISTENER = new CacheRemoveListener<Object, Object>() {

        @Override
        public void beforeRemove(Object key, Object value, Cause cause) {
        }

        @Override
        public void afterRemove(Object key, Object value, Cause cause) {
        }
    };

    static <K, V> CacheCreateListener<K, V> emptyCreateListener() {
        return (CacheCreateListener<K, V>) EMPTY_CREATE_LISTENER;
    }

    static <K, V> CacheReadListener<K, V> emptyReadListener() {
        return (CacheReadListener<K, V>) EMPTY_READ_LISTENER;
    }

    static <K, V> CacheUpdateListener<K, V> emptyUpdateListener() {
        return (CacheUpdateListener<K, V>) EMPTY_UPDATE_LISTENER;
    }

    static <K, V> CacheRemoveListener<K, V> emptyRemoveListener() {
        return (CacheRemoveListener<K, V>) EMPTY_REMOVE_LISTENER;
    }
}
