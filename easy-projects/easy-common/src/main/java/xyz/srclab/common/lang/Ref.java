package xyz.srclab.common.lang;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Checker;

public abstract class Ref<T> {

    public static <T> Ref<T> ofEmpty() {
        return new RefImpl<>();
    }

    public static <T> Ref<T> of(@Nullable T object) {
        return new RefImpl<>(object);
    }

    private @Nullable T object;

    protected Ref() {
    }

    protected Ref(@Nullable T object) {
        this.object = object;
    }

    @Nullable
    public T get() {
        return object;
    }

    public T getNonNull() {
        @Nullable T result = get();
        Checker.checkNull(result != null);
        return result;
    }

    public void set(@Nullable T value) {
        this.object = object;
    }

    public boolean isPresent() {
        return object != null;
    }

    private static final class RefImpl<T> extends Ref<T> {
        public RefImpl() {
        }

        public RefImpl(@Nullable T object) {
            super(object);
        }
    }
}
