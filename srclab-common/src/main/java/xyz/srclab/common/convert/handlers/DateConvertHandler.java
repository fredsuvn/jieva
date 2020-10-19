package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.lang.finder.Finder;

import java.lang.reflect.Type;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.function.BiFunction;

/**
 * @author sunqian
 */
public class DateConvertHandler implements ConvertHandler {

    private final Finder<Type, BiFunction<Object, DateTimeFormatter, Object>> finder =
            Finder.pairHashFinder(ConvertFunctions.TABLE);

    private final DateTimeFormatter dateTimeFormatter;

    public DateConvertHandler() {
        this(DateTimeFormatter.ISO_DATE_TIME);
    }

    public DateConvertHandler(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
    }

    public DateConvertHandler(String pattern) {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public @Nullable Object convert(Object from, Class<?> to, Converter converter) {
        if (to.isAssignableFrom(String.class) || to.isAssignableFrom(CharSequence.class)) {
            return toString(from, dateTimeFormatter);
        }
        @Nullable BiFunction<Object, DateTimeFormatter, Object> function = finder.find(to);
        if (function == null) {
            return null;
        }
        return function.apply(from, dateTimeFormatter);
    }

    @Override
    public @Nullable Object convert(Object from, Type to, Converter converter) {
        if (to instanceof Class) {
            return convert(from, (Class<?>) to, converter);
        }
        @Nullable BiFunction<Object, DateTimeFormatter, Object> function = finder.find(to);
        if (function == null) {
            return null;
        }
        return function.apply(from, dateTimeFormatter);
    }

    @Nullable
    private static String toString(Object from, DateTimeFormatter dateTimeFormatter) {
        if (from instanceof TemporalAccessor) {
            return dateTimeFormatter.format((TemporalAccessor) from);
        }
        if (from instanceof Date) {
            return dateTimeFormatter.format(((Date) from).toInstant());
        }
        return null;
    }

    private static final class ConvertFunctions {

        private static final BiFunction<Object, DateTimeFormatter, Object> TO_DATE =
                ConvertFunctions::toDate;
        private static final BiFunction<Object, DateTimeFormatter, Object> TO_INSTANT =
                ConvertFunctions::toInstant;
        private static final BiFunction<Object, DateTimeFormatter, Object> TO_LOCAL_DATE_TIME =
                ConvertFunctions::toLocalDateTime;
        private static final BiFunction<Object, DateTimeFormatter, Object> TO_ZONED_DATE_TIME =
                ConvertFunctions::toZonedDateTime;
        private static final BiFunction<Object, DateTimeFormatter, Object> TO_OFFSET_DATE_TIME =
                ConvertFunctions::toOffsetDateTime;
        private static final BiFunction<Object, DateTimeFormatter, Object> TO_DURATION =
                ConvertFunctions::toDuration;

        private static final Object[] TABLE = {
                Date.class, TO_DATE,
                Instant.class, TO_INSTANT,
                LocalDateTime.class, TO_LOCAL_DATE_TIME,
                ZonedDateTime.class, TO_ZONED_DATE_TIME,
                OffsetDateTime.class, TO_OFFSET_DATE_TIME,
                Duration.class, TO_DURATION,
        };

        private static Date toDate(Object from, DateTimeFormatter dateTimeFormatter) {
            if (from instanceof Number) {
                return new Date(((Number) from).longValue());
            }
            if (from instanceof Instant) {
                return Date.from((Instant) from);
            }
            if (from instanceof TemporalAccessor) {
                TemporalAccessor temporalAccessor = toInstantTemporalAccessor((TemporalAccessor) from);
                return Date.from(Instant.from(temporalAccessor));
            }
            return Date.from(toZonedDateTime(from, dateTimeFormatter).toInstant());
        }

        private static Instant toInstant(Object from, DateTimeFormatter dateTimeFormatter) {
            if (from instanceof Number) {
                return Instant.ofEpochMilli(((Number) from).longValue());
            }
            if (from instanceof TemporalAccessor) {
                TemporalAccessor temporalAccessor = toInstantTemporalAccessor((TemporalAccessor) from);
                return Instant.from(temporalAccessor);
            }
            return toZonedDateTimeWithoutTemporal(from, dateTimeFormatter).toInstant();
        }

        private static ZonedDateTime toZonedDateTime(Object from, DateTimeFormatter dateTimeFormatter) {
            if (from instanceof TemporalAccessor) {
                TemporalAccessor temporalAccessor = toOffsetTemporalAccessor((TemporalAccessor) from);
                return toZonedDateTimeWithTemporal(temporalAccessor);
            }
            return toZonedDateTimeWithoutTemporal(from, dateTimeFormatter);
        }

        private static LocalDateTime toLocalDateTime(Object from, DateTimeFormatter dateTimeFormatter) {
            if (from instanceof Number) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) from), ZoneId.systemDefault());
            }
            return LocalDateTime.parse(from.toString(), dateTimeFormatter);
        }

        private static OffsetDateTime toOffsetDateTime(Object from, DateTimeFormatter dateTimeFormatter) {
            if (from instanceof Number) {
                return OffsetDateTime.ofInstant(Instant.ofEpochMilli((Long) from), ZoneId.systemDefault());
            }
            return OffsetDateTime.parse(from.toString(), dateTimeFormatter);
        }

        private static Duration toDuration(Object from, DateTimeFormatter dateTimeFormatter) {
            if (from instanceof Number) {
                return Duration.ofMillis(((Number) from).longValue());
            }
            return Duration.parse(from.toString());
        }

        private static ZonedDateTime toZonedDateTimeWithTemporal(TemporalAccessor temporalAccessor) {
            return ZonedDateTime.from(temporalAccessor);
        }

        private static ZonedDateTime toZonedDateTimeWithoutTemporal(Object from, DateTimeFormatter dateTimeFormatter) {
            if (from instanceof Number) {
                return ZonedDateTime.ofInstant(Instant.ofEpochMilli((Long) from), ZoneId.systemDefault());
            }
            TemporalAccessor temporalAccessor = toOffsetTemporalAccessor(dateTimeFormatter.parse(from.toString()));
            return toZonedDateTimeWithTemporal(temporalAccessor);
        }

        private static TemporalAccessor toInstantTemporalAccessor(TemporalAccessor temporalAccessor) {
            if (temporalAccessor.isSupported(ChronoField.INSTANT_SECONDS)
                    && temporalAccessor.isSupported(ChronoField.NANO_OF_SECOND)) {
                return temporalAccessor;
            }
            LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
            return localDateTime.atZone(ZoneId.systemDefault());
        }

        private static TemporalAccessor toOffsetTemporalAccessor(TemporalAccessor temporalAccessor) {
            if (temporalAccessor.isSupported(ChronoField.OFFSET_SECONDS)) {
                return temporalAccessor;
            }
            LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
            return localDateTime.atZone(ZoneId.systemDefault());
        }
    }
}
