package xyz.srclab.common.base;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

/**
 * Properties of FS.
 *
 * @author fredsuvn
 */
public class FsProps {

    /**
     * Default date pattern: "yyyy-MM-dd HH:mm:ss.SSS".
     */
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * Default date time formatter: "yyyy-MM-dd HH:mm:ss.SSS".
     */
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);

    /**
     * Default IO buffer size: 1024.
     */
    public static final int IO_BUFFER_SIZE = 1024;

    /**
     * Returns default charset: {@link StandardCharsets#UTF_8}.
     */
    public static Charset charset() {
        return StandardCharsets.UTF_8;
    }

    /**
     * Returns new DateFormat of pattern: {@link #DATE_PATTERN}.
     */
    public static DateFormat dateFormat() {
        return new SimpleDateFormat(DATE_PATTERN);
    }

    /**
     * Returns default date time formatter : {@link #DATE_FORMATTER}.
     */
    public static DateTimeFormatter dateFormatter() {
        return DATE_FORMATTER;
    }

    /**
     * Returns default IO buffer size: {@link #IO_BUFFER_SIZE}.
     */
    public static int ioBufferSize() {
        return IO_BUFFER_SIZE;
    }
}
