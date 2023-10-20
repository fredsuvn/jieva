package xyz.fsgek.common.io;

import java.io.IOException;
import java.io.Writer;
import java.nio.CharBuffer;

final class CharBufferWriter extends Writer {

    private final CharBuffer buffer;

    CharBufferWriter(CharBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public synchronized void write(char[] b, int off, int len) throws IOException {
        try {
            buffer.put(b, off, len);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized void write(int b) throws IOException {
        try {
            buffer.put((char) b);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized void write(String str) throws IOException {
        try {
            buffer.put(str);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized void write(String str, int off, int len) throws IOException {
        try {
            buffer.put(str, off, off + len);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized Writer append(CharSequence csq) throws IOException {
        try {
            buffer.append(csq);
        } catch (Exception e) {
            throw new IOException(e);
        }
        return this;
    }

    @Override
    public synchronized Writer append(CharSequence csq, int start, int end) throws IOException {
        try {
            buffer.append(csq, start, end);
        } catch (Exception e) {
            throw new IOException(e);
        }
        return this;
    }

    @Override
    public synchronized Writer append(char c) throws IOException {
        try {
            buffer.append(c);
        } catch (Exception e) {
            throw new IOException(e);
        }
        return this;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}