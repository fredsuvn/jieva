package xyz.srclab.common.codec;

import xyz.srclab.common.base.FsCheck;
import xyz.srclab.common.io.FsIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;

final class Base64Encoder implements FsEncoder {

    private final Base64.Encoder encoder;
    private final Base64.Decoder decoder;

    Base64Encoder(Base64.Encoder encoder, Base64.Decoder decoder) {
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    public byte[] encode(byte[] source) {
        return encoder.encode(source);
    }

    @Override
    public byte[] encode(byte[] source, int offset, int length) {
        if (offset == 0 && length == source.length) {
            return encoder.encode(source);
        }
        return encoder.encode(copyArray(source, offset, length));
    }

    @Override
    public int encode(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        if (sourceOffset == 0 && destOffset == 0 && length == source.length) {
            return encoder.encode(source, dest);
        }
        ByteBuffer in = ByteBuffer.wrap(source, sourceOffset, length);
        ByteBuffer out = ByteBuffer.wrap(dest, destOffset, dest.length - destOffset);
        return encode(in, out);
    }

    @Override
    public ByteBuffer encode(ByteBuffer source) {
        return encoder.encode(source);
    }

    @Override
    public int encode(ByteBuffer source, ByteBuffer dest) {
        return (int) encode(FsIO.toInputStream(source), FsIO.toOutputStream(dest));
    }

    @Override
    public long encode(InputStream source, OutputStream dest) {
        OutputStreamWrapper wrapper = new OutputStreamWrapper(dest);
        OutputStream out = encoder.wrap(wrapper);
        FsIO.readBytesTo(source, out);
        try {
            out.flush();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return wrapper.count;
    }

    @Override
    public String encodeToString(byte[] source) {
        return encoder.encodeToString(source);
    }

    @Override
    public byte[] decode(byte[] source) {
        return decoder.decode(source);
    }

    @Override
    public byte[] decode(byte[] source, int offset, int length) {
        if (offset == 0 && length == source.length) {
            return decoder.decode(source);
        }
        return decoder.decode(copyArray(source, offset, length));
    }

    @Override
    public int decode(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        if (sourceOffset == 0 && destOffset == 0 && length == source.length) {
            return decoder.decode(source, dest);
        }
        ByteBuffer in = ByteBuffer.wrap(source, sourceOffset, length);
        ByteBuffer out = ByteBuffer.wrap(dest, destOffset, dest.length - destOffset);
        return decode(in, out);
    }

    @Override
    public ByteBuffer decode(ByteBuffer source) {
        return decoder.decode(source);
    }

    @Override
    public int decode(ByteBuffer source, ByteBuffer dest) {
        return (int) decode(FsIO.toInputStream(source), FsIO.toOutputStream(dest));
    }

    @Override
    public long decode(InputStream source, OutputStream dest) {
        OutputStreamWrapper wrapper = new OutputStreamWrapper(dest);
        InputStream in = decoder.wrap(source);
        FsIO.readBytesTo(in, wrapper);
        return wrapper.count;
    }

    private byte[] copyArray(byte[] src, int offset, int length) {
        FsCheck.checkRangeInBounds(offset, offset + length, 0, src.length);
        return Arrays.copyOfRange(src, offset, offset + length);
    }

    private static final class OutputStreamWrapper extends OutputStream {

        private final OutputStream outputStream;
        private long count;

        private OutputStreamWrapper(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void write(byte[] b) throws IOException {
            outputStream.write(b);
            count += b.length;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            outputStream.write(b, off, len);
            count += len;
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
            count++;
        }
    }
}
