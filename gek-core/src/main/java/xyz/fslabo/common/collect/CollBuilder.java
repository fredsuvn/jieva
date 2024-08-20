package xyz.fslabo.common.collect;

import xyz.fslabo.common.base.Jie;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;

/**
 * Builder to build a {@link Collection}, for example:
 * <pre>
 *     CollBuilder.newBuilder().initialSize(100).initialFunction(i-&gt;random()).toList();
 * </pre>
 *
 * @author fredsuvn
 */
public class CollBuilder {

    public static CollBuilder newBuilder() {
        return new CollBuilder();
    }

    private int initialCapacity;
    private int initialSize;
    private Object initialElements;
    private IntFunction<?> initialFunction;
    private boolean immutable;

    private CollBuilder() {
    }

    /**
     * Sets initial capacity.
     *
     * @param initialCapacity initial capacity
     * @return this
     */
    public CollBuilder initialCapacity(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        return this;
    }

    /**
     * Sets initial size. This is typically used for {@link #initialFunction(IntFunction)}.
     *
     * @param initialSize initial size
     * @return this
     */
    public CollBuilder initialSize(int initialSize) {
        this.initialSize = initialSize;
        return this;
    }

    /**
     * Sets initial elements:
     * <ul>
     *     <li>
     *         To build a collection, each element will be added;
     *     </li>
     *     <li>
     *         To build a map, the first element will be put as key1, second as value1,
     *         third as key2, fourth as value2 and so on.
     *         If last key{@code n} is not followed by a value{@code n}, it will be ignored;
     *     </li>
     * </ul>
     *
     * @param initialElements initial elements
     * @return this
     */
    public CollBuilder initialElements(Iterable<?> initialElements) {
        this.initialElements = initialElements;
        return this;
    }

    /**
     * Sets initial elements:
     * <ul>
     *     <li>
     *         To build a collection, each element will be added;
     *     </li>
     *     <li>
     *         To build a map, the first element will be put as key1, second as value1,
     *         third as key2, fourth as value2 and so on.
     *         If last key{@code n} is not followed by a value{@code n}, it will be ignored;
     *     </li>
     * </ul>
     *
     * @param initialElements initial elements
     * @return this
     */
    public CollBuilder initialElements(Object... initialElements) {
        this.initialElements = JieArray.listOf(initialElements);
        return this;
    }

    /**
     * Sets initial function, the function will be passed to the index (0 based) and return an element at the index:
     * <ul>
     *     <li>
     *         To build a collection, each element will be added;
     *     </li>
     *     <li>
     *         To build a map, the first element will be put as key1, second as value1,
     *         third as key2, fourth as value2 and so on.
     *         If last key{@code n} is not followed by a value{@code n}, it will be ignored;
     *     </li>
     * </ul>
     *
     * @param initialFunction initial function
     * @return this
     */
    public CollBuilder initialFunction(IntFunction<?> initialFunction) {
        this.initialFunction = initialFunction;
        return this;
    }

    /**
     * Sets whether built collection is immutable.
     *
     * @param immutable whether built collection is immutable
     * @return this
     */
    public CollBuilder immutable(boolean immutable) {
        this.immutable = immutable;
        return this;
    }

    /**
     * Sets built collection is immutable.
     *
     * @return this
     */
    public CollBuilder immutable() {
        return immutable(true);
    }

    /**
     * Builds and returns {@link ArrayList}.
     *
     * @param <T> type of element
     * @return {@link ArrayList}
     */
    public <T> ArrayList<T> toArrayList() {
        ArrayList<T> result =
            initialCapacity <= 0 ? new ArrayList<>() : new ArrayList<>(initialCapacity);
        fillCollection(result);
        return result;
    }

    /**
     * Builds and returns {@link LinkedList}.
     *
     * @param <T> type of element
     * @return {@link ArrayList}
     */
    public <T> LinkedList<T> toLinkedList() {
        LinkedList<T> result = new LinkedList<>();
        fillCollection(result);
        return result;
    }

    /**
     * Builds and returns {@link HashSet}.
     *
     * @param <T> type of element
     * @return {@link HashSet}
     */
    public <T> HashSet<T> toHashSet() {
        HashSet<T> result =
            initialCapacity <= 0 ? new HashSet<>() : new HashSet<>(initialCapacity);
        fillCollection(result);
        return result;
    }

    /**
     * Builds and returns {@link HashSet}.
     *
     * @param <T> type of element
     * @return {@link HashSet}
     */
    public <T> LinkedHashSet<T> toLinkedHashSet() {
        LinkedHashSet<T> result =
            initialCapacity <= 0 ? new LinkedHashSet<>() : new LinkedHashSet<>(initialCapacity);
        fillCollection(result);
        return result;
    }

    /**
     * Builds and returns {@link TreeSet}.
     *
     * @param <T> type of element
     * @return {@link TreeSet}
     */
    public <T> TreeSet<T> toTreeSet() {
        TreeSet<T> result = new TreeSet<>();
        fillCollection(result);
        return result;
    }

    /**
     * Builds and returns {@link TreeSet}.
     *
     * @param comparator the comparator
     * @param <T>        type of element
     * @return {@link TreeSet}
     */
    public <T> TreeSet<T> toTreeSet(Comparator<? super T> comparator) {
        TreeSet<T> result = new TreeSet<>(comparator);
        fillCollection(result);
        return result;
    }

    /**
     * Builds and returns {@link ConcurrentHashMap.KeySetView}.
     *
     * @param <T> type of keys
     * @return {@link ConcurrentHashMap.KeySetView}
     */
    public <T> ConcurrentHashMap.KeySetView<T, Boolean> toConcurrentSet() {
        ConcurrentHashMap.KeySetView<T, Boolean> result =
            initialCapacity <= 0 ? ConcurrentHashMap.newKeySet() : ConcurrentHashMap.newKeySet(initialCapacity);
        fillCollection(result);
        return result;
    }

    /**
     * Builds and returns {@link HashMap}.
     *
     * @param <K> type of keys
     * @param <V> type of values
     * @return {@link HashMap}
     */
    public <K, V> HashMap<K, V> toHashMap() {
        HashMap<K, V> result =
            initialCapacity <= 0 ? new HashMap<>() : new HashMap<>(initialCapacity);
        fillMap(result);
        return result;
    }

    /**
     * Builds and returns {@link LinkedHashMap}.
     *
     * @param <K> type of keys
     * @param <V> type of values
     * @return {@link LinkedHashMap}
     */
    public <K, V> LinkedHashMap<K, V> toLinkedHashMap() {
        LinkedHashMap<K, V> result =
            initialCapacity <= 0 ? new LinkedHashMap<>() : new LinkedHashMap<>(initialCapacity);
        fillMap(result);
        return result;
    }

    /**
     * Builds and returns {@link ConcurrentHashMap}.
     *
     * @param <K> type of keys
     * @param <V> type of values
     * @return {@link ConcurrentHashMap}
     */
    public <K, V> ConcurrentHashMap<K, V> toConcurrentHashMap() {
        ConcurrentHashMap<K, V> result =
            initialCapacity <= 0 ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(initialCapacity);
        fillMap(result);
        return result;
    }

    private void fillCollection(Collection<?> collection) {
        Collection<Object> dest = Jie.as(collection);
        if (initialElements != null) {
            if (initialElements instanceof Collection) {
                dest.addAll((Collection<?>) initialElements);
                return;
            }
            if (initialElements instanceof Object[]) {
                JieColl.addAll(dest, (Object[]) initialElements);
                return;
            }
            if (initialElements instanceof Iterable) {
                JieColl.addAll(dest, (Iterable<?>) initialElements);
                return;
            }
            throw new IllegalArgumentException("Initial elements must be iterable or array.");
        } else if (initialSize > 0 && initialFunction != null) {
            for (int i = 0; i < initialSize; i++) {
                Object v = initialFunction.apply(i);
                dest.add(v);
            }
        }
    }

    private void fillMap(Map<?, ?> map) {
        Map<Object, Object> dest = Jie.as(map);
        if (initialElements != null) {
            if (initialElements instanceof Object[]) {
                JieColl.addAll(dest, (Object[]) initialElements);
                return;
            }
            if (initialElements instanceof Iterable) {
                JieColl.addAll(dest, (Iterable<?>) initialElements);
                return;
            }
            throw new IllegalArgumentException("Initial elements must be iterable or array.");
        } else if (initialSize > 0 && initialFunction != null) {
            int i = 1;
            while (i < initialSize) {
                Object key = initialFunction.apply(i - 1);
                Object value = initialFunction.apply(i);
                dest.put(key, value);
                i += 2;
            }
        }
    }

    /**
     * Builds and returns list.
     *
     * @param <T> type of element
     * @return list
     */
    public <T> List<T> toList() {
        if (!immutable) {
            return toArrayList();
        }
        if (initialElements != null) {
            if (initialElements instanceof Object[]) {
                return Jie.as(JieColl.toList((Object[]) initialElements));
            }
            if (initialElements instanceof Iterable) {
                return Jie.as(JieColl.toList((Iterable<?>) initialElements));
            }
            throw new IllegalArgumentException("Initial elements must be iterable or array.");
        } else if (initialSize > 0 && initialFunction != null) {
            Object[] array = new Object[initialSize];
            for (int i = 0; i < initialSize; i++) {
                Object v = initialFunction.apply(i);
                array[i] = v;
            }
            return Jie.as(JieArray.listOf(array));
        }
        return Collections.emptyList();
    }

    /**
     * Builds and returns set.
     *
     * @param <T> type of element
     * @return set
     */
    public <T> Set<T> toSet() {
        return immutable ? Collections.unmodifiableSet(toLinkedHashSet()) : toLinkedHashSet();
    }

    /**
     * Builds and returns map.
     *
     * @param <K> type of keys
     * @param <V> type of values
     * @return map
     */
    public <K, V> Map<K, V> toMap() {
        return immutable ? Collections.unmodifiableMap(toLinkedHashMap()) : toLinkedHashMap();
    }

    /**
     * Resets all build settings.
     *
     * @return this
     */
    public CollBuilder reset() {
        this.initialSize = 0;
        this.initialCapacity = 0;
        this.initialElements = null;
        this.initialFunction = null;
        this.immutable = false;
        return this;
    }
}
