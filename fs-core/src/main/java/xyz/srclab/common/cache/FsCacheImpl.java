package xyz.srclab.common.cache;

import lombok.Getter;
import xyz.srclab.annotations.Nullable;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Default implementation for {@link FsCache}.
 */
final class FsCacheImpl<K, V> implements FsCache<K, V> {

    private final Map<K, Entry<K>> map;
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private final FsCache.RemoveListener<K, V> removeListener;
    private final boolean isSoft;
    private volatile boolean inCleanUp = false;

    FsCacheImpl(boolean isSoft) {
        this.map = new ConcurrentHashMap<>();
        this.removeListener = null;
        this.isSoft = isSoft;
    }

    FsCacheImpl(boolean isSoft, FsCache.RemoveListener<K, V> removeListener) {
        this.map = new ConcurrentHashMap<>();
        this.removeListener = removeListener;
        this.isSoft = isSoft;
    }

    FsCacheImpl(boolean isSoft, int initialCapacity) {
        this.map = new ConcurrentHashMap<>(initialCapacity);
        this.removeListener = null;
        this.isSoft = isSoft;
    }

    FsCacheImpl(boolean isSoft, int initialCapacity, FsCache.RemoveListener<K, V> removeListener) {
        this.map = new ConcurrentHashMap<>(initialCapacity);
        this.removeListener = removeListener;
        this.isSoft = isSoft;
    }

    @Override
    public @Nullable V get(K key) {
        cleanUp();
        Entry<K> entry = map.get(key);
        if (entry == null) {
            return null;
        }
        Object result = entry.get();
        if (result instanceof Null) {
            return null;
        }
        return (V) result;
    }

    @Override
    public @Nullable Optional<V> getOptional(K key) {
        cleanUp();
        Entry<K> entry = map.get(key);
        if (entry == null) {
            return null;
        }
        Object result = entry.get();
        if (result == null) {
            return null;
        }
        if (result instanceof Null) {
            return Optional.empty();
        }
        return (Optional<V>) Optional.of(result);
    }

    @Override
    public @Nullable V get(K key, Function<? super K, ? extends V> loader) {
        cleanUp();
        Entry<K> entry = map.computeIfAbsent(key, it -> newEntry(it, loader.apply(it)));
        Object result = entry.get();
        if (result instanceof Null) {
            return null;
        }
        return (V) result;
    }

    @Override
    public Optional<V> getOptional(K key, Function<? super K, ? extends V> loader) {
        cleanUp();
        Entry<K> entry = map.computeIfAbsent(key, it -> newEntry(it, loader.apply(it)));
        Object result = entry.get();
        if (result instanceof Null) {
            return Optional.empty();
        }
        return (Optional<V>) Optional.of(result);
    }

    @Override
    public void put(K key, @Nullable V value) {
        cleanUp();
        map.put(key, newEntry(key, value));
    }

    private Entry<K> newEntry(K key, @Nullable Object value) {
        return isSoft ?
            new SoftEntry<>(key, value == null ? new Null() : value, queue)
            :
            new WeakEntry<>(key, value == null ? new Null() : value, queue);
    }

    @Override
    public void remove(K key) {
        Entry<K> entry = map.get(key);
        if (entry != null) {
            entry.enqueue();
            map.remove(key);
        }
        cleanUp();
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void clear() {
        Map<K, Entry<K>> mapCopy = new HashMap<>(map);
        mapCopy.forEach((k, v) -> {
            v.enqueue();
            map.remove(k);
        });
        cleanUp();
    }

    @Override
    public void cleanUp() {
        if (inCleanUp) {
            return;
        }
        synchronized (this) {
            if (inCleanUp) {
                return;
            }
            inCleanUp = true;
        }
        while (true) {
            Object x = queue.poll();
            if (x == null) {
                break;
            }
            Entry<K> entry = (Entry<K>) x;
            map.remove(entry.getKey());
            if (removeListener != null) {
                removeListener.onRemove(this, entry.getKey());
            }
        }
        inCleanUp = false;
    }

    private interface Entry<K> {

        K getKey();

        Object get();

        boolean enqueue();
    }

    @Getter
    private static class SoftEntry<K> extends SoftReference<Object> implements Entry<K> {

        private final K key;

        public SoftEntry(K key, Object value, ReferenceQueue<? super Object> q) {
            super(value, q);
            this.key = key;
        }
    }

    @Getter
    private static class WeakEntry<K> extends WeakReference<Object> implements Entry<K> {

        private final K key;

        public WeakEntry(K key, Object value, ReferenceQueue<? super Object> q) {
            super(value, q);
            this.key = key;
        }
    }

    private static final class Null {
    }
}
