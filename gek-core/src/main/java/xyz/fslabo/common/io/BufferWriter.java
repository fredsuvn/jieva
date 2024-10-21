package xyz.fslabo.common.io;

import java.io.IOException;
import java.nio.CharBuffer;

final class BufferWriter extends AbstractWriter {

    private final CharBuffer buffer;

    BufferWriter(char[] buf) {
        this(buf, 0, buf.length);
    }

    BufferWriter(char[] buf, int offset, int length) {
        IOMisc.checkReadBounds(buf, offset, length);
        this.buffer = CharBuffer.wrap(buf, offset, length);
    }

    BufferWriter(CharBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    protected void doWrite(char c) {
        buffer.put(c);
    }

    @Override
    protected void doWrite(char[] cbuf, int off, int len) {
        buffer.put(cbuf, off, len);
    }

    @Override
    protected void doWrite(String str, int off, int len) {
        buffer.put(str, off, off + len);
    }

    @Override
    protected void doAppend(CharSequence csq, int start, int end) {
        buffer.append(csq, start, end);
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws IOException {
    }
}