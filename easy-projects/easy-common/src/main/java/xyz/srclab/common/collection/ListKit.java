package xyz.srclab.common.collection;

import com.google.common.collect.ImmutableList;
import xyz.srclab.annotation.Immutable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class ListKit {

    @Immutable
    public static <NE, OE> List<NE> map(OE[] array, Function<? super OE, ? extends NE> mapper) {
        List<NE> result = new ArrayList<>(array.length);
        for (OE o : array) {
            result.add(mapper.apply(o));
        }
        return immutable(result);
    }

    @Immutable
    public static <NE, OE> List<NE> map(Iterable<? extends OE> iterable, Function<? super OE, ? extends NE> mapper) {
        List<NE> result = new LinkedList<>();
        for (OE o : iterable) {
            result.add(mapper.apply(o));
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
    public static <E> List<E> concat(Iterable<Iterable<? extends E>> iterables) {
        List<E> result = new LinkedList<>();
        for (Iterable<? extends E> iterable : iterables) {
            CollectionKit.addAll(result, iterable);
        }
        return immutable(result);
    }

    @Immutable
    public static <E> List<E> immutable(Iterable<? extends E> elements) {
        return ImmutableList.copyOf(elements);
    }

    @SafeVarargs
    @Immutable
    public static <E> List<E> immutable(E... elements) {
        return ImmutableList.copyOf(elements);
    }
}
