package xyz.srclab.common.collection;

import org.apache.commons.collections4.IterableUtils;

import java.util.Collection;

public class CollectionHelper {

    public static <E> Collection<E> castToCollection(Iterable<E> iterable) {
        return iterable instanceof Collection ?
                (Collection<E>) iterable : IterableUtils.toList(iterable);
    }
}
