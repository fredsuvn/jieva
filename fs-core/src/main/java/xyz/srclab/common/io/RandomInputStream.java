package xyz.srclab.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

final class RandomInputStream extends InputStream {

    private final RandomAccessFile random;
    private final long offset;

    RandomInputStream(RandomAccessFile random, long offset) {
        this.random = random;
        this.offset = offset;
        try {
            this.random.seek(offset);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int read(byte[] array, int off, int len) throws IOException {
        return random.read(array, off, len);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return random.read(b);
    }

    @Override
    public int read() throws IOException {
        return random.read();
    }

    @Override
    public void close() throws IOException {
        random.close();
    }
}
