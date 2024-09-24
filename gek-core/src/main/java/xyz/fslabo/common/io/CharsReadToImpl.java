package xyz.fslabo.common.io;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.JieString;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;
import java.util.function.Function;

final class CharsReadToImpl implements CharsReadTo {

    private Object source;
    private Object dest;
    private long readLimit = -1;
    private int blockSize = JieIO.BUFFER_SIZE;
    private boolean breakIfNoRead = false;
    private Function<CharBuffer, CharBuffer> conversion;

    @Override
    public CharsReadTo input(Reader source) {
        this.source = source;
        return this;
    }

    @Override
    public CharsReadTo input(char[] source) {
        this.source = source;
        return this;
    }

    @Override
    public CharsReadTo input(char[] source, int offset, int length) {
        if (offset == 0 && length == source.length) {
            return input(source);
        }
        try {
            this.source = CharBuffer.wrap(source, offset, length);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
        return this;
    }

    @Override
    public CharsReadTo input(CharBuffer source) {
        this.source = source;
        return this;
    }

    @Override
    public CharsReadTo output(Appendable dest) {
        this.dest = dest;
        return this;
    }

    @Override
    public CharsReadTo output(char[] dest) {
        this.dest = CharBuffer.wrap(dest);
        return this;
    }

    @Override
    public CharsReadTo output(char[] dest, int offset, int length) {
        try {
            this.dest = CharBuffer.wrap(dest, offset, length);
        } catch (Exception e) {
            throw new IORuntimeException(e);
        }
        return this;
    }

    @Override
    public CharsReadTo output(CharBuffer dest) {
        this.dest = dest;
        return this;
    }

    @Override
    public CharsReadTo readLimit(long readLimit) {
        this.readLimit = readLimit;
        return this;
    }

    @Override
    public CharsReadTo blockSize(int blockSize) {
        if (blockSize <= 0) {
            throw new IORuntimeException("blockSize must > 0!");
        }
        this.blockSize = blockSize;
        return this;
    }

    @Override
    public CharsReadTo breakIfNoRead(boolean breakIfNoRead) {
        this.breakIfNoRead = breakIfNoRead;
        return this;
    }

    @Override
    public CharsReadTo conversion(Function<CharBuffer, CharBuffer> conversion) {
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
        if (src instanceof Reader) {
            return new ReaderBufferIn((Reader) src, actualBlockSize, readLimit);
        }
        if (src instanceof char[]) {
            return new CharsBufferIn((char[]) src, actualBlockSize);
        }
        if (src instanceof CharBuffer) {
            return new BufferBufferIn((CharBuffer) src, actualBlockSize);
        }
        throw new IORuntimeException("Unexpected source type: " + src.getClass());
    }

    private BufferOut toBufferOut(Object dst) {
        if (dst instanceof CharBuffer) {
            return new AppendableBufferOut(JieIO.toWriter((CharBuffer) dst));
        }
        if (dst instanceof Appendable) {
            return new AppendableBufferOut((Appendable) dst);
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
            CharBuffer buf = in.read();
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
                CharBuffer converted = conversion.apply(buf);
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
        CharBuffer read() throws Exception;
    }

    private interface BufferOut {
        void write(CharBuffer buffer) throws Exception;
    }

    private static final class ReaderBufferIn implements BufferIn {

        private final Reader source;
        private final char[] block;
        private final CharBuffer blockBuffer;
        private  long remaining;

        private ReaderBufferIn(Reader source, int blockSize, long remaining) {
            this.source = source;
            this.block = new char[blockSize];
            this.blockBuffer = CharBuffer.wrap(block);
            this.remaining = remaining;
        }

        @Override
        public CharBuffer read() throws IOException {
            int readSize = remaining > 0 ? (int) Math.min(block.length, remaining) : block.length;
            int size = source.read(block, 0, readSize);
            if (size < 0) {
                return null;
            }
            blockBuffer.position(0);
            blockBuffer.limit(size);
            if (remaining > 0) {
                remaining -= size;
            }
            return blockBuffer;
        }
    }

    private static final class CharsBufferIn implements BufferIn {

        private final char[] source;
        private final CharBuffer sourceBuffer;
        private final int blockSize;
        private int pos = 0;

        private CharsBufferIn(char[] source, int blockSize) {
            this.source = source;
            this.sourceBuffer = CharBuffer.wrap(source);
            this.blockSize = blockSize;
        }

        @Override
        public CharBuffer read() {
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

        private final CharBuffer source;
        private final int blockSize;
        private int pos;
        private final int finalLimit;

        private BufferBufferIn(CharBuffer source, int blockSize) {
            this.source = source;
            this.blockSize = blockSize;
            this.pos = this.source.position();
            this.finalLimit = this.source.limit();
        }

        @Override
        public CharBuffer read() {
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

    private static final class AppendableBufferOut implements BufferOut {

        private final Appendable dest;

        private AppendableBufferOut(Appendable dest) {
            this.dest = dest;
        }

        @Override
        public void write(CharBuffer buffer) throws IOException {
            if (dest instanceof Writer) {
                write(buffer, (Writer) dest);
                return;
            }
            if (buffer.hasArray()) {
                int remaining = buffer.remaining();
                int start = buffer.arrayOffset() + buffer.position();
                dest.append(JieString.asChars(buffer.array(), start, start + remaining));
                buffer.position(buffer.position() + remaining);
            } else {
                char[] buf = new char[buffer.remaining()];
                buffer.get(buf);
                dest.append(JieString.asChars(buf, 0, buf.length));
            }
        }

        private void write(CharBuffer buffer, Writer writer) throws IOException {
            if (buffer.hasArray()) {
                int remaining = buffer.remaining();
                writer.write(buffer.array(), buffer.arrayOffset() + buffer.position(), remaining);
                buffer.position(buffer.position() + remaining);
            } else {
                char[] buf = new char[buffer.remaining()];
                buffer.get(buf);
                writer.write(buf);
            }
        }
    }
}
