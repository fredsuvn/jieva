package xyz.srclab.common.collection;

import com.google.common.collect.ImmutableSet;
import xyz.srclab.annotation.Immutable;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class SetKit {

    @Immutable
    public static <O, N> Set<N> map(O[] array, Function<? super O, ? extends N> mapper) {
        Set<N> result = new LinkedHashSet<>(array.length);
        for (O o : array) {
            result.add(mapper.apply(o));
        }
        return immutable(result);
    }

    @Immutable
    public static <O, N> Set<N> map(Iterable<? extends O> iterable, Function<? super O, ? extends N> mapper) {
        Set<N> result = new LinkedHashSet<>();
        for (O o : iterable) {
            result.add(mapper.apply(o));
        }
        return immutable(result);
    }

    @Immutable
    public static <E> Set<E> filter(E[] array, Predicate<? super E> predicate) {
        Set<E> result = new LinkedHashSet<>();
        for (E e : array) {
            if (predicate.test(e)) {
                result.add(e);
            }
        }
        return immutable(result);
    }

    @Immutable
    public static <E> Set<E> filter(Iterable<? extends E> iterable, Predicate<? super E> predicate) {
        Set<E> result = new LinkedHashSet<>();
        for (E e : iterable) {
            if (predicate.test(e)) {
                result.add(e);
            }
        }
        return immutable(result);
    }

    @SafeVarargs
    @Immutable
    public static <E> Set<E> concat(Iterable<? extends E>... iterables) {
        return concat(Arrays.asList(iterables));
    }

    @Immutable
    public static <E> Set<E> concat(Iterable<? extends Iterable<? extends E>> iterables) {
        Set<E> result = new LinkedHashSet<>();
        for (Iterable<? extends E> iterable : iterables) {
            result.addAll(IterableKit.toList(iterable));
        }
        return immutable(result);
    }

    @Immutable
    public static <E> Set<E> enumerationToSet(Enumeration<? extends E> enumeration) {
        Set<E> result = new LinkedHashSet<>();
        while (enumeration.hasMoreElements()) {
            result.add(enumeration.nextElement());
        }
        return immutable(result);
    }

    @Immutable
    public static <E> Set<E> immutable(Iterable<? extends E> elements) {
        return ImmutableSet.copyOf(elements);
    }

    @SafeVarargs
    @Immutable
    public static <E> Set<E> immutable(E... elements) {
        return ImmutableSet.copyOf(elements);
    }

    public static <E> E firstElement(Iterable<? extends E> iterable) {
        return IterableKit.firstElement(iterable);
    }
}
