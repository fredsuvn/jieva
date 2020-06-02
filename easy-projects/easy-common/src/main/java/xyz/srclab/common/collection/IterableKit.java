package xyz.srclab.common.collection;

import xyz.srclab.annotation.Immutable;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class IterableKit {

    @Immutable
    public static <NE, OE> Iterable<NE> map(Iterable<? extends OE> old, Function<? super OE, ? extends NE> mapper) {
        List<NE> result = new LinkedList<>();
        for (OE oe : old) {
            result.add(mapper.apply(oe));
        }
        return ListKit.immutable(result);
    }

    @Immutable
    public static <E> List<E> toList(Iterable<? extends E> iterable) {
        return ListKit.immutable(iterable);
    }

    @Immutable
    public static <E> Set<E> toSet(Iterable<? extends E> iterable) {
        return SetKit.immutable(iterable);
    }
}
