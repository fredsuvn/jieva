package xyz.srclab.common.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

final class RandomOutputStream extends OutputStream {

    private final RandomAccessFile random;
    private final long offset;

    RandomOutputStream(RandomAccessFile random, long offset) {
        this.random = random;
        this.offset = offset;
        try {
            this.random.seek(offset);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        random.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        random.write(b);
    }

    @Override
    public void write(int b) throws IOException {
        random.write(b);
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
        random.close();
    }
}