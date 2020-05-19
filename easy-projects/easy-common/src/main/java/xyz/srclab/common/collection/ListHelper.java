package xyz.srclab.common.collection;

import com.google.common.collect.ImmutableList;
import xyz.srclab.annotation.Immutable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public class ListHelper {

    @SafeVarargs
    @Immutable
    public static <E> List<E> concat(Iterable<? extends E>... iterables) {
        return concat(Arrays.asList(iterables));
    }

    @Immutable
    public static <E> List<E> concat(Iterable<Iterable<? extends E>> iterables) {
        List<E> result = new LinkedList<>();
        for (Iterable<? extends E> iterable : iterables) {
            result.addAll(IterableHelper.asList(iterable));
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

    @Immutable
    public static <NE, OE> List<NE> map(OE[] array, Function<OE, NE> mapper) {
        List<NE> result = new ArrayList<>(array.length);
        for (OE o : array) {
            result.add(mapper.apply(o));
        }
        return immutable(result);
    }

    @Immutable
    public static <NE, OE> List<NE> map(Iterable<? extends OE> iterable, Function<OE, NE> mapper) {
        List<NE> result = new LinkedList<>();
        for (OE o : iterable) {
            result.add(mapper.apply(o));
        }
        return immutable(result);
    }
}
