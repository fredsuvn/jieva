package xyz.fslabo.common.base;

/**
 * Base interface for {@code Starter}.
 *
 * @param <T> type of started object
 * @param <S> type of starter
 * @author fredsuvn
 */
public interface BaseStarter<T, S extends BaseStarter<T, S>> {

    /**
     * Builds and starts a new starter.
     *
     * @return new starter
     */
    T start();

    /**
     * Resets all setting.
     *
     * @return this
     */
    S reset();
}
