package xyz.srclab.common.codec.jdk;

import xyz.srclab.common.base.FsCheck;
import xyz.srclab.common.codec.FsCodecException;
import xyz.srclab.common.codec.FsEncoder;
import xyz.srclab.common.io.FsIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

final class JdkBase64Encoder implements FsEncoder {

    private final String algorithmName;
    private final java.util.Base64.Encoder encoder;
    private final java.util.Base64.Decoder decoder;

    JdkBase64Encoder(String algorithmName, java.util.Base64.Encoder encoder, java.util.Base64.Decoder decoder) {
        this.algorithmName = algorithmName;
        this.encoder = encoder;
        this.decoder = decoder;
    }

    @Override
    public String algorithmName() {
        return algorithmName;
    }

    @Override
    public byte[] encode(byte[] source) {
        try {
            return encoder.encode(source);
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public byte[] encode(byte[] source, int offset, int length) {
        try {
            if (offset == 0 && length == source.length) {
                return encoder.encode(source);
            }
            return encoder.encode(copyArray(source, offset, length));
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public int encode(byte[] source, byte[] dest) {
        try {
            return encoder.encode(source, dest);
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public int encode(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        if (sourceOffset == 0 && destOffset == 0 && length == source.length) {
            return encoder.encode(source, dest);
        }
        return FsEncoder.super.encode(source, sourceOffset, dest, destOffset, length);
    }

    @Override
    public ByteBuffer encode(ByteBuffer source) {
        try {
            return encoder.encode(source);
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public long encode(InputStream source, OutputStream dest) {
        try {
            OutputStreamWrapper wrapper = new OutputStreamWrapper(dest);
            OutputStream out = encoder.wrap(wrapper);
            FsIO.readBytesTo(source, out);
            out.flush();
            return wrapper.count;
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public String encodeToString(byte[] source) {
        try {
            return encoder.encodeToString(source);
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
            return decoder.decode(source);
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
                return decoder.decode(source);
            }
            return decoder.decode(copyArray(source, offset, length));
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public int decode(byte[] source, byte[] dest) {
        try {
            return decoder.decode(source, dest);
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public int decode(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        if (sourceOffset == 0 && destOffset == 0 && length == source.length) {
            return decoder.decode(source, dest);
        }
        return FsEncoder.super.decode(source, sourceOffset, dest, destOffset, length);
    }

    @Override
    public ByteBuffer decode(ByteBuffer source) {
        try {
            return decoder.decode(source);
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public long decode(InputStream source, OutputStream dest) {
        try {
            OutputStreamWrapper wrapper = new OutputStreamWrapper(dest);
            InputStream in = decoder.wrap(source);
            FsIO.readBytesTo(in, wrapper);
            return wrapper.count;
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
