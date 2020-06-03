package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;

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
        return type.isPrimitive() || (BasicTypeTable.search(type) != null);
    }

    private static final class BasicTypeTable {

        private static final Class<?>[] table = {
                CharSequence.class,
                Number.class,
                Type.class,
                Date.class,
                Temporal.class,
        };

        @Nullable
        private static Class<?> search(Class<?> type) {
            for (Class<?> aClass : table) {
                if (aClass.isAssignableFrom(type)) {
                    return aClass;
                }
            }
            return null;
        }
    }
}
