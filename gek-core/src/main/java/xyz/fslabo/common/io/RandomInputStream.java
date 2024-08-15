package xyz.fslabo.common.io;

import xyz.fslabo.common.base.GekCheck;

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
                GekCheck.checkArgument(offset >= 0 && length >= 0, "offset and length must >= 0.");
                this.limit = offset + length;
            } else {
                this.limit = length;
            }
            this.random = random;
            this.pos = offset;
            this.random.seek(pos);
        } catch (IOException e) {
            throw new JieIOException(e);
        }
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        try {
            GekCheck.checkRangeInBounds(off, off + len, 0, b.length);
            if (len == 0) {
                return 0;
            }
            int result;
            if (limit == -1) {
                result = random.read(b, off, len);
            } else {
                if (pos >= limit) {
                    return -1;
                }
                long remaining = limit - pos;
                result = random.read(b, off, (int) Math.min(len, remaining));
            }
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
    public synchronized int read() throws IOException {
        try {
            int result;
            if (limit != -1) {
                if (pos >= limit) {
                    return -1;
                }
            }
            result = random.read();
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
        try {
            if (n <= 0) {
                return 0;
            }
            if (limit == -1) {
                random.seek(pos + n);
                pos += n;
                return n;
            }
            long result = Math.min(n, limit - pos);
            random.seek(pos + result);
            pos += result;
            return result;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized int available() throws IOException {
        long remaining = (limit == -1 ? random.length() - pos : limit - pos);
        if (remaining > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (remaining <= 0L) {
            return 0;
        } else {
            return (int) remaining;
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
    public synchronized void close() throws IOException {
        random.close();
    }
}
