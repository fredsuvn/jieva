package xyz.fslabo.common.base;

import xyz.fslabo.annotations.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * Date utilities.
 *
 * @author fredsuvn
 */
public class JieDate {

    /**
     * Default date pattern: "yyyy-MM-dd HH:mm:ss.SSS".
     */
    public static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * Default date formatter of {@link #DEFAULT_PATTERN} with zone at {@link ZoneId#systemDefault()}.
     */
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter
        .ofPattern(DEFAULT_PATTERN).withZone(ZoneId.systemDefault());

    /**
     * Returns {@link DateFormat} of given pattern.
     *
     * @param pattern given pattern
     * @return {@link DateFormat} of given pattern
     */
    public static DateFormat dateFormat(CharSequence pattern) {
        return new SimpleDateFormat(pattern.toString());
    }

    /**
     * Formats given date with {@link #DEFAULT_PATTERN}.
     *
     * @param date given date
     * @return formatted string
     */
    public static String format(Date date) {
        return format(date, DEFAULT_PATTERN);
    }

    /**
     * Formats given date with given pattern.
     *
     * @param date    given date
     * @param pattern given pattern
     * @return formatted string
     */
    public static String format(Date date, CharSequence pattern) {
        return dateFormat(pattern).format(date);
    }

    /**
     * Parses given date string with {@link #DEFAULT_PATTERN}.
     *
     * @param date given date string
     * @return parsed date
     */
    public static Date parse(CharSequence date) {
        return parse(date, DEFAULT_PATTERN);
    }

    /**
     * Parses given date string with given pattern.
     *
     * @param date    given date string
     * @param pattern given pattern
     * @return parsed date
     */
    public static Date parse(CharSequence date, CharSequence pattern) {
        try {
            return dateFormat(pattern).parse(date.toString());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Formats given date with {@link #DEFAULT_PATTERN}.
     * If given date is null, return null.
     *
     * @param date given date
     * @return formatted string or null
     */
    @Nullable
    public static String toString(@Nullable Date date) {
        return toString(date, DEFAULT_PATTERN);
    }

    /**
     * Formats given date with given pattern.
     * If given date or pattern is null or empty, return null.
     *
     * @param date    given date
     * @param pattern given pattern
     * @return formatted string or null
     */
    @Nullable
    public static String toString(@Nullable Date date, @Nullable CharSequence pattern) {
        if (date == null || JieString.isEmpty(pattern)) {
            return null;
        }
        return format(date, pattern);
    }

    /**
     * Parses given date string with {@link #DEFAULT_PATTERN}.
     * If given date string is null, return null.
     *
     * @param date given date string
     * @return parsed date or null
     */
    @Nullable
    public static Date toDate(@Nullable CharSequence date) {
        return toDate(date, DEFAULT_PATTERN);
    }

    /**
     * Parses given date string with given pattern.
     * If given date string or pattern is null or empty, return null.
     *
     * @param date    given date string
     * @param pattern given pattern
     * @return parsed date or null
     */
    @Nullable
    public static Date toDate(@Nullable CharSequence date, @Nullable CharSequence pattern) {
        if (JieString.isEmpty(date) || JieString.isEmpty(pattern)) {
            return null;
        }
        return parse(date, pattern);
    }

    /**
     * Returns current zone offset.
     *
     * @return current zone offset
     */
    public static ZoneOffset zoneOffset() {
        ZoneId.systemDefault().getRules().isFixedOffset();
        return OffsetDateTime.now().getOffset();
    }

    /**
     * Returns zone offset from given zone id.
     *
     * @param zoneId given zone id
     * @return zone offset from given zone id
     */
    public static ZoneOffset toZoneOffset(ZoneId zoneId) {
        return zoneId.getRules().getOffset(Instant.now());
    }

    /**
     * Returns {@link Instant} from given temporal, or null if failed.
     *
     * @param temporal given temporal
     * @return {@link Instant} from given temporal, or null if failed
     */
    @Nullable
    public static Instant toInstant(TemporalAccessor temporal) {
        if (temporal instanceof Instant) {
            return (Instant) temporal;
        }
        long seconds;
        if (temporal.isSupported(ChronoField.INSTANT_SECONDS)) {
            seconds = temporal.getLong(ChronoField.INSTANT_SECONDS);
        } else {
            return null;
        }
        long nanos = 0;
        if (temporal.isSupported(ChronoField.NANO_OF_SECOND)) {
            nanos = temporal.getLong(ChronoField.NANO_OF_SECOND);
        } else if (temporal.isSupported(ChronoField.MICRO_OF_SECOND)) {
            nanos = temporal.getLong(ChronoField.MICRO_OF_SECOND) * 1000;
        } else if (temporal.isSupported(ChronoField.MILLI_OF_SECOND)) {
            nanos = temporal.getLong(ChronoField.MILLI_OF_SECOND) * 1000_000;
        }
        return Instant.ofEpochSecond(seconds, nanos);
    }

    /**
     * Returns {@link Instant} from given temporal with specified zone offset.
     *
     * @param temporal given temporal
     * @param offset specified zone offset
     * @return {@link Instant} from given temporal with specified zone offset.
     */
    public static Instant toInstant(TemporalAccessor temporal, ZoneOffset offset) {
        if (temporal instanceof Instant) {
            return (Instant) temporal;
        }
        if (temporal instanceof LocalDateTime) {
            return ((LocalDateTime) temporal).toInstant(offset);
        }
        if (temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).toLocalDateTime().toInstant(offset);
        }
        if (temporal instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).toLocalDateTime().toInstant(offset);
        }
        if (temporal instanceof LocalDate) {
            return LocalDateTime.of((LocalDate) temporal, LocalTime.MIN).toInstant(offset);
        }
        if (temporal instanceof LocalTime) {
            return LocalDateTime.of(LocalDate.MIN, (LocalTime) temporal).toInstant(offset);
        }
        return LocalDateTime.from(temporal).toInstant(offset);
    }
}
