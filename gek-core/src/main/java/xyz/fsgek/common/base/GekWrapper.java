package xyz.fsgek.common.base;

import xyz.fsgek.annotations.Nullable;

/**
 * Object wrapper to wrap an object.
 *
 * @author fredsuvn
 */
public interface GekWrapper<T> {

    /**
     * Wraps given object.
     *
     * @param obj given object
     * @param <T> type of given object
     * @return wrapper of given object
     */
    static <T> GekWrapper<T> wrap(T obj) {
        return () -> obj;
    }

    /**
     * Returns an empty wrapper which wraps {@code null}.
     *
     * @param <T> type of wrapped object
     * @return empty wrapper which wraps {@code null}
     */
    static <T> GekWrapper<T> empty() {
        return Gek.as(Impls.NULL_WRAPPER);
    }

    /**
     * Returns wrapped object.
     *
     * @return wrapped object
     */
    @Nullable
    T get();
}
