package xyz.srclab.common.lang.ref;

import xyz.srclab.annotation.Nullable;

/**
 * @author sunqian
 */
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
