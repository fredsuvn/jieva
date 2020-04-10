package xyz.srclab.common.collection.map;

import xyz.srclab.annotation.concurrent.ThreadSafe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Fixed-keys map, this map's keys are immutable, but values are mutable. That's means, you cannot put new key but
 * can change value of old key.
 * <p>
 * This map is thread-safe for reading (not for writing), fast, applicable to initialing scenes.
 */
@ThreadSafe
public class FixedKeysMap<K, V> implements Map<K, V> {

    private final HashMap<K, V> hashMap;

    public FixedKeysMap(Map<K, V> map) {
        this.hashMap = new HashMap<>(map);
    }

    @Override
    public int size() {
        return hashMap.size();
    }

    @Override
    public boolean isEmpty() {
        return hashMap.isEmpty();
    }

    @Override
    public V get(Object key) {
        return hashMap.get(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return hashMap.containsKey(key);
    }

    @Override
    public V put(K key, V value) {
        if (value == null || !hashMap.containsKey(key)) {
            throw new UnsupportedOperationException("Cannot add new key: " + key + ", value: " + value);
        }
        return hashMap.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        m.forEach((k, v) -> {
            if (v == null || !hashMap.containsKey(k)) {
                throw new UnsupportedOperationException("Cannot add new key: " + k + ", value: " + v);
            }
        });
        hashMap.putAll(m);
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsValue(Object value) {
        return hashMap.containsValue(value);
    }

    @Override
    public Set<K> keySet() {
        return hashMap.keySet();
    }

    @Override
    public Collection<V> values() {
        return hashMap.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return hashMap.entrySet();
    }

    @Override
    public String toString() {
        return hashMap.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        return hashMap.equals(object);
    }

    @Override
    public int hashCode() {
        return hashMap.hashCode();
    }
}
