package xyz.srclab.common.io;

import xyz.srclab.common.base.FsCheck;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

final class RandomOutputStream extends OutputStream {

    private final RandomAccessFile random;
    private final long limit;
    private long pos;

    RandomOutputStream(RandomAccessFile random, long offset, long length) {
        try {
            FsCheck.checkArgument(offset >= 0 && length >= 0, "offset and length must >= 0.");
            this.random = random;
            this.limit = offset + length;
            this.pos = offset;
            this.random.seek(pos);
        } catch (IOException e) {
            throw new FsIOException(e);
        }
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        try {
            FsCheck.checkRangeInBounds(off, off + len, 0, b.length);
            FsCheck.checkInBounds(pos + len - 1, pos, limit);
            random.write(b, off, len);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public synchronized void write(int b) throws IOException {
        try {
            FsCheck.checkInBounds(pos, pos, limit);
            random.write(b);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws IOException {
        random.close();
    }
}