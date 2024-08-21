package xyz.fslabo.common.exec;

import xyz.fslabo.common.base.BaseBuilder;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.io.JieIOException;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * Thread pool builder for {@link ScheduledExecutorService}.
 *
 * @author fredsuvn
 */
public abstract class ScheduledPoolBuilder implements BaseBuilder<ScheduledExecutorService, ScheduledPoolBuilder> {

    static ScheduledPoolBuilder newInstance() {
        return new ScheduledPoolBuilder.OfJdk8();
    }

    private int corePoolSize;
    private ThreadFactory threadFactory;
    private RejectedExecutionHandler rejectHandler;
    private boolean allowCoreThreadTimeOut = false;
    private Duration keepAliveTime;

    ScheduledPoolBuilder() {
        reset();
    }

    /**
     * Sets core pool size.
     *
     * @param corePoolSize core pool size
     * @return this
     */
    public ScheduledPoolBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    /**
     * Sets thread factory.
     *
     * @param threadFactory thread factory
     * @return this
     */
    public ScheduledPoolBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    /**
     * Sets the handler to use when execution is rejected because the thread bounds and queue capacities are reached.
     *
     * @param rejectHandler the handler to use when execution is rejected
     * @return this
     */
    public ScheduledPoolBuilder rejectHandler(RejectedExecutionHandler rejectHandler) {
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
    public ScheduledPoolBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }

    /**
     * Sets keep alive time for threads which are created exceed core threads.
     *
     * @param keepAliveTime keep alive time for threads which are created exceed core threads
     * @return this
     */
    public ScheduledPoolBuilder keepAliveTime(Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    @Override
    public ScheduledPoolBuilder reset() {
        this.corePoolSize = 0;
        this.keepAliveTime = null;
        this.threadFactory = null;
        this.rejectHandler = null;
        this.allowCoreThreadTimeOut = false;
        return this;
    }

    /**
     * Returns scheduled thread pool which is configured by this.
     *
     * @return scheduled thread pool which is configured by this
     * @throws JieIOException IO exception
     */
    public ScheduledExecutorService build() throws JieIOException {
        try {
            ScheduledThreadPoolExecutor pool = buildExecutor();
            if (allowCoreThreadTimeOut) {
                Duration keepTime = Jie.orDefault(keepAliveTime, Duration.ZERO);
                pool.setKeepAliveTime(keepTime.toNanos(), TimeUnit.NANOSECONDS);
                pool.allowCoreThreadTimeOut(true);
            }
            return pool;
        } catch (Exception e) {
            throw new JieIOException(e);
        }
    }

    private ScheduledThreadPoolExecutor buildExecutor() {
        ScheduledThreadPoolExecutor pool;
        if (threadFactory == null && rejectHandler == null) {
            pool = new ScheduledThreadPoolExecutor(corePoolSize);
        } else if (rejectHandler == null) {
            pool = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
        } else if (threadFactory == null) {
            pool = new ScheduledThreadPoolExecutor(corePoolSize, rejectHandler);
        } else {
            pool = new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, rejectHandler);
        }
        return pool;
    }

    private static final class OfJdk8 extends ScheduledPoolBuilder {
    }
}
