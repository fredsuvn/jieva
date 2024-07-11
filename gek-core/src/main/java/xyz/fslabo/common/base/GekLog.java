package xyz.fslabo.common.base;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.cache.Cache;

import java.time.Instant;
import java.util.Date;

/**
 * Simple log system of Jie.
 * Overrides {@link #buildRecord(int, Level, Object...)} and {@link #writeRecord(Record)} can custom log actions.
 *
 * @author fredsuvn
 */
public abstract class GekLog {

    private static final Cache<Level, GekLog> cache = Cache.softCache();

    /**
     * Returns instance of {@link GekLog} with {@link Level#INFO}.
     * The output of returned log is ({@link System#out}).
     *
     * @return instance of {@link GekLog} with {@link Level#INFO}
     */
    public static GekLog getInstance() {
        return getInstance(Level.INFO);
    }

    /**
     * Returns instance of {@link GekLog} with specified level.
     * The output of returned log is ({@link System#out}).
     *
     * @param level specified level
     * @return instance of {@link GekLog} with specified level
     */
    public static GekLog getInstance(Level level) {
        return cache.compute(level, Impl::new);
    }

    private final Level level;

    protected GekLog() {
        this(Level.INFO);
    }

    protected GekLog(Level level) {
        this.level = level;
    }

    /**
     * Logs message with level of {@link Level#TRACE}.
     *
     * @param messages messages
     */
    public void trace(Object... messages) {
        log(Level.TRACE, messages);
    }

    /**
     * Logs message with level of {@link Level#DEBUG}.
     *
     * @param messages messages
     */
    public void debug(Object... messages) {
        log(Level.DEBUG, messages);
    }

    /**
     * Logs message with level of {@link Level#INFO}.
     *
     * @param messages messages
     */
    public void info(Object... messages) {
        log(Level.INFO, messages);
    }

    /**
     * Logs message with level of {@link Level#WARN}.
     *
     * @param messages messages
     */
    public void warn(Object... messages) {
        log(Level.WARN, messages);
    }

    /**
     * Logs message with level of {@link Level#ERROR}.
     *
     * @param messages messages
     */
    public void error(Object... messages) {
        log(Level.ERROR, messages);
    }

    private void log(Level level, Object... messages) {
        logOffset(level, 2, messages);
    }

    /**
     * Logs message with specified level and trace offset.
     * The trace offset specifies the offset from where logging occurs to this method,
     * see {@link #buildRecord(int, Level, Object...)} and {@link GekTrace#findCallerTrace(String, String, int)}.
     *
     * @param level    specified level
     * @param offset   trace offset
     * @param messages messages
     * @see #buildRecord(int, Level, Object...)
     * @see GekTrace#findCallerTrace(String, String, int)
     */
    public void logOffset(Level level, int offset, Object... messages) {
        if (level == null) {
            return;
        }
        if (!level.isGreaterOrEquals(getLevel())) {
            return;
        }
        Record record = buildRecord(offset, level, messages);
        writeRecord(record);
    }

    /**
     * Returns level of this log.
     *
     * @return level of this log
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Builds one log record of given message array, specified level and trace offset.
     * The trace offset specifies the offset from where logging occurs to this method,
     * see {@link GekTrace#findCallerTrace(String, String, int)}.
     *
     * @param traceOffset offset from where logging occurs to this method
     * @param level       record level
     * @param messages    given message array
     * @return one log record of given message array and specified level
     * @see GekTrace#findCallerTrace(String, String, int)
     */
    protected Record buildRecord(int traceOffset, Level level, Object... messages) {
        Instant now = Instant.now();
        Thread thread = Thread.currentThread();
        StackTraceElement trace = GekTrace.findCallerTrace(
            GekLog.class.getName(), "buildRecord", traceOffset);
        return new Record() {

            @Override
            public Level level() {
                return level;
            }

            @Override
            public Instant time() {
                return now;
            }

            @Override
            public @Nullable Thread thread() {
                return thread;
            }

            @Override
            public @Nullable StackTraceElement trace() {
                return trace;
            }

            @Override
            public Object[] messages() {
                return messages;
            }
        };
    }

    /**
     * Do write record.
     *
     * @param record log record
     */
    protected void writeRecord(Record record) {
        StringBuilder message = new StringBuilder();
        message.append(GekDate.format(Date.from(record.time())))
            .append("[")
            .append(record.level().description())
            .append("]");
        StackTraceElement trace = record.trace();
        if (trace != null) {
            message.append("@")
                .append(trace.getClassName())
                .append(".")
                .append(trace.getMethodName())
                .append("(").append(trace.getLineNumber()).append(")");
        }
        Thread thread = record.thread();
        if (thread != null) {
            message.append("-")
                .append("[")
                .append(thread.getName())
                .append("]: ");
        }
        for (Object o : record.messages()) {
            message.append(o);
        }
        System.out.println(message);
    }

    /**
     * Denotes log level.
     */
    public enum Level {

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
         * Returns description of this level (such as "DEBUG", "INFO"...).
         *
         * @return description of this level
         */
        public String description() {
            return description;
        }

        /**
         * Returns whether this level is greater than or equals to given other level,
         *
         * @param other given other level
         * @return whether this level is greater than or equals to given other level
         */
        public boolean isGreaterOrEquals(Level other) {
            return this.value >= other.value;
        }
    }

    /**
     * Represents one log record.
     */
    public interface Record {

        /**
         * Returns level of this log record.
         *
         * @return level of this log record
         */
        Level level();

        /**
         * Returns record time of this log record.
         *
         * @return record time of this log record
         */
        Instant time();

        /**
         * Returns thread where the logging occurs.
         *
         * @return thread where the logging occurs
         */
        @Nullable
        Thread thread();

        /**
         * Returns stack trace info where the logging occurs.
         *
         * @return stack trace info where the logging occurs
         */
        @Nullable
        StackTraceElement trace();

        /**
         * Returns message array of this log record, each element represents a part of record info, concatenates them as
         * string can obtain full log record string.
         *
         * @return message array of this log record
         */
        Object[] messages();
    }

    private static final class Impl extends GekLog {
        private Impl(Level level) {
            super(level);
        }
    }
}
