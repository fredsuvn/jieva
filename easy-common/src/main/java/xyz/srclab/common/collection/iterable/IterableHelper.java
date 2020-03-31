package xyz.srclab.common.collection.iterable;

import org.apache.commons.collections4.IterableUtils;

import java.util.List;

public class IterableHelper {

    public static <E> List<E> castToList(Iterable<E> iterable) {
        return iterable instanceof List ?
                (List<E>) iterable : IterableUtils.toList(iterable);
    }
}
