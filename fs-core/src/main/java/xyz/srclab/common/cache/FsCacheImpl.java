package xyz.srclab.common.cache;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsObject;
import xyz.srclab.common.base.ref.Ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Default implementation for {@link FsCache}.
 */
final class FsCacheImpl<T> implements FsCache<T> {

    private static final Object NULL = "[null]";

    private final Map<Object, Entry> map;
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private final Consumer<Object> removeListener;

    FsCacheImpl() {
        this.map = new ConcurrentHashMap<>();
        this.removeListener = null;
    }

    FsCacheImpl(Consumer<Object> removeListener) {
        this.map = new ConcurrentHashMap<>();
        this.removeListener = removeListener;
    }

    FsCacheImpl(int initialCapacity) {
        this.map = new ConcurrentHashMap<>(initialCapacity);
        this.removeListener = null;
    }

    FsCacheImpl(int initialCapacity, Consumer<Object> removeListener) {
        this.map = new ConcurrentHashMap<>(initialCapacity);
        this.removeListener = removeListener;
    }

    @Override
    public @Nullable T get(Object key) {
        cleanUp();
        Entry entry = map.get(key);
        if (entry == null) {
            return null;
        }
        Object result = entry.get();
        if (result == NULL) {
            return null;
        }
        return FsObject.as(result);
    }

    @Override
    public @Nullable Optional<T> getOptional(Object key) {
        cleanUp();
        Entry entry = map.get(key);
        if (entry == null) {
            return null;
        }
        Object result = entry.get();
        if (result == null) {
            return null;
        }
        if (result == NULL) {
            return Optional.empty();
        }
        return Optional.of(FsObject.as(result));
    }

    @Override
    public <K> @Nullable T get(K key, Function<K, T> loader) {
        cleanUp();
        Ref<T> newValue = new Ref<>();
        map.computeIfAbsent(key, it -> {
            newValue.set(loader.apply(FsObject.as(it)));
            return newEntry(it, newValue.get());
        });
        T result = newValue.get();
        if (result == NULL) {
            return null;
        }
        return result;
    }

    @Override
    public <K> Optional<T> getOptional(K key, Function<K, T> loader) {
        T result = get(key, loader);
        if (result == null) {
            return Optional.empty();
        }
        return Optional.of(result);
    }

    @Override
    public void set(Object key, @Nullable T value) {
        cleanUp();
        map.put(key, newEntry(key, value));
    }

    private Entry newEntry(Object key, @Nullable T value) {
        return new Entry(key, value == null ? NULL : value, queue);
    }

    @Override
    public void remove(Object key) {
        cleanUp();
        Entry entry = map.get(key);
        if (entry != null) {
            entry.clear();
            map.remove(key);
        }
    }

    @Override
    public void clear() {
        map.clear();
        while (true) {
            Object x = queue.poll();
            if (x == null) {
                return;
            }
            Entry entry = (Entry) x;
            entry.clear();
            if (removeListener != null) {
                removeListener.accept(entry.key);
            }
        }
    }

    @Override
    public void cleanUp() {
        while (true) {
            Object x = queue.poll();
            if (x == null) {
                return;
            }
            Entry entry = FsObject.as(x);
            map.remove(entry.key);
            if (removeListener != null) {
                removeListener.accept(entry.key);
            }
        }
    }

    private static final class Entry extends SoftReference<Object> {
        private final Object key;

        public Entry(Object key, Object value, ReferenceQueue<? super Object> q) {
            super(value, q);
            this.key = key;
        }
    }
}
