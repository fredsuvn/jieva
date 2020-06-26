package xyz.srclab.common.lang.tuple;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.util.Objects;

@Immutable
public interface Pair<A, B> extends Tuple {

    static <A, B> Pair<A, B> of(@Nullable A first, @Nullable B second) {
        return TupleSupport.newPair(first, second);
    }

    static <A, B> MutablePair<A, B> mutable() {
        return MutablePair.create();
    }

    static <A, B> MutablePair<A, B> mutableOf(@Nullable A first, @Nullable B second) {
        return MutablePair.of(first, second);
    }

    @Nullable
    A first();

    default A firstNonNull() throws NullPointerException {
        return Objects.requireNonNull(first());
    }

    @Nullable
    B second();

    default B secondNonNull() throws NullPointerException {
        return Objects.requireNonNull(second());
    }
}
