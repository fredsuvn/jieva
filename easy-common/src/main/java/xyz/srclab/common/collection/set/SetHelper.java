package xyz.srclab.common.collection.set;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public class SetHelper {

    public static <E> Set<E> immutable(Iterable<? extends E> elements) {
        return ImmutableSet.copyOf(elements);
    }
}
