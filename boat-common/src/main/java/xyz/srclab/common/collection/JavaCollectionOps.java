package xyz.srclab.common.collection;

import java.util.Collection;

/**
 * @author sunqian
 */
final class JavaCollectionOps {

    static Object[] toArray(Collection<?> collection) {
        return collection.toArray();
    }

    static <T> T[] toArray(Collection<T> collection, T[] array) {
        return collection.toArray(array);
    }
}
