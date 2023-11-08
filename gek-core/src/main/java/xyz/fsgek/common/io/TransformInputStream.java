package xyz.fsgek.common.io;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.base.GekCheck;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.function.Function;

public class TransformInputStream extends InputStream {

    private final InputStream source;
    private final int bufferSize;
    private final Function<ByteBuffer, ByteBuffer> transformer;

    private ByteBuffer buffer;
    private boolean end = false;

    public TransformInputStream(InputStream source, int bufferSize, Function<ByteBuffer, ByteBuffer> transformer) {
        this.source = source;
        this.bufferSize = bufferSize;
        this.transformer = transformer;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        GekCheck.checkRangeInBounds(off, off + len, 0, b.length);
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
    }

    @Override
    public int read() throws IOException {
        while (true) {
            refreshBuffer();
            if (end) {
                return -1;
            }
            if (buffer.hasRemaining()) {
                return buffer.get() & 0x000000ff;
            }
        }
    }

    private void refreshBuffer() {
        if (buffer == null || !buffer.hasRemaining()) {
            byte[] sourceBytes = GekIO.read(source, bufferSize);
            if (sourceBytes == null) {
                end = true;
                return;
            }
            buffer = transformer.apply(ByteBuffer.wrap(sourceBytes));
        }
    }

    @Override
    public long skip(long n) throws IOException {
        GekCheck.checkRangeInBounds(off, off + len, 0, b.length);
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
    }

    @Override
    public int available() throws IOException {
        return super.available();
    }
}
