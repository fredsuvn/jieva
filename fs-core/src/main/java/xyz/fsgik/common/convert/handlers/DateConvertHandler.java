package xyz.fsgik.common.convert.handlers;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.common.base.FsDate;
import xyz.fsgik.common.convert.FsConvertException;
import xyz.fsgik.common.convert.FsConverter;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Convert handler implementation which is used to support conversion of date, duration and zone types.
 * <p>
 * Supported conversion of date types:
 * <ul>
 *     <li>{@link String};</li>
 *     <li>{@link StringBuilder};</li>
 *     <li>{@link StringBuffer};</li>
 *     <li>long and {@link Long} (as milliseconds);</li>
 *     <li>{@link Date};</li>
 *     <li>{@link Instant};</li>
 *     <li>{@link LocalDateTime};</li>
 *     <li>{@link OffsetDateTime};</li>
 *     <li>{@link ZonedDateTime};</li>
 * </ul>
 * Supported conversion of duration types:
 * <ul>
 *     <li>{@link String};</li>
 *     <li>{@link StringBuilder};</li>
 *     <li>{@link StringBuffer};</li>
 *     <li>long and {@link Long} (as milliseconds);</li>
 *     <li>{@link Duration};</li>
 * </ul>
 * Supported conversion of zone types:
 * <ul>
 *     <li>{@link String};</li>
 *     <li>{@link StringBuilder};</li>
 *     <li>{@link StringBuffer};</li>
 *     <li>{@link TimeZone};</li>
 *     <li>{@link ZoneId};</li>
 *     <li>{@link ZoneOffset};</li>
 * </ul>
 * Pattern of this handler can be assigned from constructors:
 * <ul>
 *     <li>{@link #DateConvertHandler()} (default);</li>
 *     <li>{@link #DateConvertHandler(CharSequence)};</li>
 *     <li>{@link #DateConvertHandler(PatternFunction)};</li>
 * </ul>
 * <p>
 * Note if the {@code obj} is null, return {@code null}.
 *
 * @author fredsuvn
 */
public class DateConvertHandler implements FsConverter.Handler {

    /**
     * An instance with {@link #DateConvertHandler()}.
     */
    public static final DateConvertHandler INSTANCE = new DateConvertHandler();

    private static final PatternFunction DEFAULT_PATTERN_FUNCTION = new PatternFunction() {

        @Override
        public DateFormat getDateFormat() {
            return FsDate.toDateFormat(FsDate.PATTERN);
        }

        @Override
        public DateTimeFormatter getFormatter() {
            return FsDate.FORMATTER;
        }

        @Override
        public DateFormat getDateFormat(CharSequence date) {
            String pattern = FsDate.toPattern(date);
            if (pattern == null) {
                throw new IllegalArgumentException("Unknown date pattern for string: " + date + ".");
            }
            return FsDate.toDateFormat(pattern);
        }

        @Override
        public DateTimeFormatter getFormatter(CharSequence date) {
            String pattern = FsDate.toPattern(date);
            if (pattern == null) {
                throw new IllegalArgumentException("Unknown date pattern for string: " + date + ".");
            }
            return FsDate.toFormatter(pattern);
        }
    };

    private final PatternFunction pattern;

    /**
     * Constructs with default pattern function,
     * the default function of which pattern from {@link FsDate#PATTERN} and {@link FsDate#toPattern(CharSequence)}.
     */
    public DateConvertHandler() {
        this(DEFAULT_PATTERN_FUNCTION);
    }

    /**
     * Constructs with given pattern.
     *
     * @param pattern given pattern
     */
    public DateConvertHandler(CharSequence pattern) {
        this.pattern = new PatternFunction() {

            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern.toString());

            @Override
            public DateFormat getDateFormat() {
                return FsDate.toDateFormat(pattern);
            }

            @Override
            public DateTimeFormatter getFormatter() {
                return dateTimeFormatter;
            }

            @Override
            public DateFormat getDateFormat(CharSequence date) {
                return getDateFormat();
            }

            @Override
            public DateTimeFormatter getFormatter(CharSequence date) {
                return getFormatter();
            }
        };
    }

    /**
     * Constructs with given pattern function.
     *
     * @param pattern given pattern function
     */
    public DateConvertHandler(PatternFunction pattern) {
        this.pattern = pattern;
    }

    @Override
    public @Nullable Object convert(@Nullable Object source, Type sourceType, Type targetType, FsConverter converter) {
        try {
            return convert0(source, sourceType, targetType);
        } catch (ParseException e) {
            throw new FsConvertException(e);
        }
    }

    private @Nullable Object convert0(@Nullable Object source, Type sourceType, Type targetType) throws ParseException {
        if (source == null) {
            return null;
        }
        if (Objects.equals(targetType, String.class)) {
            Date date = tryDate(source, sourceType);
            if (date != null) {
                return pattern.getDateFormat().format(date);
            }
            Instant instant = tryInstant(source, sourceType);
            if (instant != null) {
                return pattern.getFormatter().format(instant);
            }
            LocalDateTime localDateTime = tryLocalDateTime(source, sourceType);
            if (localDateTime != null) {
                return pattern.getFormatter().format(localDateTime);
            }
            ZonedDateTime zonedDateTime = tryZonedDateTime(source, sourceType);
            if (zonedDateTime != null) {
                return pattern.getFormatter().format(zonedDateTime);
            }
            OffsetDateTime offsetDateTime = tryOffsetDateTime(source, sourceType);
            if (offsetDateTime != null) {
                return pattern.getFormatter().format(offsetDateTime);
            }
            if (Objects.equals(Duration.class, sourceType)
                || Objects.equals(TimeZone.class, sourceType)
                || Objects.equals(ZoneId.class, sourceType)
                || Objects.equals(ZoneOffset.class, sourceType)) {
                return source.toString();
            }
            return null;
        } else if (Objects.equals(targetType, StringBuilder.class)) {
            Object result = convert0(source, sourceType, String.class);
            if (result != null && Objects.equals(result.getClass(), String.class)) {
                return new StringBuilder(result.toString());
            }
            return null;
        } else if (Objects.equals(targetType, StringBuffer.class)) {
            Object result = convert0(source, sourceType, String.class);
            if (result != null && Objects.equals(result.getClass(), String.class)) {
                return new StringBuffer(result.toString());
            }
            return null;
        } else if (Objects.equals(targetType, Date.class)) {
            String str = tryString(source, sourceType);
            if (str != null) {
                return pattern.getDateFormat(str).parse(str);
            }
            Long l = tryLong(source, sourceType);
            if (l != null) {
                return new Date(l);
            }
            Instant instant = tryInstant(source, sourceType);
            if (instant != null) {
                return Date.from(instant);
            }
            LocalDateTime localDateTime = tryLocalDateTime(source, sourceType);
            if (localDateTime != null) {
                return Date.from(localDateTime.toInstant(FsDate.currentZoneOffset()));
            }
            ZonedDateTime zonedDateTime = tryZonedDateTime(source, sourceType);
            if (zonedDateTime != null) {
                return Date.from(zonedDateTime.toInstant());
            }
            OffsetDateTime offsetDateTime = tryOffsetDateTime(source, sourceType);
            if (offsetDateTime != null) {
                return Date.from(offsetDateTime.toInstant());
            }
            return null;
        } else if (Objects.equals(targetType, Instant.class)) {
            String str = tryString(source, sourceType);
            if (str != null) {
                return FsDate.toInstant(pattern.getFormatter(str).parse(str));
            }
            Long l = tryLong(source, sourceType);
            if (l != null) {
                return Instant.ofEpochMilli(l);
            }
            Date date = tryDate(source, sourceType);
            if (date != null) {
                return date.toInstant();
            }
            LocalDateTime localDateTime = tryLocalDateTime(source, sourceType);
            if (localDateTime != null) {
                return localDateTime.toInstant(FsDate.currentZoneOffset());
            }
            ZonedDateTime zonedDateTime = tryZonedDateTime(source, sourceType);
            if (zonedDateTime != null) {
                return zonedDateTime.toInstant();
            }
            OffsetDateTime offsetDateTime = tryOffsetDateTime(source, sourceType);
            if (offsetDateTime != null) {
                return offsetDateTime.toInstant();
            }
            return null;
        } else if (Objects.equals(targetType, LocalDateTime.class)) {
            String str = tryString(source, sourceType);
            if (str != null) {
                return LocalDateTime.from(pattern.getFormatter(str).parse(str));
            }
            Long l = tryLong(source, sourceType);
            if (l != null) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());
            }
            Date date = tryDate(source, sourceType);
            if (date != null) {
                return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            }
            Instant instant = tryInstant(source, sourceType);
            if (instant != null) {
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            ZonedDateTime zonedDateTime = tryZonedDateTime(source, sourceType);
            if (zonedDateTime != null) {
                return zonedDateTime.toLocalDateTime();
            }
            OffsetDateTime offsetDateTime = tryOffsetDateTime(source, sourceType);
            if (offsetDateTime != null) {
                return offsetDateTime.toLocalDateTime();
            }
            return null;
        } else if (Objects.equals(targetType, ZonedDateTime.class)) {
            String str = tryString(source, sourceType);
            if (str != null) {
                return ZonedDateTime.from(pattern.getFormatter(str).parse(str));
            }
            Long l = tryLong(source, sourceType);
            if (l != null) {
                return ZonedDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());
            }
            Date date = tryDate(source, sourceType);
            if (date != null) {
                return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            }
            Instant instant = tryInstant(source, sourceType);
            if (instant != null) {
                return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            LocalDateTime localDateTime = tryLocalDateTime(source, sourceType);
            if (localDateTime != null) {
                return localDateTime.atZone(ZoneId.systemDefault());
            }
            OffsetDateTime offsetDateTime = tryOffsetDateTime(source, sourceType);
            if (offsetDateTime != null) {
                return offsetDateTime.toZonedDateTime();
            }
            return null;
        } else if (Objects.equals(targetType, OffsetDateTime.class)) {
            String str = tryString(source, sourceType);
            if (str != null) {
                return OffsetDateTime.from(pattern.getFormatter(str).parse(str));
            }
            Long l = tryLong(source, sourceType);
            if (l != null) {
                return OffsetDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());
            }
            Date date = tryDate(source, sourceType);
            if (date != null) {
                return OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            }
            Instant instant = tryInstant(source, sourceType);
            if (instant != null) {
                return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            LocalDateTime localDateTime = tryLocalDateTime(source, sourceType);
            if (localDateTime != null) {
                return localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
            }
            ZonedDateTime zonedDateTime = tryZonedDateTime(source, sourceType);
            if (zonedDateTime != null) {
                return zonedDateTime.toOffsetDateTime();
            }
            return null;
        } else if (Objects.equals(targetType, Duration.class)) {
            String str = tryString(source, sourceType);
            if (str != null) {
                return Duration.parse(str);
            }
            Long l = tryLong(source, sourceType);
            if (l != null) {
                return Duration.ofMillis(l);
            }
            return null;
        } else if (Objects.equals(targetType, TimeZone.class)) {
            String str = tryString(source, sourceType);
            if (str != null) {
                return TimeZone.getTimeZone(str);
            }
            ZoneId zoneId = tryZoneId(source, sourceType);
            if (zoneId != null) {
                return TimeZone.getTimeZone(zoneId);
            }
            ZoneOffset zoneOffset = tryZoneOffset(source, sourceType);
            if (zoneOffset != null) {
                return TimeZone.getTimeZone(zoneOffset);
            }
            return null;
        } else if (Objects.equals(targetType, ZoneId.class)) {
            String str = tryString(source, sourceType);
            if (str != null) {
                return ZoneId.of(str);
            }
            TimeZone timeZone = tryTimeZone(source, sourceType);
            if (timeZone != null) {
                return timeZone.toZoneId();
            }
            ZoneOffset zoneOffset = tryZoneOffset(source, sourceType);
            if (zoneOffset != null) {
                return zoneOffset;
            }
            return null;
        } else if (Objects.equals(targetType, ZoneOffset.class)) {
            String str = tryString(source, sourceType);
            if (str != null) {
                return ZoneOffset.of(str);
            }
            TimeZone timeZone = tryTimeZone(source, sourceType);
            if (timeZone != null) {
                return FsDate.toZoneOffset(timeZone.toZoneId());
            }
            ZoneId zoneId = tryZoneId(source, sourceType);
            if (zoneId != null) {
                return FsDate.toZoneOffset(zoneId);
            }
            return null;
        }
        return null;
    }

    @Nullable
    private String tryString(Object obj, Type type) {
        if (Objects.equals(type, String.class)
            || Objects.equals(type, CharSequence.class)
            || Objects.equals(type, StringBuilder.class)
            || Objects.equals(type, StringBuffer.class)) {
            return obj.toString();
        }
        return null;
    }

    @Nullable
    private Long tryLong(Object obj, Type type) {
        if (Objects.equals(type, Long.class) || Objects.equals(type, long.class)) {
            return (Long) obj;
        }
        return null;
    }

    @Nullable
    private Date tryDate(Object obj, Type type) {
        if (Objects.equals(type, Date.class)) {
            return (Date) obj;
        }
        return null;
    }

    @Nullable
    private Instant tryInstant(Object obj, Type type) {
        if (Objects.equals(type, Instant.class)) {
            return (Instant) obj;
        }
        return null;
    }

    @Nullable
    private LocalDateTime tryLocalDateTime(Object obj, Type type) {
        if (Objects.equals(type, LocalDateTime.class)) {
            return (LocalDateTime) obj;
        }
        return null;
    }

    @Nullable
    private OffsetDateTime tryOffsetDateTime(Object obj, Type type) {
        if (Objects.equals(type, OffsetDateTime.class)) {
            return (OffsetDateTime) obj;
        }
        return null;
    }

    @Nullable
    private ZonedDateTime tryZonedDateTime(Object obj, Type type) {
        if (Objects.equals(type, ZonedDateTime.class)) {
            return (ZonedDateTime) obj;
        }
        return null;
    }

    @Nullable
    private Duration tryDuration(Object obj, Type type) {
        if (Objects.equals(type, Duration.class)) {
            return (Duration) obj;
        }
        return null;
    }

    @Nullable
    private TimeZone tryTimeZone(Object obj, Type type) {
        if (Objects.equals(type, TimeZone.class)) {
            return (TimeZone) obj;
        }
        return null;
    }

    @Nullable
    private ZoneOffset tryZoneOffset(Object obj, Type type) {
        if (Objects.equals(type, ZoneOffset.class)) {
            return (ZoneOffset) obj;
        }
        return null;
    }

    @Nullable
    private ZoneId tryZoneId(Object obj, Type type) {
        if (Objects.equals(type, ZoneId.class)) {
            return (ZoneId) obj;
        }
        return null;
    }

    /**
     * Returns pattern function of this handler.
     *
     * @return pattern function of this handler
     */
    public PatternFunction getPattern() {
        return pattern;
    }

    /**
     * Function to parse date string to pattern and get date formatter.
     */
    public interface PatternFunction {

        /**
         * Returns {@link DateFormat}.
         *
         * @return {@link DateFormat}
         */
        DateFormat getDateFormat();

        /**
         * Returns {@link DateTimeFormatter}.
         *
         * @return {@link DateTimeFormatter}
         */
        DateTimeFormatter getFormatter();

        /**
         * Returns {@link DateFormat} with given date string (<b>NOT PATTERN STRING</b>)
         *
         * @param date date string
         * @return {@link DateFormat} with given date string
         */
        DateFormat getDateFormat(CharSequence date);

        /**
         * Returns {@link DateTimeFormatter} with given date string (<b>NOT PATTERN STRING</b>)
         *
         * @param date date string
         * @return {@link DateTimeFormatter} with given date string
         */
        DateTimeFormatter getFormatter(CharSequence date);
    }
}
