package xyz.fsgek.common.cache;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.base.ref.GekRef;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.Function;

final class CacheImplV1<K, V> implements GekCache<K, V> {

    private final GekCache.RemoveListener<K, V> removeListener;
    private final boolean useSoft;
    private final Map<K, Entry<K, V>> map;
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private final long expireAfterWrite;
    private final long expireAfterAccess;

    CacheImplV1(Builder<K, V> builder) {
        this.useSoft = builder.useSoft();
        this.expireAfterAccess = builder.expireAfterAccessMillis();
        this.expireAfterWrite = builder.expireAfterWriteMillis();
        this.map = builder.initialCapacity() <= 0 ?
            new ConcurrentHashMap<>() : new ConcurrentHashMap<>(builder.initialCapacity());
        this.removeListener = builder.removeListener();
    }

    @Override
    public @Nullable V get(K key) {
        Entry<K, V> entry = map.get(key);
        ValuePack v = checkEntry(entry, false);
        cleanUp();
        return v == null ? null : v.value;
    }

    @Override
    public @Nullable V get(K key, Function<? super K, ? extends V> loader) {
        Entry<K, V> entry = map.get(key);
        ValuePack v = checkEntry(entry, false);
        if (v != null) {
            cleanUp();
            return v.value;
        }
        GekRef<ValuePack> result = GekRef.ofNull();
        map.compute(key, (k, old) -> {
            ValuePack oldValue = checkEntry(old, false);
            if (oldValue != null) {
                result.set(oldValue);
                return old;
            }
            ValuePack newValue = new ValuePack(loader.apply(k), expireAfterWrite, expireAfterAccess);
            result.set(newValue);
            return newEntry(k, newValue);
        });
        cleanUp();
        return result.get().value;
    }

    @Override
    public @Nullable Optional<V> getOptional(K key) {
        Entry<K, V> entry = map.get(key);
        ValuePack v = checkEntry(entry, false);
        cleanUp();
        return v == null ? null : Optional.ofNullable(v.value);
    }

    @Override
    public @Nullable Optional<V> getOptional(K key, Function<? super K, @Nullable Value<? extends V>> loader) {
        Entry<K, V> entry = map.get(key);
        ValuePack v = checkEntry(entry, false);
        if (v != null) {
            cleanUp();
            return Optional.ofNullable(v.value);
        }
        GekRef<ValuePack> result = GekRef.ofNull();
        map.compute(key, (k, old) -> {
            ValuePack oldValue = checkEntry(old, false);
            if (oldValue != null) {
                result.set(oldValue);
                return old;
            }
            Value<? extends V> vi = loader.apply(k);
            if (vi == null) {
                return null;
            }
            ValuePack newValue = new ValuePack(vi.get(), vi.expireAfterWriteMillis(), vi.expireAfterAccessMillis());
            result.set(newValue);
            return newEntry(k, newValue);
        });
        cleanUp();
        ValuePack rv = result.get();
        return rv == null ? null : Optional.ofNullable(rv.value);
    }

    @Override
    public boolean contains(K key) {
        Entry<K, V> entry = map.get(key);
        ValuePack v = checkEntry(entry, null);
        cleanUp();
        return v != null;
    }

    @Override
    public void put(K key, V value) {
        put(key, value, expireAfterWrite);
    }

    @Override
    public void put(K key, V value, long expireAfterWriteMillis) {
        map.put(key, newEntry(key, value, expireAfterWriteMillis, expireAfterAccess));
        cleanUp();
    }

    @Override
    public void put(K key, Value<V> value) {
        map.put(key, newEntry(key, value.get(), value.expireAfterWriteMillis(), value.expireAfterAccessMillis()));
        cleanUp();
    }

    @Override
    public void expire(K key, long expireAfterWriteMillis) {
        Entry<K, V> entry = map.get(key);
        ValuePack v = checkEntry(entry, null);
        if (v != null) {
            v.customExpireAfterWrite = expireAfterWriteMillis;
            v.refreshWrite();
        }
        cleanUp();
    }

    @Override
    public void expire(K key, long expireAfterWriteMillis, long expireAfterAccessMillis) {
        Entry<K, V> entry = map.get(key);
        ValuePack v = checkEntry(entry, null);
        if (v != null) {
            v.customExpireAfterWrite = expireAfterWriteMillis;
            v.customExpireAfterAccess = expireAfterAccessMillis;
            v.refreshWrite();
        }
        cleanUp();
    }

    @Override
    public void expire(K key, @Nullable Duration expireAfterWrite, @Nullable Duration expireAfterAccess) {
        expire(
            key,
            expireAfterWrite == null ? -1 : expireAfterWrite.toMillis(),
            expireAfterAccess == null ? -1 : expireAfterAccess.toMillis()
        );
    }

    @Override
    public void remove(K key) {
        Entry<K, V> entry = map.remove(key);
        if (entry != null) {
            entry.clear();
        }
        map.remove(key);
        cleanUp();
    }

    @Override
    public void removeIf(BiPredicate<K, V> predicate) {
        map.entrySet().removeIf(it -> {
            K key = it.getKey();
            Entry<K, V> entry = it.getValue();
            ValuePack v = checkEntry(entry, null);
            if (v == null) {
                return true;
            }
            if (predicate.test(key, v.value)) {
                entry.clear();
                return true;
            }
            return false;
        });
        cleanUp();
    }

    @Override
    public void removeEntry(BiPredicate<K, Value<V>> predicate) {
        map.entrySet().removeIf(it -> {
            K key = it.getKey();
            Entry<K, V> entry = it.getValue();
            ValuePack v = checkEntry(entry, null);
            if (v == null) {
                return true;
            }
            Value<V> wrapper = Value.of(
                v.value,
                v.customExpireAfterWrite < 0 ? expireAfterWrite : v.customExpireAfterWrite,
                v.customExpireAfterAccess < 0 ? expireAfterAccess : v.customExpireAfterAccess
            );
            if (predicate.test(key, wrapper)) {
                entry.clear();
                return true;
            }
            return false;
        });
        cleanUp();
    }

    @Override
    public int size() {
        cleanUp();
        return map.size();
    }

    @Override
    public void clear() {
        removeIf((k, v) -> true);
    }

    @Override
    public void cleanUp() {
        while (true) {
            Object x = queue.poll();
            if (x == null) {
                break;
            }
            Entry<K, V> entry = Gek.as(x);
            entry.onRemove();
            K key = entry.key();
            map.compute(key, (k, v) -> {
                if (v == entry) {
                    v.onRemove();
                    return null;
                }
                return v;
            });
        }
    }

    @Nullable
    private CacheImplV1<K, V>.ValuePack checkEntry(@Nullable Entry<K, V> entry, Boolean isWrite) {
        if (entry == null) {
            return null;
        }
        ValuePack value = entry.value();
        if (value == null) {
            return null;
        }
        if (value.isExpired()) {
            entry.clear();
            return null;
        }
        if (isWrite == null) {
            return value;
        }
        if (isWrite) {
            value.refreshWrite();
        } else {
            value.refreshAccess();
        }
        return value;
    }

    private Entry<K, V> newEntry(K key, @Nullable V value, long expireAfterWrite, long expireAfterAccess) {
        ValuePack v = new ValuePack(value, expireAfterWrite, expireAfterAccess);
        return newEntry(key, v);
    }

    private Entry<K, V> newEntry(K key, ValuePack value) {
        return useSoft ? new SoftEntry(key, value) : new WeakEntry(key, value);
    }

    private interface Entry<K, V> {

        K key();

        CacheImplV1<K, V>.ValuePack value();

        void clear();

        void onRemove();
    }

    private final class SoftEntry extends SoftReference<ValuePack> implements Entry<K, V> {

        private final K key;
        private final AtomicInteger counter = removeListener == null ? null : new AtomicInteger();

        public SoftEntry(K key, ValuePack referent) {
            super(referent, queue);
            this.key = key;
        }

        @Override
        public K key() {
            return key;
        }

        @Override
        public ValuePack value() {
            return super.get();
        }

        @Override
        public void clear() {
            super.clear();
            onRemove();
        }

        @Override
        public void onRemove() {
            if (counter == null) {
                return;
            }
            int c = counter.getAndIncrement();
            if (c > 0) {
                return;
            }
            removeListener.onRemove(key, null, CacheImplV1.this);
        }
    }

    private final class WeakEntry extends WeakReference<ValuePack> implements Entry<K, V> {

        private final K key;
        private final AtomicInteger counter = removeListener == null ? null : new AtomicInteger();

        public WeakEntry(K key, ValuePack referent) {
            super(referent, queue);
            this.key = key;
        }

        @Override
        public K key() {
            return key;
        }

        @Override
        public ValuePack value() {
            return super.get();
        }

        @Override
        public void clear() {
            super.clear();
            onRemove();
        }

        @Override
        public void onRemove() {
            if (counter == null) {
                return;
            }
            int c = counter.getAndIncrement();
            if (c > 0) {
                return;
            }
            removeListener.onRemove(key, null, CacheImplV1.this);
        }
    }

    private final class ValuePack {

        private final V value;
        private long customExpireAfterWrite;
        private long customExpireAfterAccess;
        private long expiredAt = -1;

        private ValuePack(V value, long expireAfterWrite, long expireAfterAccess) {
            this.value = value;
            this.customExpireAfterWrite = expireAfterWrite;
            this.customExpireAfterAccess = expireAfterAccess;
            refreshWrite();
        }

        public boolean isExpired() {
            if (expiredAt < 0) {
                return false;
            }
            long now = System.currentTimeMillis();
            return now > expiredAt;
        }

        public void refreshWrite() {
            long exp = customExpireAfterWrite < 0 ? expireAfterWrite : customExpireAfterWrite;
            if (exp < 0) {
                return;
            }
            this.expiredAt = System.currentTimeMillis() + exp;
        }

        public void refreshAccess() {
            long exp = customExpireAfterAccess < 0 ? expireAfterAccess : customExpireAfterAccess;
            if (exp < 0) {
                return;
            }
            this.expiredAt = System.currentTimeMillis() + exp;
        }
    }
}
