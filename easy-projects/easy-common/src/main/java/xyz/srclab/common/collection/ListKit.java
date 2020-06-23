package xyz.srclab.common.collection;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.base.Cast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
        return ImmutableSupport.ImmutableList.from(elements);
    }

    @Immutable
    public static <E> List<E> immutable(Iterable<? extends E> elements) {
        if (elements instanceof ImmutableSupport.ImmutableList) {
            return Cast.as(elements);
        }
        return ImmutableSupport.ImmutableList.from(elements);
    }
}
