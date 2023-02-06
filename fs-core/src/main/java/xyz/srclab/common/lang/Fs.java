package xyz.srclab.common.lang;

import xyz.srclab.annotations.Nullable;

/**
 * Common utilities.
 */
public class Fs {

    /**
     * Returns default value if given object is null, or given object itself if it is not null.
     *
     * @param obj          given object
     * @param defaultValue default value
     */
    public static <T> T notNull(@Nullable T obj, T defaultValue) {
        return obj == null ? defaultValue : obj;
    }
}
