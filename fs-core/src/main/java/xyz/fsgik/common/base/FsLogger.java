package xyz.fsgik.common.base;

import xyz.fsgik.annotations.Nullable;

import java.time.Instant;
import java.util.Date;

/**
 * Logging for fs.
 *
 * @author fredsuvn
 */
public interface FsLogger {

    /**
     * Returns default logger, which using {@link System#out} to output,
     * and its level is {@link Level#INFO}.
     */
    static FsLogger defaultLogger() {
        return Builder.DEFAULT;
    }

    /**
     * Returns a new logger with given level and default output: ({@link System#out}).
     *
     * @param level given level
     */
    static FsLogger ofLevel(Level level) {
        return newBuilder().level(level).build();
    }

    /**
     * Returns new logger builder.
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Logs message in level of {@link Level#TRACE}.
     */
    default void trace(Object... messages) {
        if (getLevel().value() > Level.TRACE.value()) {
            return;
        }
        LogInfo logInfo = buildLog(messages, 1);
        writeLog(logInfo);
    }

    /**
     * Logs message in level of {@link Level#DEBUG}.
     */
    default void debug(Object... messages) {
        if (getLevel().value() > Level.DEBUG.value()) {
            return;
        }
        LogInfo logInfo = buildLog(messages, 1);
        writeLog(logInfo);
    }

    /**
     * Logs message in level of {@link Level#INFO}.
     */
    default void info(Object... messages) {
        if (getLevel().value() > Level.INFO.value()) {
            return;
        }
        LogInfo logInfo = buildLog(messages, 1);
        writeLog(logInfo);
    }

    /**
     * Logs message in level of {@link Level#WARN}.
     */
    default void warn(Object... messages) {
        if (getLevel().value() > Level.WARN.value()) {
            return;
        }
        LogInfo logInfo = buildLog(messages, 1);
        writeLog(logInfo);
    }

    /**
     * Logs message in level of {@link Level#ERROR}.
     */
    default void error(Object... messages) {
        if (getLevel().value() > Level.ERROR.value()) {
            return;
        }
        LogInfo logInfo = buildLog(messages, 1);
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
    void writeLog(LogInfo logInfo);

    /**
     * Builds log info with given message array
     * and offset between the log method and this method.
     * For example, calling chain from the default method {@link #info(Object...)} to this method is:
     * <pre>
     *     info(Object... messages) -> buildLog(Object[] msg, int stackTraceOffset)
     * </pre>
     * In this case the {@code stackTraceOffset} is 1. And the default implementation of {@link #info(Object...)} is:
     * <pre>
     *     default void info(Object... messages) {
     *         if (getLevel().value() > Level.INFO.value()) {
     *             return;
     *         }
     *         LogInfo logInfo = buildLog(messages, 1);
     *         writeLog(logInfo);
     *     }
     * </pre>
     *
     * @param msg              given message array
     * @param stackTraceOffset offset between the point where log operation occurs and this method
     */
    default LogInfo buildLog(Object[] msg, int stackTraceOffset) {
        return new LogInfo() {

            private final Level level = FsLogger.this.getLevel();
            private final Instant time = Instant.now();
            private final @Nullable StackTraceElement stackTrace =
                FsTrace.findCallerStackTrace(FsLogger.class.getName(), "buildLog", stackTraceOffset);
            private final Object[] messages = msg.clone();

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
     * Formatter to format output log into underlying device.
     */
    interface Formatter {
        /**
         * Formats and output given log info.
         *
         * @param logInfo given log info
         */
        void format(LogInfo logInfo);
    }

    /**
     * Builder for {@link FsLogger}.
     */
    class Builder {

        private static final FsLogger DEFAULT = new Builder().build();

        private Level level = Level.INFO;
        private Formatter formatter = logInfo -> {
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
        };

        /**
         * Sets level, default is {@link Level#INFO}.
         *
         * @param level log level
         */
        public Builder level(Level level) {
            this.level = level;
            return this;
        }

        /**
         * Sets formatter, default formatter will format and write into {@link System#out}.
         *
         * @param formatter log formatter
         */
        public Builder formatter(Formatter formatter) {
            this.formatter = formatter;
            return this;
        }

        public FsLogger build() {
            return new FsLogger() {

                private final Level level = Builder.this.level;
                private final Formatter formatter = Builder.this.formatter;

                @Override
                public Level getLevel() {
                    return level;
                }

                @Override
                public void writeLog(LogInfo logInfo) {
                    formatter.format(logInfo);
                }
            };
        }
    }
}
