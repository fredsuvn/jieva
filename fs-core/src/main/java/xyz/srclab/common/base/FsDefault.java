package xyz.srclab.common.base;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * Default configuration and global setting for fs.
 *
 * @author fredsuvn
 */
public class FsDefault {

    private static final String dateTimePattern = "yyyy-MM-dd HH:mm:ss.SSS";
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateTimePattern);

    /**
     * Default charset: {@link StandardCharsets#UTF_8}
     */
    public static Charset charset() {
        return StandardCharsets.UTF_8;
    }

    /**
     * Default date time pattern: yyyy-MM-dd HH:mm:ss.SSS
     */
    public static String dateTimePattern() {
        return dateTimePattern;
    }

    /**
     * Default date time formatter: yyyy-MM-dd HH:mm:ss.SSS
     */
    public static DateTimeFormatter dateTimeFormatter() {
        return dateTimeFormatter;
    }

    /**
     * Default date format: yyyy-MM-dd HH:mm:ss.SSS
     * <p>
     * Note this method will create and return a new one for each invoking,
     * because the default implementation of {@link DateFormat} is not thread-safe.
     */
    public static DateFormat dateFormat() {
        return new SimpleDateFormat(dateTimePattern);
    }
}
