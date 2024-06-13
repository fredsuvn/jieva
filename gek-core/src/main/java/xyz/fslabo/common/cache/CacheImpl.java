package xyz.fslabo.common.cache;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Gek;
import xyz.fslabo.common.base.ref.GekRef;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Function;

final class CacheImpl<K, V> implements GekCache<K, V> {

    private final boolean useSoft;
    private final Map<K, Entry<K>> data;
    private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
    private final long expireAfterWrite;
    private final long expireAfterAccess;
    private final RemovalListener<? super K, ? super V> removalListener;

    CacheImpl(Builder<K, V> builder) {
        this.useSoft = builder.isSoftValues();
        this.expireAfterAccess = builder.expireAfterAccessMillis();
        this.expireAfterWrite = builder.expireAfterWriteMillis();
        this.data = builder.initialCapacity() <= 0 ?
            new ConcurrentHashMap<>() : new ConcurrentHashMap<>(builder.initialCapacity());
        this.removalListener = builder.removeListener();
        ;
    }

    @Override
    public @Nullable V get(K key) {
        Entry<K> entry = data.get(key);
        if (entry == null) {
            cleanUp();
            return null;
        }
        if (entry.isExpired()) {
            entry.clear(RemovalListener.Cause.EXPIRED);
            cleanUp();
            return null;
        }
        entry.refreshAccess();
        cleanUp();
        return valueAs(entry.value());
    }

    @Override
    public @Nullable V get(K key, Function<? super K, ? extends V> loader) {
        Entry<K> entry = data.get(key);
        if (entry == null) {
            return compute(key, loader);
        }
        if (entry.isExpired()) {
            entry.clear(RemovalListener.Cause.EXPIRED);
            return compute(key, loader);
        }
        Object value = entry.value();
        if (value == null) {
            return compute(key, loader);
        }
        entry.refreshAccess();
        cleanUp();
        return valueAs(value);
    }

    private @Nullable V compute(K key, Function<? super K, ? extends V> loader) {
        GekRef<V> result = GekRef.ofNull();
        data.compute(key, (k, old) -> {
            if (old == null) {
                V nv = loader.apply(k);
                Entry<K> newEntry = newEntry(k, nv, expireAfterWrite, expireAfterAccess);
                result.set(nv);
                return newEntry;
            }
            if (old.isExpired()) {
                old.clear(RemovalListener.Cause.EXPIRED);
                V nv = loader.apply(k);
                Entry<K> newEntry = newEntry(k, nv, expireAfterWrite, expireAfterAccess);
                result.set(nv);
                return newEntry;
            }
            Object ov = old.value();
            if (ov == null) {
                V nv = loader.apply(k);
                Entry<K> newEntry = newEntry(k, nv, expireAfterWrite, expireAfterAccess);
                result.set(nv);
                return newEntry;
            }
            result.set(valueAs(ov));
            old.refreshAccess();
            return old;
        });
        cleanUp();
        return result.get();
    }

//    @Override
//    public @Nullable GekObject<V> getWrapper(K key) {
//        Entry<K> entry = data.get(key);
//        if (entry == null) {
//            cleanUp();
//            return null;
//        }
//        if (entry.isExpired()) {
//            entry.clear(RemovalListener.Cause.EXPIRED);
//            cleanUp();
//            return null;
//        }
//        Object value = entry.value();
//        if (value == null) {
//            return null;
//        }
//        entry.refreshAccess();
//        cleanUp();
//        return GekObject.of(valueAs(value));
//    }

//    @Override
//    public @Nullable GekObject<V> getWrapper(
//        K key, Function<? super K, @Nullable ? extends Value<? extends V>> loader) {
//        Entry<K> entry = data.get(key);
//        if (entry == null) {
//            return computeWrapper(key, loader);
//        }
//        if (entry.isExpired()) {
//            entry.clear(RemovalListener.Cause.EXPIRED);
//            return computeWrapper(key, loader);
//        }
//        Object value = entry.value();
//        if (value == null) {
//            return computeWrapper(key, loader);
//        }
//        entry.refreshAccess();
//        cleanUp();
//        return GekObject.of(valueAs(value));
//    }

//    private @Nullable GekObject<V> computeWrapper(
//        K key, Function<? super K, @Nullable ? extends Value<? extends V>> loader) {
//        GekRef<GekObject<V>> result = GekRef.ofNull();
//        data.compute(key, (k, old) -> {
//            if (old == null) {
//                Value<? extends V> nv = loader.apply(k);
//                if (nv == null) {
//                    return null;
//                }
//                Entry<K> newEntry = newEntry(k, nv.get(), nv.expireAfterWriteMillis(), nv.expireAfterAccessMillis());
//                result.set(GekObject.of(nv.get()));
//                return newEntry;
//            }
//            if (old.isExpired()) {
//                old.clear(RemovalListener.Cause.EXPIRED);
//                Value<? extends V> nv = loader.apply(k);
//                if (nv == null) {
//                    return null;
//                }
//                Entry<K> newEntry = newEntry(k, nv.get(), nv.expireAfterWriteMillis(), nv.expireAfterAccessMillis());
//                result.set(GekObject.of(nv.get()));
//                return newEntry;
//            }
//            Object ov = old.value();
//            if (ov == null) {
//                Value<? extends V> nv = loader.apply(k);
//                if (nv == null) {
//                    return null;
//                }
//                Entry<K> newEntry = newEntry(k, nv.get(), nv.expireAfterWriteMillis(), nv.expireAfterAccessMillis());
//                result.set(GekObject.of(nv.get()));
//                return newEntry;
//            }
//            result.set(GekObject.of(valueAs(ov)));
//            old.refreshAccess();
//            return old;
//        });
//        cleanUp();
//        return result.get();
//    }

    @Override
    public boolean contains(K key) {
        Entry<K> entry = data.get(key);
        if (entry == null) {
            cleanUp();
            return false;
        }
        if (entry.isExpired()) {
            entry.clear(RemovalListener.Cause.EXPIRED);
            cleanUp();
            return false;
        }
        Object value = entry.value();
        if (value == null) {
            cleanUp();
            return false;
        }
        cleanUp();
        return true;
    }

    @Override
    public void put(K key, V value) {
        put(key, value, expireAfterWrite);
    }

    @Override
    public void put(K key, V value, long expireAfterWriteMillis) {
        Entry<K> old = data.put(key, newEntry(key, value, expireAfterWriteMillis, expireAfterAccess));
        if (old != null) {
            old.clear(RemovalListener.Cause.REPLACED);
        }
        cleanUp();
    }

    @Override
    public void put(K key, Value<? extends V> value) {
        Entry<K> old = data.put(
            key, newEntry(key, value.get(), value.expireAfterWriteMillis(), value.expireAfterAccessMillis()));
        if (old != null) {
            old.clear(RemovalListener.Cause.REPLACED);
        }
        cleanUp();
    }

    @Override
    public void expire(K key, long expireAfterWriteMillis) {
        Entry<K> entry = data.get(key);
        if (entry == null) {
            cleanUp();
            return;
        }
        entry.setExpireAfterWrite(expireAfterWriteMillis);
        entry.refreshWrite();
        cleanUp();
    }

    @Override
    public void expire(K key, long expireAfterWriteMillis, long expireAfterAccessMillis) {
        Entry<K> entry = data.get(key);
        if (entry == null) {
            cleanUp();
            return;
        }
        entry.setExpireAfterWrite(expireAfterWriteMillis);
        entry.setExpireAfterAccess(expireAfterAccessMillis);
        entry.refreshWrite();
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
        Entry<K> entry = data.remove(key);
        if (entry != null) {
            entry.clear(RemovalListener.Cause.EXPLICIT);
        }
        cleanUp();
    }

    @Override
    public void removeIf(BiPredicate<? super K, ? super V> predicate) {
        data.entrySet().removeIf(it -> {
            K key = it.getKey();
            Entry<K> entry = it.getValue();
            if (entry == null) {
                return true;
            }
            if (entry.isExpired()) {
                entry.clear(RemovalListener.Cause.EXPIRED);
                return true;
            }
            Object value = entry.value();
            if (value == null) {
                return true;
            }
            if (predicate.test(key, valueAs(value))) {
                entry.clear(RemovalListener.Cause.EXPLICIT);
                return true;
            }
            return false;
        });
        cleanUp();
    }

    @Override
    public void removeEntry(BiPredicate<? super K, ? super Value<? super V>> predicate) {
        data.entrySet().removeIf(it -> {
            K key = it.getKey();
            Entry<K> entry = it.getValue();
            if (entry == null) {
                return true;
            }
            if (entry.isExpired()) {
                entry.clear(RemovalListener.Cause.EXPIRED);
                return true;
            }
            Object value = entry.value();
            if (value == null) {
                return true;
            }
            Value<V> v = Value.of(valueAs(value), entry.expireAfterWrite(), entry.expireAfterAccess());
            if (predicate.test(key, v)) {
                entry.clear(RemovalListener.Cause.EXPLICIT);
                return true;
            }
            return false;
        });
        cleanUp();
    }

    @Override
    public int size() {
        cleanUp();
        return data.size();
    }

    @Override
    public void clear() {
        data.entrySet().removeIf(it -> {
            Entry<K> entry = it.getValue();
            if (entry == null) {
                return true;
            }
            if (entry.isExpired()) {
                entry.clear(RemovalListener.Cause.EXPIRED);
                return true;
            }
            Object value = entry.value();
            if (value == null) {
                return true;
            }
            entry.clear(RemovalListener.Cause.EXPLICIT);
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
            entry.onRemoval(RemovalListener.Cause.COLLECTED);
            K key = entry.key();
            data.computeIfPresent(key, (k, v) -> {
                if (v == entry) {
                    return null;
                }
                return v;
            });
        }
    }

    private Entry<K> newEntry(K key, @Nullable V value, long expireAfterWrite, long expireAfterAccess) {
        Object v = value == null ? new Null() : value;
        return useSoft ?
            new SoftEntry(key, v, expireAfterWrite, expireAfterAccess)
            :
            new WeakEntry(key, value, expireAfterWrite, expireAfterAccess);
    }

    @Nullable
    private V valueAs(@Nullable Object value) {
        if (value == null || value instanceof Null) {
            return null;
        }
        return Gek.as(value);
    }


    protected interface Entry<K> {

        K key();

        Object value();

        boolean isExpired();

        void refreshWrite();

        void refreshAccess();

        void setExpireAfterWrite(long expireAfterWrite);

        void setExpireAfterAccess(long expireAfterAccess);

        long expireAfterWrite();

        long expireAfterAccess();

        void clear(RemovalListener.Cause cause);

        void onRemoval(RemovalListener.Cause cause);
    }

    private final class SoftEntry extends SoftReference<Object> implements Entry<K> {

        private final K key;
        private long customExpireAfterWrite;
        private long customExpireAfterAccess;
        private long expiredAt = -1;
        private boolean removed = false;

        public SoftEntry(K key, Object referent, long expireAfterWrite, long expireAfterAccess) {
            super(referent, CacheImpl.this.queue);
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

        public boolean isExpired() {
            if (removed) {
                return true;
            }
            if (expiredAt < 0) {
                return false;
            }
            long now = System.currentTimeMillis();
            return now > expiredAt;
        }

        public void refreshWrite() {
            long exp = expireAfterWrite();
            if (exp < 0) {
                return;
            }
            this.expiredAt = System.currentTimeMillis() + exp;
        }

        public void refreshAccess() {
            long exp = expireAfterAccess();
            if (exp < 0) {
                return;
            }
            this.expiredAt = System.currentTimeMillis() + exp;
        }

        @Override
        public void setExpireAfterWrite(long expireAfterWrite) {
            customExpireAfterWrite = expireAfterWrite;
        }

        @Override
        public void setExpireAfterAccess(long expireAfterAccess) {
            customExpireAfterAccess = expireAfterAccess;
        }

        @Override
        public long expireAfterWrite() {
            return customExpireAfterWrite < 0 ? CacheImpl.this.expireAfterWrite : customExpireAfterWrite;
        }

        @Override
        public long expireAfterAccess() {
            return customExpireAfterAccess < 0 ? CacheImpl.this.expireAfterAccess : customExpireAfterAccess;
        }

        @Override
        public void clear(RemovalListener.Cause cause) {
            if (removed) {
                return;
            }
            super.clear();
            onRemoval(cause);
        }

        @Override
        public void onRemoval(RemovalListener.Cause cause) {
            if (removed) {
                return;
            }
            if (CacheImpl.this.removalListener == null) {
                removed = true;
                return;
            }
            synchronized (SoftEntry.this) {
                if (removed) {
                    return;
                }
                removed = true;
                CacheImpl.this.removalListener.onRemoval(key(), valueAs(value()), cause);
            }
        }
    }

    private final class WeakEntry extends WeakReference<Object> implements Entry<K> {

        private final K key;
        private long customExpireAfterWrite;
        private long customExpireAfterAccess;
        private long expiredAt = -1;
        private boolean removed = false;

        public WeakEntry(K key, Object referent, long expireAfterWrite, long expireAfterAccess) {
            super(referent, CacheImpl.this.queue);
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

        public boolean isExpired() {
            if (removed) {
                return true;
            }
            if (expiredAt < 0) {
                return false;
            }
            long now = System.currentTimeMillis();
            return now > expiredAt;
        }

        public void refreshWrite() {
            long exp = expireAfterWrite();
            if (exp < 0) {
                return;
            }
            this.expiredAt = System.currentTimeMillis() + exp;
        }

        public void refreshAccess() {
            long exp = expireAfterAccess();
            if (exp < 0) {
                return;
            }
            this.expiredAt = System.currentTimeMillis() + exp;
        }

        @Override
        public void setExpireAfterWrite(long expireAfterWrite) {
            customExpireAfterWrite = expireAfterWrite;
        }

        @Override
        public void setExpireAfterAccess(long expireAfterAccess) {
            customExpireAfterAccess = expireAfterAccess;
        }

        @Override
        public long expireAfterWrite() {
            return customExpireAfterWrite < 0 ? CacheImpl.this.expireAfterWrite : customExpireAfterWrite;
        }

        @Override
        public long expireAfterAccess() {
            return customExpireAfterAccess < 0 ? CacheImpl.this.expireAfterAccess : customExpireAfterAccess;
        }

        @Override
        public void clear(RemovalListener.Cause cause) {
            if (removed) {
                return;
            }
            super.clear();
            onRemoval(cause);
        }

        @Override
        public void onRemoval(RemovalListener.Cause cause) {
            if (removed) {
                return;
            }
            if (CacheImpl.this.removalListener == null) {
                removed = true;
                return;
            }
            synchronized (WeakEntry.this) {
                if (removed) {
                    return;
                }
                removed = true;
                CacheImpl.this.removalListener.onRemoval(key(), valueAs(value()), cause);
            }
        }
    }

    private static final class Null {
    }
}
