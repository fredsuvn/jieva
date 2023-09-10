package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Logging for fs.
 *
 * @author fredsuvn
 */
public interface FsLogger {

    /**
     * Returns system default logger, which using {@link System#out} to output,
     * and its level is {@link Level#INFO}.
     */
    static FsLogger system() {
        return FsUnsafe.ForLogger.SYSTEM_LOGGER;
    }

    /**
     * Returns a new logger with given level and default output: ({@link System#out}).
     *
     * @param level given level
     */
    static FsLogger ofLevel(Level level) {
        return () -> level;
    }

    /**
     * Returns a new logger with given level and specified output.
     *
     * @param level  given level
     * @param output specified output
     */
    static FsLogger newLogger(Level level, Consumer<LogInfo> output) {
        return new FsLogger() {
            @Override
            public Level getLevel() {
                return level;
            }

            @Override
            public void writeLog(LogInfo logInfo) {
                output.accept(logInfo);
            }
        };
    }

    /**
     * Logs message in level of {@link Level#TRACE}.
     */
    default void trace(Object... messages) {
        if (getLevel().value() > Level.TRACE.value()) {
            return;
        }
        LogInfo logInfo = buildLog(messages, 2);
        writeLog(logInfo);
    }

    /**
     * Logs message in level of {@link Level#DEBUG}.
     */
    default void debug(Object... messages) {
        if (getLevel().value() > Level.DEBUG.value()) {
            return;
        }
        LogInfo logInfo = buildLog(messages, 2);
        writeLog(logInfo);
    }

    /**
     * Logs message in level of {@link Level#INFO}.
     */
    default void info(Object... messages) {
        if (getLevel().value() > Level.INFO.value()) {
            return;
        }
        LogInfo logInfo = buildLog(messages, 2);
        writeLog(logInfo);
    }

    /**
     * Logs message in level of {@link Level#WARN}.
     */
    default void warn(Object... messages) {
        if (getLevel().value() > Level.WARN.value()) {
            return;
        }
        LogInfo logInfo = buildLog(messages, 2);
        writeLog(logInfo);
    }

    /**
     * Logs message in level of {@link Level#ERROR}.
     */
    default void error(Object... messages) {
        if (getLevel().value() > Level.ERROR.value()) {
            return;
        }
        LogInfo logInfo = buildLog(messages, 2);
        writeLog(logInfo);
    }

    /**
     * Returns level of this log.
     */
    Level getLevel();

    /**
     * Writes given log info.
     *
     * @param logInfo given log info
     */
    default void writeLog(LogInfo logInfo) {
        StringBuilder message = new StringBuilder();
        Thread thread = Thread.currentThread();
        message
            .append(FsDate.format(Date.from(logInfo.time())))
            .append("[").append(logInfo.level().description()).append("]");
        StackTraceElement stackTrace = logInfo.stackTrace();
        if (stackTrace != null) {
            message
                .append(stackTrace.getClassName())
                .append(".")
                .append(stackTrace.getMethodName())
                .append("(").append(stackTrace.getLineNumber()).append(")");
        }
        message
            .append("[").append(thread.getName()).append("]:");
        for (Object o : logInfo.messages()) {
            message.append(o);
        }
        System.out.println(message);
    }

    /**
     * Builds log info with given message array
     * and offset between the point where log operation occurs and this method.
     * For example, let method {@code A} be the method which call the log method,
     * let {@code LoggerImpl} be the FsLogger implementation, and the calling-chain is:
     * <pre>
     *     A -> LoggerImpl -> buildLog
     * </pre>
     * In this case the {@code stackTraceOffset} is 2.
     *
     * @param msg              given message array
     * @param stackTraceOffset offset between the point where log operation occurs and this method
     */
    default LogInfo buildLog(Object[] msg, int stackTraceOffset) {
        return new LogInfo() {

            private final Level level = FsLogger.this.getLevel();
            private final Instant time = Instant.now();
            private final @Nullable StackTraceElement stackTrace = getStackTrace();
            private final Object[] messages = msg.clone();

            @Nullable
            private StackTraceElement getStackTrace() {
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                if (FsArray.isEmpty(stackTraceElements)) {
                    return null;
                }
                for (int i = 0; i < stackTraceElements.length; i++) {
                    StackTraceElement element = stackTraceElements[i];
                    if (Objects.equals(element.getClassName(), FsLogger.class.getName())
                        && Objects.equals(element.getMethodName(), "buildLog")
                        && i + stackTraceOffset < stackTraceElements.length) {
                        return stackTraceElements[i + stackTraceOffset];
                    }
                }
                return null;
            }

            @Override
            public Level level() {
                return level;
            }

            @Override
            public Instant time() {
                return time;
            }

            @Override
            public @Nullable StackTraceElement stackTrace() {
                return stackTrace;
            }

            @Override
            public Object[] messages() {
                return messages;
            }
        };
    }

    /**
     * Denotes log info, including current stack trace info.
     */
    interface LogInfo {

        /**
         * Returns level of this log.
         */
        Level level();

        /**
         * Returns record time of this log.
         */
        Instant time();

        /**
         * Returns stack trace info of this log.
         */
        @Nullable
        StackTraceElement stackTrace();

        /**
         * Returns message array of this log, each element represents a message to be logged.
         */
        Object[] messages();
    }

    /**
     * Denotes log level.
     */
    enum Level {

        /**
         * Trace level, value: 1000.
         */
        TRACE(1000, "TRACE"),
        /**
         * Debug level, value: 2000.
         */
        DEBUG(2000, "DEBUG"),
        /**
         * Info level, value: 3000.
         */
        INFO(3000, "INFO"),
        /**
         * Warn level, value: 4000.
         */
        WARN(4000, "WARN"),
        /**
         * Error level, value: 5000.
         */
        ERROR(5000, "ERROR"),
        ;

        private final int value;
        private final String description;

        Level(int value, String description) {
            this.value = value;
            this.description = description;
        }

        /**
         * Returns value of this level (2000, 3000...).
         */
        public int value() {
            return value;
        }

        /**
         * Returns description of this level (such as "DEBUG", "INFO"...).
         */
        public String description() {
            return description;
        }
    }
}
