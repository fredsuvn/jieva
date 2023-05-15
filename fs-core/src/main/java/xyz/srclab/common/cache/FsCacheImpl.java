package xyz.srclab.common.cache;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsObject;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation for {@link FsCache}.
 */
class FsCacheImpl<T> implements FsCache<T> {

    private static final Object NULL = "[null]";

    private final Map<Object, Entry> map;
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();

    FsCacheImpl() {
        this.map = new ConcurrentHashMap<>();
    }

    FsCacheImpl(int initialCapacity) {
        this.map = new ConcurrentHashMap<>(initialCapacity);
    }

    @Override
    public @Nullable T get(Object key) {
        expungeStaleEntries();
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
        expungeStaleEntries();
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
    public void set(Object key, @Nullable T value) {
        expungeStaleEntries();
        map.put(key, new Entry(key, value, queue));
    }

    @Override
    public void remove(Object key) {
        expungeStaleEntries();
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
        }
    }

    private static final class Entry extends SoftReference<Object> {
        private final Object key;

        public Entry(Object key, Object value, ReferenceQueue<? super Object> q) {
            super(value, q);
            this.key = key;
        }
    }

    private void expungeStaleEntries() {
        while (true) {
            Object x = queue.poll();
            if (x == null) {
                return;
            }
            Entry entry = FsObject.as(x);
            map.remove(entry.key);
        }
    }
}
