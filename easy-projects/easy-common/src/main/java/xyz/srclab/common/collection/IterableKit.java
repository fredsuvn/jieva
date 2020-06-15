package xyz.srclab.common.collection;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.base.Cast;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class IterableKit {

    public static <E> Iterable<E> fromIterator(Iterator<? extends E> iterator) {
        Iterator<E> castIterator = Cast.as(iterator);
        return () -> castIterator;
    }

    public static <N, O> Iterable<N> map(O[] array, Function<? super O, ? extends N> mapper) {
        return fromIterator(Arrays.stream(array).map(mapper).iterator());
    }

    public static <N, O> Iterable<N> map(Iterable<? extends O> iterable, Function<? super O, ? extends N> mapper) {
        return fromIterator(toStream(iterable).map(mapper).iterator());
    }

    public static <E> Iterable<E> filter(E[] array, Predicate<? super E> predicate) {
        return fromIterator(Arrays.stream(array)
                .filter(predicate)
                .iterator()
        );
    }

    public static <E> Iterable<E> filter(Iterable<? extends E> iterable, Predicate<? super E> predicate) {
        return fromIterator(toStream(iterable)
                .filter(predicate)
                .iterator()
        );
    }

    @SafeVarargs
    public static <E> Iterable<E> concat(Iterable<? extends E>... iterables) {
        return fromIterator(Arrays.stream(iterables)
                .flatMap(iterable -> StreamSupport.stream(iterable.spliterator(), false))
                .iterator()
        );
    }

    public static <E> Iterable<E> concat(Iterable<? extends Iterable<? extends E>> iterables) {
        return fromIterator(toStream(iterables)
                .flatMap(iterable -> StreamSupport.stream(iterable.spliterator(), false))
                .iterator()
        );
    }

    @Immutable
    public static <E> List<E> toList(Iterable<? extends E> iterable) {
        return ListKit.immutable(iterable);
    }

    @Immutable
    public static <E> Set<E> toSet(Iterable<? extends E> iterable) {
        return SetKit.immutable(iterable);
    }

    public static <E> Stream<E> toStream(Iterable<E> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <E> E[] toArray(Iterable<? extends E> iterable, Class<?> componentType) {
        List<E> list = toList(iterable);
        E[] array = ArrayKit.newArray(componentType, list.size());
        return list.toArray(array);
    }

    public static <E> E firstElement(Iterable<? extends E> iterable) {
        return iterable.iterator().next();
    }
}
