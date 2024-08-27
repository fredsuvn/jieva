package xyz.fslabo.common.base;

/**
 * Simple logging interface for Jie.
 *
 * @author fredsuvn
 */
public interface JieLog {

    /**
     * Log level: TRACE = 1000.
     */
    int LEVEL_TRACE = 1000;

    /**
     * Log level: DEBUG = 2000.
     */
    int LEVEL_DEBUG = 2000;

    /**
     * Log level: INFO = 3000.
     */
    int LEVEL_INFO = 3000;

    /**
     * Log level: WARN = 4000.
     */
    int LEVEL_WARN = 4000;

    /**
     * Log level: TERROR = 5000.
     */
    int LEVEL_ERROR = 5000;

    /**
     * Returns system default log with {@link #LEVEL_INFO} and {@link System#out}.
     *
     * @return system default log with {@link #LEVEL_INFO} and {@link System#out}
     */
    static JieLog system() {
        return JieLogImpls.DEFAULT;
    }

    /**
     * Returns a new {@link JieLog} with specified level and appendable object to be written.
     *
     * @param level      specified level
     * @param appendable appendable object to be written
     * @return a new {@link JieLog} with specified level and appendable object to be written
     */
    static JieLog of(int level, Appendable appendable) {
        return new JieLogImpls.JieLogImpl(level, appendable);
    }

    /**
     * Logs given message with {@link #LEVEL_TRACE}. The message will be output in order of the array.
     *
     * @param message given message
     */
    default void trace(Object... message) {
        log(LEVEL_TRACE, message);
    }

    /**
     * Logs given message with {@link #LEVEL_TRACE}. The message will be output in order of the array.
     *
     * @param message given message
     */
    default void debug(Object... message) {
        log(LEVEL_DEBUG, message);
    }

    /**
     * Logs given message with {@link #LEVEL_TRACE}. The message will be output in order of the array.
     *
     * @param message given message
     */
    default void info(Object... message) {
        log(LEVEL_INFO, message);
    }

    /**
     * Logs given message with {@link #LEVEL_TRACE}. The message will be output in order of the array.
     *
     * @param message given message
     */
    default void warn(Object... message) {
        log(LEVEL_WARN, message);
    }

    /**
     * Logs given message with {@link #LEVEL_TRACE}. The message will be output in order of the array.
     *
     * @param message given message
     */
    default void error(Object... message) {
        log(LEVEL_ERROR, message);
    }

    /**
     * Logs given message with specified level. The message will be output in order of the array.
     *
     * @param level   specified level
     * @param message given message
     */
    void log(int level, Object... message);

    /**
     * Returns level of this log.
     *
     * @return level of this log
     */
    int getLevel();
}
