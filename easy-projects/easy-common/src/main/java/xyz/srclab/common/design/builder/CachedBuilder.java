package xyz.srclab.common.design.builder;

import xyz.srclab.annotation.Nullable;

public abstract class CachedBuilder<T> implements Builder<T> {

    private @Nullable T cache;
    private boolean update = false;

    @Override
    public T build() {
        if (cache == null || update) {
            cache = buildNew();
            update = false;
        }
        return cache;
    }

    /**
     * Called after any change which leads to refresh cache.
     */
    protected void updateState() {
        update = true;
    }

    protected abstract T buildNew();
}
