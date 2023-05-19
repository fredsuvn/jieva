package xyz.srclab.common.io;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

final class CharBufferWriter extends Writer {

    private final CharBuffer buffer;

    CharBufferWriter(CharBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(char[] b, int off, int len) throws IOException {
        try {
            buffer.put(b, off, len);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(char[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(int b) throws IOException {
        try {
            buffer.put((char) b);
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