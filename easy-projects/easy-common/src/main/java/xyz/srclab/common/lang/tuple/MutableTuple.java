package xyz.srclab.common.lang.tuple;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
public interface MutableTuple extends Tuple {

    static MutableTuple create(int length) {
        return Tuple0.newMutableTuple(length);
    }

    static MutableTuple of(Object... values) {
        return Tuple0.newMutableTuple(values);
    }

    void set(int index, @Nullable Object value);
}
