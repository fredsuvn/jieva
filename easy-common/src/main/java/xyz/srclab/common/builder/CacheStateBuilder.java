package xyz.srclab.common.builder;

public abstract class CacheStateBuilder<T> implements Builder<T> {

    private T cache;
    // true: cached; false: cache invalid
    private boolean state;

    @Override
    public T build() {
        if (!state) {
            cache = buildNew();
            state = true;
        }
        return cache;
    }

    protected void changeState() {
        state = false;
    }

    protected abstract T buildNew();
}
