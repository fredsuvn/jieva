package xyz.srclab.common.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.srclab.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * Logging for fs.
 *
 * @author fredsuvn
 */
public interface FsLogger {

    /**
     * Returns system default logger, which using {@link System#out} to output,
     * and its level is {@link FsLogger#INFO_LEVEL}.
     */
    static FsLogger system() {
        return FsUnsafe.systemLogger();
    }

    /**
     * Converts given level to description, for example: {@link #INFO_LEVEL} -> "INFO".
     * <p>
     * If given level cannot match at least one level of {@link FsLogger}, return String.valueOf(level).
     *
     * @param level given level
     */
    static String toLevelDescription(int level) {
        switch (level) {
            case TRACE_LEVEL:
                return "TRACE";
            case DEBUG_LEVEL:
                return "DEBUG";
            case INFO_LEVEL:
                return "INFO";
            case WARN_LEVEL:
                return "WARN";
            case ERROR_LEVEL:
                return "ERROR";
            default:
                return String.valueOf(level);
        }
    }

    /**
     * Returns a new logger with given level and default output (System.out).
     *
     * @param level given level
     */
    static FsLogger ofLevel(int level) {
        return () -> level;
    }

    /**
     * Returns a new logger with given level and output.
     *
     * @param level  given level
     * @param output given output
     */
    static FsLogger newLogger(int level, Consumer<Log> output) {
        return new FsLogger() {
            @Override
            public int getLevel() {
                return level;
            }

            @Override
            public void output(Log log) {
                output.accept(log);
            }
        };
    }

    /**
     * Trace level.
     */
    int TRACE_LEVEL = 1000;
    /**
     * Debug level.
     */
    int DEBUG_LEVEL = 2000;
    /**
     * Info level.
     */
    int INFO_LEVEL = 3000;
    /**
     * Warn level.
     */
    int WARN_LEVEL = 4000;
    /**
     * Error level.
     */
    int ERROR_LEVEL = 5000;

    /**
     * Logs with {@link FsLogger#TRACE_LEVEL}.
     */
    default void trace(Object... message) {
        FsUnsafe.log(this, TRACE_LEVEL, message);
    }

    /**
     * Logs with {@link FsLogger#DEBUG_LEVEL}.
     */
    default void debug(Object... message) {
        FsUnsafe.log(this, DEBUG_LEVEL, message);
    }

    /**
     * Logs with {@link FsLogger#INFO_LEVEL}.
     */
    default void info(Object... message) {
        FsUnsafe.log(this, INFO_LEVEL, message);
    }

    /**
     * Logs with {@link FsLogger#WARN_LEVEL}.
     */
    default void warn(Object... message) {
        FsUnsafe.log(this, WARN_LEVEL, message);
    }

    /**
     * Logs with {@link FsLogger#ERROR_LEVEL}.
     */
    default void error(Object... message) {
        FsUnsafe.log(this, ERROR_LEVEL, message);
    }

    default void log(int level, Object... message) {
        FsUnsafe.log(this, level, message);
    }

    /**
     * Returns level of this log.
     */
    int getLevel();

    /**
     * Outputs given log info.
     *
     * @param log given log info
     */
    default void output(Log log) {
        StringBuilder printInfo = new StringBuilder();
        Thread thread = Thread.currentThread();
        printInfo
            .append(FsDefault.dateTimeFormatter().format(log.date))
            .append("[").append(toLevelDescription(log.level)).append("]");
        if (log.stackTrace != null) {
            printInfo
                .append(log.stackTrace.getClassName())
                .append(".")
                .append(log.stackTrace.getMethodName())
                .append("(").append(log.stackTrace.getLineNumber()).append(")");
        }
        printInfo
            .append("[").append(thread.getName()).append("]:");
        for (Object o : log.message) {
            printInfo.append(o);
        }
        System.out.println(printInfo);
    }

    /**
     * Log info.
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    class Log {
        private int level;
        private LocalDateTime date;
        @Nullable
        private StackTraceElement stackTrace;
        private Object[] message;
    }
}
