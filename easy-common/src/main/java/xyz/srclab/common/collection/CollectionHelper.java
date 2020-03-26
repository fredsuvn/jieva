package xyz.srclab.common.collection;

import org.apache.commons.collections4.IterableUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class CollectionHelper {

    public static <E> Collection<E> castCollection(Iterable<E> iterable) {
        return iterable instanceof Collection ?
                (Collection<E>) iterable : IterableUtils.toList(iterable);
    }

    @SafeVarargs
    public static <E> List<E> concat(Iterable<? extends E>... iterables) {
        return concat(Arrays.asList(iterables));
    }

    public static <E> List<E> concat(Iterable<Iterable<? extends E>> iterables) {
        List<E> returned = new LinkedList<>();
        for (Iterable<? extends E> iterable : iterables) {
            returned.addAll(castCollection(iterable));
        }
        return returned;
    }
}
