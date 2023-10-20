package xyz.fsgek.common.data;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.io.GekBuffer;
import xyz.fsgek.common.io.GekIO;
import xyz.fsgek.common.base.GekCheck;

import java.io.InputStream;
import java.nio.ByteBuffer;

final class BufferData implements GekData {

    private final ByteBuffer buffer;

    BufferData(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public synchronized byte[] toBytes() {
        return GekBuffer.getBytes(buffer);
    }

    @Override
    public synchronized int write(byte[] dest, int offset, int length) {
        GekCheck.checkRangeInBounds(offset, offset + length, 0, dest.length);
        int len = Math.min(buffer.remaining(), length);
        buffer.get(dest, offset, len);
        return len;
    }

    @Override
    public synchronized int write(ByteBuffer dest) {
        int len = Math.min(buffer.remaining(), dest.remaining());
        if (len == 0) {
            return 0;
        }
        ByteBuffer src = buffer;
        if (len != buffer.remaining()) {
            src = buffer.slice();
            src.limit(len);
        }
        dest.put(src);
        buffer.position(buffer.position() + len);
        return len;
    }

    @Override
    public synchronized InputStream toInputStream() {
        return GekIO.toInputStream(buffer);
    }

    @Override
    public boolean hasBackArray() {
        return false;
    }

    @Override
    public @Nullable byte[] backArray() {
        return null;
    }
}
