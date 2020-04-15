package xyz.srclab.common.builder;

import xyz.srclab.annotations.Nullable;

public abstract class CacheStateBuilder<T> implements Builder<T> {

    private @Nullable T cache;
    // true: cached; false: cache invalid
    private boolean state;

    @Override
    public T build() {
        if (cache == null || !state) {
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
