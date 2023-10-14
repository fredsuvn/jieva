package xyz.fsgik.common.base;

/**
 * Object wrapper to wrap an object.
 *
 * @author fredsuvn
 */
public class FsWrapper<T> {

    private static final FsWrapper<?> NULL_WRAPPER = wrap(null);

    /**
     * Wraps given object.
     *
     * @param obj given object
     * @param <T> type of given object
     * @return wrapper of given object
     */
    public static <T> FsWrapper<T> wrap(T obj) {
        return new FsWrapper<>(obj);
    }

    /**
     * Returns an empty wrapper which wraps {@code null}.
     *
     * @param <T> type of wrapped object
     * @return empty wrapper which wraps {@code null}
     */
    public static <T> FsWrapper<T> empty() {
        return Fs.as(NULL_WRAPPER);
    }

    private final T source;

    private FsWrapper(T source) {
        this.source = source;
    }

    /**
     * Returns wrapped object.
     *
     * @return wrapped object
     */
    public T get() {
        return source;
    }
}
