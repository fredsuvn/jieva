package xyz.srclab.common.base;

import org.apache.commons.lang3.reflect.FieldUtils;
import xyz.srclab.common.EasyBoot;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Defaults {

    public static final String LINE_SEPARATOR = System.lineSeparator();

    public static final Locale LOCALE;

    public static final Charset CHARSET;

    public static final TimeUnit TIME_UNIT;

    static {
        try {
            Map<String, String> defaultsProperties = EasyBoot.getDefaultsProperties();
            LOCALE = (Locale) FieldUtils.readStaticField(
                    FieldUtils.getField(Locale.class, defaultsProperties.get("local")));
            CHARSET = Charset.forName(defaultsProperties.get("charset"));
            TIME_UNIT = (TimeUnit) FieldUtils.readStaticField(
                    FieldUtils.getField(TimeUnit.class, defaultsProperties.get("time-unit")));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
