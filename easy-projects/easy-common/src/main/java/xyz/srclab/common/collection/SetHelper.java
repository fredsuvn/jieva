package xyz.srclab.common.collection;

import com.google.common.collect.ImmutableSet;
import xyz.srclab.annotation.Immutable;

import java.util.*;

public class SetHelper {
    @Immutable
    public static <E> Set<E> immutable(Iterable<? extends E> elements) {
        return ImmutableSet.copyOf(elements);
    }

    @SafeVarargs
    @Immutable
    public static <E> Set<E> immutable(E... elements) {
        return ImmutableSet.copyOf(elements);
    }

    @Immutable
    public static <E> Set<E> enumerationToSet(Enumeration<? extends E> enumeration) {
        Set<E> result = new LinkedHashSet<>();
        while (enumeration.hasMoreElements()) {
            result.add(enumeration.nextElement());
        }
        return immutable(result);
    }

    public static boolean deepEquals(Set<?> set1, Set<?> set2) {
        if (set1 == set2) {
            return true;
        }
        if (set1.size() != set2.size()) {
            return false;
        }
        Iterator<?> iterator1 = set1.iterator();
        Iterator<?> iterator2 = set2.iterator();
        while (iterator1.hasNext()) {
            if (!iterator2.hasNext()) {
                return false;
            }
            Object o1 = iterator1.next();
            Object o2 = iterator2.next();
            if (o1 instanceof Set && o2 instanceof Set) {
                if (deepEquals((Set<?>) o1, (Set<?>) o2)) {
                    continue;
                } else {
                    return false;
                }
            }
            if (o1 instanceof Set || o2 instanceof Set) {
                return false;
            }
            if (!Objects.deepEquals(o1, o2)) {
                return false;
            }
        }
        return true;
    }
}
