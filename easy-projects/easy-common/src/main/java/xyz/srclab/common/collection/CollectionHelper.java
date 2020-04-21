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
        Iterator<?> it1 = collection1.iterator();
        Iterator<?> it2 = collection2.iterator();
        while (it1.hasNext()) {
            Object e1 = it1.next();
            Object e2 = it2.next();
            if (!Objects.deepEquals(e1, e2)) {
                return false;
            }
        }
        return true;
    }
}
