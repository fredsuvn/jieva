package xyz.srclab.common.data;

import xyz.srclab.common.base.FsCheck;
import xyz.srclab.common.io.FsIO;

import java.io.InputStream;
import java.nio.ByteBuffer;

final class ByteBufferData implements FsData {

    private final ByteBuffer buffer;

    ByteBufferData(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public synchronized byte[] toBytes() {
        return FsIO.getBytes(buffer);
    }

    @Override
    public synchronized int write(byte[] dest, int offset, int length) {
        FsCheck.checkRangeInBounds(offset, offset + length, 0, dest.length);
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
        return FsIO.toInputStream(buffer);
    }
}
