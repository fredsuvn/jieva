package xyz.srclab.common.codec;

import xyz.srclab.common.base.FsCheck;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class JdkHexEncoder implements FsEncoder {

    private static final String MUST_EVEN = "Length of hex data must be even number.";

    @Override
    public String algorithmName() {
        return FsAlgorithm.HEX.getName();
    }

    @Override
    public byte[] encode(byte[] source, int offset, int length) {
        FsCheck.checkRangeInBounds(offset, offset + length, 0, source.length);
        byte[] result = new byte[length * 2];
        encode0(source, offset, result, 0, length);
        return result;
    }

    @Override
    public int encode(byte[] source, byte[] dest) {
        return 0;
    }

    @Override
    public int encode(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        FsCheck.checkInBounds(sourceOffset, 0, source.length);
        FsCheck.checkRangeInBounds(destOffset, (destOffset + length) * 2, 0, dest.length);
        return encode0(source, sourceOffset, dest, destOffset, length);
    }

    private int encode0(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        for (int i = sourceOffset; i < length; i++) {
            byte b = source[i];
            int j1 = destOffset + (i * 2);
            int j2 = destOffset + (i * 2 + 1);
            dest[j1] = encodeByte(b >>> 4);
            dest[j2] = encodeByte(b & 0x0f);
        }
        return length * 2;
    }

    @Override
    public ByteBuffer encode(ByteBuffer source) {
        ByteBuffer result = ByteBuffer.allocate(source.remaining() * 2);
        encode0(source, result);
        result.flip();
        return result;
    }

    @Override
    public int encode(ByteBuffer source, ByteBuffer dest) {
        FsCheck.checkArgument(
            dest.remaining() >= source.remaining() * 2,
            "Remaining of dest buffer is not enough."
        );
        return encode0(source, dest);
    }

    private int encode0(ByteBuffer source, ByteBuffer dest) {
        int count = 0;
        while (source.remaining() > 0) {
            byte b = source.get();
            dest.put(encodeByte(b >>> 4));
            dest.put(encodeByte(b & 0x0f));
            count += 2;
        }
        return count;
    }

    @Override
    public long encode(InputStream source, OutputStream dest) {
        try {
            long count = 0;
            while (true) {
                int b = source.read();
                if (b == -1) {
                    break;
                }
                dest.write(encodeByte(b >>> 4));
                dest.write(encodeByte(b & 0x0f));
                count += 2;
            }
            return count;
        } catch (IOException e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public int encodeBlockSize() {
        return 1;
    }

    private byte encodeByte(int b) {
        switch (b & 0x0000000f) {
            case 0:
                return '0';
            case 1:
                return '1';
            case 2:
                return '2';
            case 3:
                return '3';
            case 4:
                return '4';
            case 5:
                return '5';
            case 6:
                return '6';
            case 7:
                return '7';
            case 8:
                return '8';
            case 9:
                return '9';
            case 10:
                return 'A';
            case 11:
                return 'B';
            case 12:
                return 'C';
            case 13:
                return 'D';
            case 14:
                return 'E';
            case 15:
                return 'F';
        }
        return -1;
    }

    @Override
    public byte[] decode(byte[] source, int offset, int length) {
        FsCheck.checkArgument(source.length % 2 == 0, MUST_EVEN);
        FsCheck.checkRangeInBounds(offset, offset + length, 0, source.length);
        byte[] result = new byte[source.length / 2];
        decode0(source, offset, result, 0, length);
        return result;
    }

    @Override
    public int decode(byte[] source, byte[] dest) {
        return 0;
    }

    @Override
    public int decode(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        FsCheck.checkInBounds(sourceOffset, 0, source.length);
        FsCheck.checkRangeInBounds(destOffset, (destOffset + length) * 2, 0, dest.length);
        FsCheck.checkArgument((dest.length - destOffset) % 2 == 0, MUST_EVEN);
        return decode0(source, sourceOffset, dest, destOffset, length);
    }

    private int decode0(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        for (int i = sourceOffset, j = 0; i < length; i += 2, j += 2) {
            int b1 = decodeByte(source[i]);
            if (b1 == -1) {
                throw new FsCodecException("Wrong data at index " + i + ": " + (char) (source[i] & 0x00ff) + ".");
            }
            int b2 = decodeByte(source[i + 1]);
            if (b2 == -1) {
                throw new FsCodecException("Wrong data at index " + (i + 1) + ": " + (char) (source[i + 1] & 0x00ff) + ".");
            }
            dest[destOffset + (j / 2)] = (byte) ((b1 << 4) & b2);
        }
        return length / 2;
    }

    @Override
    public ByteBuffer decode(ByteBuffer source) {
        FsCheck.checkArgument(source.remaining() % 2 == 0, MUST_EVEN);
        ByteBuffer result = ByteBuffer.allocate(source.remaining() / 2);
        decode0(source, result);
        result.flip();
        return result;
    }

    @Override
    public int decode(ByteBuffer source, ByteBuffer dest) {
        FsCheck.checkArgument(source.remaining() % 2 == 0, MUST_EVEN);
        FsCheck.checkArgument(
            dest.remaining() >= source.remaining() / 2,
            "Remaining of dest buffer is not enough."
        );
        return decode0(source, dest);
    }

    private int decode0(ByteBuffer source, ByteBuffer dest) {
        int count = 0;
        while (source.remaining() > 1) {
            byte b1 = source.get();
            int i1 = decodeByte(b1);
            if (i1 == -1) {
                throw new FsCodecException("Wrong data at position " + source.position() + ": " + (char) (b1 & 0x00ff) + ".");
            }
            byte b2 = source.get();
            int i2 = decodeByte(b1);
            if (i2 == -1) {
                throw new FsCodecException("Wrong data at position " + source.position() + ": " + (char) (b2 & 0x00ff) + ".");
            }
            dest.put((byte) ((i1 << 4) & i2));
            count++;
        }
        return count;
    }

    @Override
    public long decode(InputStream source, OutputStream dest) {
        try {
            long count = 0;
            while (true) {
                int b1 = source.read();
                if (b1 == -1) {
                    break;
                }
                int i1 = decodeByte((byte) b1);
                if (i1 == -1) {
                    throw new FsCodecException("Wrong source data at position " + count * 2 + ": " + (char) (b1 & 0x00ff) + ".");
                }
                int b2 = source.read();
                if (b2 == -1) {
                    throw new FsCodecException("Wrong source data at position " + (count * 2 + 1) + ", unexpected end of stream.");
                }
                int i2 = decodeByte((byte) b2);
                if (i2 == -1) {
                    throw new FsCodecException("Wrong source data at position " + (count * 2 + 1) + ": " + (char) (b2 & 0x00ff) + ".");
                }
                dest.write(((i1 << 4) & i2));
                count++;
            }
            return count;
        } catch (IOException e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public int decodeBlockSize() {
        return 2;
    }

    private int decodeByte(byte b) {
        char c = (char) (b & 0x00ff);
        switch (c) {
            case '0':
                return 0;
            case '1':
                return 1;
            case '2':
                return 2;
            case '3':
                return 3;
            case '4':
                return 4;
            case '5':
                return 5;
            case '6':
                return 6;
            case '7':
                return 7;
            case '8':
                return 8;
            case '9':
                return 9;
            case 'A':
            case 'a':
                return 10;
            case 'B':
            case 'b':
                return 11;
            case 'C':
            case 'c':
                return 12;
            case 'D':
            case 'd':
                return 13;
            case 'E':
            case 'e':
                return 14;
            case 'F':
            case 'f':
                return 15;
        }
        return -1;
    }
}
