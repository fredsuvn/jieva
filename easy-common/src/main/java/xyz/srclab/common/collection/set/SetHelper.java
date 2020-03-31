package xyz.srclab.common.collection.set;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class SetHelper {

    public static <E> Set<E> immutableSet(Iterable<? extends E> elements) {
        return ImmutableSet.copyOf(elements);
    }
}
