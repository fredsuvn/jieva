package xyz.srclab.common.collection;

import xyz.srclab.annotation.Immutable;

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
        return unmodifiable(result);
    }

    @Immutable
    public static <O, N> List<N> map(Iterable<? extends O> iterable, Function<? super O, ? extends N> mapper) {
        List<N> result = new LinkedList<>();
        for (O o : iterable) {
            result.add(mapper.apply(o));
        }
        return unmodifiable(result);
    }

    @Immutable
    public static <E> List<E> filter(E[] array, Predicate<? super E> predicate) {
        List<E> result = new LinkedList<>();
        for (E e : array) {
            if (predicate.test(e)) {
                result.add(e);
            }
        }
        return unmodifiable(result);
    }

    @Immutable
    public static <E> List<E> filter(Iterable<? extends E> iterable, Predicate<? super E> predicate) {
        List<E> result = new LinkedList<>();
        for (E e : iterable) {
            if (predicate.test(e)) {
                result.add(e);
            }
        }
        return unmodifiable(result);
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
        return unmodifiable(result);
    }

    @SafeVarargs
    @Immutable
    public static <E> List<E> immutable(E... elements) {
        return ImmutableSupport.list(elements);
    }

    @Immutable
    public static <E> List<E> immutable(Iterable<? extends E> elements) {
        return ImmutableSupport.list(elements);
    }

    public static <E> List<E> unmodifiable(Iterable<? extends E> elements) {
        return Collections.unmodifiableList(IterableKit.asList(elements));
    }

    @Immutable
    public static <E> List<E> empty() {
        return Collections.emptyList();
    }
}
