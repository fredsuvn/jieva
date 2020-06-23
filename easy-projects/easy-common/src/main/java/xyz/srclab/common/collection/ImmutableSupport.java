package xyz.srclab.common.collection;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.base.Cast;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * @author sunqian
 */
final class ImmutableSupport {

    static final class ImmutableList<E> extends AbstractList<E> {

        @SafeVarargs
        public static <E> List<E> from(E... elements) {
            return from0(elements);
        }

        public static <E> List<E> from(Iterable<? extends E> elements) {
            Object[] array = iterableToArray(elements);
            return from0(array);
        }

        private static <E> List<E> from0(Object[] elements) {
            return new ImmutableList<>(elements);
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

        private final Object[] elementData;

        private ImmutableList(Object[] elementData) {
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
        @SuppressWarnings("unchecked")
        public <T> T[] toArray(T[] a) {
            int size = size();
            if (a.length < size)
                return Arrays.copyOf(this.elementData, size,
                        (Class<? extends T[]>) a.getClass());
            System.arraycopy(this.elementData, 0, a, 0, size);
            if (a.length > size)
                a[size] = null;
            return a;
        }

        @Override
        @Nullable
        public E get(int index) {
            return Cast.nullable(elementData[index]);
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
            Objects.requireNonNull(action);
            for (Object e : elementData) {
                action.accept(Cast.nullable(e));
            }
        }

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            Objects.requireNonNull(operator);
            Object[] a = this.elementData;
            UnaryOperator<Object> unaryOperator = Cast.as(operator);
            for (int i = 0; i < a.length; i++) {
                a[i] = unaryOperator.apply(a[i]);
            }
        }

        @Override
        public void sort(Comparator<? super E> c) {
            Arrays.sort(elementData, Cast.as(c));
        }
    }

    static final class ImmutableSet<E> implements Set<E> {

        private final Set<E> source;

        @SafeVarargs
        ImmutableSet(E... elements) {
            this.source = Collections.unmodifiableSet(CollectionKit.addAll(new LinkedHashSet<>(), elements));
        }

        ImmutableSet(Iterable<? extends E> elements) {
            this.source = Collections.unmodifiableSet(CollectionKit.addAll(new LinkedHashSet<>(), elements));
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
            return source.add(e);
        }

        @Override
        public boolean remove(Object o) {
            return source.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return source.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends E> c) {
            return source.addAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return source.retainAll(c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return source.removeAll(c);
        }

        @Override
        public void clear() {
            source.clear();
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
            return source.removeIf(filter);
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

    static final class ImmutableMap<K, V> implements Map<K, V> {

        private final Map<K, V> source;

        ImmutableMap(Map<? extends K, ? extends V> source) {
            this.source = Collections.unmodifiableMap(new LinkedHashMap<>(source));
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
            return source.put(key, value);
        }

        @Override
        public V remove(Object key) {
            return source.remove(key);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            source.putAll(m);
        }

        @Override
        public void clear() {
            source.clear();
        }

        @Override
        public Set<K> keySet() {
            return source.keySet();
        }

        @Override
        public Collection<V> values() {
            return source.values();
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return source.entrySet();
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
            source.replaceAll(function);
        }

        @Override
        public V putIfAbsent(K key, V value) {
            return source.putIfAbsent(key, value);
        }

        @Override
        public boolean remove(Object key, Object value) {
            return source.remove(key, value);
        }

        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            return source.replace(key, oldValue, newValue);
        }

        @Override
        public V replace(K key, V value) {
            return source.replace(key, value);
        }

        @Override
        public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
            return source.computeIfAbsent(key, mappingFunction);
        }

        @Override
        @Nullable
        public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return source.computeIfPresent(key, remappingFunction);
        }

        @Override
        public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            return source.compute(key, remappingFunction);
        }

        @Override
        public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            return source.merge(key, value, remappingFunction);
        }
    }
}
