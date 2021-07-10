package xyz.srclab.common.id;

/**
 * SnowFlake id generator.
 */
public class SnowflakeIdGenerator {

    private final long timestampOffset;

    private final long timestampLeftShiftBits;
    private final long workerIdLeftShiftBits;

    private final long timestampMask;
    private final long workerIdMask;
    private final long sequenceMask;

    private final long workerId;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long workerId) {
        this(0, 41, 10, workerId);
    }

    /**
     * Note used timestamp is timestamp + timestampOffset。
     *
     * @param timestampOffset timestamp offset
     * @param timestampBits   timestamp bits number
     * @param workerIdBits    worker id bits number
     * @param workerId        worker id value
     */
    public SnowflakeIdGenerator(
        long timestampOffset,
        int timestampBits,
        int workerIdBits,
        long workerId
    ) {
        if (timestampBits <= 0 || timestampBits >= 63) {
            throw new IllegalArgumentException("timestampBits: " + timestampBits);
        }
        if (workerIdBits <= 0 || workerIdBits >= 63) {
            throw new IllegalArgumentException("workerIdBits: " + workerIdBits);
        }
        if (timestampBits + workerIdBits >= 63) {
            throw new IllegalArgumentException("timestampBits + workerIdBits: " + (timestampBits + workerIdBits));
        }
        this.timestampOffset = timestampOffset;
        this.workerId = workerId;
        long sequenceBits = 63 - timestampBits - workerIdBits;

        this.timestampLeftShiftBits = workerIdBits + sequenceBits;
        this.workerIdLeftShiftBits = sequenceBits;

        this.timestampMask = 0xffffffffffffffffL >>> (64 - timestampBits);
        this.workerIdMask = 0xffffffffffffffffL >>> (64 - workerIdBits);
        this.sequenceMask = 0xffffffffffffffffL >>> (64 - sequenceBits);
    }

    /**
     * return next id.
     */
    public synchronized long next() {
        long timestamp = timestamp();

        //Clock moved backwards
        if (timestamp < lastTimestamp) {
            throw new IllegalStateException(
                String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //Overflow
            if (sequence == 0) {
                timestamp = awaitForNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return (((timestamp + timestampOffset) & timestampMask) << timestampLeftShiftBits)
            | ((workerId & workerIdMask) << workerIdLeftShiftBits) //
            | (sequence & sequenceMask);
    }

    protected long awaitForNextMillis(long lastTimestamp) {
        long timestamp = timestamp();
        while (timestamp <= lastTimestamp) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                throw new IllegalStateException("Await for next millis failed.", e);
            }
            timestamp = timestamp();
        }
        return timestamp;
    }

    protected long timestamp() {
        return System.currentTimeMillis();
    }
}
