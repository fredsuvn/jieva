package xyz.srclab.common.collect;

import xyz.srclab.build.annotations.FsMethods;
import xyz.srclab.common.base.FsArray;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Collection utilities.
 *
 * @author sunq62
 */
@FsMethods
public class FsCollect {

    /**
     * Puts all given elements into dest collection, and returns the dest collection.
     *
     * @param dest     dest collection
     * @param elements given elements
     */
    @SafeVarargs
    public static <T, C extends Collection<? super T>> C toCollection(C dest, T... elements) {
        dest.addAll(Arrays.asList(elements));
        return dest;
    }

    /**
     * Puts all given elements into dest collection, and returns the dest collection.
     *
     * @param dest     dest collection
     * @param elements given elements
     */
    public static <T, C extends Collection<? super T>> C toCollection(C dest, Iterable<T> elements) {
        for (T t : elements) {
            dest.add(t);
        }
        return dest;
    }

    /**
     * Puts all given key-values into dest map, and returns the dest map.
     * <p>
     * The key-values is an array of which elements are in order of key followed by value,
     * means the first element (index 0) is key, second (index 1) is value, third is key, fourth is value and so on.
     * If the array miss the last value, the last value will be considered as null.
     *
     * @param dest      dest collection
     * @param keyValues given key-values
     */
    public static <K, V, M extends Map<? super K, ? super V>> M toMap(M dest, Object... keyValues) {
        int count = 0;
        K key = null;
        V value;
        for (Object keyValue : keyValues) {
            if (count == 0) {
                key = (K) keyValue;
                count++;
            } else {
                value = (V) keyValue;
                dest.put(key, value);
                count = 0;
            }
        }
        if (count > 0) {
            dest.put(key, null);
        }
        return dest;
    }

    /**
     * Returns an array contains all elements from given iterable in its order.
     *
     * @param iterable given iterable
     * @param type     array's component type
     */
    public static <T> T[] toArray(Iterable<? extends T> iterable, Class<T> type) {
        if (iterable instanceof Collection) {
            T[] array = FsArray.newArray(type, ((Collection<? extends T>) iterable).size());
            int i = 0;
            for (T t : iterable) {
                array[i++] = t;
            }
            return array;
        } else {
            LinkedList<T> list = toCollection(new LinkedList<>(), iterable);
            return toArray(list, type);
        }
    }

    /**
     * Creates an {@link ArrayList} and put all given elements into it.
     *
     * @param elements given elements
     */
    @SafeVarargs
    public static <T> ArrayList<T> arrayList(T... elements) {
        return toCollection(new ArrayList<>(elements.length), elements);
    }

    /**
     * Creates an {@link LinkedList} and put all given elements into it.
     *
     * @param elements given elements
     */
    @SafeVarargs
    public static <T> LinkedList<T> linkedList(T... elements) {
        return new LinkedList<>(Arrays.asList(elements));
    }

    /**
     * Creates an {@link HashMap} and put all given key-values into it.
     * <p>
     * The key-values is an array of which elements are in order of key followed by value,
     * means the first element (index 0) is key, second (index 1) is value, third is key, fourth is value and so on.
     * If the array miss the last value, the last value will be considered as null.
     *
     * @param keyValues given key-values
     */
    public static <K, V> HashMap<K, V> hashMap(Object... keyValues) {
        return toMap(new HashMap<>(keyValues.length / 2 + 1), keyValues);
    }

    /**
     * Creates an {@link LinkedHashMap} and put all given key-values into it.
     * <p>
     * The key-values is an array of which elements are in order of key followed by value,
     * means the first element (index 0) is key, second (index 1) is value, third is key, fourth is value and so on.
     * If the array miss the last value, the last value will be considered as null.
     *
     * @param keyValues given key-values
     */
    public static <K, V> LinkedHashMap<K, V> linkedHashMap(Object... keyValues) {
        return toMap(new LinkedHashMap<>(keyValues.length / 2 + 1), keyValues);
    }

    /**
     * Creates an {@link ConcurrentHashMap} and put all given key-values into it.
     * <p>
     * The key-values is an array of which elements are in order of key followed by value,
     * means the first element (index 0) is key, second (index 1) is value, third is key, fourth is value and so on.
     * If the array miss the last value, the last value will be considered as null.
     *
     * @param keyValues given key-values
     */
    public static <K, V> ConcurrentHashMap<K, V> concurrentHashMap(Object... keyValues) {
        return toMap(new ConcurrentHashMap<>(keyValues.length / 2 + 1), keyValues);
    }
}
