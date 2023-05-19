package xyz.srclab.common.io;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class ByteBufferOutputStream extends OutputStream {

    private final ByteBuffer buffer;

    ByteBufferOutputStream(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        try {
            buffer.put(b, off, len);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
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
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }
}