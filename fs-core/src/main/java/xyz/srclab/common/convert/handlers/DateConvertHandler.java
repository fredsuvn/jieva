//package xyz.srclab.common.convert.handlers;
//
//import xyz.srclab.annotations.Nullable;
//import xyz.srclab.common.base.FsDate;
//import xyz.srclab.common.base.FsProps;
//import xyz.srclab.common.convert.FsConvertHandler;
//import xyz.srclab.common.convert.FsConverter;
//import xyz.srclab.common.reflect.FsType;
//
//import java.lang.reflect.Type;
//import java.text.DateFormat;
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.OffsetDateTime;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//import java.util.Objects;
//import java.util.function.Function;
//
///**
// * Convert handler implementation supports converting object to date or date to object.
// * It supports:
// * <ul>
// *     <li>Converts date types each other;</li>
// *     <li>Converts date types to string;</li>
// *     <li>Converts string to date types;</li>
// *     <li>Converts date type to long (as milliseconds);</li>
// *     <li>Converts long (as milliseconds) to date types;</li>
// *     <li>Converts zone types each other;</li>
// *     <li>Converts zone types to string;</li>
// *     <li>Converts string to zone types;</li>
// *     <li>Converts {@link java.time.Duration} to string;</li>
// *     <li>Converts string to {@link java.time.Duration};</li>
// * </ul>
// * The date types include:
// * <ul>
// *     <li>{@link java.util.Date};</li>
// *     <li>{@link java.time.Instant};</li>
// *     <li>{@link java.time.LocalDateTime};</li>
// *     <li>{@link java.time.OffsetDateTime};</li>
// *     <li>{@link java.time.ZonedDateTime};</li>
// * </ul>
// * The zone types include:
// * <ul>
// *     <li>{@link java.util.TimeZone};</li>
// *     <li>{@link java.time.ZoneOffset};</li>
// *     <li>{@link java.time.ZoneId};</li>
// * </ul>
// * Pattern of this handler can be assigned in constructors:
// * <ul>
// *     <li>{@link #DateConvertHandler()};</li>
// *     <li>{@link #DateConvertHandler(String)};</li>
// *     <li>{@link #DateConvertHandler(Function)};</li>
// * </ul>
// * <p>
// * Note if the {@code obj} is null, return {@link #NOT_SUPPORTED}.
// *
// * @author fredsuvn
// */
//public class DateConvertHandler implements FsConvertHandler {
//
//    private static PatternFunction DEFAULT_PATTERN_FUNCTION = new PatternFunction() {
//
//        private final DateTimeFormatter yyyyMMdd = DateTimeFormatter.ofPattern("yyyyMMdd");
//        private final DateTimeFormatter hhmmssSSS = DateTimeFormatter.ofPattern("hh:mm:ss.SSS");
//
//        @Override
//        public DateFormat getDateFormat(CharSequence date) {
//            switch (date.length()) {
//                case 8:
//                    return FsDate.dateFormat("yyyyMMdd");
//                case 12:
//                    return FsDate.dateFormat("hh:mm:ss.SSS");
//            }
//            return FsProps.
//        }
//
//        @Override
//        public DateTimeFormatter getDateTimeFormatter(CharSequence date) {
//            return dateTimeFormatter;
//        }
//    };
//
//    private final PatternFunction pattern;
//
//    /**
//     * Constructs with default pattern function.
//     * The default pattern function will attempt to match patterns in:
//     * <ul>
//     *     <li>yyyyMMdd</li>
//     *     <li>yyyy-MM-dd</li>
//     *     <li>hh:mm:ss</li>
//     *     <li>hh:mm:ss.SSS</li>
//     *     <li>yyyy-MM-dd hh:mm:ss.SSS</li>
//     * </ul>
//     * If length of date string is 8, pattern is "yyyyMMdd";
//     * If length of date string is 12, pattern is "hh:mm:ss.SSS";
//     * Else pattern is {@link FsProps#DATE_PATTERN}.
//     */
//    public DateConvertHandler() {
//        DateTimeFormatter.ISO_DATE
//        this.pattern =
//            cs -> {
//            switch (cs.length()) {
//                case 8:
//                    return "yyyyMMdd";
//                case 12:
//                    return "hh:mm:ss.SSS";
//            }
//            return FsProps.DATE_PATTERN;
//        };
//    }
//
//    /**
//     * Constructs with given pattern.
//     *
//     * @param pattern given pattern
//     */
//    public DateConvertHandler(CharSequence pattern) {
//        this.pattern = new PatternFunction() {
//
//            private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern.toString());
//
//            @Override
//            public DateFormat getDateFormat(CharSequence date) {
//                return FsDate.dateFormat(pattern);
//            }
//
//            @Override
//            public DateTimeFormatter getDateTimeFormatter(CharSequence date) {
//                return dateTimeFormatter;
//            }
//        };
//    }
//
//    /**
//     * Constructs with given pattern function.
//     * The argument of function is date string, and it expects to return the date pattern.
//     *
//     * @param pattern given pattern function
//     */
//    public DateConvertHandler(PatternFunction pattern) {
//        this.pattern = pattern;
//    }
//
//    @Override
//    public @Nullable Object convert(@Nullable Object obj, Type fromType, Type targetType, FsConverter converter) {
//        if (obj == null) {
//            return NOT_SUPPORTED;
//        }
//        if (Objects.equals(targetType, Date.class)) {
//            if (FsType.isAssignableFrom(CharSequence.class, fromType)) {
//                return FsDate.parse((CharSequence) obj, pattern.apply((CharSequence) obj));
//            } else if (Objects.equals(fromType, long.class) || Objects.equals(fromType, Long.class)) {
//                return new Date((Long) obj);
//            } else if (FsType.isAssignableFrom(Instant.class, fromType)) {
//                return Date.from((Instant) obj);
//            } else if (FsType.isAssignableFrom(LocalDateTime.class, fromType)) {
//                return Date.from(((LocalDateTime) obj).toInstant(FsDate.currentZoneOffset()));
//            } else if (FsType.isAssignableFrom(OffsetDateTime.class, fromType)) {
//                return Date.from(((OffsetDateTime) obj).toInstant());
//            } else if (FsType.isAssignableFrom(ZonedDateTime.class, fromType)) {
//                return Date.from(((ZonedDateTime) obj).toInstant());
//            }
//            return NOT_SUPPORTED;
//        } else if (Objects.equals(targetType, Instant.class)) {
//            if (FsType.isAssignableFrom(CharSequence.class, fromType)) {
//                return
//            } else if (Objects.equals(fromType, long.class) || Objects.equals(fromType, Long.class)) {
//                return new Date((Long) obj);
//            } else if (FsType.isAssignableFrom(Instant.class, fromType)) {
//                return Date.from((Instant) obj);
//            } else if (FsType.isAssignableFrom(LocalDateTime.class, fromType)) {
//                return Date.from(((LocalDateTime) obj).toInstant(FsDate.currentZoneOffset()));
//            } else if (FsType.isAssignableFrom(OffsetDateTime.class, fromType)) {
//                return Date.from(((OffsetDateTime) obj).toInstant());
//            } else if (FsType.isAssignableFrom(ZonedDateTime.class, fromType)) {
//                return Date.from(((ZonedDateTime) obj).toInstant());
//            }
//            return NOT_SUPPORTED;
//        } else {
//            return NOT_SUPPORTED;
//        }
//    }
//
//    /**
//     * Function to get date formatter.
//     */
//    public interface PatternFunction {
//
//        /**
//         * Returns DateFormat with given date string (<b>NOT PATTERN STRING</b>)
//         *
//         * @param date date string
//         */
//        DateFormat getDateFormat(CharSequence date);
//
//        /**
//         * Returns DateTimeFormatter with given date string (<b>NOT PATTERN STRING</b>)
//         *
//         * @param date date string
//         */
//        DateTimeFormatter getDateTimeFormatter(CharSequence date);
//    }
//}
