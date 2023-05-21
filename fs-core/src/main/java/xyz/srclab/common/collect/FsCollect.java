package xyz.srclab.common.collect;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.build.annotations.FsMethods;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsArray;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Collection utilities.
 *
 * @author fresduvn
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
        boolean isKey = true;
        K key = null;
        for (Object keyValue : keyValues) {
            if (isKey) {
                key = Fs.as(keyValue);
                isKey = false;
            } else {
                V value = Fs.as(keyValue);
                dest.put(key, value);
                isKey = true;
            }
        }
        if (!isKey) {
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

    /**
     * Returns whether given iterable is null or empty.
     *
     * @param iterable given iterable
     */
    public static boolean isEmpty(@Nullable Iterable<?> iterable) {
        if (iterable == null) {
            return true;
        }
        if (iterable instanceof Collection) {
            return ((Collection<?>) iterable).isEmpty();
        }
        return iterable.iterator().hasNext();
    }

    /**
     * Returns whether given map is null or empty.
     *
     * @param map given map
     */
    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        if (map == null) {
            return true;
        }
        return map.isEmpty();
    }

    /**
     * Returns value from given iterable at specified index, if failed to obtain, return null.
     *
     * @param iterable given iterable
     * @param index    specified index
     */
    public static <T> T get(@Nullable Iterable<T> iterable, int index) {
        return get(iterable, index, null);
    }

    /**
     * Returns value from given iterable at specified index,
     * if the value is null or failed to obtain, return default value.
     *
     * @param iterable     given iterable
     * @param index        specified index
     * @param defaultValue default value
     */
    public static <T> T get(@Nullable Iterable<T> iterable, int index, @Nullable T defaultValue) {
        if (iterable == null || index < 0) {
            return defaultValue;
        }
        if (iterable instanceof List) {
            List<T> list = (List<T>) iterable;
            if (list.size() > index) {
                T result = list.get(index);
                if (result != null) {
                    return result;
                }
            }
            return defaultValue;
        }
        int i = 0;
        for (T t : iterable) {
            if (index == i) {
                if (t != null) {
                    return t;
                }
                break;
            }
            i++;
        }
        return defaultValue;
    }

    /**
     * Returns value from given map at specified key, if failed to obtain, return null.
     *
     * @param map given map
     * @param key specified key
     */
    public static <K, V> V get(@Nullable Map<K, V> map, K key) {
        return get(map, key, null);
    }

    /**
     * Returns value from given map at specified key, if the value is null or failed to obtain, return default value.
     *
     * @param map          given map
     * @param key          specified key
     * @param defaultValue default value
     */
    public static <K, V> V get(@Nullable Map<K, V> map, K key, @Nullable V defaultValue) {
        if (map == null) {
            return defaultValue;
        }
        V v = map.get(key);
        return v == null ? defaultValue : v;
    }

    /**
     * Converts given enumeration to iterable.
     *
     * @param enumeration given enumeration
     */
    public static <T> Iterable<T> toIterable(Enumeration<T> enumeration) {
        return () -> new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return enumeration.hasMoreElements();
            }

            @Override
            public T next() {
                return enumeration.nextElement();
            }
        };
    }
}
