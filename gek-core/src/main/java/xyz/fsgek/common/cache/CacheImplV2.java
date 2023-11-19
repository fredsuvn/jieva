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

final class CacheImplV2<K, V> implements GekCache<K, V> {

    private final GekCache.RemoveListener<K, V> removeListener;
    private final boolean useSoft;
    private final Map<K, Entry<K>> map;
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private final long expireAfterWrite;
    private final long expireAfterAccess;

    CacheImplV2(Builder<K, V> builder) {
        this.useSoft = builder.useSoft();
        this.expireAfterAccess = builder.expireAfterAccessMillis();
        this.expireAfterWrite = builder.expireAfterWriteMillis();
        this.map = builder.initialCapacity() <= 0 ?
            new ConcurrentHashMap<>() : new ConcurrentHashMap<>(builder.initialCapacity());
        this.removeListener = builder.removeListener();
    }

    @Override
    public @Nullable V get(K key) {
        Entry<K> entry = map.get(key);
        entry = getEntry(entry, false, true);
        cleanUp();
        return entry == null ? null : clearKeepValue(entry);
    }

    @Override
    public @Nullable V get(K key, Function<? super K, ? extends V> loader) {
        Entry<K> entry = map.get(key);
        entry = getEntry(entry, false, true);
        if (entry != null) {
            cleanUp();
            return clearKeepValue(entry);
        }
        GekRef<Entry<K>> result = GekRef.ofNull();
        map.compute(key, (k, old) -> {
            Entry<K> oldEntry = getEntry(old, false, true);
            if (oldEntry != null) {
                result.set(oldEntry);
                return old;
            }
            V newValue = loader.apply(k);
            Entry<K> newEntry = newEntry(k, newValue, expireAfterWrite, expireAfterAccess);
            newEntry.keepValue(newValue);
            result.set(newEntry);
            return newEntry;
        });
        cleanUp();
        return clearKeepValue(result.get());
    }

    @Override
    public @Nullable Optional<V> getOptional(K key) {
        Entry<K> entry = map.get(key);
        entry = getEntry(entry, false, true);
        cleanUp();
        return entry == null ? null : Optional.ofNullable(clearKeepValue(entry));
    }

    @Override
    public @Nullable Optional<V> getOptional(K key, Function<? super K, @Nullable Value<? extends V>> loader) {
        Entry<K> entry = map.get(key);
        entry = getEntry(entry, false, true);
        if (entry != null) {
            cleanUp();
            return Optional.ofNullable(clearKeepValue(entry));
        }
        GekRef<Entry<K>> result = GekRef.ofNull();
        map.compute(key, (k, old) -> {
            Entry<K> oldEntry = getEntry(old, false, true);
            if (oldEntry != null) {
                result.set(oldEntry);
                return old;
            }
            Value<? extends V> newValue = loader.apply(k);
            if (newValue == null) {
                return null;
            }
            Entry<K> newEntry = newEntry(
                k, newValue.get(), newValue.expireAfterWriteMillis(), newValue.expireAfterAccessMillis());
            newEntry.keepValue(newValue.get());
            result.set(newEntry);
            return newEntry;
        });
        cleanUp();
        Entry<K> resultEntry = result.get();
        if (resultEntry == null) {
            return null;
        }
        return Optional.ofNullable(clearKeepValue(resultEntry));
    }

    @Override
    public boolean contains(K key) {
        Entry<K> entry = map.get(key);
        entry = getEntry(entry, null, false);
        cleanUp();
        return entry != null;
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
        Entry<K> entry = map.get(key);
        entry = getEntry(entry, null, false);
        if (entry != null) {
            entry.refreshWrite(expireAfterWriteMillis);
            entry.refreshWrite();
        }
        cleanUp();
    }

    @Override
    public void expire(K key, long expireAfterWriteMillis, long expireAfterAccessMillis) {
        Entry<K> entry = map.get(key);
        entry = getEntry(entry, null, false);
        if (entry != null) {
            entry.refreshWrite(expireAfterWriteMillis);
            entry.refreshAccess(expireAfterAccessMillis);
            entry.refreshWrite();
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
        Entry<K> entry = map.remove(key);
        if (entry != null && entry.value() != null) {
            entry.clear();
        }
        cleanUp();
    }

    @Override
    public void removeIf(BiPredicate<K, V> predicate) {
        map.entrySet().removeIf(it -> {
            K key = it.getKey();
            Entry<K> entry = it.getValue();
            entry = getEntry(entry, null, true);
            if (entry == null) {
                return true;
            }
            if (predicate.test(key, clearKeepValue(entry))) {
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
            Entry<K> entry = it.getValue();
            entry = getEntry(entry, null, true);
            if (entry == null) {
                return true;
            }
            Value<V> wrapper = Value.of(
                clearKeepValue(entry),
                entry.expireAfterWrite() < 0 ? expireAfterWrite : entry.expireAfterWrite(),
                entry.expireAfterAccess() < 0 ? expireAfterAccess : entry.expireAfterAccess()
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
        map.entrySet().removeIf(it -> {
            Entry<K> entry = it.getValue();
            entry = getEntry(entry, null, false);
            if (entry == null) {
                return true;
            }
            entry.clear();
            return true;
        });
        cleanUp();
    }

    @Override
    public void cleanUp() {
        while (true) {
            Object x = queue.poll();
            if (x == null) {
                break;
            }
            Entry<K> entry = Gek.as(x);
            entry.onRemove();
            K key = entry.key();
            map.compute(key, (k, v) -> {
                if (v == entry) {
                    return null;
                }
                return v;
            });
        }
    }

    @Nullable
    private Entry<K> getEntry(@Nullable Entry<K> entry, Boolean isWrite, boolean needKeep) {
        if (entry == null) {
            return null;
        }
        Object value = entry.value();
        if (value == null) {
            return null;
        }
        if (entry.isExpired()) {
            entry.clear();
            return null;
        }
        if (needKeep) {
            entry.keepValue(value);
        }
        if (isWrite == null) {
            return entry;
        }
        if (isWrite) {
            entry.refreshWrite();
        } else {
            entry.refreshAccess();
        }
        return entry;
    }

    @Nullable
    private V clearKeepValue(@Nullable Entry<K> entry) {
        Object value = entry.keepValue();
        entry.clearKeepValue();
        if (value instanceof Null) {
            return null;
        }
        return Gek.as(value);
    }

    private Entry<K> newEntry(K key, @Nullable V value, long expireAfterWrite, long expireAfterAccess) {
        Object v = value == null ? new Null() : value;
        return useSoft ?
            new SoftEntry(key, v, expireAfterWrite, expireAfterAccess)
            :
            new WeakEntry(key, value, expireAfterWrite, expireAfterAccess);
    }

    private interface Entry<K> {

        K key();

        Object value();

        void clear();

        boolean isExpired();

        void refreshWrite();

        void refreshAccess();

        void refreshWrite(long expireAfterWrite);

        void refreshAccess(long expireAfterAccess);

        long expireAfterWrite();

        long expireAfterAccess();

        void keepValue(Object value);

        Object keepValue();

        void clearKeepValue();

        void onRemove();
    }

    private final class SoftEntry extends SoftReference<Object> implements Entry<K> {

        private final K key;
        private long customExpireAfterWrite;
        private long customExpireAfterAccess;
        private long expiredAt = -1;
        private Object keepValue;
        private final AtomicInteger counter = removeListener == null ? null : new AtomicInteger();

        public SoftEntry(K key, Object referent, long expireAfterWrite, long expireAfterAccess) {
            super(referent, queue);
            this.key = key;
            this.customExpireAfterWrite = expireAfterWrite;
            this.customExpireAfterAccess = expireAfterAccess;
            refreshWrite();
        }

        @Override
        public K key() {
            return key;
        }

        @Override
        public Object value() {
            return super.get();
        }

        @Override
        public void clear() {
            super.clear();
            onRemove();
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

        @Override
        public void refreshWrite(long expireAfterWrite) {
            customExpireAfterWrite = expireAfterWrite;
        }

        @Override
        public void refreshAccess(long expireAfterAccess) {
            customExpireAfterAccess = expireAfterAccess;
        }

        @Override
        public long expireAfterWrite() {
            return customExpireAfterWrite;
        }

        @Override
        public long expireAfterAccess() {
            return customExpireAfterAccess;
        }

        @Override
        public void keepValue(Object value) {
            keepValue = (value == null ? new Null() : value);
        }

        @Override
        public Object keepValue() {
            return keepValue;
        }

        @Override
        public void clearKeepValue() {
            keepValue = null;
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
            removeListener.onRemove(key, null, CacheImplV2.this);
        }
    }

    private final class WeakEntry extends WeakReference<Object> implements Entry<K> {

        private final K key;
        private long customExpireAfterWrite;
        private long customExpireAfterAccess;
        private long expiredAt = -1;
        private Object keepValue;
        private final AtomicInteger counter = removeListener == null ? null : new AtomicInteger();

        public WeakEntry(K key, Object referent, long expireAfterWrite, long expireAfterAccess) {
            super(referent, queue);
            this.key = key;
            this.customExpireAfterWrite = expireAfterWrite;
            this.customExpireAfterAccess = expireAfterAccess;
            refreshWrite();
        }

        @Override
        public K key() {
            return key;
        }

        @Override
        public Object value() {
            return super.get();
        }

        @Override
        public void clear() {
            super.clear();
            onRemove();
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

        @Override
        public void refreshWrite(long expireAfterWrite) {
            customExpireAfterWrite = expireAfterWrite;
        }

        @Override
        public void refreshAccess(long expireAfterAccess) {
            customExpireAfterAccess = expireAfterAccess;
        }

        @Override
        public long expireAfterWrite() {
            return customExpireAfterWrite;
        }

        @Override
        public long expireAfterAccess() {
            return customExpireAfterAccess;
        }

        @Override
        public void keepValue(Object value) {
            keepValue = (value == null ? new Null() : value);
        }

        @Override
        public Object keepValue() {
            return keepValue;
        }

        @Override
        public void clearKeepValue() {
            keepValue = null;
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
            removeListener.onRemove(key, null, CacheImplV2.this);
        }
    }

    private static final class Null {
    }
}
