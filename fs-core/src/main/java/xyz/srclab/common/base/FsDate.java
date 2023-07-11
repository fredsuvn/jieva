package xyz.srclab.common.base;

import xyz.srclab.annotations.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
     * Date pattern: "yyyy-MM-dd".
     */
    public static final String PATTERN_yyyyMMdd = "yyyy-MM-dd";

    /**
     * Date pattern: "HH:mm:ss".
     */
    public static final String PATTERN_HHmmss = "HH:mm:ss";

    /**
     * Date pattern: "HH:mm:ss.SSS".
     */
    public static final String PATTERN_HHmmssSSS = "HH:mm:ss.SSS";

    /**
     * Default date formatter: "yyyy-MM-dd HH:mm:ss.SSS".
     */
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(PATTERN);

    /**
     * Date formatter: "yyyy-MM-dd".
     */
    public static final DateTimeFormatter FORMATTER_yyyyMMdd = DateTimeFormatter.ofPattern(PATTERN_yyyyMMdd);

    /**
     * Date formatter: "HH:mm:ss".
     */
    public static final DateTimeFormatter FORMATTER_HHmmss = DateTimeFormatter.ofPattern(PATTERN_HHmmss);

    /**
     * Date formatter: "HH:mm:ss.SSS".
     */
    public static final DateTimeFormatter FORMATTER_HHmmssSSS = DateTimeFormatter.ofPattern(PATTERN_HHmmssSSS);

    /**
     * Returns a new Date format.
     */
    public static DateFormat dateFormat(CharSequence pattern) {
        return new SimpleDateFormat(pattern.toString());
    }

    /**
     * Formats given date with given pattern.
     *
     * @param date    given date
     * @param pattern given pattern
     */
    public static String format(Date date, CharSequence pattern) {
        return dateFormat(pattern).format(date);
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
            return dateFormat(pattern).parse(date.toString());
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
        return dateFormat(pattern).format(date);
    }

    /**
     * Formats given date with {@link #PATTERN}.
     * If given date is null, return null.
     *
     * @param date given date
     */
    @Nullable
    public static String toString(@Nullable Date date) {
        return format(date, PATTERN);
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
        try {
            return dateFormat(pattern).parse(date.toString());
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Parses given date string with {@link #PATTERN}.
     * If given date string is null, return null.
     *
     * @param date given date string
     */
    @Nullable
    public static Date toDate(@Nullable CharSequence date) {
        return parse(date, PATTERN);
    }

    /**
     * Returns current zone offset.
     */
    public static ZoneOffset currentZoneOffset() {
        return ZonedDateTime.now().getOffset();
    }
}
