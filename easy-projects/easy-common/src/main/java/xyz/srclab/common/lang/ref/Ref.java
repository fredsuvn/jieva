package xyz.srclab.common.lang.ref;

import xyz.srclab.annotation.Nullable;

public interface Ref<T> {

    static <T> Ref<T> ofEmpty() {
        return new RefImpl<>();
    }

    static <T> Ref<T> of(@Nullable T object) {
        return new RefImpl<>(object);
    }

    @Nullable
    T get();

    void set(@Nullable T value);
}
