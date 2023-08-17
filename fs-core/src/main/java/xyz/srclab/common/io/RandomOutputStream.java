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
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        try {
            FsCheck.checkRangeInBounds(off, off + len, 0, b.length);
            if (limit != -1) {
                FsCheck.checkInBounds(pos + len - 1, pos, limit);
            }
            random.write(b, off, len);
            pos += len;
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
            if (limit != -1) {
                FsCheck.checkInBounds(pos, pos, limit);
            }
            random.write(b);
            pos += 1;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void flush() throws IOException {
        random.getChannel().force(true);
    }

    @Override
    public void close() throws IOException {
        random.close();
    }
}