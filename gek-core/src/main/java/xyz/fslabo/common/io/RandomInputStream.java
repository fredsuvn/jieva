package xyz.fslabo.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

final class RandomInputStream extends InputStream {

    private final RandomAccessFile random;
    private long mark = -1;

    RandomInputStream(RandomAccessFile random, long initialSeek) throws IOException {
        this.random = random;
        this.random.seek(initialSeek);
    }

    @Override
    public int read() throws IOException {
        return random.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        IOMisc.checkReadBounds(b, off, len);
        if (len <= 0) {
            return 0;
        }
        return random.read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        if (n <= 0) {
            return 0;
        }
        return random.skipBytes((int) n);
    }

    @Override
    public int available() throws IOException {
        return (int) (random.length() - random.getFilePointer());
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void mark(int readlimit) {
        try {
            this.mark = random.getFilePointer();
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    @Override
    public void reset() throws IOException {
        if (mark < 0) {
            throw new IOException("Mark has not been set.");
        }
        random.seek(mark);
    }

    @Override
    public void close() throws IOException {
        random.close();
    }
}
