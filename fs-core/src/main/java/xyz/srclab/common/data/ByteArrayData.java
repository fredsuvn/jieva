package xyz.srclab.common.data;

import xyz.srclab.common.base.FsCheck;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

final class ByteArrayData implements FsData {

    private final byte[] array;
    private final int offset;
    private final int length;

    ByteArrayData(byte[] array, int offset, int length) {
        this.array = array;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public synchronized byte[] toBytes() {
        return Arrays.copyOfRange(array, offset, offset + length);
    }

    @Override
    public synchronized int write(byte[] dest, int offset, int length) {
        FsCheck.checkRangeInBounds(offset, offset + length, 0, dest.length);
        int len = Math.min(this.length, length);
        System.arraycopy(array, this.offset, dest, offset, len);
        return len;
    }

    @Override
    public synchronized int write(ByteBuffer dest) {
        int len = Math.min(this.length, dest.remaining());
        if (len == 0) {
            return 0;
        }
        dest.put(array, offset, len);
        return len;
    }

    @Override
    public synchronized InputStream toInputStream() {
        return new ByteArrayInputStream(array, offset, length);
    }
}
