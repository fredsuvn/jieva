package xyz.srclab.common.base;

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
     * Returns a new Date format.
     */
    public static DateFormat dateFormat(CharSequence pattern) {
        return new SimpleDateFormat(pattern.toString());
    }

    /**
     * Returns default date time formatter : {@link FsProps#DATE_FORMATTER}.
     */
    public static DateTimeFormatter dateFormatter() {
        return DATE_FORMATTER;
    }

    /**
     * Formats given date with given pattern.
     *
     * @param date    given date
     * @param pattern given pattern
     */
    public static String format(Date date, CharSequence pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        return dateFormat(pattern).format(date);
    }

    /**
     * Formats given date with {@link FsProps#DATE_PATTERN}.
     *
     * @param date given date
     */
    public static String format(Date date) {
        return format(date, FsProps.DATE_PATTERN);
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
     * Parses given date string with {@link FsProps#DATE_PATTERN}.
     *
     * @param date given date string
     */
    public static Date parse(CharSequence date) {
        return parse(date, FsProps.DATE_PATTERN);
    }

    /**
     * Returns current zone offset.
     */
    public static ZoneOffset currentZoneOffset() {
        return ZonedDateTime.now().getOffset();
    }
}
