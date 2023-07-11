package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsDate;
import xyz.srclab.common.convert.FsConvertHandler;
import xyz.srclab.common.convert.FsConverter;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Convert handler implementation supports converting object to date or date to object.
 * These types are supported for date converting:
 * <ul>
 *     <li>{@link String}</li>
 *     <li>{@link StringBuilder}</li>
 *     <li>{@link StringBuffer}</li>
 *     <li>long and {@link Long} (as milliseconds)</li>
 *     <li>{@link Date}</li>
 *     <li>{@link Instant}</li>
 *     <li>{@link LocalDateTime}</li>
 *     <li>{@link OffsetDateTime}</li>
 *     <li>{@link ZonedDateTime}</li>
 * </ul>
 * These types are supported for duration converting:
 * <ul>
 *     <li>{@link String}</li>
 *     <li>{@link StringBuilder}</li>
 *     <li>{@link StringBuffer}</li>
 *     <li>long and {@link Long} (as milliseconds)</li>
 *     <li>{@link Duration}</li>
 * </ul>
 * These types are supported for zone converting:
 * <ul>
 *     <li>{@link String}</li>
 *     <li>{@link StringBuilder}</li>
 *     <li>{@link StringBuffer}</li>
 *     <li>{@link TimeZone}</li>
 *     <li>{@link ZoneId}</li>
 *     <li>{@link ZoneOffset}</li>
 * </ul>
 * Pattern of this handler can be assigned from constructors:
 * <ul>
 *     <li>{@link #DateConvertHandler()} (default);</li>
 *     <li>{@link #DateConvertHandler(CharSequence)};</li>
 *     <li>{@link #DateConvertHandler(PatternFunction)};</li>
 * </ul>
 * <p>
 * Note if the {@code obj} is null, return {@link #NOT_SUPPORTED}.
 *
 * @author fredsuvn
 */
public class DateConvertHandler implements FsConvertHandler {

    private static PatternFunction DEFAULT_PATTERN_FUNCTION = new PatternFunction() {

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
    public @Nullable Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter) {
        try {
            return convert0(obj, fromType, targetType, converter);
        } catch (ParseException e) {
            throw new IllegalStateException(e);
        }
    }

    public @Nullable Object convert0(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter)
        throws ParseException {
        if (obj == null) {
            return NOT_SUPPORTED;
        }
        if (Objects.equals(targetType, String.class)) {
            Date date = tryDate(obj, fromType);
            if (date != null) {
                return pattern.getDateFormat().format(date);
            }
            Instant instant = tryInstant(obj, fromType);
            if (instant != null) {
                return pattern.getFormatter().format(instant);
            }
            LocalDateTime localDateTime = tryLocalDateTime(obj, fromType);
            if (localDateTime != null) {
                return pattern.getFormatter().format(localDateTime);
            }
            ZonedDateTime zonedDateTime = tryZonedDateTime(obj, fromType);
            if (zonedDateTime != null) {
                return pattern.getFormatter().format(zonedDateTime);
            }
            OffsetDateTime offsetDateTime = tryOffsetDateTime(obj, fromType);
            if (offsetDateTime != null) {
                return pattern.getFormatter().format(offsetDateTime);
            }
            if (Objects.equals(Duration.class, fromType)
                || Objects.equals(TimeZone.class, fromType)
                || Objects.equals(ZoneId.class, fromType)
                || Objects.equals(ZoneOffset.class, fromType)) {
                return obj.toString();
            }
            return NOT_SUPPORTED;
        } else if (Objects.equals(targetType, StringBuilder.class)) {
            Object result = convert0(obj, fromType, String.class, converter);
            if (result != null && Objects.equals(result.getClass(), String.class)) {
                return new StringBuilder(result.toString());
            }
        } else if (Objects.equals(targetType, StringBuffer.class)) {
            Object result = convert0(obj, fromType, String.class, converter);
            if (result != null && Objects.equals(result.getClass(), String.class)) {
                return new StringBuffer(result.toString());
            }
        } else if (Objects.equals(targetType, Date.class)) {
            String str = tryString(obj, fromType);
            if (str != null) {
                return pattern.getDateFormat(str).parse(str);
            }
            Long l = tryLong(obj, fromType);
            if (l != null) {
                return new Date(l);
            }
            Instant instant = tryInstant(obj, fromType);
            if (instant != null) {
                return Date.from(instant);
            }
            LocalDateTime localDateTime = tryLocalDateTime(obj, fromType);
            if (localDateTime != null) {
                return Date.from(localDateTime.toInstant(FsDate.currentZoneOffset()));
            }
            ZonedDateTime zonedDateTime = tryZonedDateTime(obj, fromType);
            if (zonedDateTime != null) {
                return Date.from(zonedDateTime.toInstant());
            }
            OffsetDateTime offsetDateTime = tryOffsetDateTime(obj, fromType);
            if (offsetDateTime != null) {
                return Date.from(offsetDateTime.toInstant());
            }
            return NOT_SUPPORTED;
        } else if (Objects.equals(targetType, Instant.class)) {
            String str = tryString(obj, fromType);
            if (str != null) {
                return FsDate.toInstant(pattern.getFormatter(str).parse(str));
            }
            Long l = tryLong(obj, fromType);
            if (l != null) {
                return Instant.ofEpochMilli(l);
            }
            Date date = tryDate(obj, fromType);
            if (date != null) {
                return date.toInstant();
            }
            LocalDateTime localDateTime = tryLocalDateTime(obj, fromType);
            if (localDateTime != null) {
                return localDateTime.toInstant(FsDate.currentZoneOffset());
            }
            ZonedDateTime zonedDateTime = tryZonedDateTime(obj, fromType);
            if (zonedDateTime != null) {
                return zonedDateTime.toInstant();
            }
            OffsetDateTime offsetDateTime = tryOffsetDateTime(obj, fromType);
            if (offsetDateTime != null) {
                return offsetDateTime.toInstant();
            }
            return NOT_SUPPORTED;
        } else if (Objects.equals(targetType, LocalDateTime.class)) {
            String str = tryString(obj, fromType);
            if (str != null) {
                return LocalDateTime.from(pattern.getFormatter(str).parse(str));
            }
            Long l = tryLong(obj, fromType);
            if (l != null) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());
            }
            Date date = tryDate(obj, fromType);
            if (date != null) {
                return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            }
            Instant instant = tryInstant(obj, fromType);
            if (instant != null) {
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            ZonedDateTime zonedDateTime = tryZonedDateTime(obj, fromType);
            if (zonedDateTime != null) {
                return zonedDateTime.toLocalDateTime();
            }
            OffsetDateTime offsetDateTime = tryOffsetDateTime(obj, fromType);
            if (offsetDateTime != null) {
                return offsetDateTime.toLocalDateTime();
            }
            return NOT_SUPPORTED;
        } else if (Objects.equals(targetType, ZonedDateTime.class)) {
            String str = tryString(obj, fromType);
            if (str != null) {
                return ZonedDateTime.from(pattern.getFormatter(str).parse(str));
            }
            Long l = tryLong(obj, fromType);
            if (l != null) {
                return ZonedDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());
            }
            Date date = tryDate(obj, fromType);
            if (date != null) {
                return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            }
            Instant instant = tryInstant(obj, fromType);
            if (instant != null) {
                return ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            LocalDateTime localDateTime = tryLocalDateTime(obj, fromType);
            if (localDateTime != null) {
                return localDateTime.atZone(ZoneId.systemDefault());
            }
            OffsetDateTime offsetDateTime = tryOffsetDateTime(obj, fromType);
            if (offsetDateTime != null) {
                return offsetDateTime.toZonedDateTime();
            }
            return NOT_SUPPORTED;
        } else if (Objects.equals(targetType, OffsetDateTime.class)) {
            String str = tryString(obj, fromType);
            if (str != null) {
                return OffsetDateTime.from(pattern.getFormatter(str).parse(str));
            }
            Long l = tryLong(obj, fromType);
            if (l != null) {
                return OffsetDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());
            }
            Date date = tryDate(obj, fromType);
            if (date != null) {
                return OffsetDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            }
            Instant instant = tryInstant(obj, fromType);
            if (instant != null) {
                return OffsetDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
            LocalDateTime localDateTime = tryLocalDateTime(obj, fromType);
            if (localDateTime != null) {
                return localDateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime();
            }
            ZonedDateTime zonedDateTime = tryZonedDateTime(obj, fromType);
            if (zonedDateTime != null) {
                return zonedDateTime.toOffsetDateTime();
            }
            return NOT_SUPPORTED;
        } else if (Objects.equals(targetType, Duration.class)) {
            String str = tryString(obj, fromType);
            if (str != null) {
                return Duration.parse(str);
            }
            Long l = tryLong(obj, fromType);
            if (l != null) {
                return Duration.ofMillis(l);
            }
            return NOT_SUPPORTED;
        } else if (Objects.equals(targetType, TimeZone.class)) {
            String str = tryString(obj, fromType);
            if (str != null) {
                return TimeZone.getTimeZone(str);
            }
            ZoneId zoneId = tryZoneId(obj, fromType);
            if (zoneId != null) {
                return TimeZone.getTimeZone(zoneId);
            }
            ZoneOffset zoneOffset = tryZoneOffset(obj, fromType);
            if (zoneOffset != null) {
                return TimeZone.getTimeZone(zoneOffset);
            }
            return NOT_SUPPORTED;
        } else if (Objects.equals(targetType, ZoneId.class)) {
            String str = tryString(obj, fromType);
            if (str != null) {
                return ZoneId.of(str);
            }
            TimeZone timeZone = tryTimeZone(obj, fromType);
            if (timeZone != null) {
                return timeZone.toZoneId();
            }
            ZoneOffset zoneOffset = tryZoneOffset(obj, fromType);
            if (zoneOffset != null) {
                return zoneOffset;
            }
            return NOT_SUPPORTED;
        } else if (Objects.equals(targetType, ZoneOffset.class)) {
            String str = tryString(obj, fromType);
            if (str != null) {
                return ZoneOffset.of(str);
            }
            TimeZone timeZone = tryTimeZone(obj, fromType);
            if (timeZone != null) {
                return FsDate.toZoneOffset(timeZone.toZoneId());
            }
            ZoneId zoneId = tryZoneId(obj, fromType);
            if (zoneId != null) {
                return FsDate.toZoneOffset(zoneId);
            }
            return NOT_SUPPORTED;
        }
        return NOT_SUPPORTED;
    }

    @Nullable
    private String tryString(Object obj, Type type) {
        if (Objects.equals(type, String.class)
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
     * Function to parse date string to pattern and get date formatter.
     */
    public interface PatternFunction {

        /**
         * Returns DateFormat.
         */
        DateFormat getDateFormat();

        /**
         * Returns DateTimeFormatter.
         */
        DateTimeFormatter getFormatter();

        /**
         * Returns DateFormat with given date string (<b>NOT PATTERN STRING</b>)
         *
         * @param date date string
         */
        DateFormat getDateFormat(CharSequence date);

        /**
         * Returns DateTimeFormatter with given date string (<b>NOT PATTERN STRING</b>)
         *
         * @param date date string
         */
        DateTimeFormatter getFormatter(CharSequence date);
    }
}
