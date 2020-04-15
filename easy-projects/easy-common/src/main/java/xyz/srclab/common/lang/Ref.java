package xyz.srclab.common.lang;

import xyz.srclab.annotations.Nullable;

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

final class RefImpl<T> implements Ref<T> {

    private @Nullable T object;

    public RefImpl() {
    }

    public RefImpl(T object) {
        this.object = object;
    }

    @Nullable
    public T get() {
        return object;
    }

    public void set(@Nullable T object) {
        this.object = object;
    }
}
