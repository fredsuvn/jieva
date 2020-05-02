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

    public static boolean deepEquals(Iterable<?> iterable1, Iterable<?> iterable2) {
        if (iterable1 == iterable2) {
            return true;
        }
        Iterator<?> iterator1 = iterable1.iterator();
        Iterator<?> iterator2 = iterable2.iterator();
        while (iterator1.hasNext()) {
            if (!iterator2.hasNext()) {
                return false;
            }
            Object o1 = iterator1.next();
            Object o2 = iterator2.next();
            if (o1 instanceof Iterable && o2 instanceof Iterable) {
                if (deepEquals((Iterable<?>) o1, (Iterable<?>) o2)) {
                    continue;
                } else {
                    return false;
                }
            }
            if (o1 instanceof Iterable || o2 instanceof Iterable) {
                return false;
            }
            if (!Objects.deepEquals(o1, o2)) {
                return false;
            }
        }
        return !iterator2.hasNext();
    }
}
