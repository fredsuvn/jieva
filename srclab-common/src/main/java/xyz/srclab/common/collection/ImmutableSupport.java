package xyz.srclab.common.collection;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.base.As;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author sunqian
 */
final class ImmutableSupport {

    @SafeVarargs
    static <E> List<E> list(E... elements) {
        return new ImmutableListByArray<>(elements);
    }

    static <E> List<E> list(Iterable<? extends E> elements) {
        if (elements instanceof ImmutableList) {
            return As.notNull(elements);
        }
        Object[] array = iterableToArray(elements);
        return As.notNull(list(array));
    }

    @SafeVarargs
    static <E> Set<E> set(E... elements) {
        return new ImmutableSet<>(elements);
    }

    static <E> Set<E> set(Iterable<? extends E> elements) {
        if (elements instanceof ImmutableSet) {
            return As.notNull(elements);
        }
        return new ImmutableSet<>(elements);
    }

    static <K, V> Map<K, V> map(Map<? extends K, ? extends V> elements) {
        if (elements instanceof ImmutableMap) {
            return As.notNull(elements);
        }
        return new ImmutableMap<>(elements);
    }

    private static <E> Object[] iterableToArray(Iterable<? extends E> elements) {
        if (elements instanceof Collection) {
            return ((Collection<?>) elements).toArray();
        }
        List<E> result = new LinkedList<>();
        for (E element : elements) {
            result.add(element);
        }
        return result.isEmpty() ? ArrayKit.EMPTY_OBJECT_ARRAY : result.toArray();
    }

    private static abstract class ImmutableList<E> extends AbstractList<E> {
    }

    private static final class ImmutableListByArray<E> extends ImmutableList<E> {

        private final Object[] elementData;

        private ImmutableListByArray(Object[] elementData) {
            this.elementData = elementData;
        }

        @Override
        public int size() {
            return elementData.length;
        }

        @Override
        public Object[] toArray() {
            return elementData.clone();
        }

        @Override
        public <T> T[] toArray(T[] array) {
            int size = size();
            if (array.length < size) {
                return As.notNull(Arrays.copyOf(this.elementData, size, array.getClass()));
            }
            System.arraycopy(this.elementData, 0, array, 0, size);
            if (array.length > size) {
                array[size] = null;
            }
            return array;
        }

        @Nullable
        @Override
        public E get(int index) {
            return As.nullable(elementData[index]);
        }

        @Override
        public int indexOf(Object o) {
            return ArrayUtils.indexOf(elementData, o);
        }

        @Override
        public boolean contains(Object o) {
            return indexOf(o) != -1;
        }

        @Override
        public Spliterator<E> spliterator() {
            return Spliterators.spliterator(elementData, Spliterator.ORDERED);
        }

        @Override
        public void forEach(Consumer<? super E> action) {
            Consumer<Object> actionCast = As.notNull(action);
            for (Object e : elementData) {
                actionCast.accept(e);
            }
        }

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            UnaryOperator<Object> unaryOperator = As.notNull(operator);
            Object[] a = this.elementData;
            for (int i = 0; i < a.length; i++) {
                a[i] = unaryOperator.apply(a[i]);
            }
        }

        @Override
        public void sort(Comparator<? super E> comparator) {
            Comparator<Object> comparatorCast = As.notNull(comparator);
            Arrays.sort(elementData, comparatorCast);
        }
    }

    private static final class ImmutableListByList<E> extends ImmutableList<E> {

        private final List<E> elementData;

        private ImmutableListByList(List<E> elementData) {
            this.elementData = elementData;
        }

        @Override
        public int size() {
            return elementData.size();
        }

        @Override
        public boolean isEmpty() {
            return elementData.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return elementData.contains(o);
        }

        @Override
        public Iterator<E> iterator() {
            return elementData.iterator();
        }

        @Override
        public Object[] toArray() {
            return elementData.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return elementData.toArray(a);
        }

        @Override
        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return elementData.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void sort(Comparator<? super E> c) {
            elementData.sort(c);
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            return elementData.equals(o);
        }

        @Override
        public int hashCode() {
            return elementData.hashCode();
        }

        @Override
        public E get(int index) {
            return elementData.get(index);
        }

        @Override
        public E set(int index, E element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void add(int index, E element) {
            throw new UnsupportedOperationException();
        }

        @Override
        public E remove(int index) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int indexOf(Object o) {
            return elementData.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return elementData.lastIndexOf(o);
        }

        @Override
        public ListIterator<E> listIterator() {
            return elementData.listIterator();
        }

        @Override
        public ListIterator<E> listIterator(int index) {
            return elementData.listIterator(index);
        }

        @Override
        public List<E> subList(int fromIndex, int toIndex) {
            return elementData.subList(fromIndex, toIndex);
        }

        @Override
        public Spliterator<E> spliterator() {
            return elementData.spliterator();
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<E> stream() {
            return elementData.stream();
        }

        @Override
        public Stream<E> parallelStream() {
            return elementData.parallelStream();
        }

        @Override
        public void forEach(Consumer<? super E> action) {
            elementData.forEach(action);
        }
    }

    private static final class ImmutableSet<E> implements Set<E> {

        private final Set<E> source;

        @SafeVarargs
        private ImmutableSet(E... elements) {
            this.source = CollectionKit.addAll(new LinkedHashSet<>(), elements);
        }

        private ImmutableSet(Iterable<? extends E> elements) {
            this.source = CollectionKit.addAll(new LinkedHashSet<>(), elements);
        }

        @Override
        public int size() {
            return source.size();
        }

        @Override
        public boolean isEmpty() {
            return source.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return source.contains(o);
        }

        @Override
        public Iterator<E> iterator() {
            return source.iterator();
        }

        @Override
        public Object[] toArray() {
            return source.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return source.toArray(a);
        }

        @Override
        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return source.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean equals(Object o) {
            return source.equals(o);
        }

        @Override
        public int hashCode() {
            return source.hashCode();
        }

        @Override
        public Spliterator<E> spliterator() {
            return source.spliterator();
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Stream<E> stream() {
            return source.stream();
        }

        @Override
        public Stream<E> parallelStream() {
            return source.parallelStream();
        }

        @Override
        public void forEach(Consumer<? super E> action) {
            source.forEach(action);
        }
    }

    private static final class ImmutableMap<K, V> implements Map<K, V> {

        private final Map<K, V> source;

        private ImmutableMap(Map<? extends K, ? extends V> source) {
            this.source = new LinkedHashMap<>(source);
        }

        @Override
        public int size() {
            return source.size();
        }

        @Override
        public boolean isEmpty() {
            return source.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return source.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return source.containsValue(value);
        }

        @Override
        public V get(Object key) {
            return source.get(key);
        }

        @Override
        public V put(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Set<K> keySet() {
            return source.keySet();
        }

        @Override
        public Collection<V> values() {
            return source.values();
        }

        private @Nullable Set<Entry<K, V>> entrySet;

        @Override
        public Set<Entry<K, V>> entrySet() {
            if (entrySet == null) {
                synchronized (this) {
                    if (entrySet == null) {
                        entrySet = newEntrySet();
                    }
                }
            }
            return entrySet;
        }

        private Set<Entry<K, V>> newEntrySet() {
            return Collections.unmodifiableSet(
                    source.entrySet().stream()
                            .map(e -> new Entry<K, V>() {
                                @Override
                                public K getKey() {
                                    return e.getKey();
                                }

                                @Override
                                public V getValue() {
                                    return e.getValue();
                                }

                                @Override
                                public V setValue(V value) {
                                    throw new UnsupportedOperationException();
                                }
                            })
                            .collect(Collectors.toSet())
            );
        }

        @Override
        public boolean equals(Object o) {
            return source.equals(o);
        }

        @Override
        public int hashCode() {
            return source.hashCode();
        }

        @Override
        public V getOrDefault(Object key, V defaultValue) {
            return source.getOrDefault(key, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super V> action) {
            source.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public V putIfAbsent(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object key, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            throw new UnsupportedOperationException();
        }

        @Nullable
        @Override
        public V replace(K key, V value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            throw new UnsupportedOperationException();
        }
    }
}
