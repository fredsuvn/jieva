package xyz.fslabo.common.io;

import xyz.fslabo.common.base.JieCheck;

import java.io.IOException;
import java.io.OutputStream;

final class ByteArrayAsOutputStream extends OutputStream {

    private final byte[] array;
    private final int end;
    private int pos;

    ByteArrayAsOutputStream(byte[] array, int offset, int length) {
        this.array = array;
        this.end = offset + length;
        this.pos = offset;
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        try {
            JieCheck.checkRangeInBounds(off, off + len, 0, b.length);
            if (end - pos < len) {
                throw new IOException("Backed byte array has been full.");
            }
            System.arraycopy(b, off, array, pos, len);
            pos += len;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized void write(int b) throws IOException {
        try {
            if (end - pos < 1) {
                throw new IOException("Backed byte array has been full.");
            }
            array[pos] = (byte) b;
            pos++;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void flush() {
    }
}