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
        return As.nullable(CurrentContextHolder.contextMap().get(key));
    }

    public static <T> T getNonNull(Object key) {
        return Objects.requireNonNull(get(key));
    }

    public static void set(Object key, @Nullable Object value) {
        CurrentContextHolder.contextMap().put(key, value);
    }

    public static void clear() {
        CurrentContextHolder.contextMap().clear();
    }

    public static Map<Object, Object> contextMap() {
        return CurrentContextHolder.contextMap();
    }

    public static void run(Runnable task) {
        UnlimitedExecutorServiceHolder.INSTANCE.submit(task);
    }

    public static <T> Future<T> run(Callable<T> task) {
        return UnlimitedExecutorServiceHolder.INSTANCE.submit(task);
    }

    public static void submit(Runnable task) {
        SingleExecutorServiceHolder.INSTANCE.submit(task);
    }

    public static <T> Future<T> submit(Callable<T> task) {
        return SingleExecutorServiceHolder.INSTANCE.submit(task);
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

    private static final class CurrentContextHolder {

        private static final ThreadLocal<Map<Object, Object>> contextMap =
                ThreadLocal.withInitial(HashMap::new);

        public static Map<Object, Object> contextMap() {
            return contextMap.get();
        }
    }

    private static final class UnlimitedExecutorServiceHolder {

        public static final ExecutorService INSTANCE =
                Executors.newCachedThreadPool();
    }

    private static final class SingleExecutorServiceHolder {

        public static final ExecutorService INSTANCE =
                Executors.newSingleThreadExecutor();
    }

    private static final class CurrentHolder {

        public static final Path USER_DIR = Paths.get(environment().userDir());
        public static final Path USER_HOME = Paths.get(environment().userHome());
        public static final Path TEMP_DIR = Paths.get(environment().javaIoTmpdir());
    }
}
