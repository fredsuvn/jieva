package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.collection.MapKit;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;

import java.lang.reflect.Type;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * @author sunqian
 */
public class DateConvertHandler implements ConvertHandler {

    @Override
    public @Nullable Object convert(Object from, Type to, Converter converter) {
        @Nullable Function<Object, Object> convertFunction = ConvertFunctionFinder.find(to);
        if (convertFunction == null) {
            return null;
        }
        return convertFunction.apply(from);
    }

    private static final class ConvertFunctionFinder {

        private static final Map<Type, Function<Object, Object>> table = MapKit.pairToMap(ConvertFunctionTable.TABLE);

        @Nullable
        public static Function<Object, Object> find(Type type) {
            return table.get(type);
        }

        private static final class ConvertFunctionTable {

            private static final Function<Object, Object> TO_BYTE = ConvertFunctionTable::toByte;
            private static final Function<Object, Object> TO_SHORT = ConvertFunctionTable::toShort;
            private static final Function<Object, Object> TO_CHAR = ConvertFunctionTable::toChar;
            private static final Function<Object, Object> TO_INT = ConvertFunctionTable::toInt;
            private static final Function<Object, Object> TO_LONG = ConvertFunctionTable::toLong;
            private static final Function<Object, Object> TO_FLOAT = ConvertFunctionTable::toFloat;
            private static final Function<Object, Object> TO_DOUBLE = ConvertFunctionTable::toDouble;

            private static final Object[] TABLE = {
                    byte.class, TO_BYTE,
                    short.class, TO_SHORT,
                    char.class, TO_CHAR,
                    int.class, TO_INT,
                    long.class, TO_LONG,
                    float.class, TO_FLOAT,
                    double.class, TO_DOUBLE,
                    Byte.class, TO_BYTE,
                    Short.class, TO_SHORT,
                    Character.class, TO_CHAR,
                    Integer.class, TO_INT,
                    Long.class, TO_LONG,
                    Float.class, TO_FLOAT,
                    Double.class, TO_DOUBLE,
            };

            private static Date toDate(Object from) {
                if (from instanceof Number) {
                    return new Date(((Number) from).longValue());
                }
                if (from instanceof Instant) {
                    return Date.from((Instant) from);
                }
                if (from instanceof TemporalAccessor) {
                    TemporalAccessor temporalAccessor = toOffsetTemporalAccessor((TemporalAccessor) from);
                    return Date.from(Instant.from(temporalAccessor));
                }
                return Date.from(toZonedDateTime(from).toInstant());
            }

            private static Instant toInstant(Object from) {
                if (from instanceof Number) {
                    return Instant.ofEpochMilli(((Number) from).longValue());
                }
                if (from instanceof TemporalAccessor) {
                    TemporalAccessor temporalAccessor = toOffsetTemporalAccessor((TemporalAccessor) from);
                    return Instant.from(temporalAccessor);
                }
                return toZonedDateTimeWithoutTemporal(from).toInstant();
            }

            private static ZonedDateTime toZonedDateTime(Object from) {
                if (from instanceof TemporalAccessor) {
                    TemporalAccessor temporalAccessor = toOffsetTemporalAccessor((TemporalAccessor) from);
                    return toZonedDateTimeWithTemporal(temporalAccessor);
                }
                return toZonedDateTimeWithoutTemporal(from);
            }

            private static ZonedDateTime toZonedDateTimeWithTemporal(TemporalAccessor temporalAccessor) {
                return ZonedDateTime.from(temporalAccessor);
            }

            private static ZonedDateTime toZonedDateTimeWithoutTemporal(Object from) {
                if (from instanceof Number) {
                    return ZonedDateTime.ofInstant(Instant.ofEpochMilli((Long) from), ZoneId.systemDefault());
                }
                TemporalAccessor temporalAccessor = toOffsetTemporalAccessor(
                        DateTimeFormatter.ISO_DATE_TIME.parse(from.toString()));
                return toZonedDateTimeWithTemporal(temporalAccessor);
            }

            private static LocalDateTime toLocalDateTime(Object from) {
                if (from instanceof Number) {
                    return LocalDateTime.ofInstant(Instant.ofEpochMilli((Long) from), ZoneId.systemDefault());
                }
                return LocalDateTime.parse(from.toString(), DateTimeFormatter.ISO_DATE_TIME);
            }

            private static Duration toDuration(Object from) {
                if (from instanceof Number) {
                    return Duration.ofMillis(((Number) from).longValue());
                }
                return Duration.parse(from.toString());
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
}
