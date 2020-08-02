package xyz.srclab.common.lang.tuple;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.util.Objects;

/**
 * @author sunqian
 */
@Immutable
public interface Tuple {

    static Tuple of(Object... values) {
        return TupleSupport.newTuple(values);
    }

    static MutableTuple mutable(int length) {
        return MutableTuple.create(length);
    }

    static MutableTuple mutableOf(Object... values) {
        return MutableTuple.of(values);
    }

    @Nullable <T> T get(int index);

    default <T> T getNonNull(int index) throws NullPointerException {
        return Objects.requireNonNull(get(index));
    }
}
