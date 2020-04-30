package xyz.srclab.common.collection.iterable;

import org.apache.commons.collections4.IterableUtils;
import xyz.srclab.annotation.Immutable;
import xyz.srclab.common.collection.map.MapHelper;
import xyz.srclab.common.collection.set.SetHelper;

import java.util.*;
import java.util.function.Function;

public class IterableHelper {

    public static <E> @Immutable List<E> asList(Iterable<E> iterable) {
        return iterable instanceof List ?
                (List<E>) iterable : IterableUtils.toList(iterable);
    }

    public static <E> @Immutable Collection<E> asCollection(Iterable<E> iterable) {
        return iterable instanceof Collection ?
                (Collection<E>) iterable : toSet(iterable);
    }

    public static <E> @Immutable Set<E> asSet(Iterable<E> iterable) {
        return iterable instanceof Set ?
                (Set<E>) iterable : toSet(iterable);
    }

    private static <E> @Immutable Set<E> toSet(Iterable<E> iterable) {
        Set<E> set = new HashSet<>();
        for (E e : iterable) {
            set.add(e);
        }
        return SetHelper.immutable(set);
    }

    private static <E, K, V> @Immutable Map<K, V> toMap(Iterable<E> iterable, Function<E, Map.Entry<K, V>> mapper) {
        Map<K, V> result = new LinkedHashMap<>();
        for (E e : iterable) {
            Map.Entry<K, V> entry = mapper.apply(e);
            result.put(entry.getKey(), entry.getValue());
        }
        return MapHelper.immutable(result);
    }
}
