package xyz.fslabo.common.data;

import xyz.fslabo.common.base.GekCheck;
import xyz.fslabo.common.io.GekIO;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class StreamData implements GekData.OfStream {

    private final InputStream stream;

    StreamData(InputStream stream) {
        this.stream = stream;
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
        return false;
    }

    @Override
    public OfBuffer asBufferData() {
        throw new GekDataException("Not a buffer data.");
    }

    @Override
    public boolean isStreamData() {
        return true;
    }

    @Override
    public OfStream asStreamData() {
        return this;
    }

    @Override
    public InputStream stream() {
        return stream;
    }

    @Override
    public int write(byte[] dest, int offset, int length) {
        GekCheck.checkRangeInBounds(offset, offset + length, 0, dest.length);
        byte[] buffer = GekIO.read(stream, length);
        if (buffer == null) {
            return -1;
        }
        if (buffer.length == 0) {
            return 0;
        }
        System.arraycopy(buffer, 0, dest, offset, buffer.length);
        return buffer.length;
    }

    @Override
    public int write(ByteBuffer dest, int length) {
        return (int) GekIO.readTo(stream, GekIO.toOutputStream(dest), length);
    }

    @Override
    public long write(OutputStream dest) {
        return GekIO.readTo(stream, dest);
    }

    @Override
    public long write(OutputStream dest, long length) {
        return GekIO.readTo(stream, dest, length);
    }

    @Override
    public byte[] toArray() {
        return GekIO.read(stream);
    }

    @Override
    public InputStream asInputStream() {
        return stream;
    }
}
