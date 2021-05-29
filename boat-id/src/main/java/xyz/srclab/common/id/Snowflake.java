package xyz.srclab.common.id;

import org.apache.commons.lang3.StringUtils;
import xyz.srclab.common.exception.ImpossibleException;

import java.math.BigInteger;
import java.util.Objects;

/**
 * @author sunqian
 */
public class Snowflake {

    private final long timestamp;
    private final int timestampBitsCount;
    private final long workerId;
    private final int workerIdBitsCount;
    private final long sequence;
    private final int sequenceBitsCount;
    private final int length;

    public Snowflake(
        long timestamp, int timestampBitsCount,
        long workerId, int workerIdBitsCount,
        long sequence, int sequenceBitsCount,
        int length
    ) {
        this.timestamp = timestamp;
        this.timestampBitsCount = timestampBitsCount;
        this.workerId = workerId;
        this.workerIdBitsCount = workerIdBitsCount;
        this.sequence = sequence;
        this.sequenceBitsCount = sequenceBitsCount;
        this.length = length;
    }

    public long timestamp() {
        return timestamp;
    }

    public long workerId() {
        return workerId;
    }

    public long sequence() {
        return sequence;
    }

    public int length() {
        return length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Snowflake)) return false;
        Snowflake snowflake = (Snowflake) o;
        return timestamp == snowflake.timestamp && workerId == snowflake.workerId && sequence == snowflake.sequence;
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, workerId, sequence);
    }

    @Override
    public String toString() {
        if (timestampBitsCount + workerIdBitsCount + sequenceBitsCount <= 64) {
            return StringUtils.leftPad(String.valueOf(joinAsLong()), length, '0');
        } else {
            return StringUtils.leftPad(new BigInteger(joinAsBytes()).toString(), length, '0');
        }
    }

    private long joinAsLong() {
        long id = sequence;
        id |= workerId << sequenceBitsCount;
        id |= timestamp << (sequenceBitsCount + workerIdBitsCount);
        return id;
    }

    private byte[] joinAsBytes() {
        long lowId = sequence;
        long highId = 0;
        lowId |= workerId << sequenceBitsCount;
        if (sequenceBitsCount + workerIdBitsCount == 64) {
            highId = timestamp;
        } else if (sequenceBitsCount + workerIdBitsCount < 64) {
            int lowTimestampBits = 64 - (sequenceBitsCount + workerIdBitsCount);
            lowId |= timestamp << sequenceBitsCount + workerIdBitsCount;
            highId = timestamp >>> lowTimestampBits;
        } else {
            throw new ImpossibleException("sequenceBitsCount + workerIdBitsCount > 64???");
        }
        byte[] result = new byte[16];
        result[0] = (byte) (highId >>> 56);
        result[1] = (byte) (highId >>> 48);
        result[2] = (byte) (highId >>> 40);
        result[3] = (byte) (highId >>> 32);
        result[4] = (byte) (highId >>> 24);
        result[5] = (byte) (highId >>> 16);
        result[6] = (byte) (highId >>> 8);
        result[7] = (byte) (highId >>> 0);
        result[8] = (byte) (lowId >>> 56);
        result[9] = (byte) (lowId >>> 48);
        result[10] = (byte) (lowId >>> 40);
        result[11] = (byte) (lowId >>> 32);
        result[12] = (byte) (lowId >>> 24);
        result[13] = (byte) (lowId >>> 16);
        result[14] = (byte) (lowId >>> 8);
        result[15] = (byte) (lowId >>> 0);
        return result;
    }
}
