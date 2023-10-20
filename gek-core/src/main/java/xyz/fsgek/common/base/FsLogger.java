package xyz.fsgek.common.base;

import xyz.fsgek.annotations.Nullable;

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
     *
     * @return default logger
     */
    static FsLogger defaultLogger() {
        return Builder.DEFAULT;
    }

    /**
     * Returns a new logger with given level and default output: ({@link System#out}).
     *
     * @param level given level
     * @return new logger with given level
     */
    static FsLogger ofLevel(Level level) {
        return newBuilder().level(level).build();
    }

    /**
     * Returns new logger builder.
     *
     * @return new logger builder
     */
    static Builder newBuilder() {
        return new Builder();
    }

    /**
     * Logs message in level of {@link Level#TRACE}.
     *
     * @param messages messages
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
     *
     * @param messages messages
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
     *
     * @param messages messages
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
     *
     * @param messages messages
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
     *
     * @param messages messages
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
     *
     * @return level of this log
     */
    Level getLevel();

    /**
     * Writes given log info.
     *
     * @param logInfo given log info
     */
    void writeLog(LogInfo logInfo);

    /**
     * Builds log info with given message array and trace offset between the log method and this method.
     * For example, calling chain from the default method {@link #info(Object...)} to this method is:
     * <pre>
     *     info(Object... messages) -&gt; buildLog(Object[] msg, int stackTraceOffset)
     * </pre>
     * In this case the {@code stackTraceOffset} is 1. And the default implementation of {@link #info(Object...)} is:
     * <pre>
     *     default void info(Object... messages) {
     *         if (getLevel().value() &gt; Level.INFO.value()) {
     *             return;
     *         }
     *         LogInfo logInfo = buildLog(messages, 1);
     *         writeLog(logInfo);
     *     }
     * </pre>
     *
     * @param msg              given message array
     * @param stackTraceOffset offset between the point where log operation occurs and this method
     * @return og info with given message array and trace offset between the log method and this method
     * @see FsTrace#findCallerStackTrace(String, String, int)
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
         *
         * @return value of this level
         */
        public int value() {
            return value;
        }

        /**
         * Returns description of this level (such as "DEBUG", "INFO"...).
         *
         * @return description of this level
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
         *
         * @return level of this log
         */
        Level level();

        /**
         * Returns record time of this log.
         *
         * @return record time of this log
         */
        Instant time();

        /**
         * Returns trace info of this log.
         *
         * @return trace info of this log or null
         */
        @Nullable
        StackTraceElement stackTrace();

        /**
         * Returns message array of this log, each element represents a message to be logged.
         *
         * @return message array of this log
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
         * @return this builder
         */
        public Builder level(Level level) {
            this.level = level;
            return this;
        }

        /**
         * Sets formatter, default formatter will format and write into {@link System#out}.
         *
         * @param formatter log formatter
         * @return this builder
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
