package xyz.fsgek.common.io;

import xyz.fsgek.common.base.FsCheck;

import java.io.IOException;
import java.io.InputStream;

final class LimitedInputStream extends InputStream {

    private final InputStream source;
    private long remaining;

    LimitedInputStream(InputStream source, long limit) {
        try {
            FsCheck.checkArgument(limit >= 0, "limit must >= 0.");
            this.source = source;
            this.remaining = limit;
        } catch (Exception e) {
            throw new FsIOException(e);
        }
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        try {
            FsCheck.checkRangeInBounds(off, off + len, 0, b.length);
            if (len == 0) {
                return 0;
            }
            if (remaining <= 0) {
                return -1;
            }
            int readSize = source.read(b, off, (int) Math.min(remaining, len));
            if (readSize == -1) {
                remaining = 0;
                return -1;
            }
            remaining -= readSize;
            return readSize;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized int read() throws IOException {
        try {
            if (remaining <= 0) {
                return -1;
            }
            int read = source.read();
            if (read == -1) {
                remaining = 0;
                return -1;
            }
            remaining--;
            return read;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        try {
            if (n <= 0 || remaining <= 0) {
                return 0;
            }
            long skip = source.skip(Math.min(remaining, n));
            if (skip > 0) {
                remaining -= skip;
            }
            return skip;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized int available() throws IOException {
        int available = source.available();
        return (int) Math.min(remaining, available);
    }

    @Override
    public synchronized void close() throws IOException {
        source.close();
    }
}
