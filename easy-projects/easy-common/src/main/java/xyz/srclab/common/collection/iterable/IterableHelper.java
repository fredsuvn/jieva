package xyz.srclab.common.collection.iterable;

import org.apache.commons.collections4.IterableUtils;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.collection.set.SetHelper;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IterableHelper {

    @Immutable
    public static <E> List<E> asList(Iterable<E> iterable) {
        return iterable instanceof List ?
                (List<E>) iterable : IterableUtils.toList(iterable);
    }

    @Immutable
    public static <E> Collection<E> asCollection(Iterable<E> iterable) {
        return iterable instanceof Collection ?
                (Collection<E>) iterable : toSet(iterable);
    }

    @Immutable
    public static <E> Set<E> asSet(Iterable<E> iterable) {
        return iterable instanceof Set ?
                (Set<E>) iterable : toSet(iterable);
    }

    @Immutable
    private static <E> Set<E> toSet(Iterable<E> iterable) {
        Set<E> set = new HashSet<>();
        for (E e : iterable) {
            set.add(e);
        }
        return SetHelper.immutable(set);
    }
}
