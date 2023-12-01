package xyz.fsgek.common.collect;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.Gek;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Collection utilities.
 *
 * @author fresduvn
 */
public class GekColl {

    /**
     * Returns an immutable list of given elements.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return an immutable list of given elements
     */
    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        return GekArray.isEmpty(elements) ? Collections.emptyList() : new ImmutableList<>(elements);
    }

    /**
     * Returns an immutable set of given elements.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return an immutable set of given elements
     */
    @SafeVarargs
    public static <T> Set<T> setOf(T... elements) {
        return GekArray.isEmpty(elements) ? Collections.emptySet() : new ImmutableSet<>(elements);
    }

    /**
     * Returns an immutable map of given elements.
     * The first element is key-1, second is value-1, third is key-2, fourth is value-2 and so on.
     * If last key-{@code n} is not followed by a value-{@code n}, it will be ignored.
     *
     * @param elements given elements
     * @param <K>      type of key
     * @param <V>      type of value
     * @param <T>      type of element
     * @return an immutable map of given elements
     */
    @SafeVarargs
    public static <K, V, T> Map<K, V> mapOf(T... elements) {
        return GekArray.isEmpty(elements) ? Collections.emptyMap() : new ImmutableMap<>(elements);
    }

    /**
     * Collects given iterable to array.
     *
     * @param iterable given iterable
     * @return array
     */
    public static Object[] toArray(@Nullable Iterable<?> iterable) {
        if (iterable == null) {
            return new Object[0];
        }
        if (iterable instanceof Collection) {
            return ((Collection<?>) iterable).toArray();
        }
        return StreamSupport.stream(iterable.spliterator(), false).toArray();
    }

    /**
     * Collects given iterable to array.
     *
     * @param iterable given iterable
     * @param <T>      type of element
     * @return array
     */
    public static <T> T[] toArray(@Nullable Iterable<? extends T> iterable, Class<T> type) {
        if (iterable == null) {
            return GekArray.newArray(type, 0);
        }
        if (iterable instanceof Collection) {
            return ((Collection<?>) iterable).toArray(
                GekArray.newArray(type, ((Collection<? extends T>) iterable).size()));
        }
        return StreamSupport.stream(iterable.spliterator(), false).toArray(size -> GekArray.newArray(type, size));
    }

    /**
     * Collects given iterable to list.
     *
     * @param iterable given iterable
     * @param <T>      type of element
     * @return list
     */
    public static <T> List<T> toList(@Nullable Iterable<? extends T> iterable) {
        if (iterable == null) {
            return Collections.emptyList();
        }
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    /**
     * Collects given iterable to set.
     *
     * @param iterable given iterable
     * @param <T>      type of element
     * @return set
     */
    public static <T> Set<T> toSet(@Nullable Iterable<? extends T> iterable) {
        if (iterable == null) {
            return Collections.emptySet();
        }
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toSet());
    }

    /**
     * Converts given enumeration to iterable.
     *
     * @param enumeration given enumeration
     * @param <T>         type of element
     * @return iterable
     */
    public static <T> Iterable<T> toIterable(@Nullable Enumeration<? extends T> enumeration) {
        if (enumeration == null) {
            return Collections.emptyList();
        }
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

    /**
     * Converts given iterable to enumeration.
     *
     * @param iterable given enumeration
     * @param <T>      type of element
     * @return enumeration
     */
    public static <T> Enumeration<T> toEnumeration(@Nullable Iterable<? extends T> iterable) {
        if (iterable == null) {
            return Gek.as(EmptyEnumeration.INSTANCE);
        }
        Iterator<? extends T> iterator = iterable.iterator();
        return new Enumeration<T>() {
            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public T nextElement() {
                return iterator.next();
            }
        };
    }

    /**
     * Converts given iterable to string list for each element with conversion method {@link String#valueOf(Object)}.
     *
     * @param iterable given iterable
     * @return converted string list
     */
    public static List<String> toStringList(Iterable<?> iterable) {
        return mapList(iterable, String::valueOf);
    }

    /**
     * Returns a new collection configurer to create a collection.
     *
     * @return a new collection configurer to create a collection
     */
    public static GekCollector collector() {
        return GekCollector.newInstance();
    }

    /**
     * Collects given elements into dest collection, and returns the dest collection.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param <T>      type of element
     * @param <C>      type of dest collection
     * @return the dest collection
     */
    @SafeVarargs
    public static <T, C extends Collection<? super T>> C collect(C dest, T... elements) {
        if (GekArray.isEmpty(elements)) {
            return dest;
        }
        dest.addAll(listOf(elements));
        return dest;
    }

    /**
     * Collects given elements into dest collection, and returns the dest collection.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param <T>      type of element
     * @param <C>      type of dest collection
     * @return the dest collection
     */
    public static <T, C extends Collection<? super T>> C collect(C dest, Iterable<T> elements) {
        if (elements instanceof Collection) {
            dest.addAll((Collection<T>) elements);
        } else {
            for (T t : elements) {
                dest.add(t);
            }
        }
        return dest;
    }

    /**
     * Collects given elements into dest map, and returns the dest map.
     * The first element is key-1, second is value-1, third is key-2, fourth is value-2 and so on.
     * If last key-{@code n} is not followed by a value-{@code n}, it will be ignored.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param <K>      key type
     * @param <V>      value type
     * @param <M>      type of dest map
     * @param <T>      type of element
     * @return dest map
     */
    @SafeVarargs
    public static <K, V, M extends Map<K, V>, T> M collect(M dest, T... elements) {
        for (int i = 0; i < elements.length; i += 2) {
            if (i + 1 >= elements.length) {
                break;
            }
            Object key = elements[i];
            Object value = elements[i + 1];
            dest.put(Gek.as(key), Gek.as(value));
        }
        return dest;
    }

    /**
     * If given element is list, return itself as list type, else puts all given elements into a new
     * list and returns.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return the list
     */
    public static <T> List<T> asOrToList(Iterable<T> elements) {
        if (elements instanceof List) {
            return (List<T>) elements;
        }
        if (elements instanceof Collection) {
            return new ArrayList<>((Collection<T>) elements);
        }
        return collect(new LinkedList<>(), elements);
    }

    /**
     * If given element is set, return itself as set type, else puts all given elements into a new
     * set and returns.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return the set
     */
    public static <T> Set<T> asOrToSet(Iterable<T> elements) {
        if (elements instanceof Set) {
            return (Set<T>) elements;
        }
        if (elements instanceof Collection) {
            return new LinkedHashSet<>((Collection<T>) elements);
        }
        return collect(new LinkedHashSet<>(), elements);
    }

    /**
     * If given element is collection, return itself as collection type, else puts all given elements into a new
     * collection and returns.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return the collection
     */
    public static <T> Collection<T> asOrToCollection(Iterable<T> elements) {
        if (elements instanceof Collection) {
            return (Collection<T>) elements;
        }
        return asOrToSet(elements);
    }

    /**
     * Returns immutable list of which elements were put from given iterable.
     *
     * @param iterable given iterable
     * @param <T>      type of element
     * @return immutable list
     */
    public static <T> @Immutable List<T> immutableList(@Nullable Iterable<? extends T> iterable) {
        if (iterable == null) {
            return Collections.emptyList();
        }
        List<T> list;
        if (iterable instanceof Collection) {
            list = collect(new ArrayList<>(((Collection<? extends T>) iterable).size()), iterable);
        } else {
            list = collect(new ArrayList<>(), iterable);
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Returns immutable list of which elements were put from given array.
     *
     * @param array given array
     * @param <T>   type of element
     * @return immutable list
     */
    public static <T> @Immutable List<T> immutableList(@Nullable T[] array) {
        if (GekArray.isEmpty(array)) {
            return Collections.emptyList();
        }
        List<T> list = collect(new ArrayList<>(array.length), array);
        return Collections.unmodifiableList(list);
    }

    /**
     * Returns immutable set of which elements were put from given iterable.
     *
     * @param iterable given iterable
     * @param <T>      type of element
     * @return immutable set
     */
    public static <T> @Immutable Set<T> immutableSet(@Nullable Iterable<? extends T> iterable) {
        if (iterable == null) {
            return Collections.emptySet();
        }
        LinkedHashSet<T> set;
        if (iterable instanceof Collection) {
            set = collect(new LinkedHashSet<>(((Collection<? extends T>) iterable).size()), iterable);
        } else {
            set = collect(new LinkedHashSet<>(), iterable);
        }
        return Collections.unmodifiableSet(set);
    }

    /**
     * Returns immutable set of which elements were put from given array.
     *
     * @param array given array
     * @param <T>   type of element
     * @return immutable set
     */
    public static <T> @Immutable Set<T> immutableSet(@Nullable T[] array) {
        if (GekArray.isEmpty(array)) {
            return Collections.emptySet();
        }
        LinkedHashSet<T> set = collect(new LinkedHashSet<>(array.length), array);
        return Collections.unmodifiableSet(set);
    }

    /**
     * Returns immutable map of which elements were put from given map.
     *
     * @param map given map
     * @param <K> key type
     * @param <V> value type
     * @return immutable map
     */
    public static <K, V> @Immutable Map<K, V> immutableMap(@Nullable Map<? extends K, ? extends V> map) {
        if (isEmpty(map)) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new LinkedHashMap<>(map));
    }

    /**
     * Returns whether given iterable is null or empty.
     *
     * @param iterable given iterable
     * @return whether given iterable is null or empty
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
     * Returns whether given iterable is not null and empty.
     *
     * @param iterable given iterable
     * @return whether given iterable is not null and empty
     */
    public static boolean isNotEmpty(@Nullable Iterable<?> iterable) {
        return !isEmpty(iterable);
    }

    /**
     * Returns whether given map is null or empty.
     *
     * @param map given map
     * @return whether given map is null or empty
     */
    public static boolean isEmpty(@Nullable Map<?, ?> map) {
        if (map == null) {
            return true;
        }
        return map.isEmpty();
    }

    /**
     * Returns whether given map is not null and empty.
     *
     * @param map given map
     * @return whether given map is not null and empty
     */
    public static boolean isNotEmpty(@Nullable Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * Returns value from given iterable at specified index, if failed to obtain, return null.
     *
     * @param iterable given iterable
     * @param index    specified index
     * @param <T>      type of element
     * @return value from given iterable at specified index
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
     * @param <T>          type of element
     * @return value from given iterable at specified index
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
     * @param <K> key type
     * @param <V> value type
     * @return value from given map at specified key
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
     * @param <K>          key type
     * @param <V>          value type
     * @return value from given map at specified key
     */
    public static <K, V> V get(@Nullable Map<K, V> map, K key, @Nullable V defaultValue) {
        if (map == null) {
            return defaultValue;
        }
        V v = map.get(key);
        return v == null ? defaultValue : v;
    }

    /**
     * Converts given iterable of type {@link T} to list of type {@link R}
     * for each element with given conversion function.
     *
     * @param iterable given iterable of type {@link T}
     * @param function given conversion function
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @return converted list
     */
    public static <T, R> List<R> mapList(Iterable<T> iterable, Function<? super T, ? extends R> function) {
        if (iterable instanceof Collection) {
            return ((Collection<T>) iterable).stream().map(function).collect(Collectors.toList());
        }
        List<R> result = new LinkedList<>();
        for (T o : iterable) {
            result.add(function.apply(o));
        }
        return result;
    }

    /**
     * Converts given iterable of type {@link T} to set of type {@link R}
     * for each element with given conversion function.
     *
     * @param iterable given iterable of type {@link T}
     * @param function given conversion function
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @return converted set
     */
    public static <T, R> Set<R> mapSet(Iterable<T> iterable, Function<? super T, ? extends R> function) {
        if (iterable instanceof Collection) {
            return ((Collection<T>) iterable).stream().map(function).collect(Collectors.toSet());
        }
        Set<R> result = new LinkedHashSet<>();
        for (T o : iterable) {
            result.add(function.apply(o));
        }
        return result;
    }

    /**
     * Converts source iterable to map, each element of source map will be converted to a map entry
     * with given conversion function and finally collected into a map.
     *
     * @param source        source iterable
     * @param keyFunction   key conversion function
     * @param valueFunction value conversion function
     * @param <T>           type of source element
     * @param <K>           key type of target map
     * @param <V>           value type of target map
     * @return converted map
     */
    public static <T, K, V> Map<K, V> mapMap(
        Iterable<T> source,
        Function<? super T, ? extends K> keyFunction,
        Function<? super T, ? extends V> valueFunction
    ) {
        if (source instanceof Collection) {
            return ((Collection<T>) source).stream().collect(Collectors.toMap(
                keyFunction,
                valueFunction,
                (v1, v2) -> v2
            ));
        }
        Map<K, V> result = new LinkedHashMap<>();
        for (T o : source) {
            result.put(keyFunction.apply(o), valueFunction.apply(o));
        }
        return result;
    }

    /**
     * Converts source map into dest map, each element of source map will be converted into dest map
     * with given conversion function.
     *
     * @param source        source map
     * @param dest          dest map
     * @param keyFunction   key conversion function
     * @param valueFunction value conversion function
     * @param <KS>          key type of source map
     * @param <VS>          value type of source map
     * @param <KR>          key type of target map
     * @param <VR>          value type of target map
     * @return converted map
     */
    public static <KS, VS, KR, VR> Map<KR, VR> mapMap(
        Map<KS, VS> source,
        Map<KR, VR> dest,
        Function<? super KS, ? extends KR> keyFunction,
        Function<? super VS, ? extends VR> valueFunction
    ) {
        source.forEach((k, v) -> {
            dest.put(keyFunction.apply(k), valueFunction.apply(v));
        });
        return dest;
    }

    /**
     * Reads all properties into a new {@link LinkedHashMap}.
     *
     * @param properties given properties
     * @return a new {@link LinkedHashMap}
     */
    public static LinkedHashMap<String, String> toMap(Properties properties) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        properties.forEach((k, v) -> {
            map.put(String.valueOf(k), String.valueOf(v));
        });
        return map;
    }

    /**
     * Nested get value from given map with given key.
     * This method gets value of given key, then let the value as next key to find next value and keep looping.
     * If last value as key cannot find next value, the last value will be returned.
     * If given key cannot find at least one value, or a same value in the given stack
     * (which will cause an infinite loop), return null.
     *
     * @param map   given map
     * @param key   given key
     * @param stack stack to store the historical values
     * @param <T>   type of element
     * @return nested value from given map with given key
     */
    @Nullable
    public static <T> T getNested(Map<?, T> map, T key, Set<T> stack) {
        T cur = key;
        stack.add(cur);
        while (true) {
            T result = map.get(cur);
            if (result == null) {
                break;
            }
            if (stack.contains(result)) {
                break;
            }
            cur = result;
            stack.add(cur);
        }
        if (Objects.equals(key, cur)) {
            return null;
        }
        return cur;
    }

    static final class ImmutableList<T> extends AbstractList<T> implements RandomAccess, Serializable {

        private final Object[] array;

        ImmutableList(Object[] array) {
            this.array = array;
        }

        @Override
        public T get(int index) {
            return Gek.as(array[index]);
        }

        @Override
        public int size() {
            return array.length;
        }
    }

    static final class ImmutableSet<T> extends AbstractSet<T> implements Serializable {

        private final Object[] array;

        ImmutableSet(Object[] array) {
            this.array = array;
        }

        @Override
        public Iterator<T> iterator() {
            return new ImmutableIterator();
        }

        @Override
        public int size() {
            return array.length;
        }

        private final class ImmutableIterator implements Iterator<T> {

            private int i = 0;

            @Override
            public boolean hasNext() {
                return i < array.length;
            }

            @Override
            public T next() {
                return Gek.as(array[i++]);
            }
        }
    }

    static final class ImmutableMap<K, V> extends AbstractMap<K, V> implements Serializable {

        private final Set<Entry<K, V>> entries;

        ImmutableMap(Object[] array) {
            Entry<K, V>[] entryArray = new Entry[array.length / 2];
            for (int i = 0, j = 0; i < array.length; i += 2, j++) {
                if (i + 1 >= array.length) {
                    break;
                }
                K key = Gek.as(array[i]);
                V value = Gek.as(array[i + 1]);
                entryArray[j] = new ImmutableEntry(key, value);
            }
            entries = new ImmutableSet<>(entryArray);
        }

        ImmutableMap(GekCollector.Pair<K, V>[] array) {
            Entry<K, V>[] entryArray = new Entry[array.length];
            for (int i = 0; i < array.length; i++) {
                K key = array[i].key();
                V value = array[i].value();
                entryArray[i] = new ImmutableEntry(key, value);
            }
            entries = new ImmutableSet<>(entryArray);
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return entries;
        }

        private final class ImmutableEntry implements Entry<K, V> {

            private final K key;
            private final V value;

            private ImmutableEntry(K key, V value) {
                this.key = key;
                this.value = value;
            }

            @Override
            public K getKey() {
                return key;
            }

            @Override
            public V getValue() {
                return value;
            }

            @Override
            public V setValue(V value) {
                throw new UnsupportedOperationException();
            }
        }
    }

    private static final class EmptyEnumeration implements Enumeration<Object> {

        private static final EmptyEnumeration INSTANCE = new EmptyEnumeration();

        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public Object nextElement() {
            throw new NoSuchElementException();
        }
    }
}
