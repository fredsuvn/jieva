package xyz.fslabo.common.exec;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * Execution utilities, including for thread and process.
 *
 * @author fredsuvn
 */
public class JieExec {

    /**
     * Async runs given runnable in backed thread pool created by {@link Executors#newCachedThreadPool()}.
     *
     * @param runnable given runnable
     */
    public static void runAsync(Runnable runnable) {
        DefaultThreadPool.INSTANCE.execute(runnable);
    }

    /**
     * Async runs given callable in backed thread pool created by {@link Executors#newCachedThreadPool()}, returns
     * {@link Future} of result.
     *
     * @param callable given callable
     * @param <V>      type of result
     * @return {@link Future} result
     */
    public static <V> Future<V> submit(Callable<V> callable) {
        return DefaultThreadPool.INSTANCE.submit(callable);
    }

    /**
     * Async schedules given runnable in backed scheduled pool created by {@link Executors#newScheduledThreadPool(int)}
     * with {@code 0} core size, returns {@link ScheduledFuture} of result.
     *
     * @param runnable given runnable
     * @param delay    delay duration
     * @return {@link Future} result
     */
    public static ScheduledFuture<?> schedule(Runnable runnable, Duration delay) {
        return DefaultScheduledPool.INSTANCE.schedule(runnable, delay.toNanos(), TimeUnit.NANOSECONDS);
    }

    /**
     * Async schedules given callable in backed scheduled pool created by {@link Executors#newScheduledThreadPool(int)}
     * with {@code 0} core size, returns {@link ScheduledFuture} of result.
     *
     * @param callable given callable
     * @param delay    delay duration
     * @param <V>      type of result
     * @return {@link Future} result
     */
    public static <V> ScheduledFuture<V> schedule(Callable<V> callable, Duration delay) {
        return DefaultScheduledPool.INSTANCE.schedule(callable, delay.toNanos(), TimeUnit.NANOSECONDS);
    }

    /**
     * Returns a new builder of {@link Thread}.
     *
     * @return a new builder of {@link Thread}
     */
    public static ThreadExecBuilder threadBuilder() {
        return ThreadExecBuilder.newInstance();
    }

    /**
     * Returns a new builder of {@link ExecutorService}.
     *
     * @return a new builder of {@link ExecutorService}
     */
    public static ThreadPoolBuilder threadPoolBuilder() {
        return ThreadPoolBuilder.newInstance();
    }

    /**
     * Returns a new builder of {@link ScheduledExecutorService}.
     *
     * @return a new builder of {@link ScheduledExecutorService}
     */
    public static ScheduledPoolBuilder scheduledPoolBuilder() {
        return ScheduledPoolBuilder.newInstance();
    }

    /**
     * Returns a new builder of {@link Process}.
     *
     * @return a new builder of {@link Process}
     */
    public static ProcessExecBuilder processBuilder() {
        return ProcessExecBuilder.newInstance();
    }

    private static final class DefaultThreadPool {
        private static final ExecutorService INSTANCE = Executors.newCachedThreadPool();
    }

    private static final class DefaultScheduledPool {
        private static final ScheduledExecutorService INSTANCE = Executors.newScheduledThreadPool(0);
    }
}
