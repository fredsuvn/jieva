package xyz.srclab.common.collection.set;

import com.google.common.collect.ImmutableSet;
import xyz.srclab.annotation.Immutable;

import java.util.Set;

public class SetHelper {

    public static <E> @Immutable Set<E> immutable(Iterable<? extends E> elements) {
        return ImmutableSet.copyOf(elements);
    }

    @SafeVarargs
    public static <E> @Immutable Set<E> immutable(E... elements) {
        return ImmutableSet.copyOf(elements);
    }
}
