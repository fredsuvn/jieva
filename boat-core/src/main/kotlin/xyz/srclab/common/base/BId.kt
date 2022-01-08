@file:JvmName("BId")

package xyz.srclab.common.base

import java.util.*

val DEFAULT_SNOWFLAKE_ID = SnowflakeId(0)

fun uuid(): String {
    return UUID.randomUUID().toString()
}

fun snowflakeId(): Long {
    return DEFAULT_SNOWFLAKE_ID.next()
}

fun newSnowflakeId(workerId: Int): SnowflakeId {
    return SnowflakeId(workerId)
}

/**
 * Snowflake is a unique id generator for distributed service providers.
 *
 * A snowflake id is a long type value (64 bits), consists of:
 *
 * ```
 * |--reserved--|--timestamp--|--workerId--|--sequence--|
 * ```
 *
 * By default, bits of each part are:
 *
 * * reserved: 1
 * * timestamp: 41
 * * workerId: 10
 * * sequence: 12
 */
open class SnowflakeId {

    private val reservedBits: Int
    private val timestampBits: Int
    private val workerIdBits: Int
    private val sequenceBits: Int

    private val maskReservedLeftBits: Int
    private val maskTimestampLeftBits: Int
    private val maskTimestampRightBits: Int
    private val maskSequenceLeftBits: Int

    private val workerId: Long

    private var sequence = 0L
    private var lastTimestamp = -1L
    private val maxSequenceMask: Long

    private val maxWaitTimeMilli: Long

    @JvmOverloads
    constructor(
        reservedBits: Int,
        timestampBits: Int,
        workerIdBits: Int,
        workerId: Int,
        maxWaitTimeMilli: Long = 1000
    ) {
        require(reservedBits in 0..61) { "reservedBits must in [0, 61]: $reservedBits" }
        require(timestampBits in 0..61) { "timestampBits must in [0, 61]: $timestampBits" }
        require(workerIdBits in 0..32) { "workerIdBits must in [0, 32]: $workerIdBits" }
        require(reservedBits + timestampBits + workerIdBits in 0..63) {
            "reservedBits + timestampBits + workerIdBits must in [0, 63]: $workerIdBits"
        }
        this.reservedBits = reservedBits
        this.timestampBits = timestampBits
        this.workerIdBits = workerIdBits
        this.sequenceBits = 64 - reservedBits - timestampBits - workerIdBits

        maskReservedLeftBits = 64 - reservedBits
        maskTimestampLeftBits = 64 - timestampBits
        maskTimestampRightBits = reservedBits
        maskSequenceLeftBits = 64 - sequenceBits
        this.maxSequenceMask = maskBits(BJava.LONG_MASK_VALUE, maskSequenceLeftBits, maskSequenceLeftBits).inv()

        val maskWorkerIdLeftBits = 64 - workerIdBits
        val maskWorkerIdRightBits = reservedBits + timestampBits
        this.workerId = maskBits(workerId.toUnsignedLong(), maskWorkerIdLeftBits, maskWorkerIdRightBits)

        this.maxWaitTimeMilli = maxWaitTimeMilli
    }

    constructor(workerId: Int) : this(
        DEFAULT_RESERVED_BITS,
        DEFAULT_TIMESTAMP_BITS,
        DEFAULT_WORKER_ID_BITS,
        workerId
    )

    /**
     * return next id.
     */
    @Synchronized
    fun next(): Long {
        val startTime = epochMilli()
        var timestamp = startTime
        do {
            //Clock moved backwards
            if (lastTimestamp > timestamp) {
                throw IllegalStateException(
                    "Clock moved backwards. Refusing to generate id for ${lastTimestamp - timestamp} milliseconds."
                )
            }
            if (timestamp > lastTimestamp) {
                sequence = 0
                break
            }
            val nextSequence = (sequence + 1) and maxSequenceMask
            //Overflow
            if (nextSequence == 0L) {
                sleep(1)
                timestamp = epochMilli()
                if (timestamp - startTime > maxWaitTimeMilli) {
                    throw IllegalStateException("Sequence overflow.")
                }
                continue
            }
            sequence = nextSequence
            lastTimestamp = timestamp
            break
        } while (true)
        return maskTimestamp(timestamp) or workerId or maskSequence(sequence)
    }

    private fun maskTimestamp(value: Long): Long {
        return maskBits(value, maskTimestampLeftBits, maskTimestampRightBits)
    }

    private fun maskSequence(value: Long): Long {
        return maskBits(value, maskSequenceLeftBits, maskSequenceLeftBits)
    }

    private fun maskBits(value: Long, leftShiftBits: Int, rightShiftBits: Int): Long {
        return (value shl leftShiftBits) ushr rightShiftBits
    }

    companion object {
        const val DEFAULT_RESERVED_BITS = 1
        const val DEFAULT_TIMESTAMP_BITS = 41
        const val DEFAULT_WORKER_ID_BITS = 10
    }
}
