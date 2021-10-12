package xyz.srclab.common.utils

import xyz.srclab.common.base.nowMillis

/**
 * Snowflake is a unique id generator for distributed service providers.
 *
 * A snowflake id is a long type value, consists of:
 *
 * * `0` at first bits (not necessary);
 * * Timestamp value in next bits;
 * * Sequence value in next bits;
 * * Worker id in last bits;
 */
open class Snowflake {

    private val timestampOffset: Long
    private val timestampLeftShiftBits: Long
    private val workerIdLeftShiftBits: Long
    private val timestampMask: Long
    private val workerIdMask: Long
    private val sequenceMask: Long
    private val workerId: Long
    private var sequence = 0L
    private var lastTimestamp = -1L

    /**
     * Constructs with [timestampOffset], [timestampBits], [workerIdBits] and [workerId].
     *
     * @param timestampOffset timestamp offset
     * @param timestampBits   timestamp bits number
     * @param workerIdBits    worker id bits number
     * @param workerId        worker id
     */
    constructor(
        timestampOffset: Long,
        timestampBits: Int,
        workerIdBits: Int,
        workerId: Long
    ) {
        require(!(timestampBits <= 0 || timestampBits >= 63)) { "timestampBits: $timestampBits" }
        require(!(workerIdBits <= 0 || workerIdBits >= 63)) { "workerIdBits: $workerIdBits" }
        require(timestampBits + workerIdBits < 63) { "timestampBits + workerIdBits: " + (timestampBits + workerIdBits) }
        this.timestampOffset = timestampOffset
        this.workerId = workerId
        val sequenceBits = (63 - timestampBits - workerIdBits).toLong()
        timestampLeftShiftBits = workerIdBits + sequenceBits
        workerIdLeftShiftBits = sequenceBits
        timestampMask = -0x1L ushr 64 - timestampBits
        workerIdMask = -0x1L ushr 64 - workerIdBits
        sequenceMask = -0x1L ushr (64 - sequenceBits).toInt()
    }

    /**
     * Constructs with [workerId].
     *
     * @param workerId worker id
     */
    constructor(workerId: Long) : this(
        DEFAULT_TIMESTAMP_OFFSET_OFFSET.toLong(),
        DEFAULT_TIMESTAMP_BITS,
        DEFAULT_WORKER_ID_BITS,
        workerId
    ) {
    }

    /**
     * return next id.
     */
    @Synchronized
    fun next(): Long {
        var timestamp = timestamp()

        //Clock moved backwards
        check(timestamp >= lastTimestamp) {
            String.format(
                "Clock moved backwards. Refusing to generate id for %d milliseconds",
                lastTimestamp - timestamp
            )
        }
        if (lastTimestamp == timestamp) {
            sequence = sequence + 1 and sequenceMask
            //Overflow
            if (sequence == 0L) {
                timestamp = awaitForNextMillis(lastTimestamp)
            }
        } else {
            sequence = 0L
        }
        lastTimestamp = timestamp
        return (timestamp + timestampOffset and timestampMask shl timestampLeftShiftBits.toInt()
            or (workerId and workerIdMask shl workerIdLeftShiftBits.toInt()) //
            or (sequence and sequenceMask))
    }

    protected fun awaitForNextMillis(lastTimestamp: Long): Long {
        var timestamp = timestamp()
        while (timestamp <= lastTimestamp) {
            try {
                Thread.sleep(1)
            } catch (e: InterruptedException) {
                throw IllegalStateException("Await for next millis failed.", e)
            }
            timestamp = timestamp()
        }
        return timestamp
    }

    protected fun timestamp(): Long {
        return nowMillis()
    }

    companion object {
        const val DEFAULT_TIMESTAMP_OFFSET_OFFSET = 0
        const val DEFAULT_TIMESTAMP_BITS = 41
        const val DEFAULT_WORKER_ID_BITS = 10
    }
}