package xyz.fslabo.common.exec;

import xyz.fslabo.common.base.BaseBuilder;
import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.io.JieIOException;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class SchedulerBuilder implements BaseBuilder<Scheduler, SchedulerBuilder> {

    private int corePoolSize;
    private int maxPoolSize;
    private Duration keepAliveTime;
    private BlockingQueue<Runnable> workQueue;
    private ThreadFactory threadFactory;
    private RejectedExecutionHandler rejectHandler;
    private boolean allowCoreThreadTimeOut;
    private boolean sharePool;

    SchedulerBuilder() {
        reset();
    }

    /**
     * Sets core pool size.
     *
     * @param corePoolSize core pool size
     * @return this
     */
    public SchedulerBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    /**
     * Sets max pool size.
     *
     * @param maxPoolSize max pool size
     * @return this
     */
    public SchedulerBuilder maxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    /**
     * Sets keep alive time for threads which are created exceed core threads.
     *
     * @param keepAliveTime keep alive time for threads which are created exceed core threads
     * @return this
     */
    public SchedulerBuilder keepAliveTime(Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    /**
     * Sets the queue to use for holding tasks before they are executed.
     *
     * @param workQueue the queue to use for holding tasks before they are executed
     * @return this
     */
    public SchedulerBuilder workQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    /**
     * Sets thread factory.
     *
     * @param threadFactory thread factory
     * @return this
     */
    public SchedulerBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    /**
     * Sets the handler to use when execution is rejected because the thread bounds and queue capacities are reached.
     *
     * @param rejectHandler the handler to use when execution is rejected
     * @return this
     */
    public SchedulerBuilder rejectHandler(RejectedExecutionHandler rejectHandler) {
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
    public SchedulerBuilder allowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
        this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
        return this;
    }

    /**
     * Sets whether share the thread pool and scheduling pool.
     *
     * @param sharePool whether share the thread pool and scheduling pool
     * @return this
     */
    public SchedulerBuilder sharePool(boolean sharePool) {
        this.sharePool = sharePool;
        return this;
    }

    @Override
    public SchedulerBuilder reset() {
        this.corePoolSize = 0;
        this.maxPoolSize = 0;
        this.keepAliveTime = null;
        this.workQueue = null;
        this.threadFactory = null;
        this.rejectHandler = null;
        this.allowCoreThreadTimeOut = false;
        return this;
    }

    @Override
    public Scheduler build() {
        if (sharePool) {
            ScheduledExecutorService service = buildScheduledService();
            return new SchedulerImpl(service, service);
        }
        ExecutorService executorService = buildExecutorService();
        ScheduledExecutorService scheduledService = buildScheduledService();
        return new SchedulerImpl(executorService, scheduledService);
    }

    private ExecutorService buildExecutorService() throws ExecException {
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

    private ScheduledExecutorService buildScheduledService() throws ExecException {
        try {
            ScheduledThreadPoolExecutor pool = buildScheduledPool();
            if (allowCoreThreadTimeOut) {
                Duration keepTime = Jie.orDefault(keepAliveTime, Duration.ZERO);
                pool.setKeepAliveTime(keepTime.toNanos(), TimeUnit.NANOSECONDS);
                pool.allowCoreThreadTimeOut(true);
            }
            return pool;
        } catch (Exception e) {
            throw new ExecException(e);
        }
    }

    private ScheduledThreadPoolExecutor buildScheduledPool() {
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

    private static final class SchedulerImpl implements Scheduler {

        private final ExecutorService executorService;
        private final ScheduledExecutorService scheduledService;

        private SchedulerImpl(ExecutorService executorService, ScheduledExecutorService scheduledService) {
            this.executorService = executorService;
            this.scheduledService = scheduledService;
        }

        @Override
        public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
            return scheduledService.schedule(command, delay, unit);
        }

        @Override
        public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
            return scheduledService.schedule(callable, delay, unit);
        }

        @Override
        public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
            return scheduledService.scheduleAtFixedRate(command, initialDelay, period, unit);
        }

        @Override
        public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
            return scheduledService.scheduleWithFixedDelay(command, initialDelay, delay, unit);
        }

        @Override
        public void shutdown() {
            executorService.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            return executorService.shutdownNow();
        }

        @Override
        public boolean isShutdown() {
            return executorService.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return executorService.isTerminated();
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
            return executorService.awaitTermination(timeout, unit);
        }

        @Override
        public <T> Future<T> submit(Callable<T> task) {
            return executorService.submit(task);
        }

        @Override
        public <T> Future<T> submit(Runnable task, T result) {
            return executorService.submit(task, result);
        }

        @Override
        public Future<?> submit(Runnable task) {
            return executorService.submit(task);
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return executorService.invokeAll(tasks);
        }

        @Override
        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
            return executorService.invokeAll(tasks, timeout, unit);
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
            return executorService.invokeAny(tasks);
        }

        @Override
        public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return executorService.invokeAny(tasks, timeout, unit);
        }

        @Override
        public void execute(Runnable command) {
            executorService.execute(command);
        }
    }
}
