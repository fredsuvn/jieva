package xyz.fslabo.common.io;

import java.io.IOException;
import java.io.OutputStream;

final class BytesOutputStream extends OutputStream {

    private final byte[] buf;
    private final int end;
    private int pos;

    BytesOutputStream(byte[] buf) {
        this(buf, 0, buf.length);
    }

    BytesOutputStream(byte[] buf, int offset, int length) {
        IOMisc.checkReadBounds(buf, offset, length);
        this.buf = buf;
        this.end = offset + length;
        this.pos = offset;
    }

    @Override
    public void write(int b) throws IOException {
        if (end - pos < 1) {
            throw new IOException("The backing array has insufficient capacity remaining.");
        }
        buf[pos] = (byte) b;
        pos++;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        IOMisc.checkReadBounds(b, off, len);
        if (len <= 0) {
            return;
        }
        if (end - pos < len) {
            throw new IOException("The backing array has insufficient capacity remaining.");
        }
        System.arraycopy(b, off, buf, pos, len);
        pos += len;
    }
}