package xyz.srclab.common.lang.ref;

import xyz.srclab.annotation.Nullable;

import java.util.Objects;

public interface Ref<T> {

    static <T> Ref<T> empty() {
        return RefSupport.newRef();
    }

    static <T> Ref<T> of(@Nullable T value) {
        return RefSupport.newRef(value);
    }

    @Nullable
    T get();

    default T getNonNull() throws NullPointerException {
        return Objects.requireNonNull(get());
    }

    void set(@Nullable T value);

    default boolean isPresent() {
        return get() != null;
    }
}
