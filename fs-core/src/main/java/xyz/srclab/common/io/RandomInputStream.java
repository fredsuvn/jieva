package xyz.srclab.common.io;

import xyz.srclab.common.base.FsCheck;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

final class RandomInputStream extends InputStream {

    private final RandomAccessFile random;
    private final long limit;
    private long pos;
    private long mark;

    RandomInputStream(RandomAccessFile random, long offset, long length) {
        try {
            if (length != -1) {
                FsCheck.checkArgument(offset >= 0 && length >= 0, "offset and length must >= 0.");
                this.limit = offset + length;
            } else {
                this.limit = length;
            }
            this.random = random;
            this.pos = offset;
            this.random.seek(pos);
        } catch (IOException e) {
            throw new FsIOException(e);
        }
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        try {
            FsCheck.checkRangeInBounds(off, off + len, 0, b.length);
            if (limit != -1 && pos >= limit) {
                return -1;
            }
            int result = random.read(b, off, len);
            if (result == -1) {
                if (limit != -1) {
                    pos = limit;
                } else {
                    return -1;
                }
            } else {
                pos += result;
            }
            return result;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public synchronized int read() throws IOException {
        try {
            if (limit != -1 && pos >= limit) {
                return -1;
            }
            int result = random.read();
            if (result == -1) {
                if (limit != -1) {
                    pos = limit;
                } else {
                    return -1;
                }
            } else {
                pos++;
            }
            return result;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        return super.skip(n);
    }

    @Override
    public synchronized int available() throws IOException {
        long remainder = (limit == -1 ? random.length() - pos : limit - pos);
        if (remainder > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (remainder <= 0L) {
            return 0;
        } else {
            return (int) remainder;
        }
    }

    @Override
    public synchronized void mark(int readlimit) {
        mark = pos;
    }

    @Override
    public synchronized void reset() throws IOException {
        pos = mark;
        random.seek(pos);
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public void close() throws IOException {
        random.close();
    }
}
