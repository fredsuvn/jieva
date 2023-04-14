package xyz.srclab.common.collect;

import xyz.srclab.build.annotations.FsMethods;
import xyz.srclab.common.base.FsArray;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Collection utilities.
 *
 * @author sunq62
 */
@FsMethods
public class FsCollect {

    /**
     * Puts all elements from given iterable into dest collection, and returns the dest collection.
     *
     * @param iterable given iterable
     * @param dest     dest collection
     */
    public static <T, C extends Collection<? super T>> C toCollection(Iterable<T> iterable, C dest) {
        for (T t : iterable) {
            dest.add(t);
        }
        return dest;
    }

    /**
     * Returns an array contains all elements from given iterable in its order.
     *
     * @param iterable given iterable
     * @param type     array's component type
     */
    public static <T> T[] toArray(Iterable<? extends T> iterable, Class<T> type) {
        if (iterable instanceof Collection) {
            T[] array = FsArray.newArray(type, ((Collection<? extends T>) iterable).size());
            int i = 0;
            for (T t : iterable) {
                array[i++] = t;
            }
            return array;
        } else {
            LinkedList<T> list = toCollection(iterable, new LinkedList<>());
            return toArray(list, type);
        }
    }
}
