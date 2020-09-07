package xyz.srclab.common.base;

import java.util.Comparator;

/**
 * @author sunqian
 */
public class Sort {

    public static final Comparator<?> SELF_COMPARABLE_COMPARATOR = (o1, o2) ->
            ((Comparable<?>) o1).compareTo(Cast.as(o2));

    public static <T> Comparator<T> selfComparableComparator() {
        return Cast.as(SELF_COMPARABLE_COMPARATOR);
    }
}
