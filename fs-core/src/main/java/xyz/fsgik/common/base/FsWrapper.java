package xyz.fsgik.common.base;

import xyz.fsgik.annotations.Nullable;

/**
 * Object wrapper to wrap an object.
 *
 * @author fredsuvn
 */
public interface FsWrapper<T> {

    /**
     * Wraps given object.
     *
     * @param obj given object
     * @param <T> type of given object
     * @return wrapper of given object
     */
    static <T> FsWrapper<T> wrap(T obj) {
        return () -> obj;
    }

    /**
     * Returns an empty wrapper which wraps {@code null}.
     *
     * @param <T> type of wrapped object
     * @return empty wrapper which wraps {@code null}
     */
    static <T> FsWrapper<T> empty() {
        return Fs.as(Impls.NULL_WRAPPER);
    }

    /**
     * Returns wrapped object.
     *
     * @return wrapped object
     */
    @Nullable
    T get();
}
