package xyz.srclab.common.collect;

import java.util.Collection;

/**
 * @author sunqian
 */
class JavaCollect {

    static Object[] toArray(Collection<?> collection) {
        return collection.toArray();
    }

    static <T> T[] toArray(Collection<T> collection, T[] array) {
        return collection.toArray(array);
    }
}
