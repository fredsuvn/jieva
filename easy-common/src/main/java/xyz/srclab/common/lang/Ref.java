package xyz.srclab.common.lang;

public interface Ref<T> {

    static <T> Ref<T> withEmpty() {
        return new RefImpl<>();
    }

    static <T> Ref<T> with(T object) {
        return new RefImpl<>(object);
    }

    T get();

    void set(T value);
}

final class RefImpl<T> implements Ref<T> {

    private T object;

    public RefImpl() {
    }

    public RefImpl(T object) {
        this.object = object;
    }

    public T get() {
        return object;
    }

    public void set(T object) {
        this.object = object;
    }
}
