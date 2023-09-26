package xyz.fsgik.common.base;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.common.cache.FsCache;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * Date utilities.
 *
 * @author fredsuvn
 */
public class FsDate {

    /**
     * Default date pattern: "yyyy-MM-dd HH:mm:ss.SSS".
     */
    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * Default date pattern: "yyyy-MM-dd HH:mm:ss".
     */
    public static final String PATTERN_SECOND = "yyyy-MM-dd HH:mm:ss";

    /**
     * Default date pattern with offset: "yyyy-MM-dd HH:mm:ss.SSS ZZZ".
     */
    public static final String PATTERN_OFFSET = "yyyy-MM-dd HH:mm:ss.SSS ZZZ";

    /**
     * Default date formatter of {@link #PATTERN}.
     */
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    /**
     * Default date formatter of {@link #PATTERN_SECOND}.
     */
    public static final DateTimeFormatter FORMATTER_SECOND = DateTimeFormatter.ofPattern(PATTERN_SECOND);

    /**
     * Default date formatter of {@link #PATTERN_OFFSET}.
     */
    public static final DateTimeFormatter FORMATTER_OFFSET = DateTimeFormatter.ofPattern(PATTERN_OFFSET);

    private static final FsCache<CharSequence, DateTimeFormatter> FORMATTER_CACHE = FsCache.softCache();

    /**
     * Returns DateFormat of given pattern.
     *
     * @param pattern given pattern
     */
    public static DateFormat toDateFormat(CharSequence pattern) {
        return new SimpleDateFormat(pattern.toString());
    }

    /**
     * Returns DateTimeFormatter of given pattern.
     *
     * @param pattern given pattern
     */
    public static DateTimeFormatter toFormatter(CharSequence pattern) {
        if (FsString.charEquals(PATTERN, pattern)) {
            return FORMATTER;
        } else if (FsString.charEquals(PATTERN_SECOND, pattern)) {
            return FORMATTER_SECOND;
        } else if (FsString.charEquals(PATTERN_OFFSET, pattern)) {
            return FORMATTER_OFFSET;
        } else {
            return FORMATTER_CACHE.get(pattern, p -> DateTimeFormatter.ofPattern(p.toString()));
        }
    }

    /**
     * Formats given date with given pattern.
     *
     * @param date    given date
     * @param pattern given pattern
     */
    public static String format(Date date, CharSequence pattern) {
        return toDateFormat(pattern).format(date);
    }

    /**
     * Formats given date with {@link #PATTERN}.
     *
     * @param date given date
     */
    public static String format(Date date) {
        return format(date, PATTERN);
    }

    /**
     * Parses given date string with given pattern.
     *
     * @param date    given date string
     * @param pattern given pattern
     */
    public static Date parse(CharSequence date, CharSequence pattern) {
        try {
            return toDateFormat(pattern).parse(date.toString());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Parses given date string with {@link #PATTERN}.
     *
     * @param date given date string
     */
    public static Date parse(CharSequence date) {
        return parse(date, PATTERN);
    }

    /**
     * Formats given date with given pattern.
     * If given date or pattern is null or empty, return null.
     *
     * @param date    given date
     * @param pattern given pattern
     */
    @Nullable
    public static String toString(@Nullable Date date, @Nullable CharSequence pattern) {
        if (date == null || FsString.isEmpty(pattern)) {
            return null;
        }
        return format(date, pattern);
    }

    /**
     * Formats given date with {@link #PATTERN}.
     * If given date is null, return null.
     *
     * @param date given date
     */
    @Nullable
    public static String toString(@Nullable Date date) {
        return toString(date, PATTERN);
    }

    /**
     * Parses given date string with given pattern.
     * If given date string or pattern is null or empty, return null.
     *
     * @param date    given date string
     * @param pattern given pattern
     */
    @Nullable
    public static Date toDate(@Nullable CharSequence date, @Nullable CharSequence pattern) {
        if (FsString.isEmpty(date) || FsString.isEmpty(pattern)) {
            return null;
        }
        return parse(date, pattern);
    }

    /**
     * Parses given date string with {@link #PATTERN}.
     * If given date string is null, return null.
     *
     * @param date given date string
     */
    @Nullable
    public static Date toDate(@Nullable CharSequence date) {
        return toDate(date, PATTERN);
    }

    /**
     * Returns current zone offset.
     */
    public static ZoneOffset currentZoneOffset() {
        return ZonedDateTime.now().getOffset();
    }

    /**
     * Returns zone offset from given zone id.
     *
     * @param zoneId given zone id
     */
    public static ZoneOffset toZoneOffset(ZoneId zoneId) {
        return zoneId.getRules().getOffset(Instant.now());
    }

    /**
     * Guesses and returns pattern of given date string.
     * It can match these pattern:
     * <ul>
     *     <li>20111203: yyyyMMdd</li>
     *     <li>10:15:30: HH:mm:ss</li>
     *     <li>2011-12-03: yyyy-MM-dd</li>
     *     <li>2011-12-03+01:00: yyyy-MM-ddZZZZZ</li>
     *     <li>2011-12-03 +0100: yyyy-MM-dd ZZZ</li>
     *     <li>10:15: HH:mm</li>
     *     <li>10:15:30.500: HH:mm:ss.SSS</li>
     *     <li>10:15:30+01:00: HH:mm:ssZZZZZ</li>
     *     <li>10:15:30 +0100: HH:mm:ss ZZZ</li>
     *     <li>2011-12-03T10:15:30: yyyy-MM-dd'T'HH:mm:ss</li>
     *     <li>2011-12-03 10:15:30: {@link #PATTERN_SECOND}</li>
     *     <li>2011-12-03T10:15:30+01:00: yyyy-MM-dd'T'HH:mm:ssZZZZZ</li>
     *     <li>2011-12-03T10:15:30 +0100: yyyy-MM-dd'T'HH:mm:ss ZZZ</li>
     *     <li>2011-12-03 10:15:30 +0100: yyyy-MM-dd HH:mm:ss ZZZ</li>
     *     <li>2011-12-03T10:15:30Z: yyyy-MM-dd'T'HH:mm:ssX</li>
     *     <li>2011-12-03 10:15:30.500: {@link #PATTERN}</li>
     *     <li>2011-12-03 10:15:30.500 +0100: {@link #PATTERN_OFFSET}</li>
     * </ul>
     * Return null if no matched pattern.
     *
     * @param date date string
     */
    @Nullable
    public static String toPattern(CharSequence date) {
        switch (date.length()) {
            //20111203, 10:15:30
            case 8:
                if (date.charAt(2) == ':' && date.charAt(5) == ':') {
                    return "HH:mm:ss";
                }
                return "yyyyMMdd";
            //2011-12-03
            case 10:
                return "yyyy-MM-dd";
            //2011-12-03+01:00 / 2011-12-03 +0100
            case 16:
                if (date.charAt(10) == ' ') {
                    return "yyyy-MM-dd ZZZ";
                }
                return "yyyy-MM-ddZZZZZ";
            //10:15
            case 5:
                return "HH:mm";
            //10:15:30.500
            case 12:
                return "HH:mm:ss.SSS";
            //10:15:30+01:00 / 10:15:30 +0100
            case 14:
                if (date.charAt(8) == ' ') {
                    return "HH:mm:ss ZZZ";
                }
                return "HH:mm:ssZZZZZ";
            //2011-12-03T10:15:30 / 2011-12-03 10:15:30
            case 19:
                if (date.charAt(10) == 'T') {
                    return "yyyy-MM-dd'T'HH:mm:ss";
                }
                return PATTERN_SECOND;
            //2011-12-03T10:15:30+01:00 / 2011-12-03T10:15:30 +0100 / 2011-12-03 10:15:30 +0100
            case 25:
                if (date.charAt(10) == 'T') {
                    if (date.charAt(19) == ' ') {
                        return "yyyy-MM-dd'T'HH:mm:ss ZZZ";
                    }
                    return "yyyy-MM-dd'T'HH:mm:ssZZZZZ";
                }
                return "yyyy-MM-dd HH:mm:ss ZZZ";
            //2011-12-03T10:15:30Z
            case 20:
                return "yyyy-MM-dd'T'HH:mm:ssX";
            //2011-12-03 10:15:30.500
            case 23:
                return PATTERN;
            //2011-12-03 10:15:30.500 +0100
            case 29:
                return PATTERN_OFFSET;
        }
        return null;
    }

    /**
     * Returns Instant from given temporal, or null if failed.
     *
     * @param temporal given temporal
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
}
