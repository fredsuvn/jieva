package xyz.srclab.common.lang;

import xyz.srclab.annotation.Nullable;

public abstract class Ref<T> {

    public static <T> Ref<T> ofEmpty() {
        return new RefImpl<>();
    }

    public static <T> Ref<T> of(@Nullable T object) {
        return new RefImpl<>(object);
    }

    @Nullable
    public abstract T get();

    public abstract void set(@Nullable T value);

    private static final class RefImpl<T> extends Ref<T> {

        private @Nullable T object;

        public RefImpl() {
        }

        public RefImpl(@Nullable T object) {
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
}
