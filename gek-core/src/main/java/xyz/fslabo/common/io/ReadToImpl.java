package xyz.fslabo.common.io;

import xyz.fslabo.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.function.Function;

final class ReadToImpl implements ReadTo {

    private Object source;
    private Object dest;
    private long readLimit = -1;
    private int blockSize = JieIO.BUFFER_SIZE;
    private boolean breakIfNoRead = false;
    private Function<ByteBuffer, ByteBuffer> conversion;

    @Override
    public ReadTo input(InputStream source) {
        this.source = source;
        return this;
    }

    @Override
    public ReadTo input(byte[] source) {
        this.source = source;
        return this;
    }

    @Override
    public ReadTo input(byte[] source, int offset, int length) {
        if (offset == 0 && length == source.length) {
            return input(source);
        }
        this.source = ByteBuffer.wrap(source, offset, length);
        return this;
    }

    @Override
    public ReadTo input(ByteBuffer source) {
        this.source = source;
        return this;
    }

    @Override
    public ReadTo output(OutputStream dest) {
        this.dest = dest;
        return this;
    }

    @Override
    public ReadTo output(byte[] dest) {
        this.dest = dest;
        return this;
    }

    @Override
    public ReadTo output(byte[] dest, int offset, int length) {
        if (offset == 0 && length == dest.length) {
            return output(dest);
        }
        this.dest = ByteBuffer.wrap(dest, offset, length);
        return this;
    }

    @Override
    public ReadTo output(ByteBuffer dest) {
        this.dest = dest;
        return this;
    }

    @Override
    public ReadTo readLimit(long readLimit) {
        this.readLimit = readLimit;
        return this;
    }

    @Override
    public ReadTo blockSize(int blockSize) {
        if (blockSize <= 0) {
            throw new IORuntimeException("blockSize must > 0!");
        }
        this.blockSize = blockSize;
        return this;
    }

    @Override
    public ReadTo breakIfNoRead(boolean breakIfNoRead) {
        this.breakIfNoRead = breakIfNoRead;
        return this;
    }

    @Override
    public ReadTo conversion(Function<ByteBuffer, ByteBuffer> conversion) {
        this.conversion = conversion;
        return this;
    }

    @Override
    public long start() throws IORuntimeException {
        if (source == null || dest == null) {
            throw new IORuntimeException("Source or dest is null!");
        }
        if (readLimit == 0) {
            return 0;
        }
        try {
            BufferIn in = toBufferIn(source);
            BufferOut out = toBufferOut(dest);
            return readTo(in, out);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
    }

    private BufferIn toBufferIn(Object src) {
        int actualBlockSize = getActualBlockSize();
        if (src instanceof InputStream) {
            return new InputStreamBufferIn((InputStream) src, actualBlockSize);
        }
        if (src instanceof byte[]) {
            return new BytesBufferIn((byte[]) src, actualBlockSize);
        }
        if (src instanceof ByteBuffer) {
            return new BufferBufferIn((ByteBuffer) src, actualBlockSize);
        }
        throw new IORuntimeException("Unexpected source type: " + src.getClass());
    }

    private BufferOut toBufferOut(Object dst) {
        if (dst instanceof OutputStream) {
            return new OutputSteamBufferOut((OutputStream) dst);
        }
        if (dst instanceof byte[]) {
            return new OutputSteamBufferOut(JieIO.toOutputStream((byte[]) dst));
        }
        if (dst instanceof ByteBuffer) {
            return new OutputSteamBufferOut(JieIO.toOutputStream((ByteBuffer) dst));
        }
        throw new IORuntimeException("Unexpected destination type: " + dst.getClass());
    }

    private int getActualBlockSize() {
        if (readLimit < 0) {
            return blockSize;
        }
        return (int) Math.min(readLimit, blockSize);
    }

    private long readTo(BufferIn in, BufferOut out) throws Exception {
        long count = 0;
        while (true) {
            ByteBuffer buf = in.read();
            if (buf == null) {
                return count == 0 ? -1 : count;
            }
            if (!buf.hasRemaining()) {
                if (breakIfNoRead) {
                    return count;
                }
                continue;
            }
            count += buf.remaining();
            if (conversion != null) {
                ByteBuffer converted = conversion.apply(buf);
                out.write(converted);
            } else {
                out.write(buf);
            }
            if (readLimit > 0 && count >= readLimit) {
                break;
            }
        }
        return count;
    }

    private interface BufferIn {
        @Nullable
        ByteBuffer read() throws Exception;
    }

    private interface BufferOut {
        void write(ByteBuffer buffer) throws Exception;
    }

    private static final class InputStreamBufferIn implements BufferIn {

        private final InputStream source;
        private final byte[] block;
        private final ByteBuffer blockBuffer;

        private InputStreamBufferIn(InputStream source, int blockSize) {
            this.source = source;
            this.block = new byte[blockSize];
            this.blockBuffer = ByteBuffer.wrap(block);
        }

        @Override
        public ByteBuffer read() throws IOException {
            int size = source.read(block);
            if (size < 0) {
                return null;
            }
            blockBuffer.position(0);
            blockBuffer.limit(size);
            return blockBuffer;
        }
    }

    private static final class BytesBufferIn implements BufferIn {

        private final byte[] source;
        private final ByteBuffer sourceBuffer;
        private final int blockSize;
        private int pos = 0;

        private BytesBufferIn(byte[] source, int blockSize) {
            this.source = source;
            this.sourceBuffer = ByteBuffer.wrap(source);
            this.blockSize = blockSize;
        }

        @Override
        public ByteBuffer read() {
            if (pos >= source.length) {
                return null;
            }
            sourceBuffer.position(pos);
            int newPos = Math.min(pos + blockSize, source.length);
            sourceBuffer.limit(newPos);
            pos = newPos;
            return sourceBuffer;
        }
    }

    private static final class BufferBufferIn implements BufferIn {

        private final ByteBuffer source;
        private final int blockSize;
        private int pos;
        private final int finalLimit;

        private BufferBufferIn(ByteBuffer source, int blockSize) {
            this.source = source;
            this.blockSize = blockSize;
            this.pos = this.source.position();
            this.finalLimit = this.source.limit();
        }

        @Override
        public ByteBuffer read() {
            int newLen = Math.min(blockSize, finalLimit - pos);
            if (newLen <= 0) {
                return null;
            }
            source.position(pos);
            source.limit(pos + newLen);
            pos = source.limit();
            return source;
        }
    }

    private static final class OutputSteamBufferOut implements BufferOut {

        private final OutputStream dest;

        private OutputSteamBufferOut(OutputStream dest) {
            this.dest = dest;
        }

        @Override
        public void write(ByteBuffer buffer) throws IOException {
            if (buffer.hasArray()) {
                int remaining = buffer.remaining();
                dest.write(buffer.array(), buffer.arrayOffset() + buffer.position(), remaining);
                buffer.position(buffer.position() + remaining);
            } else {
                byte[] buf = new byte[buffer.remaining()];
                buffer.get(buf);
                dest.write(buf);
            }
        }
    }
}
