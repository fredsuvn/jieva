package xyz.srclab.common.design.builder;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Cast;

public abstract class UnboundedCachedBuilder {

    private @Nullable Object cache;
    private int stateVersion = 0;
    private int buildVersion = 0;

    public <T> T build() {
        if (cache == null || isUpdateSinceLastBuild()) {
            cache = buildNew();
            buildVersion = stateVersion;
        }
        return Cast.as(cache);
    }

    protected abstract Object buildNew();

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
