package xyz.srclab.common.collection.map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.srclab.annotation.concurrent.ThreadSafe;
import xyz.srclab.common.lang.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Fast fixed-keys map, this map's keys are immutable, but values are mutable. That's means, you cannot put new key but
 * can change value of old key.
 * <p>
 * This map is thread-safe for reading (not for writing), fast, applicable to initialing scenes.
 *
 * @param <K>
 * @param <V>
 */
@ThreadSafe
public class FastFixedKeysMap<K, V> implements Map<K, V> {

    private static <K, V> Map<K, V> keysToMap(Iterable<K> keys) {
        Map<K, V> returned = new HashMap<>();
        for (K key : keys) {
            returned.put(key, null);
        }
        return returned;
    }

    private static final int BINARY_SEARCH_MIN_LENGTH = 8;

    private final Object[] nodes;
    private final Node<K, V>[] nodeCollection;
    private final int size;

    public FastFixedKeysMap(Map<? extends K, ? extends V> data) {
        Pair<Object[], Node<K, V>[]> pair = computeNodes(data);
        this.nodes = pair.get0();
        this.nodeCollection = pair.get1();
        this.size = data.size();
    }

    public FastFixedKeysMap(Iterable<? extends K> keys) {
        this(keysToMap(keys));
    }

    private Pair<Object[], Node<K, V>[]> computeNodes(Map<? extends K, ? extends V> data) {
        Object[] array = new Object[data.size() * 2];
        Node<K, V>[] nodeCollection = new Node[data.size()];
        int[] count = {0};
        data.forEach((k, v) -> {
            int hash = k.hashCode();
            int index = Math.abs(hash) % array.length;
            Node<K, V> newNode = new Node<>(hash, k, v);
            nodeCollection[count[0]] = newNode;
            count[0]++;
            Object target = array[index];
            if (target == null) {
                array[index] = newNode;
                return;
            }
            array[index] = extendNodeArray(array[index], newNode);
        });
        return Pair.of(array, nodeCollection);
    }

    private Node<K, V>[] extendNodeArray(Object old, Node<K, V> newNode) {
        if (old instanceof Node) {
            Node<K, V>[] newArray = new Node[2];
            newArray[0] = (Node<K, V>) old;
            newArray[1] = newNode;
            sortArray(newArray);
            return newArray;
        }
        Node<K, V>[] oldArray = (Node<K, V>[]) old;
        Node<K, V>[] newArray = Arrays.copyOf(oldArray, oldArray.length + 1);
        sortArray(newArray);
        return newArray;
    }

    private void sortArray(Node<K, V>[] array) {
        Arrays.sort(array, Comparator.comparingInt(Node::getHash));
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return findNode(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        for (int i = 0; i < nodeCollection.length; i++) {
            if (Objects.equals(value, nodeCollection[i].getValue())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(Object key) {
        Node<K, V> node = findNode(key);
        return node == null ? null : node.value;
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        Node<K, V> node = findNode(key);
        if (node == null) {
            throw new UnsupportedOperationException("Current FastFixedKeysMap doesn't contains key: " + key);
        }
        V old = node.value;
        node.setValue(value);
        return old;
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("FastFixedKeysMap doesn't support remove operation");
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("FastFixedKeysMap doesn't support clear operation");
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return Arrays.stream(nodeCollection).map(Node::getKey).collect(Collectors.toSet());
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return Arrays.stream(nodeCollection).map(Node::getValue).collect(Collectors.toSet());
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return Arrays.stream(nodeCollection).collect(Collectors.toSet());
    }

    @Nullable
    private Node<K, V> findNode(Object key) {
        int hash = key.hashCode();
        int index = Math.abs(hash) % nodes.length;
        Object node = nodes[index];
        if (node == null) {
            return null;
        }
        if (node instanceof Node) {
            Node<K, V> n = (Node<K, V>) node;
            return Objects.equals(key, n.key) ? n : null;
        }
        Node<K, V>[] subNodes = (Node<K, V>[]) node;
        if (subNodes.length < BINARY_SEARCH_MIN_LENGTH) {
            for (int i = 0; i < subNodes.length; i++) {
                if (Objects.equals(key, subNodes[i].key)) {
                    return subNodes[i];
                }
            }
            return null;
        }
        return binarySearch(subNodes, hash, key);
    }

    @Nullable
    private Node<K, V> binarySearch(Node<K, V>[] nodes, int hash, Object key) {
        int low = 0;
        int high = nodes.length - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            Node<K, V> midVal = nodes[mid];

            if (midVal.hash < hash) {
                low = mid + 1;
            } else if (midVal.hash > hash) {
                high = mid - 1;
            } else if (Objects.equals(key, midVal.key)) {
                return midVal; // key found
            }
        }
        return null;  // key not found.
    }

    private final class Node<K, V> implements Entry<K, V> {

        private final int hash;
        private final K key;
        private V value;

        private Node(int hash, K key, V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }

        public int getHash() {
            return hash;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }
}
