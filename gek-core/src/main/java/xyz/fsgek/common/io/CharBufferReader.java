package xyz.fsgek.common.io;

import xyz.fsgek.common.base.GekCheck;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

final class CharBufferReader extends Reader {

    private final CharBuffer buffer;

    CharBufferReader(CharBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public synchronized int read(char[] b, int off, int len) throws IOException {
        try {
            GekCheck.checkRangeInBounds(off, off + len, 0, b.length);
            if (len == 0) {
                return 0;
            }
            int actualLength = Math.min(buffer.remaining(), len);
            if (actualLength <= 0) {
                return -1;
            }
            buffer.get(b, off, actualLength);
            return actualLength;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized int read() throws IOException {
        try {
            if (buffer.remaining() <= 0) {
                return -1;
            }
            return buffer.get() & 0xFF;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized int read(CharBuffer target) throws IOException {
        return buffer.read(target);
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        try {
            if (n <= 0) {
                return 0;
            }
            int remaining = buffer.remaining();
            if (remaining <= 0) {
                return 0;
            }
            if (n >= remaining) {
                buffer.position(buffer.limit());
                return remaining;
            }
            long k = buffer.position() + n;
            buffer.position((int) k);
            return n;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public boolean ready() {
        return true;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void mark(int readAheadLimit) {
        buffer.mark();
    }

    @Override
    public synchronized void reset() throws IOException {
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
