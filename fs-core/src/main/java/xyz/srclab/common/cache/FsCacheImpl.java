package xyz.srclab.common.cache;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.Fs;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
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
    private volatile boolean inCleanUp = false;

    FsCacheImpl() {
        this.map = new ConcurrentHashMap<>();
        this.removeListener = null;
    }

    FsCacheImpl(FsCache.RemoveListener<K, V> removeListener) {
        this.map = new ConcurrentHashMap<>();
        this.removeListener = removeListener;
    }

    FsCacheImpl(int initialCapacity) {
        this.map = new ConcurrentHashMap<>(initialCapacity);
        this.removeListener = null;
    }

    FsCacheImpl(int initialCapacity, FsCache.RemoveListener<K, V> removeListener) {
        this.map = new ConcurrentHashMap<>(initialCapacity);
        this.removeListener = removeListener;
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
        // BooleanRef createNew = new BooleanRef(false);
        // Ref<Object> newValueRef = new Ref<>();
        Entry<K> entry = map.computeIfAbsent(key, it -> {
            // V v = loader.apply(it);
            // newValueRef.set(v);
            // createNew.set(true);
            return newEntry(it, loader.apply(it));
        });
        // if (createNew.get()) {
        //     Object result = newValueRef.get();
        //     if (result instanceof Null) {
        //         return null;
        //     }
        //     return (V) result;
        // }
        Object result = entry.get();
        if (result instanceof Null) {
            // T newResult = loader.apply(key);
            // map.put(key, newEntry(key, newResult));
            // return newResult;
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
        return new Entry<>(key, value == null ? new Null() : value, queue);
    }

    @Override
    public void remove(K key) {
        cleanUp();
        Entry<K> entry = map.get(key);
        if (entry != null) {
            entry.clear();
            map.remove(key);
        }
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void clear() {
        map.clear();
        while (true) {
            Object x = queue.poll();
            if (x == null) {
                return;
            }
            Entry<K> entry = (Entry) x;
            entry.clear();
            if (removeListener != null) {
                removeListener.onRemove(this, entry.key);
            }
        }
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
            Entry<K> entry = Fs.as(x);
            map.remove(entry.key);
            if (removeListener != null) {
                removeListener.onRemove(this, entry.key);
            }
        }
        inCleanUp = false;
    }

    private static final class Entry<K> extends SoftReference<Object> {

        private final K key;

        public Entry(K key, Object value, ReferenceQueue<? super Object> q) {
            super(value, q);
            this.key = key;
        }
    }

    private static final class Null {
    }
}
