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

final class WriterOutputStream extends OutputStream {

    private final Writer writer;
    private final CharsetDecoder decoder;
    private final ByteBuffer inBuffer = ByteBuffer.allocate(128);
    private final CharBuffer outBuffer;

    private WriterOutputStream(Writer writer, CharsetDecoder decoder, int bufferSize) {
        this.writer = writer;
        this.decoder = decoder;
        outBuffer = CharBuffer.allocate(bufferSize);
    }

    private WriterOutputStream(Writer writer, Charset charset, int bufferSize) {
        this(writer,
            charset.newDecoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE)
                .replaceWith("?"),
            bufferSize
        );
    }

    WriterOutputStream(Writer writer, Charset charset) {
        this(writer, charset, FsIO.DEFAULT_BUFFER_SIZE);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        FsCheck.checkRangeInBounds(0, b.length, off, off + len);
        int offset = off;
        int remaining = len;
        while (remaining > 0) {
            int writeSize = Math.min(remaining, inBuffer.remaining());
            inBuffer.put(b, offset, writeSize);
            encodeBuffer(false);
            remaining -= writeSize;
            offset += writeSize;
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[]{(byte) b}, 0, 1);
    }

    @Override
    public void flush() throws IOException {
        flushOutput();
        writer.flush();
    }

    @Override
    public void close() throws IOException {
        encodeBuffer(true);
        flushOutput();
        writer.close();
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
            writer.write(outBuffer.array(), 0, outBuffer.position());
            outBuffer.rewind();
        }
    }
}