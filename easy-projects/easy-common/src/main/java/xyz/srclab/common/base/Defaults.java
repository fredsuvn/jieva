package xyz.srclab.common.base;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Defaults {

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    public static final Locale LOCALE = Locale.getDefault();

    public static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    public static final int CONCURRENCY_LEVEL = 16;
}
