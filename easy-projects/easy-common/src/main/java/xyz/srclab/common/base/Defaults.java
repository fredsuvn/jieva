package xyz.srclab.common.base;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Defaults {

    public static final Charset CHARSET = StandardCharsets.UTF_8;

    public static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    public static final int CONCURRENCY_LEVEL = 16;

    public static boolean isBasicType(Class<?> type) {
        return BasicTypeTable.find(type);
    }

    private static final class BasicTypeTable {

        private static final Class<?>[] table = {
                CharSequence.class,
                Number.class,
                Type.class,
                Date.class,
                Temporal.class,
        };

        private static boolean find(Class<?> type) {
            for (Class<?> aClass : table) {
                if (aClass.equals(type)) {
                    return true;
                }
            }
            return false;
        }
    }
}
