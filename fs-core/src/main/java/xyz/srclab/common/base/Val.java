package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;

/**
 * Val is an immutable value wrapper, of which value cannot be reassigned.
 *
 * @author fredsuvn
 */
public interface Val<T> {

    /**
     * Returns wrapped value.
     */
    @Nullable
    T get();

    T orElse();
}
