package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.base.FsDate;
import xyz.srclab.common.convert.FsConvertHandler;
import xyz.srclab.common.convert.FsConverter;
import xyz.srclab.common.reflect.FsType;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

/**
 * Convert handler implementation supports converting object to date or date to object.
 * It supports:
 * <ul>
 *     <li>Converts date types each other;</li>
 *     <li>Converts date types to string;</li>
 *     <li>Converts string to date types;</li>
 *     <li>Converts date type to long (as milliseconds);</li>
 *     <li>Converts long (as milliseconds) to date types;</li>
 *     <li>Converts zone types each other;</li>
 *     <li>Converts zone types to string;</li>
 *     <li>Converts string to zone types;</li>
 *     <li>Converts {@link java.time.Duration} to string;</li>
 *     <li>Converts string to {@link java.time.Duration};</li>
 * </ul>
 * The date types include:
 * <ul>
 *     <li>{@link java.util.Date};</li>
 *     <li>{@link java.time.Instant};</li>
 *     <li>{@link java.time.LocalDateTime};</li>
 *     <li>{@link java.time.OffsetDateTime};</li>
 *     <li>{@link java.time.ZonedDateTime};</li>
 * </ul>
 * The zone types include:
 * <ul>
 *     <li>{@link java.util.TimeZone};</li>
 *     <li>{@link java.time.ZoneOffset};</li>
 *     <li>{@link java.time.ZoneId};</li>
 * </ul>
 * Pattern of this handler can be assigned in constructors:
 * <ul>
 *     <li>{@link #DateConvertHandler()};</li>
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
     * the default function parses out pattern by {@link FsDate#toPattern(CharSequence)}.
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
            public DateFormat getDateFormat(CharSequence date) {
                return FsDate.toDateFormat(pattern);
            }

            @Override
            public DateTimeFormatter getFormatter(CharSequence date) {
                return dateTimeFormatter;
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
        if (Objects.equals(targetType, Date.class)) {
            if (FsType.isAssignableFrom(CharSequence.class, fromType)) {
                return pattern.getDateFormat((CharSequence) obj).parse(obj.toString());
            } else if (Objects.equals(fromType, long.class) || Objects.equals(fromType, Long.class)) {
                return new Date((Long) obj);
            } else if (FsType.isAssignableFrom(Instant.class, fromType)) {
                return Date.from((Instant) obj);
            } else if (FsType.isAssignableFrom(LocalDateTime.class, fromType)) {
                return Date.from(((LocalDateTime) obj).toInstant(FsDate.currentZoneOffset()));
            } else if (FsType.isAssignableFrom(OffsetDateTime.class, fromType)) {
                return Date.from(((OffsetDateTime) obj).toInstant());
            } else if (FsType.isAssignableFrom(ZonedDateTime.class, fromType)) {
                return Date.from(((ZonedDateTime) obj).toInstant());
            }
            return NOT_SUPPORTED;
        } else if (Objects.equals(targetType, Instant.class)) {
            if (FsType.isAssignableFrom(CharSequence.class, fromType)) {
                return FsDate.toInstant(pattern.getFormatter((CharSequence) obj).parse((CharSequence) obj));
            } else if (Objects.equals(fromType, long.class) || Objects.equals(fromType, Long.class)) {
                return Instant.ofEpochMilli((Long) obj);
            } else if (FsType.isAssignableFrom(Date.class, fromType)) {
                return ((Date) obj).toInstant();
            } else if (FsType.isAssignableFrom(LocalDateTime.class, fromType)) {
                return ((LocalDateTime)obj).toInstant(FsDate.currentZoneOffset());
            } else if (FsType.isAssignableFrom(OffsetDateTime.class, fromType)) {
                return ((OffsetDateTime) obj).toInstant();
            } else if (FsType.isAssignableFrom(ZonedDateTime.class, fromType)) {
                return ((ZonedDateTime) obj).toInstant();
            }
            return NOT_SUPPORTED;
        } else {
            return NOT_SUPPORTED;
        }
    }

    /**
     * Function to parse date string to pattern and get date formatter.
     */
    public interface PatternFunction {

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
