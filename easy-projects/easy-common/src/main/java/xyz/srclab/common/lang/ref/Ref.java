package xyz.srclab.common.lang.ref;

import xyz.srclab.annotation.Nullable;

public interface Ref<T> {

    static <T> Ref<T> withEmpty() {
        return new RefImpl<>();
    }

    static <T> Ref<T> with(T object) {
        return new RefImpl<>(object);
    }

    @Nullable
    T get();

    void set(@Nullable T value);
}
