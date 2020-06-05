package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author sunqian
 */
public class Current {

    @Nullable
    public static <T> T get(Object key) {
        return Cast.nullable(ThreadLocalHolder.get(key));
    }

    public static <T> T getNonNull(Object key) {
        return Objects.requireNonNull(get(key));
    }

    public static void set(Object key, @Nullable Object value) {
        ThreadLocalHolder.set(key, value);
    }

    public static long mills() {
        return System.currentTimeMillis();
    }

    public static long nanos() {
        return System.nanoTime();
    }

    public static Thread thread() {
        return Thread.currentThread();
    }

    public static ExecutorService executor() {
        return ExecutorServiceHolder.GLOBAL_EXECUTOR_SERVICE;
    }

    private static final class ThreadLocalHolder {

        private static final ThreadLocal<Map<Object, Object>> globalThreadLocal =
                ThreadLocal.withInitial(HashMap::new);

        @Nullable
        public static Object get(Object key) {
            return globalThreadLocal.get().get(key);
        }

        public static void set(Object key, @Nullable Object value) {
            globalThreadLocal.get().put(key, value);
        }
    }

    private static final class ExecutorServiceHolder {

        public static final ExecutorService GLOBAL_EXECUTOR_SERVICE =
                Executors.newCachedThreadPool();
    }
}
