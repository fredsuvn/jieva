package xyz.srclab.common.utils

import xyz.srclab.common.base.sleep
import java.util.function.BiFunction
import java.util.function.Function

/**
 * This class represents a type of serial, of which consists of twe part: `compare` and `serial`.
 * When calls the `next()` method, it:
 *
 * * First gets a new `compare` by [compare];
 * * Let new one compare to the last one;
 * * If they are not equal, set the new compare as `last compare`,
 * and return the result from the new compare and [initSerial] by [doFinal];
 * * If they are equal, gets a new `serial` by [serial];
 * * If the new serial is null, that means the serial is overflow and return null;
 * * If the new serial is ok, set the new serial as `last serial`,
 * and return the result from the last compare and new serial by [doFinal].
 *
 * Note legal compare and serial should be not null.
 *
 * These process is very similar to the snowflake algorithm, but it doesn't require `increment`.
 *
 * @param initCompare init compare
 * @param initSerial init serial
 * @param compare to create new compare
 * @param serial to create new serial, or null if overflow
 * @param doFinal to create final result with compare and serial
 */
open class CompareSerial<C : Any, S : Any, T : Any>(
    private val initCompare: C,
    private val initSerial: S,
    private val compare: Function<C, C>,
    private val serial: Function<S, S?>,
    private val doFinal: BiFunction<C, S, T>,
) {

    private var lastCompare: C = initCompare
    private var lastSerial: S = initSerial

    /**
     * Returns next value, same as `next(0, 0)`.
     */
    fun next(): T {
        return next(0, 0)
    }

    /**
     * Returns next value, same as `next(retryCount, 1000)`.
     */
    fun next(retryCount: Int): T {
        return next(retryCount, 1000L)
    }

    /**
     * Returns next value, retry count is [retryCount], and delay [delayMillis] after each retry.
     */
    fun next(retryCount: Int, delayMillis: Long): T {
        val result = nextOrNull(retryCount, delayMillis)
        if (result === null) {
            throw IllegalStateException("Serial overflow in same compare.")
        }
        return result
    }

    /**
     * Returns next value, or null if serial was overflow.
     */
    fun nextOrNull(): T? {
        return next0()
    }

    /**
     * Returns next value, or null if serial was overflow, same as `nextOrNull(retryCount, 1000)`.
     */
    fun nextOrNull(retryCount: Int): T? {
        return nextOrNull(retryCount, 1000L)
    }

    /**
     * Returns next value, or null if serial was overflow,
     * retry count is [retryCount], and delay [delayMillis] after each retry.
     */
    fun nextOrNull(retryCount: Int, delayMillis: Long): T? {
        val result = next0()
        if (result !== null) {
            return result
        }
        var count = retryCount
        while (count > 0) {
            if (delayMillis > 0) {
                sleep(delayMillis)
            }
            val r = nextOrNull()
            if (r !== null) {
                return r
            }
            count--
        }
        return null
    }

    @Synchronized
    private fun next0(): T? {
        val newCompare = compare.apply(lastCompare)
        if (newCompare == lastCompare) {
            val newSerial = serial.apply(lastSerial)
            if (newSerial === null) {
                return null
            }
            lastSerial = newSerial
            return doFinal.apply(newCompare, newSerial)
        } else {
            lastCompare = newCompare
            lastSerial = initSerial
            return doFinal.apply(lastCompare, initSerial)
        }
    }
}