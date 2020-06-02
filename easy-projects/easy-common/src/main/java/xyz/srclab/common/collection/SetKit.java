package xyz.srclab.common.collection;

import com.google.common.collect.ImmutableSet;
import xyz.srclab.annotation.Immutable;

import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class SetKit {

    @Immutable
    public static <NE, OE> Set<NE> map(OE[] array, Function<OE, NE> mapper) {
        Set<NE> result = new LinkedHashSet<>(array.length);
        for (OE o : array) {
            result.add(mapper.apply(o));
        }
        return immutable(result);
    }

    @Immutable
    public static <NE, OE> Set<NE> map(Iterable<? extends OE> iterable, Function<OE, NE> mapper) {
        Set<NE> result = new LinkedHashSet<>();
        for (OE o : iterable) {
            result.add(mapper.apply(o));
        }
        return immutable(result);
    }

    @Immutable
    public static <E> Set<E> filter(Iterable<? extends E> iterable, Predicate<? super E> predicate) {
        Set<E> result = new LinkedHashSet<>();
        for (E e : iterable) {
            if (predicate.test(e)) {
                result.add(e);
            }
        }
        return immutable(result);
    }

    @Immutable
    public static <E> Set<E> enumerationToSet(Enumeration<? extends E> enumeration) {
        Set<E> result = new LinkedHashSet<>();
        while (enumeration.hasMoreElements()) {
            result.add(enumeration.nextElement());
        }
        return immutable(result);
    }

    @Immutable
    public static <E> Set<E> immutable(Iterable<? extends E> elements) {
        return ImmutableSet.copyOf(elements);
    }

    @SafeVarargs
    @Immutable
    public static <E> Set<E> immutable(E... elements) {
        return ImmutableSet.copyOf(elements);
    }
}
