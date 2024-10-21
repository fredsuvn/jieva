package xyz.fslabo.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

final class BufferInputStream extends InputStream {

    private final ByteBuffer buffer;

    BufferInputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int read() throws IOException {
        if (!buffer.hasRemaining()) {
            return -1;
        }
        return read0();
    }

    private int read0() throws IOException {
        try {
            return buffer.get() & 0xff;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        IOMisc.checkReadBounds(b, off, len);
        if (len <= 0) {
            return 0;
        }
        if (!buffer.hasRemaining()) {
            return -1;
        }
        int avail = Math.min(buffer.remaining(), len);
        read0(b, off, avail);
        return avail;
    }

    private void read0(byte[] b, int off, int avail) throws IOException {
        try {
            buffer.get(b, off, avail);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public long skip(long n) throws IOException {
        if (n <= 0) {
            return 0;
        }
        int avail = (int) Math.min(buffer.remaining(), n);
        if (avail <= 0) {
            return 0;
        }
        skip0(avail);
        return avail;
    }

    private void skip0(int avail) throws IOException {
        try {
            buffer.position(buffer.position() + avail);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public int available() {
        return buffer.remaining();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readlimit) {
        buffer.mark();
    }

    @Override
    public void reset() throws IOException {
        try {
            buffer.reset();
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() {
    }
}
