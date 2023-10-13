package xyz.fsgik.common.base;

import xyz.fsgik.annotations.Nullable;

import java.time.Duration;

/**
 * Utilities for thread.
 *
 * @author fredsuvn
 */
public class FsThread {

    /**
     * Runs a new thread.
     */
    public static Thread start(Runnable runnable) {
        return start(null, false, runnable);
    }

    /**
     * Runs a new thread with given thread name.
     *
     * @param threadName given thread name
     * @param runnable   run content
     */
    public static Thread start(@Nullable String threadName, Runnable runnable) {
        return start(threadName, false, runnable);
    }

    /**
     * Runs a new thread with given thread name, whether the thread is daemon.
     *
     * @param threadName given thread name
     * @param daemon     whether the thread is daemon
     * @param runnable   run content
     */
    public static Thread start(@Nullable String threadName, boolean daemon, Runnable runnable) {
        Thread thread = new Thread(runnable);
        if (threadName != null) {
            thread.setName(threadName);
        }
        if (daemon) {
            thread.setDaemon(daemon);
        }
        thread.start();
        return thread;
    }

    /**
     * Sleeps current thread for specified milliseconds.
     *
     * @param millis specified milliseconds
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Sleeps current thread for specified duration.
     *
     * @param duration specified duration
     */
    public static void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis(), duration.getNano());
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
