package xyz.fsgek.common.data;

import xyz.fsgek.annotations.Nullable;
import xyz.fsgek.common.io.GekIO;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class InputStreamData implements GekData {

    private final InputStream inputStream;

    InputStreamData(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public synchronized byte[] toBytes() {
        return GekIO.readBytes(inputStream);
    }

    @Override
    public synchronized int write(byte[] dest, int offset, int length) {
        OutputStream out = GekIO.toOutputStream(dest, offset, length);
        return (int) GekIO.readBytesTo(inputStream, out, length);
    }

    @Override
    public synchronized int write(ByteBuffer dest) {
        return (int) GekIO.readBytesTo(inputStream, GekIO.toOutputStream(dest), dest.remaining());
    }

    @Override
    public synchronized InputStream toInputStream() {
        return inputStream;
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
