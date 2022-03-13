package xyz.srclab.common.run

import xyz.srclab.annotations.concurrent.ThreadSafe
import java.time.Duration
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Represents a thread latch,
 * a synchronization helper that allows one or more threads block to wait,
 * until the lock value of this latch equals to less than zero.
 *
 * @see CountDownLatch
 */
@ThreadSafe
interface RunLatch {

    /**
     * The lock value.
     */
    val lockValue: Long

    /**
     * Adds lock value with `1`.
     */
    fun lockUp() {
        lockUp(1)
    }

    /**
     * Adds lock value [value].
     */
    fun lockUp(value: Long)

    /**
     * Minus lock value with `1`.
     */
    fun lockDown() {
        lockDown(1)
    }

    /**
     * Minus lock value with [value].
     */
    fun lockDown(value: Long)

    /**
     * Sets lock value with [value].
     */
    fun lockTo(value: Long)

    /**
     * Blocks the current thread until the lock value of this latch equals to less than zero.
     */
    fun await()

    /**
     * Blocks the current thread until the lock value of this latch equals to less than zero.,
     * for [timeoutMillis] millis.
     */
    fun await(timeoutMillis: Long)

    /**
     * Blocks the current thread until the lock value of this latch equals to less than zero.,
     * for [timeout] duration.
     */
    fun await(timeout: Duration)

    companion object {

        /**
         * Returns a new [RunLatch] with lock value [initLockValue].
         *
         * Default [initLockValue] is 1.
         */
        @JvmOverloads
        @JvmStatic
        fun newRunLatch(initLockValue: Long = 1): RunLatch {
            return RunLatchImpl(initLockValue)
        }

        private class RunLatchImpl(
            override var lockValue: Long
        ) : RunLatch {

            @Volatile
            private var countDownLatch: CountDownLatch? = null

            init {
                resetCountDownLatch()
            }

            @Synchronized
            override fun lockDown(value: Long) {
                lockValue -= value
                resetCountDownLatch()
            }

            @Synchronized
            override fun lockTo(value: Long) {
                lockValue = value
                resetCountDownLatch()
            }

            @Synchronized
            override fun lockUp(value: Long) {
                lockValue += value
                resetCountDownLatch()
            }

            private fun resetCountDownLatch() {
                val countDownLatch = this.countDownLatch
                if (lockValue > 0) {
                    if (countDownLatch === null) {
                        this.countDownLatch = CountDownLatch(1)
                    } else {
                        if (countDownLatch.count <= 0) {
                            this.countDownLatch = CountDownLatch(1)
                        }
                    }
                } else {
                    if (countDownLatch !== null) {
                        countDownLatch.countDown()
                        this.countDownLatch = null
                    }
                }
            }

            override fun await() {
                val countDownLatch = this.countDownLatch
                if (countDownLatch === null) {
                    return
                }
                countDownLatch.await()
            }

            override fun await(timeoutMillis: Long) {
                val countDownLatch = this.countDownLatch
                if (countDownLatch === null) {
                    return
                }
                countDownLatch.await(timeoutMillis, TimeUnit.MILLISECONDS)
            }

            override fun await(timeout: Duration) {
                val countDownLatch = this.countDownLatch
                if (countDownLatch === null) {
                    return
                }
                countDownLatch.await(timeout.toNanos(), TimeUnit.NANOSECONDS)
            }
        }
    }
}