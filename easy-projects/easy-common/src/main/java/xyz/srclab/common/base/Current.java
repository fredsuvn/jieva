package xyz.srclab.common.base;

import xyz.srclab.annotation.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author sunqian
 */
public class Current {

    public static Environment environment() {
        return Environment.currentEnvironment();
    }

    public static Thread thread() {
        return Thread.currentThread();
    }

    public static long mills() {
        return System.currentTimeMillis();
    }

    public static long nanos() {
        return System.nanoTime();
    }

    @Nullable
    public static <T> T get(Object key) {
        return Cast.asNullable(ThreadLocalHolder.get(key));
    }

    public static <T> T getNonNull(Object key) {
        return Objects.requireNonNull(get(key));
    }

    public static void set(Object key, @Nullable Object value) {
        ThreadLocalHolder.set(key, value);
    }

    public static Future<?> run(Runnable task) {
        return ImmediateExecutorServiceHolder.INSTANCE.submit(task);
    }

    public static <T> Future<T> run(Callable<T> task) {
        return ImmediateExecutorServiceHolder.INSTANCE.submit(task);
    }

    public static Future<?> submit(Runnable task) {
        return BackedExecutorServiceHolder.INSTANCE.submit(task);
    }

    public static <T> Future<T> submit(Callable<T> task) {
        return BackedExecutorServiceHolder.INSTANCE.submit(task);
    }

    public static Path processPath() {
        return CurrentHolder.USER_DIR;
    }

    public static Path homePath() {
        return CurrentHolder.USER_HOME;
    }

    public static Path tempPath() {
        return CurrentHolder.TEMP_DIR;
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

    private static final class ImmediateExecutorServiceHolder {

        public static final ExecutorService INSTANCE =
                Executors.newCachedThreadPool();
    }

    private static final class BackedExecutorServiceHolder {

        public static final ExecutorService INSTANCE =
                Executors.newSingleThreadExecutor();
    }

    private static final class CurrentHolder {

        public static final Path USER_DIR = Paths.get(environment().getUserDir());
        public static final Path USER_HOME = Paths.get(environment().getUserHome());
        public static final Path TEMP_DIR = Paths.get(environment().getJavaIoTmpdir());
    }
}
