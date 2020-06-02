package xyz.srclab.common.collection;

import xyz.srclab.annotation.Out;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author sunqian
 */
public class CollectionKit {

    public static <E> void addAll(@Out Collection<? super E> collection, E... elements) {
        collection.addAll(Arrays.asList(elements));
    }

    public static <E> void addAll(@Out Collection<? super E> collection, Iterable<? extends E> elements) {
        collection.addAll(IterableKit.toList(elements));
    }
}
