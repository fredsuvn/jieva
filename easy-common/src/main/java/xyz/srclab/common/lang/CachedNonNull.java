package xyz.srclab.common.lang;

import xyz.srclab.annotation.Nullable;

public abstract class CachedNonNull<T> {

    protected @Nullable T cache;

    protected T getNonNull() {
        if (cache == null) {
            cache = newNonNull();
        }
        return cache;
    }

    protected void refreshNonNull() {
        this.cache = newNonNull();
    }

    protected abstract T newNonNull();
}
