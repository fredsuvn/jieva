package xyz.fslabo.common.exec;

import xyz.fslabo.common.base.BaseBuilder;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.io.JieIOException;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * Thread pool builder for {@link ExecutorService}.
 *
 * @author fredsuvn
 */
public abstract class ThreadPoolBuilder implements BaseBuilder<ExecutorService, ThreadPoolBuilder> {

    static ThreadPoolBuilder newInstance() {
        return new ThreadPoolBuilder.OfJdk8();
    }

    private int corePoolSize;
    private int maxPoolSize;
    private Duration keepAliveTime;
    private BlockingQueue<Runnable> workQueue;
    private ThreadFactory threadFactory;
    private RejectedExecutionHandler rejectHandler;
    private boolean allowCoreThreadTimeOut;

    ThreadPoolBuilder() {
        reset();
    }

    /**
     * Sets core pool size.
     *
     * @param corePoolSize core pool size
     * @return this
     */
    public ThreadPoolBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    /**
     * Sets max pool size.
     *
     * @param maxPoolSize max pool size
     * @return this
     */
    public ThreadPoolBuilder maxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    /**
     * Sets keep alive time for threads which are created exceed core threads.
     *
     * @param keepAliveTime keep alive time for threads which are created exceed core threads
     * @return this
     */
    public ThreadPoolBuilder keepAliveTime(Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    /**
     * Sets the queue to use for holding tasks before they are executed.
     *
     * @param workQueue the queue to use for holding tasks before they are executed
     * @return this
     */
    public ThreadPoolBuilder workQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    /**
     * Sets thread factory.
     *
     * @param threadFactory thread factory
     * @return this
     */
    public ThreadPoolBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    /**
     * Sets the handler to use when execution is rejected because the thread bounds and queue capacities are reached.
     *
     * @param rejectHandler the handler to use when execution is rejected
     * @return this
     */
    public ThreadPoolBuilder rejectHandler(RejectedExecutionHandler rejectHandler) {
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
    public ThreadPoolBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }

    @Override
    public ThreadPoolBuilder reset() {
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
     * @throws ExecException execution exception
     */
    public ExecutorService build() throws ExecException {
        try {
            Duration keepTime = Jie.orDefault(keepAliveTime, Duration.ZERO);
            BlockingQueue<Runnable> queue = Jie.orDefault(workQueue, LinkedBlockingQueue::new);
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
            throw new JieIOException(e);
        }
    }

    private static final class OfJdk8 extends ThreadPoolBuilder {
    }
}
