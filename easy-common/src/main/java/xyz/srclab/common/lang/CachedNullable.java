package xyz.srclab.common.lang;

import xyz.srclab.annotation.Nullable;

public abstract class CachedNullable<T> {

    protected boolean alreadyCached;
    protected @Nullable T cache;

    @Nullable
    protected T getNullable() {
        if (!alreadyCached) {
            cache = newNullable();
            alreadyCached = true;
        }
        return cache;
    }

    protected void refreshNull() {
        cache = newNullable();
        alreadyCached = true;
    }

    @Nullable
    protected abstract T newNullable();
}
