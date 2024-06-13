package xyz.fslabo.common.base;

/**
 * Long version of {@link SubFunction}, used to generate sub-object such as sub-string, sub-list,
 * but type of its parameters is long.
 *
 * @param <T> type of source object to be sub-ed.
 * @param <R> type of result object after sub-ing.
 * @author fredsuvn
 */
@FunctionalInterface
public interface LongSubFunction<T, R> {

    /**
     * Returns sub-object of source object from start index inclusive to end index exclusive.
     *
     * @param source source object
     * @param start  start index inclusive
     * @param end    end index exclusive
     * @return sub-object of source object from start index inclusive to end index exclusive
     */
    R apply(T source, long start, long end);
}
