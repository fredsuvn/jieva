package xyz.srclab.common.design.builder;

import xyz.srclab.annotation.Nullable;

public abstract class BaseProductCachingBuilder<T> {

    private @Nullable T cache;
    private int stateVersion = 0;
    private int buildVersion = 0;

    protected abstract T buildNew();

    protected T buildCaching() {
        if (cache == null || isUpdatedSinceLastBuild()) {
            cache = buildNew();
            refreshBuildVersion();
        }
        return cache;
    }

    /**
     * Called after any change which leads to refresh cache.
     */
    protected void updateState() {
        stateVersion++;
        if (stateVersion == buildVersion) {
            stateVersion++;
        }
    }

    protected boolean isUpdatedSinceLastBuild() {
        return stateVersion != buildVersion;
    }

    private void refreshBuildVersion() {
        buildVersion = stateVersion;
    }
}
