package xyz.srclab.common.collection.iterable;

import java.util.*;

public class IterableHelper {

    public static <E> List<E> asList(Iterable<E> iterable) {
        return iterable instanceof List ?
                (List<E>) iterable : toList(iterable);
    }

    private static <E> List<E> toList(Iterable<E> iterable) {
        List<E> list = new LinkedList<>();
        for (E e : iterable) {
            list.add(e);
        }
        return list;
    }

    public static <E> Collection<E> asCollection(Iterable<E> iterable) {
        return iterable instanceof Collection ?
                (Collection<E>) iterable : asSet(iterable);
    }

    public static <E> Set<E> asSet(Iterable<E> iterable) {
        return iterable instanceof Set ?
                (Set<E>) iterable : toSet(iterable);
    }

    private static <E> Set<E> toSet(Iterable<E> iterable) {
        Set<E> set = new LinkedHashSet<>();
        for (E e : iterable) {
            set.add(e);
        }
        return set;
    }
}
