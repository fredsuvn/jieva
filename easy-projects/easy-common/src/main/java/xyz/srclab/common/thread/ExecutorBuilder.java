package xyz.srclab.common.thread;

import xyz.srclab.annotation.Nullable;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * @author sunqian
 */
public class ExecutorBuilder {

    public static ExecutorBuilder newBuilder() {
        return new ExecutorBuilder();
    }

    private int corePoolSize = 1;
    private int maximumPoolSize = 1;
    private int workQueueCapacity = Integer.MAX_VALUE;
    private @Nullable Duration keepAliveTime;
    private @Nullable BlockingQueue<Runnable> workQueue;
    private @Nullable ThreadFactory threadFactory;
    private @Nullable RejectedExecutionHandler rejectedExecutionHandler;

    public ExecutorBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public ExecutorBuilder maximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    public ExecutorBuilder workQueueCapacity(int workQueueCapacity) {
        this.workQueueCapacity = workQueueCapacity;
        return this;
    }

    public ExecutorBuilder keepAliveTime(Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public ExecutorBuilder workQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    public ExecutorBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ExecutorBuilder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        return this;
    }

    public ExecutorService buildExecutor() {
        long keepTime;
        TimeUnit keepUnit;
        if (keepAliveTime == null) {
            keepTime = 0;
            keepUnit = TimeUnit.MILLISECONDS;
        } else {
            keepTime = keepAliveTime.toNanos();
            keepUnit = TimeUnit.NANOSECONDS;
        }
        if (workQueue == null) {
            workQueue = new LinkedBlockingQueue<>(workQueueCapacity);
        }
        if (threadFactory == null) {
            threadFactory = Executors.defaultThreadFactory();
        }
        return rejectedExecutionHandler == null ?
                new ThreadPoolExecutor(
                        corePoolSize, maximumPoolSize, keepTime, keepUnit, workQueue, threadFactory)
                :
                new ThreadPoolExecutor(
                        corePoolSize,
                        maximumPoolSize,
                        keepTime,
                        keepUnit,
                        workQueue,
                        threadFactory,
                        rejectedExecutionHandler
                );
    }

    public ScheduledExecutorService buildScheduledExecutor() {
        if (threadFactory == null) {
            threadFactory = Executors.defaultThreadFactory();
        }
        return rejectedExecutionHandler == null ?
                new ScheduledThreadPoolExecutor(corePoolSize, threadFactory)
                :
                new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, rejectedExecutionHandler);
    }
}
