package xyz.fsgek.common.collect;

import xyz.fsgek.common.base.Gek;
import xyz.fsgek.common.base.GekConfigurer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntFunction;

/**
 * This class is used to configure and build collection in method chaining:
 * <pre>
 *     collector.initialSize(100).initialFunction(i-&gt;random()).toList();
 * </pre>
 * Its instance is reusable, re-set and re-build are permitted.
 *
 * @author fredsuvn
 */
public abstract class GekCollector implements GekConfigurer<GekCollector> {

    static GekCollector newInstance() {
        return new OfJdk8();
    }

    private int initialCapacity;
    private int initialSize;
    private Object initialElements;
    private IntFunction<?> initialFunction;
    private boolean immutable;

    GekCollector() {
        reset();
    }

    /**
     * Sets initial capacity.
     *
     * @param initialCapacity initial capacity
     * @return this
     */
    public GekCollector initialCapacity(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        return this;
    }

    /**
     * Sets initial size.
     *
     * @param initialSize initial size
     * @return this
     */
    public GekCollector initialSize(int initialSize) {
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
    public GekCollector initialElements(Iterable<?> initialElements) {
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
    public GekCollector initialElements(Object... initialElements) {
        this.initialElements = initialElements;
        return this;
    }

    /**
     * Sets initial function:
     * <ul>
     *     <li>
     *         To build a collection, the function will be passed to the index and return an element at the index;
     *     </li>
     *     <li>
     *         To build a map, the function will be passed to the index and return
     *         an array of {@link Map.Entry} (such as {@link AbstractMap.SimpleImmutableEntry}) at the index;
     *     </li>
     * </ul>
     * If the {@link #initialElements} is set, this configuration will be ignored.
     *
     * @param initialFunction initial function
     * @return this
     */
    public GekCollector initialFunction(IntFunction<?> initialFunction) {
        this.initialFunction = initialFunction;
        return this;
    }

    /**
     * Sets whether built collection is immutable.
     *
     * @param immutable whether built collection is immutable
     * @return this
     */
    public GekCollector immutable(boolean immutable) {
        this.immutable = immutable;
        return this;
    }

    /**
     * Sets built collection is immutable.
     *
     * @return this
     */
    public GekCollector immutable() {
        return immutable(true);
    }

    /**
     * Builds and returns {@link ArrayList}.
     *
     * @param <T> type of element
     * @return {@link ArrayList}
     */
    public <T> ArrayList<T> toArrayList() {
        ArrayList<T> result = initialCapacity <= 0 ? new ArrayList<>() : new ArrayList<>(initialCapacity);
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
        HashSet<T> result = initialCapacity <= 0 ? new HashSet<>() : new HashSet<>(initialCapacity);
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
        LinkedHashSet<T> result = initialCapacity <= 0 ? new LinkedHashSet<>() : new LinkedHashSet<>(initialCapacity);
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
     * @param <T> type of key
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
     * @param <K> type of key
     * @param <V> type of value
     * @return {@link HashMap}
     */
    public <K, V> HashMap<K, V> toHashMap() {
        HashMap<K, V> result = initialSize <= 0 ? new HashMap<>() : new HashMap<>(initialCapacity);
        fillMap(result);
        return result;
    }

    /**
     * Builds and returns {@link LinkedHashMap}.
     *
     * @param <K> type of key
     * @param <V> type of value
     * @return {@link LinkedHashMap}
     */
    public <K, V> LinkedHashMap<K, V> toLinkedHashMap() {
        LinkedHashMap<K, V> result = initialSize <= 0 ? new LinkedHashMap<>() : new LinkedHashMap<>(initialCapacity);
        fillMap(result);
        return result;
    }

    /**
     * Builds and returns {@link ConcurrentHashMap}.
     *
     * @param <K> type of key
     * @param <V> type of value
     * @return {@link ConcurrentHashMap}
     */
    public <K, V> ConcurrentHashMap<K, V> toConcurrentHashMap() {
        ConcurrentHashMap<K, V> result =
            initialSize <= 0 ? new ConcurrentHashMap<>() : new ConcurrentHashMap<>(initialCapacity);
        fillMap(result);
        return result;
    }

    private void fillCollection(Collection<?> collection) {
        Collection<Object> dest = Gek.as(collection);
        if (initialElements != null) {
            if (initialElements instanceof Collection) {
                dest.addAll((Collection<?>) initialElements);
                return;
            }
            if (initialElements instanceof Object[]) {
                GekColl.collect(dest, (Object[]) initialElements);
                return;
            }
            if (initialElements instanceof Iterable) {
                ((Iterable<?>) initialElements).forEach(dest::add);
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
        Map<Object, Object> dest = Gek.as(map);
        if (initialElements != null) {
            if (initialElements instanceof Object[]) {
                GekColl.collect(dest, (Object[]) initialElements);
                return;
            }
            if (initialElements instanceof Iterable) {
                Iterable<?> iterable = (Iterable<?>) initialElements;
                GekColl.collect(dest, iterable);
                return;
            }
            throw new IllegalArgumentException("Initial elements must be iterable or array.");
        } else if (initialSize > 0 && initialFunction != null) {
            for (int i = 0; i < initialSize; i++) {
                Map.Entry<?, ?> p = Gek.as(initialFunction.apply(i));
                dest.put(Gek.as(p.getKey()), Gek.as(p.getValue()));
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
                return Gek.as(GekColl.listOf((Object[]) initialElements));
            }
            if (initialElements instanceof Iterable) {
                return Gek.as(GekColl.toList((Iterable<?>) initialElements));
            }
            throw new IllegalArgumentException("Initial elements must be iterable or array.");
        } else if (initialSize > 0 && initialFunction != null) {
            Object[] array = new Object[initialSize];
            for (int i = 0; i < array.length; i++) {
                array[i] = initialFunction.apply(i);
            }
            return Gek.as(GekColl.listOf(array));
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
        if (!immutable) {
            return toLinkedHashSet();
        }
        if (initialElements != null) {
            if (initialElements instanceof Object[]) {
                return Gek.as(GekColl.setOf((Object[]) initialElements));
            }
            if (initialElements instanceof Iterable) {
                return Gek.as(GekColl.toSet((Iterable<?>) initialElements));
            }
            throw new IllegalArgumentException("Initial elements must be iterable or array.");
        } else if (initialSize > 0 && initialFunction != null) {
            Object[] array = new Object[initialSize];
            for (int i = 0; i < array.length; i++) {
                array[i] = initialFunction.apply(i);
            }
            return Gek.as(GekColl.setOf(array));
        }
        return Collections.emptySet();
    }

    /**
     * Builds and returns map.
     *
     * @param <K> type of key
     * @param <V> type of value
     * @return map
     */
    public <K, V> Map<K, V> toMap() {
        if (!immutable) {
            return toLinkedHashMap();
        }
        if (initialElements != null) {
            if (initialElements instanceof Object[]) {
                return Gek.as(GekColl.mapOf((Object[]) initialElements));
            }
            if (initialElements instanceof Iterable) {
                return Gek.as(GekColl.toMap((Iterable<?>) initialElements));
            }
            throw new IllegalArgumentException("Initial elements must be iterable or array.");
        } else if (initialSize > 0 && initialFunction != null) {
            Map.Entry<?, ?>[] entries = new Map.Entry<?, ?>[initialSize];
            for (int i = 0; i < initialSize; i++) {
                entries[i] = Gek.as(initialFunction.apply(i));
            }
            return Gek.as(GekColl.mapOfEntries(entries));
        }
        return Collections.emptyMap();
    }

    @Override
    public GekCollector reset() {
        this.initialSize = 0;
        this.initialCapacity = 0;
        this.initialElements = null;
        this.initialFunction = null;
        this.immutable = false;
        return this;
    }

    private static final class OfJdk8 extends GekCollector {
    }
}
