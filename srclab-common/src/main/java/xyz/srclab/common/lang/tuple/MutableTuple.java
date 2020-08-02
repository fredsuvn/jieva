package xyz.srclab.common.lang.tuple;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface MutableTuple extends Tuple {

    static MutableTuple create(int length) {
        return TupleSupport.newMutableTuple(length);
    }

    static MutableTuple of(Object... values) {
        return TupleSupport.newMutableTuple(values);
    }

    void set(int index, @Nullable Object value);
}
