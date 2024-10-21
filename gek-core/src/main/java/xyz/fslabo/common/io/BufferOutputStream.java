package xyz.fslabo.common.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class BufferOutputStream extends OutputStream {

    private final ByteBuffer buffer;

    BufferOutputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int b) throws IOException {
        try {
            buffer.put((byte) b);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        IOMisc.checkReadBounds(b, off, len);
        if (len <= 0) {
            return;
        }
        try {
            buffer.put(b, off, len);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}