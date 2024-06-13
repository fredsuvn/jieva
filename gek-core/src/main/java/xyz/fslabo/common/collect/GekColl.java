package xyz.fslabo.common.collect;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.Gek;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Collection utilities.
 *
 * @author fresduvn
 */
public class GekColl {

    /**
     * Returns given elements as immutable list, any change for the elements will reflect to the list.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return given elements as immutable list
     */
    @SafeVarargs
    public static <T> List<T> asList(T... elements) {
        return GekArray.isEmpty(elements) ? Collections.emptyList() : new ImmutableList<>(elements);
    }

    /**
     * Returns given elements as immutable set, any change for the elements will reflect to the set.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return given elements as immutable set
     */
    @SafeVarargs
    public static <T> Set<T> asSet(T... elements) {
        return GekArray.isEmpty(elements) ? Collections.emptySet() : new ImmutableSet<>(elements, true);
    }

    /**
     * Returns an immutable list contains given elements.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return an immutable list contains given elements
     */
    @SafeVarargs
    public static <T> List<T> listOf(T... elements) {
        return GekArray.isEmpty(elements) ? Collections.emptyList() : new ImmutableList<>(elements.clone());
    }

    /**
     * Returns an immutable set contains given elements.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return an immutable set contains given elements
     */
    @SafeVarargs
    public static <T> Set<T> setOf(T... elements) {
        return GekArray.isEmpty(elements) ? Collections.emptySet() : new ImmutableSet<>(elements.clone(), true);
    }

    /**
     * Returns an immutable map contains given elements.
     * The first element is key-1, second is value-1, third is key-2, fourth is value-2 and so on.
     * If last key-{@code n} is not followed by a value-{@code n}, it will be ignored.
     *
     * @param elements given elements
     * @param <K>      type of keys
     * @param <V>      type of values
     * @param <T>      type of element
     * @return an immutable map contains given elements
     */
    @SafeVarargs
    public static <K, V, T> Map<K, V> mapOf(T... elements) {
        return GekArray.isEmpty(elements) ? Collections.emptyMap() : new ImmutableMap<>(elements);
    }

    /**
     * Returns an immutable map of given entries.
     *
     * @param entries given elements
     * @param <K>     type of keys
     * @param <V>     type of values
     * @return an immutable map of given entries
     */
    @SafeVarargs
    public static <K, V> Map<K, V> mapOfEntries(Map.Entry<? extends K, ? extends V>... entries) {
        if (GekArray.isEmpty(entries)) {
            return Collections.emptyMap();
        }
        Object[] array = new Object[entries.length * 2];
        for (int i = 0; i < entries.length; i++) {
            array[i * 2] = entries[i].getKey();
            array[i * 2 + 1] = entries[i].getValue();
        }
        return new ImmutableMap<>(array);
    }

    /**
     * Collects given elements to array.
     *
     * @param elements given elements
     * @return array
     */
    public static Object[] toArray(@Nullable Iterable<?> elements) {
        if (elements == null) {
            return new Object[0];
        }
        if (elements instanceof Collection) {
            return ((Collection<?>) elements).toArray();
        }
        return StreamSupport.stream(elements.spliterator(), false).toArray();
    }

    /**
     * Collects given elements to array.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return array
     */
    public static <T> T[] toArray(@Nullable Iterable<? extends T> elements, Class<T> type) {
        if (elements == null) {
            return GekArray.newArray(type, 0);
        }
        return StreamSupport.stream(elements.spliterator(), false)
            .toArray(size -> GekArray.newArray(type, size));
    }

    /**
     * Collects given elements to array, converts each element from type {@code T} to type {@code R}.
     *
     * @param elements given elements
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @return array
     */
    public static <T, R> R[] toArray(
        @Nullable Iterable<? extends T> elements, Class<R> type, Function<? super T, ? extends R> function) {
        if (elements == null) {
            return GekArray.newArray(type, 0);
        }
        return StreamSupport.stream(elements.spliterator(), false)
            .map(function)
            .toArray(size -> GekArray.newArray(type, size));
    }

    /**
     * Collects given elements to immutable list.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return immutable list
     */
    public static <T> List<T> toList(@Nullable Iterable<? extends T> elements) {
        if (elements == null) {
            return Collections.emptyList();
        }
        return new ImmutableList<>(toArray(elements));
    }

    /**
     * Collects given elements to immutable list, converts each element from type {@code T} to type {@code R}.
     *
     * @param elements source elements
     * @param function conversion function
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @return immutable list
     */
    public static <T, R> List<R> toList(
        @Nullable Iterable<? extends T> elements, Function<? super T, ? extends R> function) {
        if (elements == null) {
            return Collections.emptyList();
        }
        return new ImmutableList<>(
            StreamSupport.stream(elements.spliterator(), false).map(function).toArray()
        );
    }

    /**
     * Collects given elements to immutable string list, converts each element to string.
     *
     * @param iterable given elements
     * @return immutable string list
     */
    public static List<String> toStringList(@Nullable Iterable<?> iterable) {
        return toList(iterable, String::valueOf);
    }

    /**
     * Collects given elements to immutable set.
     *
     * @param elements given elements
     * @param <T>      type of element
     * @return immutable set
     */
    public static <T> Set<T> toSet(@Nullable Iterable<? extends T> elements) {
        if (elements == null) {
            return Collections.emptySet();
        }
        return new ImmutableSet<>(
            StreamSupport.stream(elements.spliterator(), false).distinct().toArray(), false);
    }

    /**
     * Collects given elements to immutable set, converts each element from type {@code T} to type {@code R}.
     *
     * @param elements source elements
     * @param function conversion function
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @return immutable set
     */
    public static <T, R> Set<R> toSet(
        @Nullable Iterable<? extends T> elements, Function<? super T, ? extends R> function) {
        if (elements == null) {
            return Collections.emptySet();
        }
        return new ImmutableSet<>(
            StreamSupport.stream(elements.spliterator(), false).map(function).distinct().toArray(),
            false
        );
    }

    /**
     * Collects given elements to immutable string set, converts each element to string.
     *
     * @param elements given elements
     * @return immutable string set
     */
    public static Set<String> toStringSet(@Nullable Iterable<?> elements) {
        return toSet(elements, String::valueOf);
    }

    /**
     * Collects given elements into immutable map.
     * The first element is key-1, second is value-1, third is key-2, fourth is value-2 and so on.
     * If last key-{@code n} is not followed by a value-{@code n}, it will be ignored.
     *
     * @param elements given elements
     * @param <K>      type of keys
     * @param <V>      type of values
     * @param <T>      type of element
     * @return immutable map
     */
    public static <K, V, T> Map<K, V> toMap(@Nullable Iterable<T> elements) {
        if (elements == null) {
            return Collections.emptyMap();
        }
        return new ImmutableMap<>(toArray(elements));
    }

    /**
     * Converts given elements to immutable map, each element of source map will be converted to a map's entry.
     *
     * @param elements      given elements
     * @param keyFunction   key conversion function
     * @param valueFunction value conversion function
     * @param <T>           type of source element
     * @param <K>           type of keys of target map
     * @param <V>           type of values of target map
     * @return immutable map
     */
    public static <T, K, V> Map<K, V> toMap(
        @Nullable Iterable<? extends T> elements,
        Function<? super T, ? extends K> keyFunction,
        Function<? super T, ? extends V> valueFunction
    ) {
        if (elements == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(
            StreamSupport.stream(elements.spliterator(), false).collect(Collectors.toMap(
                keyFunction,
                valueFunction,
                (v1, v2) -> v2,
                LinkedHashMap::new
            ))
        );
    }

    /**
     * Converts source map to immutable map, each entry of source map will be converted to entry of type
     * {@code K2} and {@code V2}.
     *
     * @param source        source map
     * @param keyFunction   key conversion function
     * @param valueFunction value conversion function
     * @param <K1>          type of source key
     * @param <V1>          type of source value
     * @param <K2>          type of target key
     * @param <V2>          type of target value
     * @return immutable map
     */
    public static <K1, V1, K2, V2> Map<K2, V2> toMap(
        @Nullable Map<K1, V1> source,
        Function<? super K1, ? extends K2> keyFunction,
        Function<? super V1, ? extends V2> valueFunction
    ) {
        if (source == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(
            source.entrySet().stream().collect(Collectors.toMap(
                e -> keyFunction.apply(e.getKey()),
                e -> valueFunction.apply(e.getValue()),
                (v1, v2) -> v2,
                LinkedHashMap::new
            ))
        );
    }

    /**
     * If given iterable is a list, return itself, otherwise collects it into an immutable list and return.
     *
     * @param iterable given iterable
     * @param <T>      type of element
     * @return list
     */
    public static <T> List<T> orList(@Nullable Iterable<T> iterable) {
        if (iterable instanceof List) {
            return (List<T>) iterable;
        }
        return toList(iterable);
    }

    /**
     * If given iterable is a set, return itself, otherwise collects it into an immutable set and return.
     *
     * @param iterable given elements
     * @param <T>      type of element
     * @return set
     */
    public static <T> Set<T> orSet(@Nullable Iterable<T> iterable) {
        if (iterable instanceof Set) {
            return (Set<T>) iterable;
        }
        return toSet(iterable);
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
     * @return dest collection
     */
    @SafeVarargs
    public static <T, C extends Collection<? super T>> C collect(C dest, T... elements) {
        if (GekArray.isEmpty(elements)) {
            return dest;
        }
        dest.addAll(asList(elements));
        return dest;
    }

    /**
     * Collects given elements into dest collection, and returns the dest collection.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param <T>      type of element
     * @param <C>      type of dest collection
     * @return dest collection
     */
    public static <T, C extends Collection<? super T>> C collect(C dest, Iterable<T> elements) {
        return collect(dest, elements, it -> it);
    }

    /**
     * Collects given elements into dest collection, converts each element from type {@code T} to type {@code R},
     * and returns the dest collection.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @param <C>      type of dest collection
     * @return dest collection
     */
    public static <T, R, C extends Collection<? super R>> C collect(
        C dest, Iterable<T> elements, Function<? super T, ? extends R> function) {
        StreamSupport.stream(elements.spliterator(), false).map(function).forEach(dest::add);
        return dest;
    }

    /**
     * Collects given elements into dest map, and returns the dest map.
     * The first element is key-1, second is value-1, third is key-2, fourth is value-2 and so on.
     * If last key-{@code n} is not followed by a value-{@code n}, it will be ignored.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param <K>      type of keys
     * @param <V>      type of values
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
     * Collects given elements into dest map, and returns the dest map.
     * The first element is key-1, second is value-1, third is key-2, fourth is value-2 and so on.
     * If last key-{@code n} is not followed by a value-{@code n}, it will be ignored.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param <K>      type of keys
     * @param <V>      type of values
     * @param <M>      type of dest map
     * @param <T>      type of element
     * @return dest map
     */
    public static <K, V, M extends Map<K, V>, T> M collect(M dest, Iterable<T> elements) {
        return collect(dest, elements, it -> it);
    }

    /**
     * Collects given elements into dest map, converts each element from type {@code T} to type {@code R},
     * and returns the dest map.
     * The first element is key-1, second is value-1, third is key-2, fourth is value-2 and so on.
     * If last key-{@code n} is not followed by a value-{@code n}, it will be ignored.
     *
     * @param dest     dest collection
     * @param elements given elements
     * @param <K>      type of keys
     * @param <V>      type of values
     * @param <M>      type of dest map
     * @param <T>      type of source element
     * @param <R>      type of target element
     * @return dest map
     */
    public static <K, V, M extends Map<K, V>, T, R> M collect(
        M dest, Iterable<? extends T> elements, Function<? super T, ? extends R> function) {
        Iterator<? extends T> iterator = elements.iterator();
        while (iterator.hasNext()) {
            R key = function.apply(iterator.next());
            if (iterator.hasNext()) {
                Object value = function.apply(iterator.next());
                dest.put(Gek.as(key), Gek.as(value));
            } else {
                break;
            }
        }
        return dest;
    }

    /**
     * Collects given elements into dest map, each entry of source map will be converted to entry of type
     * {@code K2} and {@code V2}, and returns the dest map.
     *
     * @param dest          dest map
     * @param source        source map
     * @param keyFunction   key conversion function
     * @param valueFunction value conversion function
     * @param <K1>          type of source key
     * @param <V1>          type of source value
     * @param <K2>          type of target key
     * @param <V2>          type of target value
     * @return converted map
     */
    public static <K1, V1, K2, V2, M extends Map<K2, V2>> M collect(
        M dest,
        Map<K1, V1> source,
        Function<? super K1, ? extends K2> keyFunction,
        Function<? super V1, ? extends V2> valueFunction
    ) {
        source.forEach((k, v) -> {
            dest.put(keyFunction.apply(k), valueFunction.apply(v));
        });
        return dest;
    }

    /**
     * Converts given enumeration to iterable.
     *
     * @param enumeration given enumeration
     * @param <T>         type of element
     * @return iterable
     */
    public static <T> Iterable<T> asIterable(@Nullable Enumeration<? extends T> enumeration) {
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
    public static <T> Enumeration<T> asEnumeration(@Nullable Iterable<? extends T> iterable) {
        if (iterable == null) {
            return Gek.as(EmptyEnumeration.INSTANCE);
        }
        return new Enumeration<T>() {

            private Iterator<? extends T> iterator = null;

            @Override
            public boolean hasMoreElements() {
                return iterator.hasNext();
            }

            @Override
            public T nextElement() {
                return iterator.next();
            }

            private Iterator<? extends T> iterator() {
                if (iterator == null) {
                    iterator = iterable.iterator();
                }
                return iterator;
            }
        };
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
     * @return value or null
     */
    @Nullable
    public static <T> T get(@Nullable Iterable<? extends T> iterable, int index) {
        return get(iterable, index, (T) null);
    }

    /**
     * Returns value from given iterable at specified index,
     * if the value is null or failed to obtain, return default value.
     *
     * @param iterable     given iterable
     * @param index        specified index
     * @param defaultValue default value
     * @param <T>          type of element
     * @return value or default value
     */
    public static <T> T get(@Nullable Iterable<? extends T> iterable, int index, T defaultValue) {
        if (iterable == null || index < 0) {
            return defaultValue;
        }
        if (iterable instanceof List) {
            List<T> list = Gek.as(iterable);
            if (index < list.size()) {
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
                } else {
                    return defaultValue;
                }
            }
            i++;
        }
        return defaultValue;
    }

    /**
     * Returns value from given iterable at specified index,
     * if the value is null or failed to obtain, compute new value by specified function and return.
     *
     * @param iterable given iterable
     * @param index    specified index
     * @param function specified function
     * @param <T>      type of element
     * @return value or computed value
     */
    public static <T> T compute(@Nullable Iterable<? extends T> iterable, int index, IntFunction<? extends T> function) {
        if (iterable == null || index < 0) {
            return function.apply(index);
        }
        if (iterable instanceof List) {
            List<T> list = Gek.as(iterable);
            if (index < list.size()) {
                T result = list.get(index);
                if (result != null) {
                    return result;
                }
            }
            return function.apply(index);
        }
        int i = 0;
        for (T t : iterable) {
            if (index == i) {
                if (t != null) {
                    return t;
                } else {
                    return function.apply(index);
                }
            }
            i++;
        }
        return function.apply(index);
    }

    /**
     * Returns value from given map at specified key, if failed to obtain, return null.
     *
     * @param map given map
     * @param key specified key
     * @param <K> type of keys
     * @param <V> type of values
     * @return value or null
     */
    @Nullable
    public static <K, V> V get(@Nullable Map<K, V> map, K key) {
        return get(map, key, (V) null);
    }

    /**
     * Returns value from given map at specified key,
     * if the value is null or failed to obtain, return default value.
     *
     * @param map          given map
     * @param key          specified key
     * @param defaultValue default value
     * @param <K>          type of keys
     * @param <V>          type of values
     * @return value or default value
     */
    public static <K, V> V get(@Nullable Map<K, V> map, K key, V defaultValue) {
        if (map == null) {
            return defaultValue;
        }
        V v = map.get(key);
        return v == null ? defaultValue : v;
    }

    /**
     * Returns value from given map at specified key,
     * if the value is null or failed to obtain, compute new value by specified function and return.
     *
     * @param map      given map
     * @param key      specified key
     * @param function specified function
     * @param <K>      type of keys
     * @param <V>      type of values
     * @return value or computed value
     */
    public static <K, V> V compute(@Nullable Map<K, V> map, K key, Function<? super K, ? extends V> function) {
        if (map == null) {
            return function.apply(key);
        }
        V v = map.get(key);
        return v == null ? function.apply(key) : v;
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

    private static final class ImmutableList<T> extends AbstractList<T> implements RandomAccess, Serializable {

        private final Object[] array;

        private ImmutableList(Object[] array) {
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

    private static final class ImmutableSet<T> extends AbstractSet<T> implements Serializable {

        private final Object[] array;

        private ImmutableSet(Object[] array, boolean distinct) {
            if (distinct) {
                this.array = Arrays.stream(array).distinct().toArray();
            } else {
                this.array = array;
            }
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

    private static final class ImmutableMap<K, V> extends AbstractMap<K, V> implements Serializable {

        private final Set<Entry<K, V>> entries;

        private ImmutableMap(Object[] array) {
            if (array.length < 2) {
                entries = Collections.emptySet();
                return;
            }
            Map<K, V> map = collect(new LinkedHashMap<>(), array);
            entries = new ImmutableSet<>(
                map.entrySet().stream().map(it -> new SimpleImmutableEntry<>(it.getKey(), it.getValue())).toArray(),
                false
            );
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return entries;
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
