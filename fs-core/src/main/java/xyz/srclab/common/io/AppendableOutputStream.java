package xyz.srclab.common.io;

import xyz.srclab.common.base.FsCheck;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

final class AppendableOutputStream extends OutputStream {

    private final Appendable appendable;
    private final CharsetDecoder decoder;
    private final ByteBuffer inBuffer = ByteBuffer.allocate(128);
    private final CharBuffer outBuffer;

    private AppendableOutputStream(Appendable appendable, CharsetDecoder decoder, int bufferSize) {
        this.appendable = appendable;
        this.decoder = decoder;
        outBuffer = CharBuffer.allocate(bufferSize);
    }

    private AppendableOutputStream(Appendable appendable, Charset charset, int bufferSize) {
        this(appendable,
            charset.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE)
                .replaceWith("?"),
            bufferSize
        );
    }

    AppendableOutputStream(Appendable appendable, Charset charset) {
        this(appendable, charset, FsIO.IO_BUFFER_SIZE);
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        try {
            FsCheck.checkRangeInBounds(off, off + len, 0, b.length);
            int offset = off;
            int remaining = len;
            while (remaining > 0) {
                int writeSize = Math.min(remaining, inBuffer.remaining());
                inBuffer.put(b, offset, writeSize);
                encodeBuffer(false);
                remaining -= writeSize;
                offset += writeSize;
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized void write(int b) throws IOException {
        write(new byte[]{(byte) b}, 0, 1);
    }

    @Override
    public synchronized void flush() throws IOException {
        try {
            flushOutput();
            if (appendable instanceof Writer) {
                ((Writer) appendable).flush();
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        try {
            encodeBuffer(true);
            flushOutput();
            if (appendable instanceof Writer) {
                ((Writer) appendable).close();
            }
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private void encodeBuffer(boolean endOfInput) throws IOException {
        inBuffer.flip();
        CoderResult coderResult;
        while (true) {
            coderResult = decoder.decode(inBuffer, outBuffer, endOfInput);
            if (coderResult.isOverflow()) {
                flushOutput();
            } else if (coderResult.isUnderflow()) {
                break;
            } else {
                throw new IOException("Encoding failed: " + coderResult);
            }
        }
        inBuffer.compact();
    }

    private void flushOutput() throws IOException {
        if (outBuffer.position() > 0) {
            if (appendable instanceof Writer) {
                ((Writer) appendable).write(outBuffer.array(), 0, outBuffer.position());
            } else {
                appendable.append(new String(outBuffer.array(), 0, outBuffer.position()));
            }
            outBuffer.rewind();
        }
    }
}