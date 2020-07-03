package xyz.srclab.common.design.builder;

import xyz.srclab.annotation.Nullable;

public abstract class CachedBuilder<T> implements Builder<T> {

    private @Nullable T cache;
    private int stateVersion = 0;
    private int buildVersion = 0;

    @Override
    public T build() {
        if (cache == null || isUpdateSinceLastBuild()) {
            cache = buildNew();
            buildVersion = stateVersion;
        }
        return cache;
    }

    protected abstract T buildNew();

    /**
     * Called after any change which leads to refresh cache.
     */
    protected void updateState() {
        stateVersion++;
        if (stateVersion == buildVersion) {
            stateVersion++;
        }
    }

    protected boolean isUpdateSinceLastBuild() {
        return stateVersion != buildVersion;
    }
}
