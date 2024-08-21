package xyz.fslabo.common.io;

import xyz.fslabo.common.base.JieCheck;
import xyz.fslabo.common.coll.JieArray;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.function.Function;

/**
 * This input stream is used to transform data from {@code source} stream by specified transformer. For example:
 * <pre>
 *     TransformInputStream trans = new TransformInputStream(source, 8, bytes-&gt;
 *         Arrays.copyOf(bytes, bytes.length / 2)
 *     );
 * </pre>
 * Example shows how to remove half of the data every 8-bytes:
 * <pre>
 *     "12345678" -&gt; "1234"
 *     "1234567887654321" -&gt; "12348765"
 * </pre>
 * This stream doesn't support mark/reset methods.
 *
 * @author fredsuvn
 */
public class TransformInputStream extends InputStream {

    private final InputStream source;
    private final int blockSize;
    private final Function<byte[], byte[]> transformer;

    private ByteBuffer buffer;
    private boolean end = false;

    /**
     * Constructs with source stream, block size and transformer.
     * The block size specifies how much data is read each time from source stream, and the read data will be pass to
     * given transformer and the transformer return transformed result.
     * If remaining bytes is less than block size, all remaining bytes will be read and pass to transform.
     * Transformer may return empty or null, in this case the passed read data is not enough to transform and need next
     * more read data.
     *
     * @param source      source stream
     * @param blockSize   block size
     * @param transformer given transformer
     */
    public TransformInputStream(InputStream source, int blockSize, Function<byte[], byte[]> transformer) {
        this.source = source;
        this.blockSize = blockSize;
        this.transformer = transformer;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        try {
            JieCheck.checkRangeInBounds(off, off + len, 0, b.length);
            if (end) {
                return -1;
            }
            int offset = off;
            int remaining = len;
            while (remaining > 0) {
                refreshBuffer();
                if (end) {
                    break;
                }
                int minLen = Math.min(buffer.remaining(), remaining);
                buffer.get(b, offset, minLen);
                remaining -= minLen;
                offset += minLen;
            }
            int readSize = len - remaining;
            if (readSize == 0 && end) {
                return -1;
            }
            return readSize;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized int read() throws IOException {
        try {
            while (true) {
                refreshBuffer();
                if (end) {
                    return -1;
                }
                if (buffer.hasRemaining()) {
                    return buffer.get() & 0x000000ff;
                }
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        try {
            if (n <= 0) {
                return 0;
            }
            long remaining = n;
            while (remaining > 0) {
                refreshBuffer();
                if (end) {
                    break;
                }
                int minLen = (int) Math.min(buffer.remaining(), remaining);
                if (minLen > 0) {
                    buffer.position(buffer.position() + minLen);
                    remaining -= minLen;
                }
            }
            return n - remaining;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized int available() throws IOException {
        try {
            refreshBuffer();
            return buffer.remaining();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private void refreshBuffer() {
        while (buffer == null || !buffer.hasRemaining()) {
            byte[] sourceBytes = JieIO.read(source, blockSize);
            if (sourceBytes == null) {
                end = true;
                return;
            }
            byte[] transformed = transformer.apply(sourceBytes);
            if (JieArray.isNotEmpty(transformed)) {
                buffer = ByteBuffer.wrap(transformed);
                return;
            }
        }
    }
}
