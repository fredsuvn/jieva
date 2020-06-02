package xyz.srclab.common.collection;

import xyz.srclab.annotation.WrittenReturn;

import java.util.*;
import java.util.function.Function;

public class IterableKit {

    public static <E> List<E> asList(Iterable<? extends E> iterable) {
        return iterable instanceof List ?
                (List<E>) iterable : toList(iterable);
    }

    private static <E> List<E> toList(Iterable<? extends E> iterable) {
        List<E> list = new LinkedList<>();
        for (E e : iterable) {
            list.add(e);
        }
        return list;
    }

    public static <E> Collection<E> asCollection(Iterable<? extends E> iterable) {
        return iterable instanceof Collection ?
                (Collection<E>) iterable : asSet(iterable);
    }

    public static <E> Set<E> asSet(Iterable<? extends E> iterable) {
        return iterable instanceof Set ?
                (Set<E>) iterable : toSet(iterable);
    }

    private static <E> Set<E> toSet(Iterable<? extends E> iterable) {
        Set<E> set = new LinkedHashSet<>();
        for (E e : iterable) {
            set.add(e);
        }
        return set;
    }

    public static <NE, OE> Iterable<NE> map(Iterable<? extends OE> old, Function<OE, NE> mapper) {
        List<NE> newList = new LinkedList<>();
        for (OE oe : old) {
            newList.add(mapper.apply(oe));
        }
        return newList;
    }

    public static <T extends Collection<E>, E> T addAll(
            @WrittenReturn T container, Iterable<? extends E> iterable) {
        container.addAll(asCollection(iterable));
        return container;
    }
}
