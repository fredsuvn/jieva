/**
 * Id utilities.
 */
@file:JvmName("IdBt")

package xyz.srclab.common.base

import java.util.*

/**
 * Returns a UUID, such as:
 *
 * ```
 * a7671f17ea414a1abefbb31bcac89201
 * ```
 */
fun uuid(): String {
    val withHyphens = UUID.randomUUID().toString()
    return hyphenMatcher().removeFrom(withHyphens)
}

/**
 * Returns a Snowflake id. See [SnowflakeId].
 *
 * Note this method use default config and workerId is random.
 *
 * @see SnowflakeId
 */
fun snowflakeId(): Long {
    return IdBtHolder.currentSnowflakeId.next()
}

/**
 * [SnowflakeId] is a unique id generator for distributed nodes.
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

    private val workerId: Long

    private var lastSequence = 0L
    private var lastTimestamp = 0L

    //Go write ASM, C or Rust if you like mask.
    //Write easy codes in JAVA!
    //private val maxSequenceMask: Long

    private val maxSequence: Long
    private val maxWaitTimeMilli: Long

    /**
     * Constructs a [SnowflakeId].
     *
     * @param reservedBits reserved bits number
     * @param timestampBits timestamp bits number
     * @param workerIdBits workerId bits number
     * @param workerId worker Id
     * @param maxWaitTimeMilli [SnowflakeId] will sleep for a short millis,
     * this parameter specifies the max sleep millis. Default is 1000.
     */
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
        this.workerId = shiftBits(workerId.toLong(), 64 - workerIdBits, reservedBits + timestampBits)
        this.maxSequence = shiftBits(JavaLong.MAX_VALUE, 64 - sequenceBits, 64 - sequenceBits)
        this.maxWaitTimeMilli = maxWaitTimeMilli
    }

    /**
     * Constructs a [SnowflakeId] with specified worker id.
     */
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
    open fun next(): Long {
        val startTime = currentMillis()
        var currentTime = startTime
        while (true) {
            if (currentTime < lastTimestamp) {
                throw IllegalStateException(
                    "Clock moved backwards. Refusing to generate id for ${lastTimestamp - currentTime} milliseconds."
                )
            }
            if (currentTime == lastTimestamp) {
                val nextSequence = lastSequence + 1
                if (nextSequence <= maxSequence) {
                    lastSequence = nextSequence
                    break
                }
                //Overflow
                sleep(1)
                currentTime = currentMillis()
                if (currentTime - startTime > maxWaitTimeMilli) {
                    throw IllegalStateException("Sequence overflow.")
                }
                continue
            }
            //currentTime > lastTime
            lastSequence = 0
            break
        }
        lastTimestamp = currentTime
        return shiftBits(currentTime, 64 - timestampBits, reservedBits) or workerId or lastSequence
    }

    private fun shiftBits(value: Long, left: Int, right: Int): Long {
        return (value shl left) ushr right
    }

    companion object {
        const val DEFAULT_RESERVED_BITS = 1
        const val DEFAULT_TIMESTAMP_BITS = 41
        const val DEFAULT_WORKER_ID_BITS = 10
    }
}

private object IdBtHolder {
    val currentSnowflakeId: SnowflakeId = SnowflakeId(systemNanos().toInt())
}