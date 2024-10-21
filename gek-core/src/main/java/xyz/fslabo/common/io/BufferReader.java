package xyz.fslabo.common.io;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

final class BufferReader extends Reader {

    private final CharBuffer buffer;

    BufferReader(char[] buf) {
        this(buf, 0, buf.length);
    }

    BufferReader(char[] buf, int offset, int length) {
        IOMisc.checkReadBounds(buf, offset, length);
        this.buffer = CharBuffer.wrap(buf, offset, length);
    }

    BufferReader(CharSequence chars) {
        this.buffer = CharBuffer.wrap(chars);
    }

    BufferReader(CharBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public int read() throws IOException {
        if (buffer.remaining() <= 0) {
            return -1;
        }
        return read0();
    }

    private int read0() throws IOException {
        try {
            return buffer.get() & 0xffff;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public int read(char[] b, int off, int len) throws IOException {
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

    private void read0(char[] b, int off, int avail) throws IOException {
        try {
            buffer.get(b, off, avail);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public int read(CharBuffer target) throws IOException {
        return buffer.read(target);
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
    public boolean ready() {
        return true;
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
