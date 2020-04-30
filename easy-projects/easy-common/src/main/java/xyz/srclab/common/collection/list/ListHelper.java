package xyz.srclab.common.collection.list;

import com.google.common.collect.ImmutableList;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.collection.iterable.IterableHelper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ListHelper {

    @SafeVarargs
    public static <E> @Immutable List<E> concat(Iterable<? extends E>... iterables) {
        return concat(Arrays.asList(iterables));
    }

    public static <E> @Immutable List<E> concat(Iterable<Iterable<? extends E>> iterables) {
        List<E> result = new LinkedList<>();
        for (Iterable<? extends E> iterable : iterables) {
            result.addAll(IterableHelper.asList(iterable));
        }
        return immutable(result);
    }

    public static <E> @Immutable List<E> immutable(Iterable<? extends E> elements) {
        return ImmutableList.copyOf(elements);
    }

    @SafeVarargs
    public static <E> @Immutable List<E> immutable(E... elements) {
        return ImmutableList.copyOf(elements);
    }
}
