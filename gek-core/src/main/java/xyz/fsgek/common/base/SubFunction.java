package xyz.fsgek.common.base;

/**
 * Functional interface used to generate sub-object such as sub-string, sub-list.
 *
 * @param <T> type of source object to be sub-ed.
 * @author fredsuvn
 */
@FunctionalInterface
public interface SubFunction<T> {

    /**
     * Returns sub-object of source object from start index inclusive to end index exclusive.
     *
     * @param source source object
     * @param start  start index inclusive
     * @param end    end index exclusive
     * @return sub-object of source object from start index inclusive to end index exclusive
     */
    T apply(T source, int start, int end);

    /**
     * Returns sub-object of source object from start index inclusive to end index exclusive.
     * This is {@code long} version of {@link #apply(Object, int, int)}.
     *
     * @param source source object
     * @param start  start index inclusive
     * @param end    end index exclusive
     * @return sub-object of source object from start index inclusive to end index exclusive
     */
    default T applyLong(T source, long start, long end) {
        return apply(source, (int) start, (int) end);
    }
}
