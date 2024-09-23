package xyz.fslabo.common.io;

import xyz.fslabo.common.base.JieCheck;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.Function;

final class TransmissionImpl implements Transmission {

    private Object source;
    private Object dest;
    private long readSize = -1;
    private int blockSize = JieIO.BUFFER_SIZE;
    private boolean breakIfNoRead = false;
    private Function<ByteBuffer, ByteBuffer> conversion;

    @Override
    public Transmission source(InputStream source) {
        this.source = source;
        return this;
    }

    @Override
    public Transmission source(byte[] source) {
        this.source = source;
        return this;
    }

    @Override
    public Transmission source(byte[] source, int offset, int length) {
        if (offset == 0 && length == source.length) {
            return source(source);
        }
        this.source = ByteBuffer.wrap(source, offset, length);
        return this;
    }

    @Override
    public Transmission source(ByteBuffer source) {
        this.source = source;
        return this;
    }

    @Override
    public Transmission dest(OutputStream dest) {
        this.dest = dest;
        return this;
    }

    @Override
    public Transmission dest(byte[] dest) {
        this.dest = dest;
        return this;
    }

    @Override
    public Transmission dest(byte[] dest, int offset, int length) {
        if (offset == 0 && length == dest.length) {
            return dest(dest);
        }
        this.dest = ByteBuffer.wrap(dest, offset, length);
        return this;
    }

    @Override
    public Transmission dest(ByteBuffer dest) {
        this.dest = dest;
        return this;
    }

    @Override
    public Transmission readSize(long readSize) {
        this.readSize = readSize;
        return this;
    }

    @Override
    public Transmission blockSize(int blockSize) {
        JieCheck.checkArgument(blockSize > 0, "blockSize must > 0!");
        this.blockSize = blockSize;
        return this;
    }

    @Override
    public Transmission breakIfNoRead(boolean breakIfNoRead) {
        this.breakIfNoRead = breakIfNoRead;
        return this;
    }

    @Override
    public Transmission conversion(Function<ByteBuffer, ByteBuffer> conversion) {
        this.conversion = conversion;
        return this;
    }

    @Override
    public long start() throws IORuntimeException {
        if (source == null || dest == null) {
            throw new IORuntimeException("Source or dest is null!");
        }
        if (readSize == 0) {
            return 0;
        }
        try {
            return start(source, dest);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private long start(Object src, Object dst) throws IOException {
        if (src instanceof InputStream) {
            if (dst instanceof OutputStream) {
                return transmit((InputStream) src, (OutputStream) dst);
            }
            if (dst instanceof byte[]) {
                return transmit((InputStream) src, (byte[]) dst, 0, ((byte[]) dst).length);
            }
            if (dst instanceof ByteBuffer) {
                return transmit((InputStream) src, (ByteBuffer) dst);
            }
            throw new IORuntimeException("Unexpected destination type: " + dst.getClass());
        }
        if (src instanceof byte[]) {
            if (dst instanceof OutputStream) {
                return transmit((byte[]) src, 0, ((byte[]) src).length, (OutputStream) dst);
            }
            if (dst instanceof byte[]) {
                return transmit((byte[]) src, 0, ((byte[]) src).length, (byte[]) dst, 0, ((byte[]) dst).length);
            }
            if (dst instanceof ByteBuffer) {
                return transmit((byte[]) src, 0, ((byte[]) src).length, (ByteBuffer) dst);
            }
            throw new IORuntimeException("Unexpected destination type: " + dst.getClass());
        }
        if (src instanceof ByteBuffer) {
            if (dst instanceof OutputStream) {
                return transmit((ByteBuffer) src, (OutputStream) dst);
            }
            if (dst instanceof byte[]) {
                return transmit((ByteBuffer) src, (byte[]) dst, 0, ((byte[]) dst).length);
            }
            if (dst instanceof ByteBuffer) {
                return transmit((ByteBuffer) src, (ByteBuffer) dst);
            }
            throw new IORuntimeException("Unexpected destination type: " + dst.getClass());
        }
        throw new IORuntimeException("Unexpected source type: " + src.getClass());
    }

    private int getBufferSize() {
        if (readSize < 0) {
            return blockSize;
        }
        return (int) Math.min(readSize, blockSize);
    }

    private long transmit(InputStream src, OutputStream dst) throws IOException {
        byte[] block = new byte[getBufferSize()];
        ByteBuffer buf = null;
        long readCount = 0;
        long writeCount = 0;
        while (readCount < readSize) {
            int c = src.read(block);
            if (c == -1) {
                return readCount == 0 ? -1 : writeCount;
            }
            if (c == 0) {
                if (breakIfNoRead) {
                    return writeCount;
                }
                continue;
            }
            readCount += c;
            if (conversion != null) {
                if (buf == null) {
                    buf = ByteBuffer.wrap(block);
                }
                buf.position(0);
                buf.limit(c);
                ByteBuffer converted = conversion.apply(buf);
                writeCount += bufToOut(converted, dst);
            } else {
                dst.write(block, 0, c);
                writeCount += c;
            }
        }
        return writeCount;
    }

    private int getActualReadSize(int len) {
        if (readSize < 0) {
            return len;
        }
        return (int) Math.min(readSize, len);
    }

    private long transmit(InputStream src, byte[] dst, int off, int len) throws IOException {
        if (conversion != null) {
            return transmit(src, JieIO.toOutputStream(dst, off, len));
        }
        int actualReadSize = getActualReadSize(len);
        int count = 0;
        while (count < actualReadSize) {
            int c = src.read(dst, off + count, actualReadSize - count);
            if (c == -1) {
                return count == 0 ? -1 : count;
            }
            if (c == 0) {
                if (breakIfNoRead) {
                    return count;
                }
                continue;
            }
            count += c;
        }
        return count;
    }

    private long transmit(InputStream src, ByteBuffer dst) throws IOException {
        if (conversion != null) {
            return transmit(src, JieIO.toOutputStream(dst));
        }
        int actualReadSize = getActualReadSize(dst.remaining());
        ByteBuffer dstSlice = JieBuffer.slice(dst, actualReadSize);
        return transmit(src, JieIO.toOutputStream(dstSlice));
    }

    private long transmit(byte[] src, int off, int len, OutputStream dst) throws IOException {
        if (conversion != null) {
            return transmit(JieIO.toInputStream(src, off, len), dst);
        }
        int actualReadSize = getActualReadSize(len);
        dst.write(src, off, actualReadSize);
        return actualReadSize;
    }

    private long transmit(byte[] src, int srcOff, int srcLen, byte[] dst, int dstOff, int dstLen) throws IOException {
        if (conversion != null) {
            return transmit(JieIO.toInputStream(src, srcOff, srcLen), JieIO.toOutputStream(dst, dstOff, dstLen));
        }
        int minLen = Math.min(srcLen, dstLen);
        int actualReadSize = getActualReadSize(minLen);
        System.arraycopy(src, srcOff, dst, dstOff, actualReadSize);
        return actualReadSize;
    }

    private long transmit(byte[] src, int off, int len, ByteBuffer dst) throws IOException {
        if (conversion != null) {
            return transmit(JieIO.toInputStream(src, off, len), JieIO.toOutputStream(dst));
        }
        return 0;
    }

    private long transmit(ByteBuffer src, OutputStream dst) throws IOException {
        return 0;
    }

    private long transmit(ByteBuffer src, byte[] dst, int off, int len) throws IOException {
        return 0;
    }

    private long transmit(ByteBuffer src, ByteBuffer dst) throws IOException {
        return 0;
    }

    private int bufToOut(ByteBuffer buffer, OutputStream out) throws IOException {
        int remain = buffer.remaining();
        if (buffer.hasArray()) {
            out.write(buffer.array(), buffer.arrayOffset(), buffer.remaining());
            buffer.position(buffer.position() + buffer.remaining());
        } else {
            while (buffer.hasRemaining()) {
                out.write(buffer.get());
            }
        }
        return remain;
    }
}
