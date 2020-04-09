package xyz.srclab.common.base;

import kotlin.text.Charsets;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Defaults {

    public static final String LINE_SEPARATOR = System.lineSeparator();

    public static final Locale LOCALE = Locale.US;

    public static final Charset CHARSET = Charsets.UTF_8;

    public static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
}
