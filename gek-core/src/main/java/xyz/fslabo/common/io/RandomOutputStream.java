package xyz.fslabo.common.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

final class RandomOutputStream extends OutputStream {

    private final RandomAccessFile random;

    RandomOutputStream(RandomAccessFile random, long initialSeek) throws IOException {
        this.random = random;
        this.random.seek(initialSeek);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        IOMisc.checkReadBounds(b, off, len);
        if (len <= 0) {
            return;
        }
        random.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        random.write(b);
    }

    @Override
    public void flush() throws IOException {
        random.getFD().sync();
    }

    @Override
    public void close() throws IOException {
        random.close();
    }
}