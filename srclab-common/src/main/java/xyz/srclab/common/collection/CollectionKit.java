package xyz.srclab.common.collection;

import xyz.srclab.annotation.Out;

import java.util.Collection;
import java.util.Collections;

/**
 * @author sunqian
 */
public class CollectionKit {

    public static <T extends Collection<? super E>, E> T addAll(@Out T collection, E... elements) {
        Collections.addAll(collection, elements);
        return collection;
    }

    public static <T extends Collection<? super E>, E> T addAll(@Out T collection, Iterable<? extends E> iterable) {
        for (E e : iterable) {
            collection.add(e);
        }
        return collection;
    }
}
