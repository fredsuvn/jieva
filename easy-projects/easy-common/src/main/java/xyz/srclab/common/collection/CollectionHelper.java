package xyz.srclab.common.collection;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class CollectionHelper {

    public static boolean deepEquals(Collection<?> collection1, Collection<?> collection2) {
        if (collection1 == collection2) {
            return true;
        }
        if (collection1.size() != collection2.size()) {
            return false;
        }
        Iterator<?> iterator1 = collection1.iterator();
        Iterator<?> iterator2 = collection2.iterator();
        while (iterator1.hasNext()) {
            if (!iterator2.hasNext()) {
                return false;
            }
            Object o1 = iterator1.next();
            Object o2 = iterator2.next();
            if (o1 instanceof Collection && o2 instanceof Collection) {
                if (deepEquals((Collection<?>) o1, (Collection<?>) o2)) {
                    continue;
                } else {
                    return false;
                }
            }
            if (o1 instanceof Collection || o2 instanceof Collection) {
                return false;
            }
            if (!Objects.deepEquals(o1, o2)) {
                return false;
            }
        }
        return true;
    }
}
