package xyz.fsgek.common.data;

import xyz.fsgek.common.base.GekCheck;
import xyz.fsgek.common.io.GekBuffer;
import xyz.fsgek.common.io.GekIO;

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
        int remaining = buffer.remaining();
        if (remaining <= 0) {
            return -1;
        }
        int len = Math.min(remaining, length);
        if (len <= 0) {
            return 0;
        }
        buffer.get(dest, offset, len);
        return len;
    }

    @Override
    public int write(ByteBuffer dest, int length) {
        return GekBuffer.put(buffer, dest, length);
    }

    @Override
    public long write(OutputStream dest) {
        return GekIO.readTo(GekIO.toInputStream(buffer), dest);
    }

    @Override
    public long write(OutputStream dest, long length) {
        return GekIO.readTo(GekIO.toInputStream(buffer), dest, length);
    }

    @Override
    public byte[] toArray() {
        if (!buffer.hasRemaining()) {
            return null;
        }
        return GekBuffer.getBytes(buffer);
    }

    @Override
    public InputStream asInputStream() {
        return GekIO.toInputStream(buffer);
    }
}
