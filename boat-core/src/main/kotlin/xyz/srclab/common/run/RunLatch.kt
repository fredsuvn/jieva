package xyz.srclab.common.run

import xyz.srclab.annotations.concurrent.ThreadSafe
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Run latch, switch to go on or pause current running thread.
 *
 * Note this is thread-safe.
 *
 * @see CountDownLatch
 */
@ThreadSafe
interface RunLatch {

    /**
     * Opens latch.
     */
    fun open()

    /**
     * Closes latch.
     */
    fun close()

    /**
     * Awaits forever.
     */
    fun await()

    /**
     * Awaits for [timeout] milliseconds.
     */
    fun await(timeout: Long)

    /**
     * Awaits for [timeout].
     */
    fun await(timeout: Duration)

    companion object {

        /**
         * Returns a new [RunLatch] with `close` status.
         */
        @JvmStatic
        fun newRunLatch(): RunLatch {
            return RunLatchImpl()
        }

        private class RunLatchImpl : RunLatch {

            @Volatile
            private var open: Boolean = false

            @Volatile
            private var countDownLatch: CountDownLatch = CountDownLatch(1)

            @Synchronized
            override fun open() {
                if (open) {
                    return
                }
                open = true
                countDownLatch.countDown()
            }

            @Synchronized
            override fun close() {
                if (!open) {
                    return
                }
                open = false
                countDownLatch = CountDownLatch(1)
            }

            override fun await() {
                if (open) {
                    return
                }
                countDownLatch.await()
            }

            override fun await(timeout: Long) {
                if (open) {
                    return
                }
                countDownLatch.await(timeout, TimeUnit.MILLISECONDS)
            }

            override fun await(timeout: Duration) {
                if (open) {
                    return
                }
                countDownLatch.await(timeout.toNanos(), TimeUnit.NANOSECONDS)
            }
        }
    }
}