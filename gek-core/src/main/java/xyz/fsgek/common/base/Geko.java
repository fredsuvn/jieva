package xyz.fsgek.common.base;

import xyz.fsgek.annotations.Immutable;
import xyz.fsgek.annotations.Nullable;

/**
 * Geko is abbreviation of "Gek Object", which is immutable object holder interface used to hold an object.
 *
 * @author fredsuvn
 */
@Immutable
public interface Geko<T> {

    /**
     * Returns an instance of {@link Geko} holds given object.
     *
     * @param obj given object
     * @param <T> type of given object
     * @return an instance of {@link Geko} holds given object
     */
    static <T> Geko<T> of(@Nullable T obj) {
        return () -> obj;
    }

    /**
     * Returns empty instance of {@link Geko} holds null.
     *
     * @param <T> type of wrapped object
     * @return empty instance of {@link Geko} holds null
     */
    static <T> Geko<T> empty() {
        return Gek.as(Companion.NULL);
    }

    /**
     * Returns held object.
     *
     * @return held object
     */
    @Nullable
    T get();

    /**
     * Companion object of {@link Geko}.
     */
    class Companion {
        private static final Geko<?> NULL = Geko.of(null);
    }
}
