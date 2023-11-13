package xyz.fsgek.common.cache;

import lombok.Getter;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.GekWrapper;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;

final class FullCache<K, V> implements GekCache<K, V> {

    private final GekCache.RemoveListener<K, V> removeListener;
    private final boolean isSoft;
    private final long defaultExpiration;

    private final Map<K, Entry> map;
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private volatile boolean inCleanUp = false;

    FullCache(
        boolean isSoft,
        long defaultExpiration,
        int initialCapacity,
        RemoveListener<K, V> removeListener
    ) {
        this.isSoft = isSoft;
        this.defaultExpiration = defaultExpiration;
        this.map = initialCapacity <= 0 ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(initialCapacity);
        this.removeListener = removeListener;
    }

    @Override
    public @Nullable V get(K key) {
        Entry entry = map.compute(key, (k, old) -> {
            if (old == null) {
                return null;
            }
            if (old.isExpired()) {
                old.remove();
                return null;
            }
            return old;
        });
        cleanUp0();
        return entry == null ? null : entry.value;
    }

    @Override
    public @Nullable V get(K key, Function<? super K, ? extends V> loader) {
        Entry entry = map.compute(key, (k, old) -> {
            if (old == null) {
                return new Entry(k, loader.apply(k), defaultExpiration);
            }
            if (old.isExpired()) {
                old.remove();
                return new Entry(k, loader.apply(k), defaultExpiration);
            }
            return old;
        });
        cleanUp0();
        return entry.value;
    }

    @Override
    public @Nullable GekWrapper<V> getWrapper(K key) {
        Entry entry = map.compute(key, (k, old) -> {
            if (old == null) {
                return null;
            }
            if (old.isExpired()) {
                old.remove();
                return null;
            }
            return old;
        });
        cleanUp0();
        return entry == null ? null : GekWrapper.wrap(entry.value);
    }

    @Override
    public @Nullable GekWrapper<V> getWrapper(K key, Function<? super K, @Nullable ValueInfo<? extends V>> loader) {
        Entry entry = map.compute(key, (k, old) -> {
            if (old == null) {
                ValueInfo<? extends V> newValue = loader.apply(k);
                if (newValue == null) {
                    return null;
                }
                return new Entry(k, newValue.get(), durationToMillis(newValue.expiration()));
            }
            if (old.isExpired()) {
                old.remove();
                ValueInfo<? extends V> newValue = loader.apply(k);
                if (newValue == null) {
                    return null;
                }
                return new Entry(k, newValue.get(), durationToMillis(newValue.expiration()));
            }
            return old;
        });
        cleanUp0();
        return entry == null ? null : GekWrapper.wrap(entry.value);
    }

    @Override
    public V put(K key, V value) {
        Entry old = map.put(key, new Entry(key, value, defaultExpiration));
        if (old != null) {
            old.remove();
        }
        cleanUp0();
        return old == null ? null : old.value;
    }

    @Override
    public V put(K key, V value, long expiration) {
        Entry old = map.put(key, new Entry(key, value, expiration));
        if (old != null) {
            old.remove();
        }
        cleanUp0();
        return old == null ? null : old.value;
    }

    @Override
    public V put(K key, V value, @Nullable Duration expiration) {
        Entry old = map.put(key, new Entry(key, value, durationToMillis(expiration)));
        if (old != null) {
            old.remove();
        }
        cleanUp0();
        return old == null ? null : old.value;
    }

    @Override
    public void expire(K key, long expiration) {
        map.compute(key, (k, old) -> {
            if (old == null) {
                return null;
            }
            if (old.isExpired()) {
                old.remove();
                return null;
            }
            old.refreshExpiration(expiration);
            return old;
        });
        cleanUp0();
    }

    @Override
    public void expire(K key, @Nullable Duration expiration) {
        map.compute(key, (k, old) -> {
            if (old == null) {
                return null;
            }
            if (old.isExpired()) {
                old.remove();
                return null;
            }
            old.refreshExpiration(durationToMillis(expiration));
            return old;
        });
        cleanUp0();
    }

    @Override
    public void remove(K key) {
        map.compute(key, (k, old) -> {
            if (old == null) {
                return null;
            }
            old.remove();
            return null;
        });
        cleanUp0();
    }

    @Override
    public void removeIf(BiPredicate<K, V> predicate) {
        map.replaceAll((k, v) -> {
            if (v.isExpired()) {
                v.remove();
                return v;
            }
            if (predicate.test(k, v.value)) {
                v.remove();
                return v;
            }
            return v;
        });
        cleanUp0();
    }

    @Override
    public void removeEntry(BiPredicate<K, ValueInfo<V>> predicate) {
        map.replaceAll((k, v) -> {
            if (v.isExpired()) {
                v.remove();
                return v;
            }
            if (predicate.test(k, ValueInfo.of(v.value, v.expirationMillis))) {
                v.remove();
                return v;
            }
            return v;
        });
        cleanUp0();
    }

    @Override
    public int size() {
        cleanUp0();
        return map.size();
    }

    @Override
    public void clear() {
        removeIf((k, v) -> true);
    }

    @Override
    public void cleanUp() {
        cleanUp0();
    }

    private void cleanUp0() {
        if (inCleanUp) {
            return;
        }
        synchronized (this) {
            if (inCleanUp) {
                return;
            }
            inCleanUp = true;
        }
        try {
            while (true) {
                Object x = queue.poll();
                if (x == null) {
                    break;
                }
                Ref<K, V> ref = (Ref<K, V>) x;
                FullCache<K, V>.Entry entry = ref.getEntry();
                if (entry.cleared()) {
                    continue;
                }
                if (entry.removed()) {
                    if (removeListener != null) {
                        removeListener.onRemove(this, entry.key);
                    }
                    entry.clear();
                    map.compute(entry.key, (k, old) -> {
                        if (old == entry) {
                            return null;
                        }
                        return old;
                    });
                    continue;
                }
                if (entry.canExpire()) {
                    entry.remove();
                    if (removeListener != null) {
                        removeListener.onRemove(this, entry.key);
                    }
                    entry.clear();
                    map.compute(entry.key, (k, old) -> {
                        if (old == entry) {
                            return null;
                        }
                        return old;
                    });
                    continue;
                }
                entry.refreshRef();
            }
        } finally {
            inCleanUp = false;
        }
    }

    private long durationToMillis(@Nullable Duration duration) {
        return duration == null ? -1 : duration.toMillis();
    }

    @Getter
    private final class Entry {

        private final K key;
        private final V value;
        private long startMillis;
        private long expirationMillis;
        private Ref<K, V> ref;

        private boolean removed = false;
        private boolean cleared = false;

        public Entry(K key, V value, long expirationMillis) {
            this.key = key;
            this.value = value;
            this.startMillis = System.currentTimeMillis();
            this.expirationMillis = expirationMillis;
        }

        public boolean isExpired() {
            if (expirationMillis < 0) {
                if (defaultExpiration < 0) {
                    return false;
                }
                return System.currentTimeMillis() > startMillis + defaultExpiration;
            }
            return System.currentTimeMillis() > startMillis + expirationMillis;
        }

        public boolean canExpire() {
            if (expirationMillis < 0) {
                if (defaultExpiration < 0) {
                    return true;
                }
                return System.currentTimeMillis() > startMillis + defaultExpiration;
            }
            return System.currentTimeMillis() > startMillis + expirationMillis;
        }

        public void refreshExpiration(long expiration) {
            this.startMillis = System.currentTimeMillis();
            this.expirationMillis = expiration;
        }

        public void remove() {
            this.removed = true;
            if (ref != null) {
                ref.release();
            }
        }

        public boolean removed() {
            return removed;
        }

        public void clear() {
            this.cleared = true;
        }

        public boolean cleared() {
            return cleared;
        }

        public void refreshRef() {
            ref = isSoft ? new SoftRef(this) : new WeakRef(this);
        }
    }

    private interface Ref<K, V> {

        FullCache<K, V>.Entry getEntry();

        void release();
    }

    private final class SoftRef extends SoftReference<Object> implements Ref<K, V> {

        private final FullCache<K, V>.Entry entry;

        public SoftRef(FullCache<K, V>.Entry entry) {
            super(new Object(), queue);
            this.entry = entry;
        }

        @Override
        public FullCache<K, V>.Entry getEntry() {
            return entry;
        }

        @Override
        public void release() {
            super.enqueue();
        }
    }

    private final class WeakRef extends WeakReference<Object> implements Ref<K, V> {

        private final FullCache<K, V>.Entry entry;

        public WeakRef(FullCache<K, V>.Entry entry) {
            super(new Object(), queue);
            this.entry = entry;
        }

        @Override
        public FullCache<K, V>.Entry getEntry() {
            return entry;
        }

        @Override
        public void release() {
            super.enqueue();
        }
    }
}
