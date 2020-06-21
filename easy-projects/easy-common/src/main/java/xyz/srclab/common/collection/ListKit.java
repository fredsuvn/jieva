package xyz.srclab.common.collection;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.base.Cast;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListKit {

    @Immutable
    public static <O, N> List<N> map(O[] array, Function<? super O, ? extends N> mapper) {
        List<N> result = new ArrayList<>(array.length);
        for (O o : array) {
            result.add(mapper.apply(o));
        }
        return immutable(result);
    }

    @Immutable
    public static <O, N> List<N> map(Iterable<? extends O> iterable, Function<? super O, ? extends N> mapper) {
        List<N> result = new LinkedList<>();
        for (O o : iterable) {
            result.add(mapper.apply(o));
        }
        return immutable(result);
    }

    @Immutable
    public static <E> List<E> filter(E[] array, Predicate<? super E> predicate) {
        List<E> result = new LinkedList<>();
        for (E e : array) {
            if (predicate.test(e)) {
                result.add(e);
            }
        }
        return immutable(result);
    }

    @Immutable
    public static <E> List<E> filter(Iterable<? extends E> iterable, Predicate<? super E> predicate) {
        List<E> result = new LinkedList<>();
        for (E e : iterable) {
            if (predicate.test(e)) {
                result.add(e);
            }
        }
        return immutable(result);
    }

    @SafeVarargs
    @Immutable
    public static <E> List<E> concat(Iterable<? extends E>... iterables) {
        return concat(Arrays.asList(iterables));
    }

    @Immutable
    public static <E> List<E> concat(Iterable<? extends Iterable<? extends E>> iterables) {
        List<E> result = new LinkedList<>();
        for (Iterable<? extends E> iterable : iterables) {
            CollectionKit.addAll(result, iterable);
        }
        return immutable(result);
    }

    @SafeVarargs
    @Immutable
    public static <E> List<E> immutable(E... elements) {
        return ImmutableList.from(elements);
    }

    @Immutable
    public static <E> List<E> immutable(Iterable<? extends E> elements) {
        return ImmutableList.from(elements);
    }

    private static final class ImmutableList<E> extends AbstractList<E> {

        @SafeVarargs
        public static <E> List<E> from(E... elements) {
            return from0(elements);
        }

        public static <E> List<E> from(Iterable<? extends E> elements) {
            Object[] array = iterableToArray(elements);
            return from0(array);
        }

        private static <E> List<E> from0(Object[] elements) {
            return elements.length == 0 ? Collections.emptyList() : new ImmutableList<>(elements);
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
        @Nullable
        public E get(int index) {
            return Cast.nullable(elementData[index]);
        }

        @Override
        public int size() {
            return elementData.length;
        }
    }
}
