package xyz.fslabo.common.io;

import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.base.JieCheck;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

final class ReaderInputStream extends InputStream {

    private final Reader reader;
    private final CharsetEncoder encoder;
    private final CharBuffer inBuffer;
    private final ByteBuffer outBuffer;
    private CoderResult lastCoderResult;
    private boolean endOfInput;

    private ReaderInputStream(Reader reader, CharsetEncoder encoder, int bufferSize) {
        this.reader = reader;
        this.encoder = encoder;
        this.inBuffer = CharBuffer.allocate(bufferSize);
        this.inBuffer.flip();
        this.outBuffer = ByteBuffer.allocate(128);
        this.outBuffer.flip();
    }

    private ReaderInputStream(Reader reader, Charset charset, int bufferSize) {
        this(reader,
            charset.newEncoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE),
            bufferSize);
    }

    ReaderInputStream(Reader reader, Charset charset) {
        this(reader, charset, JieIO.IO_BUFFER_SIZE);
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        try {
            JieCheck.checkRangeInBounds(off, off + len, 0, b.length);
            if (len == 0) {
                return 0;
            }
            return read0(b, off, len, true);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized int read() throws IOException {
        try {
            while (true) {
                if (outBuffer.hasRemaining()) {
                    return outBuffer.get() & 0x000000FF;
                }
                fillBuffer();
                if (endOfInput && !outBuffer.hasRemaining()) {
                    return -1;
                }
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized long skip(long n) throws IOException {
        try {
            try {
                if (n <= 0) {
                    return 0;
                }
                return read0(null, 0, n, false);
            } catch (Exception e) {
                throw new IOException(e);
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        reader.close();
    }

    private int read0(@Nullable byte[] b, int off, long len, boolean fillBytes) throws IOException {
        int readNum = 0;
        int offset = off;
        long remaining = len;
        while (true) {
            if (outBuffer.hasRemaining()) {
                long readSize = Math.min(outBuffer.remaining(), remaining);
                if (fillBytes) {
                    outBuffer.get(b, offset, (int) readSize);
                } else {
                    long newPos = outBuffer.position() + readSize;
                    outBuffer.position((int) newPos);
                }
                offset += readSize;
                remaining -= readSize;
                readNum += readSize;
            } else {
                fillBuffer();
                if (endOfInput && !outBuffer.hasRemaining()) {
                    break;
                }
            }
            if (remaining <= 0) {
                break;
            }
        }
        return readNum == 0 && endOfInput ? -1 : readNum;
    }

    private void fillBuffer() throws IOException {
        if (!endOfInput &&
            (lastCoderResult == null || lastCoderResult.isUnderflow() || lastCoderResult.isOverflow())) {
            inBuffer.compact();
            int position = inBuffer.position();
            if (position < inBuffer.capacity()) {
                int readSize = reader.read(inBuffer.array(), position, inBuffer.remaining());
                if (readSize == -1) {
                    endOfInput = true;
                } else {
                    inBuffer.position(position + readSize);
                }
                inBuffer.flip();
            }
        }
        outBuffer.compact();
        lastCoderResult = encoder.encode(inBuffer, outBuffer, endOfInput);
        if (lastCoderResult.isMalformed() || lastCoderResult.isMalformed()) {
            throw new IOException("Encoding failed: " + lastCoderResult);
        }
        outBuffer.flip();
    }
}
