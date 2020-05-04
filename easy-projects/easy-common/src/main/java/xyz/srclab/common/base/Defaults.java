package xyz.srclab.common.base;

import org.apache.commons.lang3.reflect.FieldUtils;
import xyz.srclab.common.ToovaBoot;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Defaults {

    public static final String LINE_SEPARATOR = System.lineSeparator();

    public static final Locale LOCALE;

    public static final Charset CHARSET;

    public static final TimeUnit TIME_UNIT;

    public static final int DEFAULT_CONCURRENCY_LEVEL;

    static {
        try {
            Map<String, String> defaultsProperties = ToovaBoot.getDefaultProperties();
            LOCALE = (Locale) FieldUtils.readStaticField(FieldUtils.getField(
                    Locale.class, defaultsProperties.getOrDefault("local", "US")));
            CHARSET = Charset.forName(
                    defaultsProperties.getOrDefault("charset", "UTF-8"));
            TIME_UNIT = (TimeUnit) FieldUtils.readStaticField(FieldUtils.getField(
                    TimeUnit.class, defaultsProperties.getOrDefault("time-unit", "SECONDS")));
            DEFAULT_CONCURRENCY_LEVEL = Integer.parseInt(
                    defaultsProperties.getOrDefault("concurrency-level", "16"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
