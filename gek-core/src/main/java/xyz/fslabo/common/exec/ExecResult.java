package xyz.fslabo.common.exec;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * Result of async execution, extension of {@link Future} and {@link ScheduledFuture}.
 *
 * @param <V> type of result
 * @author fredsuvn
 */
public interface ExecResult<V> extends ScheduledFuture<V> {

    /**
     * Waits if necessary for the computation to complete, and then retrieves its result. This method is unchecked
     * version of {@link #get()}, it is equivalent to:
     * <pre>
     *     try {
     *         return get();
     *     } catch (Exception e) {
     *         throw new ExecException(e);
     *     }
     * </pre>
     *
     * @return the computed result
     * @throws ExecException if any exception occurs,
     */
    default V getUnchecked() throws ExecException {
        try {
            return get();
        } catch (Exception e) {
            throw new ExecException(e);
        }
    }

    /**
     * Waits if necessary for at most the given time for the computation to complete, and then retrieves its result, if
     * available. This method is unchecked version of {@link #get()}, it is equivalent to:
     * <pre>
     *     try {
     *         return get(timeout, unit);
     *     } catch (Exception e) {
     *         throw new ExecException(e);
     *     }
     * </pre>
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the computed result
     * @throws ExecException if any exception occurs,
     */
    default V getUnchecked(long timeout, TimeUnit unit) throws ExecException {
        try {
            return get(timeout, unit);
        } catch (Exception e) {
            throw new ExecException(e);
        }
    }

    /**
     * Waits if necessary for at most the given time for the computation to complete, and then retrieves its result, if
     * available. This method is unchecked version of {@link #get()}, it is equivalent to:
     * <pre>
     *     try {
     *         return get(timeout);
     *     } catch (Exception e) {
     *         throw new ExecException(e);
     *     }
     * </pre>
     *
     * @param timeout the maximum time to wait
     * @return the computed result
     * @throws ExecException if any exception occurs,
     */
    default V getUnchecked(Duration timeout) throws ExecException {
        try {
            return get(timeout);
        } catch (Exception e) {
            throw new ExecException(e);
        }
    }

    /**
     * Waits if necessary for at most the given time for the computation to complete, and then retrieves its result, if
     * available.
     *
     * @param timeout the maximum time to wait
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException    if the computation threw an
     *                               exception
     * @throws InterruptedException  if the current thread was interrupted
     *                               while waiting
     * @throws TimeoutException      if the wait timed out
     */
    default V get(Duration timeout) throws InterruptedException, ExecutionException, TimeoutException {
        return get(timeout.toNanos(), TimeUnit.NANOSECONDS);
    }

    /**
     * Returns the remaining delay associated with this object.
     *
     * @return the remaining delay, zero or negative values indicate that the delay has already elapsed
     */
    default Duration getDelay() {
        return Duration.ofNanos(getDelay(TimeUnit.NANOSECONDS));
    }
}
