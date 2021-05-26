package xyz.srclab.common.id;

import org.jetbrains.annotations.NotNull;
import xyz.srclab.common.lang.Checks;
import xyz.srclab.common.lang.Current;
import xyz.srclab.common.lang.Nums;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * Id component to generate id component with Snowflake， result type is {@link Snowflake}.
 * <p>
 * It usually used in {@link IdSpec}, In {@link IdSpec}, its type is {@link SnowflakeComponent#TYPE},
 * and has at most 4 arguments:
 * length (default is 20),
 * timestampBitsCount (default 41 bits),
 * workerIdBitsCount (default 10 bits),
 * sequenceBitsCount (default 12 bits).
 * <p>
 * For example:
 * <pre>
 *     //May print: seq-06803239610792857600-tail
 *     String spec = "seq-{Snowflake, 20, 41, 10, 12}-tail";
 * </pre>
 * Bits count argument must in [1, 63] bits long.
 * <p>
 * You can extend this class to custom, override {@link #workerId()} to custom worker id.
 *
 * @author sunqian
 * @see IdSpec
 * @see Snowflake
 */
public class SnowflakeComponent implements IdComponent<Snowflake> {

    public static final String TYPE = "Snowflake";

    private long timestamp = Current.millis();
    private long workerId = workerId();
    private long sequence = 0;
    private int length = 20;

    private int timestampBitsCount = 41;
    private int workerIdBitsCount = 10;
    private int sequenceBitsCount = 12;
    private long timestampBitsMask = 0L;
    private long workerIdBitsMask = 0L;
    private long sequenceBitsMask = 0L;

    @NotNull
    @Override
    public String type() {
        return TYPE;
    }

    /**
     * Return work id. Default is come from {@link InetAddress}.
     */
    public long workerId() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            byte[] bytes = address.getAddress();
            long workerId = 0;
            for (int i = bytes.length - 1, j = 0; i >= 0 && j <= 56; i--, j += 8) {
                long b = bytes[i] & 0x00000000000000ffL;
                workerId |= (b << j);
            }
            return workerId;
        } catch (UnknownHostException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void init(@NotNull List<?> args) {
        if (args.size() >= 1) {
            length = Nums.toInt(args.get(0));
        }
        if (args.size() >= 2) {
            timestampBitsCount = Nums.toInt(args.get(1));
            if (!Checks.isIndexInBounds(timestampBitsCount, 1, 64)) {
                throw new IllegalArgumentException("timestampBitsCount must in [1, 63].");
            }
        }
        if (args.size() >= 3) {
            workerIdBitsCount = Nums.toInt(args.get(2));
            if (!Checks.isIndexInBounds(workerIdBitsCount, 1, 64)) {
                throw new IllegalArgumentException("workerIdBitsCount must in [1, 63].");
            }
        }
        if (args.size() >= 4) {
            sequenceBitsCount = Nums.toInt(args.get(3));
            if (!Checks.isIndexInBounds(sequenceBitsCount, 1, 64)) {
                throw new IllegalArgumentException("sequenceBitsCount must in [1, 63].");
            }
        }
        timestampBitsMask = ~(0xffffffffffffffffL << timestampBitsCount);
        workerIdBitsMask = ~(0xffffffffffffffffL << workerIdBitsCount);
        sequenceBitsMask = ~(0xffffffffffffffffL << sequenceBitsCount);
    }

    @Override
    public synchronized Snowflake newValue(@NotNull IdContext context) {
        long now = Current.millis();
        if (now < timestamp) {
            throw new IllegalStateException(
                "Clock moved backwards. Refusing to generate id for " + (timestamp - now) + " milliseconds"
            );
        }
        if (now == timestamp) {
            if (isOverflow(sequence, sequenceBitsMask)) {
                throw new IllegalStateException("Sequence overflow in one millisecond: " + sequence);
            }
            return new Snowflake(now & timestampBitsMask, timestampBitsCount,
                workerId & workerIdBitsMask, workerIdBitsCount,
                sequence++, sequenceBitsCount,
                length);
        }
        sequence = 0;
        timestamp = now;
        return new Snowflake(now & timestampBitsMask, timestampBitsCount,
            workerId & workerIdBitsMask, workerIdBitsCount,
            sequence++, sequenceBitsCount,
            length);
    }

    private boolean isOverflow(long value, long mask) {
        return (value & ~mask) != 0;
    }
}
