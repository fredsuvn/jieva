package xyz.fsgek.common.base;

import xyz.fsgek.common.io.GekIOException;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * This class is used to configure a {@link ExecutorService} in method chaining:
 * <pre>
 *     pool.corePoolSize(10).maxPoolSize(20).build();
 * </pre>
 * Its instance is reusable, re-set and re-build are permitted.
 *
 * @author fredsuvn
 */
public abstract class GekThreadPool {

    static GekThreadPool newInstance() {
        return new GekThreadPool.OfJdk8();
    }

    private int corePoolSize;
    private int maxPoolSize;
    private Duration keepAliveTime;
    private BlockingQueue<Runnable> workQueue;
    private ThreadFactory threadFactory;
    private RejectedExecutionHandler rejectHandler;
    private boolean allowCoreThreadTimeOut = false;

    GekThreadPool() {
    }

    /**
     * Sets core pool size.
     *
     * @param corePoolSize core pool size
     * @return this
     */
    public GekThreadPool corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    /**
     * Sets max pool size.
     *
     * @param maxPoolSize max pool size
     * @return this
     */
    public GekThreadPool maxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    /**
     * Sets keep alive time for threads which are created exceed core threads.
     *
     * @param keepAliveTime keep alive time for threads which are created exceed core threads
     * @return this
     */
    public GekThreadPool keepAliveTime(Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    /**
     * Sets the queue to use for holding tasks before they are executed.
     *
     * @param workQueue the queue to use for holding tasks before they are executed
     * @return this
     */
    public GekThreadPool workQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    /**
     * Sets thread factory.
     *
     * @param threadFactory thread factory
     * @return this
     */
    public GekThreadPool threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    /**
     * Sets the handler to use when execution is rejected because the thread bounds and queue capacities are reached.
     *
     * @param rejectHandler the handler to use when execution is rejected
     * @return this
     */
    public GekThreadPool rejectHandler(RejectedExecutionHandler rejectHandler) {
        this.rejectHandler = rejectHandler;
        return this;
    }

    /**
     * Sets whether core threads may time out and terminate if no tasks arrive within the keep-alive time.
     *
     * @param allowCoreThreadTimeOut whether core threads may time out and terminate if no tasks arrive within the
     *                               keep-alive time
     * @return this
     */
    public GekThreadPool allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }

    /**
     * Clears current configurations.
     *
     * @return this
     */
    public GekThreadPool clear() {
        this.corePoolSize = 0;
        this.maxPoolSize = 0;
        this.keepAliveTime = null;
        this.workQueue = null;
        this.threadFactory = null;
        this.rejectHandler = null;
        this.allowCoreThreadTimeOut = false;
        return this;
    }

    /**
     * Returns new thread pool which is configured by this.
     *
     * @return thread pool which is configured by this
     * @throws GekIOException IO exception
     */
    public ExecutorService build() throws GekIOException {
        try {
            Duration keepTime = Gek.notNull(keepAliveTime, Duration.ZERO);
            BlockingQueue<Runnable> queue = Gek.notNull(workQueue, LinkedBlockingQueue::new);
            int maxPool = Math.max(maxPoolSize, corePoolSize);
            ThreadPoolExecutor pool;
            if (threadFactory == null && rejectHandler == null) {
                pool = new ThreadPoolExecutor(
                    corePoolSize,
                    maxPool,
                    keepTime.toNanos(),
                    TimeUnit.NANOSECONDS,
                    queue);
            } else if (rejectHandler == null) {
                pool = new ThreadPoolExecutor(
                    corePoolSize,
                    maxPool,
                    keepTime.toNanos(),
                    TimeUnit.NANOSECONDS,
                    queue,
                    threadFactory
                );
            } else if (threadFactory == null) {
                pool = new ThreadPoolExecutor(
                    corePoolSize,
                    maxPool,
                    keepTime.toNanos(),
                    TimeUnit.NANOSECONDS,
                    queue,
                    rejectHandler
                );
            } else {
                pool = new ThreadPoolExecutor(
                    corePoolSize,
                    maxPool,
                    keepTime.toNanos(),
                    TimeUnit.NANOSECONDS,
                    queue,
                    threadFactory,
                    rejectHandler
                );
            }
            if (allowCoreThreadTimeOut) {
                pool.allowCoreThreadTimeOut(true);
            }
            return pool;
        } catch (Exception e) {
            throw new GekIOException(e);
        }
    }

    private static final class OfJdk8 extends GekThreadPool {
    }
}
