package xyz.srclab.common.collection.list;

import com.google.common.collect.ImmutableList;
import xyz.srclab.annotations.Immutable;
import xyz.srclab.common.collection.iterable.IterableHelper;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
}
