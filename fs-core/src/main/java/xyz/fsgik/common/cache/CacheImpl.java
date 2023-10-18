//package xyz.fsgik.common.cache;
//
//import lombok.Getter;
//import xyz.fsgik.annotations.Nullable;
//import xyz.fsgik.common.base.FsCheck;
//import xyz.fsgik.common.base.FsWrapper;
//
//import java.lang.ref.ReferenceQueue;
//import java.lang.ref.SoftReference;
//import java.lang.ref.WeakReference;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.function.BiPredicate;
//import java.util.function.Function;
//
///**
// * Default implementation for {@link FsCache}.
// */
//final class CacheImpl<K, V> implements FsCache<K, V> {
//
//    private final BackMap backMap;
//
//    CacheImpl(boolean isSoft) {
//        this.backMap = new BackMap(0, null, isSoft);
//    }
//
//    CacheImpl(boolean isSoft, FsCache.RemoveListener<K, V> removeListener) {
//        this.backMap = new BackMap(0, removeListener, isSoft);
//    }
//
//    CacheImpl(boolean isSoft, int initialCapacity) {
//        this.backMap = new BackMap(initialCapacity, null, isSoft);
//    }
//
//    CacheImpl(boolean isSoft, int initialCapacity, FsCache.RemoveListener<K, V> removeListener) {
//        this.backMap = new BackMap(initialCapacity, removeListener, isSoft);
//    }
//
//    @Override
//    public @Nullable V get(K key) {
//        return backMap.get(key);
//    }
//
//    @Override
//    public @Nullable V get(K key, Function<? super K, ? extends V> loader) {
//        return backMap.get(key, loader);
//    }
//
//    @Override
//    public @Nullable FsWrapper<V> getWrapper(K key) {
//        return null;
//    }
//
//    @Override
//    public @Nullable FsWrapper<V> getWrapper(K key, Function<? super K, @Nullable FsWrapper<? extends V>> loader) {
//        return null;
//    }
//
//    @Override
//    public V put(K key, V value) {
//        return backMap.put(key, value);
//    }
//
//    @Override
//    public void remove(K key) {
//        backMap.remove(key);
//    }
//
//    @Override
//    public void removeIf(BiPredicate<K, V> predicate) {
//        backMap.removeIf(predicate);
//    }
//
//    @Override
//    public int size() {
//        return backMap.size();
//    }
//
//    @Override
//    public void clear() {
//        backMap.clear();
//    }
//
//    @Override
//    public void cleanUp() {
//        backMap.cleanUp();
//    }
//
//    private interface CacheEntry<K> extends FsWrapper<Object>{
//
//        K getKey();
//
//        void invalid();
//
//        void clear();
//    }
//
//    @Getter
//    private static final class SoftCacheEntry<K> extends SoftReference<Object> implements CacheEntry<K> {
//
//        private final K key;
//
//        public SoftCacheEntry(K key, Object value, ReferenceQueue<? super Object> q) {
//            super(value, q);
//            this.key = key;
//        }
//
//        @Override
//        public void invalid() {
//            this.enqueue();
//        }
//    }
//
//    @Getter
//    private static final class WeakCacheEntry<K> extends WeakReference<Object> implements CacheEntry<K> {
//
//        private final K key;
//
//        public WeakCacheEntry(K key, Object value, ReferenceQueue<? super Object> q) {
//            super(value, q);
//            this.key = key;
//        }
//
//        @Override
//        public void invalid() {
//            this.enqueue();
//        }
//    }
//
//    private static final class Removed {
//    }
//
//    private final class BackMap implements Map<K, V> {
//
//        private final Map<K, CacheEntry<K>> map;
//        private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
//        private final FsCache.RemoveListener<K, V> removeListener;
//        private final boolean isSoft;
//        private volatile boolean inCleanUp = false;
//
//        private BackMap(int initialCapacity, @Nullable RemoveListener<K, V> removeListener, boolean isSoft) {
//            this.map = initialCapacity == 0 ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(initialCapacity);
//            this.removeListener = removeListener;
//            this.isSoft = isSoft;
//        }
//
//        @Override
//        public int size() {
//            cleanUp();
//            return map.size();
//        }
//
//        @Override
//        public boolean isEmpty() {
//            return size() == 0;
//        }
//
//        @Override
//        public boolean containsKey(Object key) {
//            return false;
//        }
//
//        @Override
//        public boolean containsValue(Object value) {
//            return false;
//        }
//
//        @Override
//        public V get(Object key) {
//            CacheEntry<K> entry = map.get(key);
//            V value = getValue(entry);
//            cleanUp();
//            return value;
//        }
//
//        public @Nullable V get(K key, Function<? super K, ? extends V> loader) {
//            CacheEntry<K> entry = map.computeIfAbsent(key, it -> newEntry(it, loader.apply(it)));
//            V value = getValue(entry);
//            cleanUp();
//            return value;
//        }
//
//        @Nullable
//        public FsWrapper<V> getWrapper(Object key) {
//            CacheEntry<K> entry = map.get(key);
//            V value = getValue(entry);
//            cleanUp();
//            return value;
//        }
//
//        public @Nullable FsWrapper<V> getWrapper(K key, Function<? super K, ? extends V> loader) {
//            CacheEntry<K> entry = map.computeIfAbsent(key, it -> newEntry(it, loader.apply(it)));
//            V value = getValue(entry);
//            cleanUp();
//            return value;
//        }
//
//        @Override
//        public V put(K key, V value) {
//            FsCheck.checkNull(value);
//            CacheEntry<K> oldEntry = map.put(key, newEntry(key, value));
//            V oldValue = getValue(oldEntry);
//            if (oldEntry != null) {
//                oldEntry.invalid();
//            }
//            cleanUp();
//            return oldValue;
//        }
//
//        @Override
//        public V remove(Object key) {
//            CacheEntry<K> entry = map.get(key);
//            V value = getValue(entry);
//            if (entry != null) {
//                entry.invalid();
//            }
//            cleanUp();
//            return value;
//        }
//
//        public void removeIf(BiPredicate<K, V> predicate) {
//            map.replaceAll((key, entry) -> {
//                V value = getValue(entry);
//                if (value != null && predicate.test(key, value)) {
//                    entry.invalid();
//                    return newEntry(key, null);
//                }
//                return entry;
//            });
//            cleanUp();
//        }
//
//        @Override
//        public void putAll(Map<? extends K, ? extends V> m) {
//        }
//
//        @Override
//        public void clear() {
//            map.replaceAll((key, entry) -> {
//                entry.invalid();
//                return newEntry(key, null);
//            });
//            cleanUp();
//        }
//
//        @Override
//        public Set<K> keySet() {
//            return map.keySet();
//        }
//
//        @Override
//        public Collection<V> values() {
//            return Collections.emptyList();
//        }
//
//        @Override
//        public Set<Entry<K, V>> entrySet() {
//            return Collections.emptySet();
//        }
//
//        public void cleanUp() {
//            if (inCleanUp) {
//                return;
//            }
//            synchronized (this) {
//                if (inCleanUp) {
//                    return;
//                }
//                inCleanUp = true;
//            }
//            while (true) {
//                Object x = queue.poll();
//                if (x == null) {
//                    break;
//                }
//                CacheEntry<K> entry = (CacheEntry<K>) x;
//                map.remove(entry.getKey());
//                entry.clear();
//                if (removeListener != null) {
//                    removeListener.onRemove(CacheImpl.this, entry.getKey());
//                }
//            }
//            inCleanUp = false;
//        }
//
//        @Nullable
//        private V getValue(@Nullable CacheEntry<K> entry) {
//            if (entry == null) {
//                return null;
//            }
//            Object value = entry.get();
//            if (value == null) {
//                return null;
//            }
//            if (value instanceof Removed) {
//                entry.invalid();
//                return null;
//            }
//            return (V) value;
//        }
//
//        private CacheEntry<K> newEntry(K key, @Nullable Object value) {
//            return isSoft ?
//                new SoftCacheEntry<>(key, value == null ? new Removed() : value, queue)
//                :
//                new WeakCacheEntry<>(key, value == null ? new Removed() : value, queue);
//        }
//    }
//}
