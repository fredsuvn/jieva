package xyz.fslabo.common.io;

import xyz.fslabo.common.base.JieChars;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

/**
 * Provides implementations and utilities for {@link OutputStream}/{@link Writer}.
 *
 * @author sunqian
 */
public class JieOutput {

    /**
     * Wraps given array as an {@link OutputStream}.
     * <p>
     * Returned stream does not guarantee thread safety, and the written data must not overflow the array. Close method
     * does nothing.
     *
     * @param array given array
     * @return given array as an {@link OutputStream}
     */
    public static OutputStream wrap(byte[] array) {
        return new BytesOutputStream(array);
    }

    /**
     * Wraps given array as {@link OutputStream} from specified offset up to specified length.
     * <p>
     * Returned stream does not guarantee thread safety, and the written data must not overflow the array. Close method
     * does nothing.
     *
     * @param array  given array
     * @param offset specified offset
     * @param length specified length
     * @return given array as an {@link OutputStream}
     */
    public static OutputStream wrap(byte[] array, int offset, int length) {
        return new BytesOutputStream(array, offset, length);
    }

    /**
     * Wraps given buffer as an {@link OutputStream}.
     * <p>
     * Returned stream does not guarantee thread safety, and the written data must not overflow the buffer. Close method
     * does nothing.
     *
     * @param buffer given buffer
     * @return given buffer as an {@link OutputStream}
     */
    public static OutputStream wrap(ByteBuffer buffer) {
        return new BufferOutputStream(buffer);
    }

    /**
     * Wraps given random access file as an {@link OutputStream} from specified initial file pointer.
     * <p>
     * Returned stream does not guarantee thread safety. It first seeks to specified initial file pointer when creating
     * the stream. The close method will close the file.
     * <p>
     * Note that if anything else seeks this file, it will affect this stream.
     *
     * @param random      given random access file
     * @param initialSeek specified initial file pointer
     * @return given random access file as an {@link OutputStream}
     * @throws IORuntimeException IO runtime exception
     */
    public static OutputStream wrap(RandomAccessFile random, long initialSeek) throws IORuntimeException {
        try {
            return new RandomOutputStream(random, initialSeek);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    /**
     * Wraps given char appender as an {@link OutputStream} with {@link JieChars#defaultCharset()}.
     * <p>
     * Returned stream does not guarantee thread safety. The written position of the appender and stream may not
     * correspond, the close method will close both appender and stream at their current positions.
     *
     * @param appender given char appender
     * @return given char appender as an {@link OutputStream}
     */
    public static OutputStream wrap(Appendable appender) {
        return wrap(appender, JieChars.defaultCharset());
    }

    /**
     * Wraps given char appender as an {@link OutputStream} with specified charset.
     * <p>
     * Returned stream does not guarantee thread safety. The written position of the appender and stream may not
     * correspond, the close method will close both appender and stream at their current positions.
     *
     * @param appender given char appender
     * @param charset  specified charset
     * @return given char appender as an {@link OutputStream}
     */
    public static OutputStream wrap(Appendable appender, Charset charset) {
        return new CharsOutputStream(appender, charset);
    }

    /**
     * Wraps given array as an {@link Writer}.
     * <p>
     * Returned writer does not guarantee thread safety, and the written data must not overflow the buffer. Close method
     * does nothing.
     *
     * @param array given array
     * @return given array as an {@link Writer}
     */
    public static Writer wrap(char[] array) {
        return new BufferWriter(array);
    }

    /**
     * Wraps given array as {@link Writer} from specified offset up to specified length.
     * <p>
     * Returned writer does not guarantee thread safety, and the written data must not overflow the buffer. Close method
     * does nothing.
     *
     * @param array  given array
     * @param offset specified offset
     * @param length specified length
     * @return given array as an {@link Writer}
     */
    public static Writer wrap(char[] array, int offset, int length) {
        return new BufferWriter(array, offset, length);
    }

    /**
     * Wraps given buffer as an {@link Writer}.
     * <p>
     * Returned writer does not guarantee thread safety, and the written data must not overflow the buffer. Close method
     * does nothing.
     *
     * @param buffer given buffer
     * @return given buffer as an {@link Writer}
     */
    public static Writer wrap(CharBuffer buffer) {
        return new BufferWriter(buffer);
    }

    private static final class BytesOutputStream extends OutputStream {

        private final byte[] buf;
        private final int end;
        private int pos;

        BytesOutputStream(byte[] buf) {
            this(buf, 0, buf.length);
        }

        BytesOutputStream(byte[] buf, int offset, int length) {
            IOMisc.checkReadBounds(buf, offset, length);
            this.buf = buf;
            this.end = offset + length;
            this.pos = offset;
        }

        @Override
        public void write(int b) throws IOException {
            if (end - pos < 1) {
                throw new IOException("The backing array has insufficient capacity remaining.");
            }
            buf[pos] = (byte) b;
            pos++;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            IOMisc.checkReadBounds(b, off, len);
            if (len <= 0) {
                return;
            }
            if (end - pos < len) {
                throw new IOException("The backing array has insufficient capacity remaining.");
            }
            System.arraycopy(b, off, buf, pos, len);
            pos += len;
        }
    }

    private static final class BufferOutputStream extends OutputStream {

        private final ByteBuffer buffer;

        BufferOutputStream(ByteBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        public void write(int b) throws IOException {
            try {
                buffer.put((byte) b);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            IOMisc.checkReadBounds(b, off, len);
            if (len <= 0) {
                return;
            }
            try {
                buffer.put(b, off, len);
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    private static final class CharsOutputStream extends OutputStream {

        private final Appendable appender;
        private final CharsetDecoder decoder;
        private final ByteBuffer bytesBuffer;

        // Should be keep flush to empty.
        private final CharBuffer charsBuffer;

        private boolean closed = false;
        private final byte[] buf = {0};

        private CharsOutputStream(Appendable appender, CharsetDecoder decoder, int inBufferSize, int outBufferSize) {
            this.appender = appender;
            this.decoder = decoder;
            this.bytesBuffer = ByteBuffer.allocate(inBufferSize);
            this.bytesBuffer.flip();
            this.charsBuffer = CharBuffer.allocate(outBufferSize);
            this.charsBuffer.flip();
        }

        private CharsOutputStream(Appendable appender, Charset charset, int inBufferSize, int outBufferSize) {
            this(
                appender,
                charset.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT),
                inBufferSize,
                outBufferSize
            );
        }

        CharsOutputStream(Appendable appender, Charset charset) {
            this(appender, charset, 64, 64);
        }

        @Override
        public void write(int b) throws IOException {
            buf[0] = (byte) b;
            write(buf, 0, 1);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            IOMisc.checkReadBounds(b, off, len);
            checkClosed();
            if (len <= 0) {
                return;
            }
            int offset = off;
            int remaining = len;
            while (remaining > 0) {
                bytesBuffer.compact();
                int bytesLimit = bytesBuffer.position();
                int avail = Math.min(bytesBuffer.remaining(), remaining);
                bytesBuffer.put(b, offset, avail);
                remaining -= avail;
                offset += avail;
                bytesBuffer.flip();
                decodeByteBuffer(bytesLimit, false);
            }
        }

        @Override
        public void flush() throws IOException {
            checkClosed();
            if (appender instanceof Flushable) {
                ((Flushable) appender).flush();
            }
        }

        @Override
        public void close() throws IOException {
            if (closed) {
                return;
            }
            if (appender instanceof Closeable) {
                ((Closeable) appender).close();
            } else if (appender instanceof AutoCloseable) {
                try {
                    ((AutoCloseable) appender).close();
                } catch (IOException e) {
                    throw e;
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }
            closed = true;
        }

        private void decodeByteBuffer(int bytesMark, boolean endOfInput) throws IOException {
            while (true) {
                charsBuffer.compact();
                CoderResult coderResult = decoder.decode(bytesBuffer, charsBuffer, endOfInput);
                if (coderResult.isUnderflow()) {
                    charsBuffer.flip();
                    flushCharBuffer(bytesMark);
                    return;
                }
                if (coderResult.isOverflow()) {
                    charsBuffer.flip();
                    flushCharBuffer(bytesMark);
                    continue;
                }
                throw new IOException("Chars decoding failed: " + coderResult);
            }
        }

        private void flushCharBuffer(int bytesLimit) throws IOException {
            if (!charsBuffer.hasRemaining()) {
                return;
            }
            try {
                if (appender instanceof Writer) {
                    ((Writer) appender).write(charsBuffer.array(), charsBuffer.position(), charsBuffer.remaining());
                    charsBuffer.position(charsBuffer.limit());
                } else if (appender instanceof StringBuilder) {
                    ((StringBuilder) appender).append(charsBuffer.array(), charsBuffer.position(), charsBuffer.remaining());
                    charsBuffer.position(charsBuffer.limit());
                } else if (appender instanceof StringBuffer) {
                    ((StringBuffer) appender).append(charsBuffer.array(), charsBuffer.position(), charsBuffer.remaining());
                    charsBuffer.position(charsBuffer.limit());
                } else if (appender instanceof CharBuffer) {
                    ((CharBuffer) appender).put(charsBuffer);
                } else {
                    while (charsBuffer.hasRemaining()) {
                        appender.append(charsBuffer.get());
                    }
                }
            } catch (IOException e) {
                rollback(bytesLimit);
                throw e;
            } catch (Exception e) {
                rollback(bytesLimit);
                throw new IOException(e);
            }
        }

        private void rollback(int limit) {
            bytesBuffer.position(0);
            bytesBuffer.limit(limit);
            charsBuffer.position(0);
            charsBuffer.limit(0);
        }

        private void checkClosed() throws IOException {
            if (closed) {
                throw new IOException("Stream closed.");
            }
        }
    }

    private static final class RandomOutputStream extends OutputStream {

        private final RandomAccessFile random;

        RandomOutputStream(RandomAccessFile random, long initialSeek) throws IOException {
            this.random = random;
            this.random.seek(initialSeek);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            IOMisc.checkReadBounds(b, off, len);
            if (len <= 0) {
                return;
            }
            random.write(b, off, len);
        }

        @Override
        public void write(int b) throws IOException {
            random.write(b);
        }

        @Override
        public void flush() throws IOException {
            random.getFD().sync();
        }

        @Override
        public void close() throws IOException {
            random.close();
        }
    }

    private static final class BufferWriter extends AbstractWriter {

        private final CharBuffer buffer;

        BufferWriter(char[] cbuf) {
            this(cbuf, 0, cbuf.length);
        }

        BufferWriter(char[] cbuf, int offset, int length) {
            IOMisc.checkReadBounds(cbuf, offset, length);
            this.buffer = CharBuffer.wrap(cbuf, offset, length);
        }

        BufferWriter(CharBuffer buffer) {
            this.buffer = buffer;
        }

        @Override
        protected void doWrite(char c) {
            buffer.put(c);
        }

        @Override
        protected void doWrite(char[] cbuf, int off, int len) {
            buffer.put(cbuf, off, len);
        }

        @Override
        protected void doWrite(String str, int off, int len) {
            buffer.put(str, off, off + len);
        }

        @Override
        protected void doAppend(CharSequence csq, int start, int end) {
            buffer.append(csq, start, end);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws IOException {
        }
    }
}
