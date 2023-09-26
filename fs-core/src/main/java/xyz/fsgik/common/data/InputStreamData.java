package xyz.fsgik.common.data;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.common.io.FsIO;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class InputStreamData implements FsData {

    private final InputStream inputStream;

    InputStreamData(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public synchronized byte[] toBytes() {
        return FsIO.readBytes(inputStream);
    }

    @Override
    public synchronized int write(byte[] dest, int offset, int length) {
        OutputStream out = FsIO.toOutputStream(dest, offset, length);
        return (int) FsIO.readBytesTo(inputStream, out, length);
    }

    @Override
    public synchronized int write(ByteBuffer dest) {
        return (int) FsIO.readBytesTo(inputStream, FsIO.toOutputStream(dest), dest.remaining());
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
