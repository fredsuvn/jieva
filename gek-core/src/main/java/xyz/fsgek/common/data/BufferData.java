package xyz.fsgek.common.data;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.io.GekBuffer;
import xyz.fsgek.common.io.GekIO;
import xyz.fsgek.common.base.GekCheck;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class BufferData implements GekData.OfBuffer {

    private final ByteBuffer buffer;

    BufferData(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public boolean isArrayData() {
        return false;
    }

    @Override
    public OfArray asArrayData() {
        throw new GekDataException("Not an array data.");
    }

    @Override
    public boolean isBufferData() {
        return true;
    }

    @Override
    public OfBuffer asBufferData() {
        return this;
    }

    @Override
    public boolean isStreamData() {
        return false;
    }

    @Override
    public OfStream asStreamData() {
        throw new GekDataException("Not a stream data.");
    }

    @Override
    public ByteBuffer buffer() {
        return buffer;
    }

    @Override
    public int write(byte[] dest, int offset, int length) {
        GekCheck.checkRangeInBounds(offset, offset + length, 0, dest.length);
        int len = Math.min(buffer.remaining(), length);
        if (len <= 0) {
            return 0;
        }
        buffer.get(dest, offset, len);
        return len;
    }

    @Override
    public int write(ByteBuffer dest, int length) {
        int len = Math.min(dest.remaining(), buffer.remaining());
        len = Math.min(len, length);
        if (len <= 0) {
            return 0;
        }
        dest.put(buffer);
        return len;
    }

    @Override
    public long write(OutputStream dest) {
        return 0;
    }

    @Override
    public long write(OutputStream dest, long length) {
        return 0;
    }

    @Override
    public byte[] toArray() {
        return new byte[0];
    }

    @Override
    public InputStream asInputStream() {
        return null;
    }
}
