package xyz.srclab.common.lang.tuple;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.util.Objects;

@Immutable
public interface Triple<A, B, C> extends Pair<A, B> {

    static <A, B, C> Triple<A, B, C> of(@Nullable A first, @Nullable B second, @Nullable C third) {
        return Tuple0.newTriple(first, second, third);
    }

    static <A, B, C> MutableTriple<A, B, C> mutable() {
        return MutableTriple.create();
    }

    static <A, B, C> MutableTriple<A, B, C> mutableOf(@Nullable A first, @Nullable B second, @Nullable C third) {
        return MutableTriple.of(first, second, third);
    }

    @Nullable
    C third();

    default C thirdNonNull() throws NullPointerException {
        return Objects.requireNonNull(third());
    }
}
