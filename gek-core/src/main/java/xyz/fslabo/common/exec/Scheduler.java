package xyz.fslabo.common.exec;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.*;

/**
 * Service for execution, extension of {@link ExecutorService} and {@link ScheduledExecutorService}.
 *
 * @author fredsuvn
 */
public interface Scheduler extends ScheduledExecutorService {

    /**
     * Creates and executes a one-shot action that becomes enabled after the given delay.
     *
     * @param command the task to execute
     * @param delay   the time from now to delay execution
     * @return a ScheduledFuture representing pending completion of the task and whose {@code get()} method will return
     * {@code null} upon completion
     * @throws RejectedExecutionException if the task cannot be scheduled for execution
     * @throws NullPointerException       if command is null
     */
    default ScheduledFuture<?> schedule(Runnable command, Duration delay) {
        return schedule(command, delay.toNanos(), TimeUnit.NANOSECONDS);
    }

    /**
     * Creates and executes a ScheduledFuture that becomes enabled after the given delay.
     *
     * @param callable the function to execute
     * @param delay    the time from now to delay execution
     * @param <V>      the type of the callable's result
     * @return a ScheduledFuture that can be used to extract result or cancel
     * @throws RejectedExecutionException if the task cannot be scheduled for execution
     * @throws NullPointerException       if callable is null
     */
    default <V> ScheduledFuture<V> schedule(Callable<V> callable, Duration delay) {
        return schedule(callable, delay.toNanos(), TimeUnit.NANOSECONDS);
    }

    /**
     * Creates and executes a one-shot action that becomes enabled at the given time.
     *
     * @param command the task to execute
     * @param time    the time to delay execution
     * @return a ScheduledFuture representing pending completion of the task and whose {@code get()} method will return
     * {@code null} upon completion
     * @throws RejectedExecutionException if the task cannot be scheduled for execution
     * @throws NullPointerException       if command is null
     */
    default ScheduledFuture<?> scheduleAt(Runnable command, Date time) {
        return scheduleAt(command, time.toInstant());
    }

    /**
     * Creates and executes a ScheduledFuture that becomes enabled at the given time.
     *
     * @param callable the function to execute
     * @param time     the time to delay execution
     * @param <V>      the type of the callable's result
     * @return a ScheduledFuture that can be used to extract result or cancel
     * @throws RejectedExecutionException if the task cannot be scheduled for execution
     * @throws NullPointerException       if callable is null
     */
    default <V> ScheduledFuture<V> scheduleAt(Callable<V> callable, Date time) {
        return scheduleAt(callable, time.toInstant());
    }

    /**
     * Creates and executes a one-shot action that becomes enabled at the given time.
     *
     * @param command the task to execute
     * @param time    the time to delay execution
     * @return a ScheduledFuture representing pending completion of the task and whose {@code get()} method will return
     * {@code null} upon completion
     * @throws RejectedExecutionException if the task cannot be scheduled for execution
     * @throws NullPointerException       if command is null
     */
    default ScheduledFuture<?> scheduleAt(Runnable command, Instant time) {
        return schedule(command, Duration.between(Instant.now(), time).abs());
    }

    /**
     * Creates and executes a ScheduledFuture that becomes enabled at the given time.
     *
     * @param callable the function to execute
     * @param time     the time to delay execution
     * @param <V>      the type of the callable's result
     * @return a ScheduledFuture that can be used to extract result or cancel
     * @throws RejectedExecutionException if the task cannot be scheduled for execution
     * @throws NullPointerException       if callable is null
     */
    default <V> ScheduledFuture<V> scheduleAt(Callable<V> callable, Instant time) {
        return schedule(callable, Duration.between(Instant.now(), time).abs());
    }

    /**
     * Creates and executes a periodic action that becomes enabled first after the given initial delay, and subsequently
     * with the given period; that is executions will commence after {@code initialDelay} then
     * {@code initialDelay+period}, then {@code initialDelay + 2 * period}, and so on.
     * <p>
     * If any execution of the task encounters an exception, subsequent executions are suppressed. Otherwise, the task
     * will only terminate via cancellation or termination of the executor.  If any execution of this task takes longer
     * than its period, then subsequent executions may start late, but will not concurrently execute.
     * <p>
     * This method is equivalent to:
     * <pre>
     *     return scheduleAtFixedRate(command, initialDelay.toNanos(), period.toNanos(), TimeUnit.NANOSECONDS);
     * </pre>
     *
     * @param command      the task to execute
     * @param initialDelay the time to delay first execution
     * @param period       the period between successive executions
     * @return a ScheduledFuture representing pending completion of the task, and whose {@code get()} method will throw
     * an exception upon cancellation
     * @throws RejectedExecutionException if the task cannot be
     *                                    scheduled for execution
     * @throws NullPointerException       if command is null
     * @throws IllegalArgumentException   if period less than or equal to zero
     */
    default ScheduledFuture<?> schedulePeriod(Runnable command, Duration initialDelay, Duration period) {
        return scheduleAtFixedRate(command, initialDelay.toNanos(), period.toNanos(), TimeUnit.NANOSECONDS);
    }

    /**
     * Creates and executes a periodic action that becomes enabled first after the given initial delay, and subsequently
     * with the given delay between the termination of one execution and the commencement of the next.
     * <p>
     * If any execution of the task encounters an exception, subsequent executions are suppressed. Otherwise, the task
     * will only terminate via cancellation or termination of the executor.
     * <p>
     * This method is equivalent to:
     * <pre>
     *     return scheduleWithFixedDelay(command, initialDelay.toNanos(), delay.toNanos(), TimeUnit.NANOSECONDS);
     * </pre>
     *
     * @param command      the task to execute
     * @param initialDelay the time to delay first execution
     * @param delay        the delay between the termination of one
     *                     execution and the commencement of the next
     * @return a ScheduledFuture representing pending completion of
     * the task, and whose {@code get()} method will throw an
     * exception upon cancellation
     * @throws RejectedExecutionException if the task cannot be
     *                                    scheduled for execution
     * @throws NullPointerException       if command is null
     * @throws IllegalArgumentException   if delay less than or equal to zero
     */
    default ScheduledFuture<?> scheduleDelay(Runnable command, Duration initialDelay, Duration delay) {
        return scheduleWithFixedDelay(command, initialDelay.toNanos(), delay.toNanos(), TimeUnit.NANOSECONDS);
    }
}
