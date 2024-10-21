package xyz.fslabo.common.io;

import java.io.IOException;
import java.io.InputStream;

final class BytesInputStream extends InputStream {

    private final byte[] buf;
    private int pos;
    private int mark = -1;
    private final int count;

    BytesInputStream(byte[] buf) {
        this(buf, 0, buf.length);
    }

    BytesInputStream(byte[] buf, int offset, int length) {
        IOMisc.checkReadBounds(buf, offset, length);
        this.buf = buf;
        this.pos = offset;
        this.count = Math.min(offset + length, buf.length);
    }

    public int read() {
        return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    public int read(byte[] b, int off, int len) {
        IOMisc.checkReadBounds(b, off, len);
        if (len <= 0) {
            return 0;
        }
        if (pos >= count) {
            return -1;
        }
        int avail = count - pos;
        avail = Math.min(len, avail);
        System.arraycopy(buf, pos, b, off, avail);
        pos += avail;
        return avail;
    }

    public long skip(long n) {
        if (n <= 0) {
            return 0;
        }
        int avail = count - pos;
        avail = (int) Math.min(n, avail);
        if (avail <= 0) {
            return 0;
        }
        pos += avail;
        return avail;
    }

    public int available() {
        return count - pos;
    }

    public boolean markSupported() {
        return true;
    }

    public void mark(int readAheadLimit) {
        mark = pos;
    }

    public void reset() throws IOException {
        if (mark < 0) {
            throw new IOException("Mark has not been set.");
        }
        pos = mark;
    }

    public void close() {
    }
}
