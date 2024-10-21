package xyz.fslabo.common.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

final class CharsOutputStream extends OutputStream {

    private final Appendable appender;
    private final CharsetDecoder decoder;
    private final ByteBuffer bytesBuffer;
    private final CharBuffer charsBuffer;
    private boolean closed = false;
    private final byte[] buf = {0};

    private CharsOutputStream(Appendable appender, CharsetDecoder decoder, int inBufferSize, int outBufferSize) {
        this.appender = appender;
        this.decoder = decoder;
        this.bytesBuffer = ByteBuffer.allocate(inBufferSize);
        this.charsBuffer = CharBuffer.allocate(outBufferSize);
    }

    private CharsOutputStream(Appendable appender, Charset charset, int inBufferSize, int outBufferSize) {
        this(
            appender,
            charset.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE)
                .replaceWith("?"),
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
            int avail = Math.min(bytesBuffer.remaining(), remaining);
            bytesBuffer.put(b, offset, avail);
            remaining -= avail;
            offset += avail;
            decodeByteBuffer(false);
            writeChars();
        }
    }

    @Override
    public void flush() throws IOException {
        checkClosed();
        while (bytesBuffer.hasRemaining()) {
            decodeByteBuffer(false);
            writeChars();
        }
        writeChars();
        if (appender instanceof Flushable) {
            ((Flushable) appender).flush();
        }
    }

    @Override
    public void close() throws IOException {
        if (closed) {
            return;
        }
        while (bytesBuffer.hasRemaining()) {
            decodeByteBuffer(true);
            writeChars();
        }
        writeChars();
        if (appender instanceof Closeable) {
            ((Closeable) appender).close();
        } else if (appender instanceof AutoCloseable) {
            try {
                ((AutoCloseable) appender).close();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
        closed = true;
    }

    private void decodeByteBuffer(boolean endOfInput) throws IOException {
        charsBuffer.compact();
        CoderResult coderResult = decoder.decode(bytesBuffer, charsBuffer, endOfInput);
        if (coderResult.isUnderflow() || coderResult.isOverflow()) {
            charsBuffer.flip();
            return;
        }
        throw new IOException("Chars decoding failed: " + coderResult);
    }

    private void writeChars() throws IOException {
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
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private void checkClosed() throws IOException {
        if (closed) {
            throw new IOException("Stream closed.");
        }
    }
}