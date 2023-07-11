package xyz.srclab.common.io;

import xyz.srclab.common.base.FsCheck;

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
        this(reader, charset, FsIO.IO_BUFFER_SIZE);
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

    @Override
    public int read(byte[] array, int off, int len) throws IOException {
        FsCheck.checkRangeInBounds(off, off + len, 0, array.length);
        if (len == 0) {
            return 0;
        }
        int readNum = 0;
        int offset = off;
        int remaining = len;
        while (true) {
            if (outBuffer.hasRemaining()) {
                int readSize = Math.min(outBuffer.remaining(), remaining);
                outBuffer.get(array, offset, readSize);
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

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read() throws IOException {
        while (true) {
            if (outBuffer.hasRemaining()) {
                return outBuffer.get() & 0xFF;
            }
            fillBuffer();
            if (endOfInput && !outBuffer.hasRemaining()) {
                return -1;
            }
        }
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
