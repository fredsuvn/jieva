package xyz.fslabo.common.io;

import xyz.fslabo.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

final class CharsInputStream extends InputStream {

    private final Reader reader;
    private final CharsetEncoder encoder;
    private final CharBuffer charsBuffer;
    private final ByteBuffer bytesBuffer;
    private boolean endOfInput;
    private boolean closed = false;
    private final byte[] buf = {0};

    private CharsInputStream(Reader reader, CharsetEncoder encoder, int inBufferSize, int outBufferSize) {
        this.reader = reader;
        this.encoder = encoder;
        this.charsBuffer = CharBuffer.allocate(inBufferSize);
        this.charsBuffer.flip();
        this.bytesBuffer = ByteBuffer.allocate(outBufferSize);
        this.bytesBuffer.flip();
    }

    private CharsInputStream(Reader reader, Charset charset, int inBufferSize, int outBufferSize) {
        this(
            reader,
            charset.newEncoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT),
            inBufferSize,
            outBufferSize
        );
    }

    CharsInputStream(Reader reader, Charset charset) {
        this(reader, charset, 64, 64);
    }

    @Override
    public int read() throws IOException {
        int readNum = read(buf, 0, 1);
        return readNum == -1 ? -1 : (buf[0] & 0xFF);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        IOMisc.checkReadBounds(b, off, len);
        checkClosed();
        if (len <= 0) {
            return 0;
        }
        int readNum = read0(b, off, len, true);
        return readNum == 0 ? -1 : readNum;
    }

    @Override
    public long skip(long n) throws IOException {
        checkClosed();
        if (n <= 0) {
            return 0;
        }
        return read0(null, 0, (int) n, false);
    }

    @Override
    public int available() {
        return bytesBuffer.remaining();
    }

    @Override
    public void close() throws IOException {
        reader.close();
        closed = true;
    }

    private int read0(@Nullable byte[] b, int off, int len, boolean fillBytes) throws IOException {
        int readNum = 0;
        int offset = off;
        int remaining = len;
        while (true) {
            if (bytesBuffer.hasRemaining()) {
                int avail = Math.min(bytesBuffer.remaining(), remaining);
                if (fillBytes) {
                    bytesBuffer.get(b, offset, avail);
                } else {
                    bytesBuffer.position(bytesBuffer.position() + avail);
                }
                offset += avail;
                remaining -= avail;
                readNum += avail;
            } else if (endOfInput) {
                if (charsBuffer.hasRemaining()) {
                    encodeCharBuffer();
                } else {
                    break;
                }
            } else {
                fillBuffer();
            }
            if (remaining <= 0) {
                break;
            }
        }
        return readNum;
    }

    private void fillBuffer() throws IOException {
        charsBuffer.compact();
        int readSize = reader.read(charsBuffer);
        if (readSize == -1) {
            endOfInput = true;
        }
        charsBuffer.flip();
        encodeCharBuffer();
    }

    private void encodeCharBuffer() throws IOException {
        bytesBuffer.compact();
        CoderResult coderResult = encoder.encode(charsBuffer, bytesBuffer, endOfInput);
        if (coderResult.isUnderflow() || coderResult.isOverflow()) {
            bytesBuffer.flip();
            return;
        }
        throw new IOException("Chars encoding failed: " + coderResult);
    }

    private void checkClosed() throws IOException {
        if (closed) {
            throw new IOException("Stream closed.");
        }
    }
}
