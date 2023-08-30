package xyz.srclab.common.codec;

import org.bouncycastle.util.encoders.Base64;
import xyz.srclab.common.base.FsCheck;
import xyz.srclab.common.io.FsIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

final class BouncyCastleBase64Encoder implements FsEncoder {

    private final String algorithmName;

    BouncyCastleBase64Encoder(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    @Override
    public String algorithmName() {
        return algorithmName;
    }

    @Override
    public byte[] encode(byte[] source) {
        try {
            return Base64.encode(source);
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public byte[] encode(byte[] source, int offset, int length) {
        try {
            return Base64.encode(source, offset, length);
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public int encode(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        try {
            OutputStreamWrapper out = new OutputStreamWrapper(FsIO.toOutputStream(
                ByteBuffer.wrap(dest, destOffset, dest.length - destOffset)));
            Base64.encode(source, sourceOffset, length, out);
            return (int) out.count;
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public long encode(InputStream source, OutputStream dest) {
        try {
            byte[] src = FsIO.readBytes(source);
            OutputStreamWrapper out = new OutputStreamWrapper(dest);
            Base64.encode(src, out);
            return out.count;
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public String encodeToString(byte[] source) {
        try {
            return Base64.toBase64String(source);
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public int encodeBlockSize() {
        return 3;
    }

    @Override
    public byte[] decode(byte[] source) {
        try {
            return Base64.decode(source);
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public byte[] decode(byte[] source, int offset, int length) {
        try {
            if (offset == 0 && length == source.length) {
                return Base64.decode(source);
            }
            return Base64.decode(copyArray(source, offset, length));
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public int decode(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        try {
            OutputStreamWrapper out = new OutputStreamWrapper(FsIO.toOutputStream(
                ByteBuffer.wrap(dest, destOffset, dest.length - destOffset)));
            Base64.decode(source, sourceOffset, length, out);
            return (int) out.count;
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public long decode(InputStream source, OutputStream dest) {
        try {
            byte[] src = FsIO.readBytes(source);
            OutputStreamWrapper out = new OutputStreamWrapper(dest);
            Base64.decode(src, 0, src.length, out);
            return out.count;
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public int decodeBlockSize() {
        return 4;
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
