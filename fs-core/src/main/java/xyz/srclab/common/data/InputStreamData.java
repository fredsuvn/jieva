package xyz.srclab.common.data;

import xyz.srclab.common.io.FsIO;

import java.io.InputStream;
import java.nio.ByteBuffer;

final class InputStreamData implements FsData {

    private final InputStream inputStream;

    InputStreamData(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public synchronized byte[] toByteArray() {
        return FsIO.readBytes(inputStream);
    }

    @Override
    public synchronized int write(byte[] dest, int offset, int length) {
        ByteBuffer destBuffer = ByteBuffer.wrap(dest, offset, length);
        return write(destBuffer);
    }

    @Override
    public synchronized int write(ByteBuffer dest) {
        return (int) FsIO.readBytesTo(inputStream, FsIO.toOutputStream(dest));
    }

    @Override
    public synchronized InputStream toInputStream() {
        return inputStream;
    }
}
