package xyz.srclab.common.lang.tuple;

import xyz.srclab.annotation.Nullable;

public interface MutableTriple<A, B, C> extends Triple<A, B, C>, MutablePair<A, B> {

    static <A, B, C> MutableTriple<A, B, C> create() {
        return Tuple0.newMutableTriple();
    }

    static <A, B, C> MutableTriple<A, B, C> of(@Nullable A first, @Nullable B second, @Nullable C third) {
        return Tuple0.newMutableTriple(first, second, third);
    }

    void third(@Nullable C value);
}
