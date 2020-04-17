package xyz.srclab.common.pattern.builder;

import xyz.srclab.annotation.Nullable;

public abstract class CachedBuilder<T> implements Builder<T> {

    private @Nullable T cache;
    // true: cached; false: cache invalid
    private boolean changed;

    @Override
    public T build() {
        if (cache == null || !changed) {
            cache = buildNew();
            changed = true;
        }
        return cache;
    }

    /**
     * Called after any change which leads to refresh cache.
     */
    protected void commitChanges() {
        changed = false;
    }

    protected abstract T buildNew();
}
