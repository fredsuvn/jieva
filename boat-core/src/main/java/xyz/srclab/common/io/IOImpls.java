package xyz.srclab.common.io;

import xyz.srclab.common.lang.MgCheck;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;

/**
 * Implementations of {@link java.io.Reader}.
 *
 * @author fredsuvn
 */
public class IOImpls {




    /**
     * {@link java.io.Reader} as {@link InputStream}.
     * <p>
     * This input stream doesn't support {@link #mark(int)} and {@link #reset()}, ant its {@link #available()} always returns 0.
     */
    public static class OfReader extends InputStream {

        private static final ByteBuffer END = ByteBuffer.allocate(0);

        static {
            END.flip();
        }

        private final Reader source;
        private final CharsetEncoder encoder;
        private final CharBuffer encodeBuffer;
        private ByteBuffer outBuffer;

        /**
         * Constructs with reader source, charset, and buffer size for encoding.
         *
         * @param source           reader source
         * @param charset          charset
         * @param encodeBufferSize buffer size for encoding
         */
        public OfReader(Reader source, Charset charset, int encodeBufferSize) {
            this.source = source;
            this.encoder = charset.newEncoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);
            this.encodeBuffer = CharBuffer.allocate(encodeBufferSize);
            this.encodeBuffer.flip();
        }

        /**
         * Constructs with reader source, charset encoder, and buffer size for encoding.
         *
         * @param source           reader source
         * @param encoder          charset encoder
         * @param encodeBufferSize buffer size for encoding
         */
        public OfReader(Reader source, CharsetEncoder encoder, int encodeBufferSize) {
            this.source = source;
            this.encoder = encoder;
            this.encodeBuffer = CharBuffer.allocate(encodeBufferSize);
            this.encodeBuffer.flip();
        }

        @Override
        public int read() throws IOException {
            fillBuffer();
            if (outBuffer == END) {
                return -1;
            }
            return outBuffer.get() & 0x000000ff;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            MgCheck.checkRangeInBounds(off, off + len, 0, b.length);
            fillBuffer();
            if (outBuffer == END) {
                return -1;
            }
            int read = 0;
            while (true) {
                int remaining = outBuffer.remaining();
                if (remaining <= 0) {
                    return read;
                }
                int curRead = Math.min(len - read, remaining);
                outBuffer.get(b, off + read, curRead);
                read += curRead;
                if (read >= len) {
                    return read;
                }
                fillBuffer();
            }
        }

        @Override
        public long skip(long n) throws IOException {
            fillBuffer();
            if (outBuffer == END) {
                return -1;
            }
            int read = 0;
            while (true) {
                int remaining = outBuffer.remaining();
                if (remaining <= 0) {
                    return read;
                }
                long curRead = Math.min(n - read, remaining);
                outBuffer.position(outBuffer.position() + (int) curRead);
                read += curRead;
                if (read >= n) {
                    return read;
                }
                fillBuffer();
            }
        }

        @Override
        public void close() throws IOException {
            source.close();
        }

        private void fillBuffer() throws IOException {
            if (outBuffer == END) {
                return;
            }
            if (outBuffer != null && outBuffer.hasRemaining()) {
                return;
            }
            encodeBuffer.clear();
            int size = source.read(encodeBuffer);
            encodeBuffer.flip();
            if (size == -1) {
                outBuffer = END;
                return;
            }
            outBuffer = encoder.encode(encodeBuffer);
        }
    }

    private abstract static class BaseReader extends Reader {
        /**
         * Returns whether current reader is end.
         *
         * @return whether current reader is end.
         */
        protected abstract boolean isEnd();
    }

    private abstract static class BaseInputStream extends InputStream {
        /**
         * Returns whether current reader is end.
         *
         * @return whether current reader is end.
         */
        protected abstract boolean isEnd();
    }
}
