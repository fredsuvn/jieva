package xyz.fslabo.common.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.Function;

final class TransmissionImpl implements Transmission {

    private Object source;
    private Object dest;
    private long readLimit = -1;
    private boolean writeLimit = true;
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
    public Transmission readLimit(long readLimit) {
        this.readLimit = readLimit;
        return this;
    }

    @Override
    public Transmission writeLimit(boolean writeLimit) {
        this.writeLimit = writeLimit;
        return this;
    }

    @Override
    public Transmission blockSize(int blockSize) {
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
        try {
            return start(source, dest);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private long start(Object src, Object dst) throws IOException {
        if (readLimit == 0) {
            return 0;
        }
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

    private long transmit(InputStream src, OutputStream dst) throws IOException {
        byte[] buf = new byte[readLimit < 0 ? blockSize : (int) Math.min(readLimit, blockSize)];
        long count = 0;
        while (true) {
            int c = src.read(buf);
            if (c == -1) {
                return count == 0 ? -1 : count;
            }
            if (c == 0) {
                if (breakIfNoRead) {
                    return count;
                }
                continue;
            }
            if (conversion != null) {
                ByteBuffer converted = conversion.apply(ByteBuffer.wrap(buf, 0, c));
                count += bufferToOut(converted, dst);
            } else {
                dst.write(buf, 0, c);
                count += c;
            }
        }
    }

    private long transmit(InputStream src, byte[] dst, int off, int len) throws IOException {
        int actualLen = len;
        if (writeLimit && readLimit > 0) {
            actualLen = (int) Math.min(readLimit, actualLen);
        }
        if (conversion != null) {
            return transmit(src, JieIO.toOutputStream(dst, off, actualLen));
        }
        int count = 0;
        while (true) {
            int c = src.read(dst, off + count, actualLen - count);
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
    }

    private long transmit(InputStream src, ByteBuffer dst) throws IOException {
        int actualLen = dst.remaining();
        if (writeLimit && readLimit > 0) {
            actualLen = (int) Math.min(readLimit, actualLen);
        }
        if (conversion != null) {
            return transmit(src, JieIO.toOutputStream(dst));
        }
        if (dst.hasArray()) {
            byte[] dstArray = dst.array();
            int dstOff = dst.arrayOffset();
            int dstLen = readLimit < 0 ? dst.remaining() : (int) Math.min(readLimit, dst.remaining());
        }
        return 0;
    }

    private long transmit(byte[] src, int off, int len, OutputStream dst) throws IOException {
        return 0;
    }

    private long transmit(byte[] src, int srcOff, int srcLen, byte[] dst, int dstOff, int detLen) throws IOException {
        return 0;
    }

    private long transmit(byte[] src, int off, int len, ByteBuffer dst) throws IOException {
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

    private int bufferToOut(ByteBuffer buffer, OutputStream out) throws IOException {
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
