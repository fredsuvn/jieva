package xyz.srclab.common.cache;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsCheck;
import xyz.srclab.common.base.ref.BooleanRef;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Default implementation for {@link FsCache}.
 */
final class FsCacheImpl<K, V> implements FsCache<K, V> {

    private final BackMap backMap;

    FsCacheImpl(boolean isSoft) {
        this.backMap = new BackMap(new ConcurrentHashMap<>(), null, isSoft);
    }

    FsCacheImpl(boolean isSoft, FsCache.RemoveListener<K, V> removeListener) {
        this.backMap = new BackMap(new ConcurrentHashMap<>(), removeListener, isSoft);
    }

    FsCacheImpl(boolean isSoft, int initialCapacity) {
        this.backMap = new BackMap(new ConcurrentHashMap<>(initialCapacity), null, isSoft);
    }

    FsCacheImpl(boolean isSoft, int initialCapacity, FsCache.RemoveListener<K, V> removeListener) {
        this.backMap = new BackMap(new ConcurrentHashMap<>(initialCapacity), removeListener, isSoft);
    }

    @Override
    public @Nullable V get(K key) {
        return backMap.get(key);
    }

    @Override
    public @Nullable V get(K key, Function<? super K, ? extends V> loader) {
        return backMap.get(key, loader);
    }

    @Override
    public V put(K key, V value) {
        return backMap.put(key, value);
    }

    @Override
    public void remove(K key) {
        backMap.remove(key);
    }

    @Override
    public int size() {
        return backMap.size();
    }

    @Override
    public void clear() {
        backMap.clear();
    }

    @Override
    public void cleanUp() {
        backMap.cleanUp();
    }

    @Override
    public Map<K, V> asMap() {
        return backMap;
    }

    private final class BackMap implements Map<K, V> {

        private final Map<K, CacheEntry<K>> map;
        private final ReferenceQueue<Object> queue = new ReferenceQueue<>();
        private final FsCache.RemoveListener<K, V> removeListener;
        private final boolean isSoft;
        private volatile boolean inCleanUp = false;

        private BackMap(Map<K, CacheEntry<K>> map, @Nullable RemoveListener<K, V> removeListener, boolean isSoft) {
            this.map = map;
            this.removeListener = removeListener;
            this.isSoft = isSoft;
        }

        @Override
        public int size() {
            cleanUp();
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public boolean containsKey(Object key) {
            return get(key) != null;
        }

        @Override
        public boolean containsValue(Object value) {
            if (value == null) {
                return false;
            }
            for (CacheEntry<K> entry : map.values()) {
                Object result = entry.get();
                if (result instanceof Removed) {
                    entry.clean();
                    continue;
                }
                if (Objects.equals(result, value)) {
                    return true;
                }
            }
            cleanUp();
            return false;
        }

        @Override
        public V get(Object key) {
            CacheEntry<K> entry = map.get(key);
            V result = getValue(entry);
            cleanUp();
            return result;
        }

        public @Nullable V get(K key, Function<? super K, ? extends V> loader) {
            CacheEntry<K> entry = map.computeIfAbsent(key, it -> {
                V newValue = loader.apply(it);
                if (newValue == null) {
                    return null;
                }
                return newEntry(it, newValue);
            });
            V result = getValue(entry);
            cleanUp();
            return result;
        }

        @Override
        public V put(K key, V value) {
            FsCheck.checkNull(value);
            CacheEntry<K> old = map.put(key, newEntry(key, value));
            V result = getValue(old);
            if (old != null) {
                old.clean();
            }
            cleanUp();
            return result;
        }

        @Override
        public V remove(Object key) {
            CacheEntry<K> entry = map.get(key);
            V result = getValue(entry);
            if (entry != null) {
                entry.clean();
            }
            cleanUp();
            return result;
        }

        @Override
        public void putAll( Map<? extends K, ? extends V> m) {
            m.forEach((k,v)->{
                FsCheck.checkNull(v);
                CacheEntry<K> old = map.put(k, newEntry(k, v));
                if (old != null) {
                    old.clean();
                }
            });
            cleanUp();
        }

        @Override
        public void clear() {
            map.replaceAll((key, old) -> {
                old.clean();
                return old;
            });
            cleanUp();
        }

        @NotNull
        @Override
        public Set<K> keySet() {
            return map.keySet();
        }

        @NotNull
        @Override
        public Collection<V> values() {
            return new Collection<V>() {
                @Override
                public int size() {
                    return 0;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public boolean contains(Object o) {
                    return false;
                }

                @NotNull
                @Override
                public Iterator<V> iterator() {
                    return null;
                }

                @NotNull
                @Override
                public Object[] toArray() {
                    return new Object[0];
                }

                @NotNull
                @Override
                public <T> T[] toArray(@NotNull T[] a) {
                    return null;
                }

                @Override
                public boolean add(V v) {
                    return false;
                }

                @Override
                public boolean remove(Object o) {
                    return false;
                }

                @Override
                public boolean containsAll(@NotNull Collection<?> c) {
                    return false;
                }

                @Override
                public boolean addAll(@NotNull Collection<? extends V> c) {
                    return false;
                }

                @Override
                public boolean removeAll(@NotNull Collection<?> c) {
                    return false;
                }

                @Override
                public boolean retainAll(@NotNull Collection<?> c) {
                    return false;
                }

                @Override
                public void clear() {

                }
            };
        }

        @NotNull
        @Override
        public Set<Entry<K, V>> entrySet() {
            return null;
        }

        @Override
        public V getOrDefault(Object key, V defaultValue) {
            Object o = new Object();
            CacheEntry<K> entry = map.getOrDefault(key, new EmptyCacheEntry<>(key, o, map));
            if (entry instanceof EmptyCacheEntry) {
                if (entry.get() == o) {
                    return defaultValue;
                }
            }
            V result = getValue(entry);
            cleanUp();
            return result;
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super V> action) {
            map.forEach((k,v)->{
                V result = getValue(v);
                if (result != null) {
                    action.accept(k, result);
                }
            });
            cleanUp();
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
            map.replaceAll((k,v)->{
                V result = getValue(v);
                if (result != null) {
                    V newValue = function.apply(k, result);
                    FsCheck.checkNull(newValue);
                    v.clean();
                    return newEntry(k, newValue);
                } else {
                    return v;
                }
            });
            cleanUp();
        }

        @Override
        public V putIfAbsent(K key, V value) {
            FsCheck.checkNull(value);
           CacheEntry<K> entry = map.putIfAbsent(key, newEntry(key, value));
            V result = getValue(entry);
            cleanUp();
            return result;
        }

        @Override
        public boolean remove(Object key, Object value) {
            if (value == null) {
                return false;
            }
            BooleanRef ref = new BooleanRef(false);
            map.forEach((k,v)->{
                if (!Objects.equals(k, key)) {
                    return;
                }
                V result = getValue(v);
                if (result != null && Objects.equals(v, value)) {
                    v.clean();
                    ref.set(true);
                }
            });
            cleanUp();
            return ref.get();
        }

        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            FsCheck.checkNull(newValue);
            BooleanRef ref = new BooleanRef(false);
            map.forEach((k,v)->{
                if (!Objects.equals(k, key)) {
                    return;
                }
                V result = getValue(v);
                if (result != null && Objects.equals(v, oldValue)) {
                    v.clean();
                    ref.set(true);
                }
            });
            cleanUp();
            return ref.get();
        }

        @org.jetbrains.annotations.Nullable
        @Override
        public V replace(K key, V value) {
            return Map.super.replace(key, value);
        }

        @Override
        public V computeIfAbsent(K key, @NotNull Function<? super K, ? extends V> mappingFunction) {
            return Map.super.computeIfAbsent(key, mappingFunction);
        }

        @Override
        public V computeIfPresent(K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return Map.super.computeIfPresent(key, remappingFunction);
        }

        @Override
        public V compute(K key, @NotNull BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return Map.super.compute(key, remappingFunction);
        }

        @Override
        public V merge(K key, @NotNull V value, @NotNull BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            return Map.super.merge(key, value, remappingFunction);
        }

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
                CacheEntry<K> entry = (CacheEntry<K>) x;
                map.remove(entry.getKey());
                if (removeListener != null) {
                    removeListener.onRemove(FsCacheImpl.this, entry.getKey());
                }
            }
            inCleanUp = false;
        }

        @Nullable
        private V getValue(@Nullable CacheEntry<K> entry) {
            if (entry == null) {
                return null;
            }
            Object result = entry.get();
            if (result == null) {
                return null;
            }
            if (result instanceof Removed) {
                entry.clean();
                return null;
            }
            return (V) result;
        }

        private CacheEntry<K> newEntry(K key, @Nullable Object value) {
            return isSoft ?
                new SoftCacheEntry<>(key, value == null ? new Removed() : value, queue)
                :
                new WeakCacheEntry<>(key, value == null ? new Removed() : value, queue);
        }
    }

    private interface CacheEntry<K> {

        K getKey();

        Object get();

        void clean();
    }

    @Getter
    private static class SoftCacheEntry<K> extends SoftReference<Object> implements CacheEntry<K> {

        private final K key;

        public SoftCacheEntry(K key, Object value, ReferenceQueue<? super Object> q) {
            super(value, q);
            this.key = key;
        }

        @Override
        public void clean() {
            this.enqueue();
        }
    }

    @Getter
    private static class WeakCacheEntry<K> extends WeakReference<Object> implements CacheEntry<K> {

        private final K key;

        public WeakCacheEntry(K key, Object value, ReferenceQueue<? super Object> q) {
            super(value, q);
            this.key = key;
        }

        @Override
        public void clean() {
            this.enqueue();
        }
    }

    @Getter
    private static class EmptyCacheEntry<K> implements CacheEntry<K> {

        private final Object key;
        private final Object value;
        private final Map<K, CacheEntry<K>> map ;

        public EmptyCacheEntry(Object key,Object value,Map<K, CacheEntry<K>> map ) {
            this.key = key;
            this.value = value;
            this.map = map;
        }

        public K getKey() {
            return (K) key;
        }

        @Override
        public Object get() {
            return value;
        }

        @Override
        public void clean() {
            map.remove(key);
        }
    }

    private static final class Removed {
    }
}
