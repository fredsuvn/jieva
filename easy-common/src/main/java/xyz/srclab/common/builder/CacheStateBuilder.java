package xyz.srclab.common.builder;

public abstract class CacheStateBuilder<T> implements Builder<T> {

    protected T cache;
    // true: cached; false: cache invalid
    protected boolean state;

    protected void changeState() {
        state = false;
    }

    @Override
    public T build() {
        if (!state) {
            cache = buildNew();
            state = true;
        }
        return cache;
    }

    protected abstract T buildNew();
}
