package xyz.fslabo.common.base;

/**
 * Base interface for {@code Builder}.
 *
 * @param <T> type of built object
 * @param <B> type of builder
 * @author fredsuvn
 */
public interface BaseBuilder<T, B extends BaseBuilder<T, B>> {

    /**
     * Build a new instance.
     *
     * @return new instance
     */
    T build();

    /**
     * Resets all setting.
     *
     * @return this
     */
    B reset();
}
