package xyz.fsgek.common.codec;

import lombok.Data;
import org.springframework.core.codec.CodecException;
import xyz.fsgek.common.base.GekChars;
import xyz.fsgek.common.base.GekString;
import xyz.fsgek.common.data.GekDataProcess;
import xyz.fsgek.common.io.GekIO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * {@link GekDataProcess} to encode/decode base64.
 *
 * @author fredsuvn
 */
public class Base64Process implements GekDataProcess<Base64Process> {

    private static final int ENCODE_MODE = 0;
    private static final int DECODE_MODE = 1;
    private static final int BASIC_TYPE = 0;
    private static final int URL_TYPE = 1;
    private static final int MIME_TYPE = 2;

    private Object input;
    private Object output;
    private int bufferSize = GekIO.IO_BUFFER_SIZE;
    private int mode = ENCODE_MODE;
    private int type = BASIC_TYPE;
    private boolean withoutPadding = false;

    private MimeParams mimeParams;

    @Override
    public Base64Process input(byte[] array) {
        this.input = array;
        return this;
    }

    @Override
    public Base64Process input(ByteBuffer buffer) {
        this.input = buffer;
        return this;
    }

    @Override
    public Base64Process input(InputStream in) {
        this.input = in;
        return this;
    }

    @Override
    public Base64Process output(byte[] array) {
        this.output = array;
        return this;
    }

    @Override
    public Base64Process output(ByteBuffer buffer) {
        this.output = buffer;
        return this;
    }

    @Override
    public Base64Process output(OutputStream out) {
        this.output = out;
        return this;
    }

    /**
     * Sets buffer size for IO operations.
     *
     * @param bufferSize buffer size
     * @return this
     */
    public Base64Process bufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
        return this;
    }

    /**
     * Sets mode to encoding which backed by {@link Base64.Encoder}.
     *
     * @return this
     */
    public Base64Process encode() {
        this.mode = ENCODE_MODE;
        return this;
    }

    /**
     * Sets mode to decoding which backed by {@link Base64.Decoder}.
     *
     * @return this
     */
    public Base64Process decode() {
        this.mode = DECODE_MODE;
        return this;
    }

    /**
     * Sets type to basic which backed by
     * {@link Base64#getEncoder()} or {@link Base64#getDecoder()}.
     *
     * @return this
     */
    public Base64Process basic() {
        this.type = BASIC_TYPE;
        return this;
    }

    /**
     * Sets type to URL and Filename safe type which backed by
     * {@link Base64#getUrlEncoder()} or {@link Base64#getUrlDecoder()}.
     *
     * @return this
     */
    public Base64Process url() {
        this.type = URL_TYPE;
        return this;
    }

    /**
     * Sets type to MIME type which backed by
     * {@link Base64#getMimeEncoder()} or {@link Base64#getMimeDecoder()}.
     *
     * @return this
     */
    public Base64Process mime() {
        this.type = URL_TYPE;
        return this;
    }

    /**
     * Sets type to MIME type which backed by
     * {@link Base64#getMimeEncoder(int, byte[])}  or {@link Base64#getMimeDecoder()}.
     *
     * @param lineLength    the length of each output line (rounded down to nearest multiple
     *                      of 4). If {@code lineLength <= 0} the output will not be separated
     *                      in lines
     * @param lineSeparator the line separator for each output line
     * @return this
     */
    public Base64Process mime(int lineLength, byte[] lineSeparator) {
        this.type = URL_TYPE;
        this.mimeParams = new MimeParams(lineLength, lineSeparator);
        return this;
    }

    /**
     * Sets whether the encoding/decoding without padding: {@link Base64.Encoder#withoutPadding()}.
     *
     * @return this
     */
    public Base64Process withoutPadding(boolean withoutPadding) {
        this.withoutPadding = withoutPadding;
        return this;
    }

    @Override
    public long doFinal() {
        switch (mode) {
            case ENCODE_MODE:
                return doEncode();
            case DECODE_MODE:
                return doDecode();
        }
        throw new IllegalStateException("Unknown mode: " + mode);
    }

    private long doEncode() {
        try {
            Base64.Encoder encoder = getEncoder();
            if (input instanceof byte[]) {
                if (output instanceof byte[]) {
                    return encoder.encode((byte[]) input, (byte[]) output);
                } else if (output instanceof ByteBuffer) {
                    byte[] result = encoder.encode((byte[]) input);
                    ((ByteBuffer) output).put(result);
                    return result.length;
                } else if (output instanceof OutputStream) {
                    byte[] result = encoder.encode((byte[]) input);
                    ((OutputStream) output).write(result);
                    return result.length;
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof ByteBuffer) {
                if (output instanceof byte[]) {
                    ByteBuffer result = encoder.encode((ByteBuffer) input);
                    int remaining = result.remaining();
                    result.get((byte[]) output);
                    return remaining;
                } else if (output instanceof ByteBuffer) {
                    ByteBuffer result = encoder.encode((ByteBuffer) input);
                    int remaining = result.remaining();
                    ((ByteBuffer) output).put(result);
                    return remaining;
                } else if (output instanceof OutputStream) {
                    ByteBuffer result = encoder.encode((ByteBuffer) input);
                    int remaining = result.remaining();
                    ((OutputStream) output).write(GekIO.readBack(result));
                    return remaining;
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof InputStream) {
                if (output instanceof byte[]) {
                    return doEncode(encoder, (InputStream) input, GekIO.toOutputStream((byte[]) output));
                } else if (output instanceof ByteBuffer) {
                    return doEncode(encoder, (InputStream) input, GekIO.toOutputStream((ByteBuffer) output));
                } else if (output instanceof OutputStream) {
                    return doEncode(encoder, (InputStream) input, (OutputStream) output);
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else {
                throw new CodecException("Unknown input type: " + input.getClass());
            }
        } catch (CodecException e) {
            throw e;
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    private long doEncode(Base64.Encoder encoder, InputStream in, OutputStream out) throws IOException {
        OutputCounter counter = new OutputCounter(out);
        OutputStream encOut = encoder.wrap(counter);
        GekIO.readTo(in, encOut, -1, bufferSize);
        encOut.close();
        return counter.count;
    }

    private long doDecode() {
        try {
            Base64.Decoder decoder = getDecoder();
            if (input instanceof byte[]) {
                if (output instanceof byte[]) {
                    return decoder.decode((byte[]) input, (byte[]) output);
                } else if (output instanceof ByteBuffer) {
                    return doDecode(decoder, GekIO.toInputStream((byte[]) input), GekIO.toOutputStream((ByteBuffer) output));
                } else if (output instanceof OutputStream) {
                    return doDecode(decoder, GekIO.toInputStream((byte[]) input), (OutputStream) output);
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof ByteBuffer) {
                if (output instanceof byte[]) {
                    return doDecode(decoder, GekIO.toInputStream((ByteBuffer) input), GekIO.toOutputStream((byte[]) output));
                } else if (output instanceof ByteBuffer) {
                    return doDecode(decoder, GekIO.toInputStream((ByteBuffer) input), GekIO.toOutputStream((ByteBuffer) output));
                } else if (output instanceof OutputStream) {
                    return doDecode(decoder, GekIO.toInputStream((ByteBuffer) input), (OutputStream) output);
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else if (input instanceof InputStream) {
                if (output instanceof byte[]) {
                    return doDecode(decoder, (InputStream) input, GekIO.toOutputStream((byte[]) output));
                } else if (output instanceof ByteBuffer) {
                    return doDecode(decoder, (InputStream) input, GekIO.toOutputStream((ByteBuffer) output));
                } else if (output instanceof OutputStream) {
                    return doDecode(decoder, (InputStream) input, (OutputStream) output);
                } else {
                    throw new CodecException("Unknown output type: " + output.getClass());
                }
            } else {
                throw new CodecException("Unknown input type: " + input.getClass());
            }
        } catch (CodecException e) {
            throw e;
        } catch (Exception e) {
            throw new GekCodecException(e);
        }
    }

    private long doDecode(Base64.Decoder decoder, InputStream in, OutputStream out) throws IOException {
        InputStream wrapper = decoder.wrap(in);
        OutputCounter counter = new OutputCounter(out);
        GekIO.readTo(wrapper, counter, -1, bufferSize);
        return counter.count;
    }

    @Override
    public byte[] finalBytes() {
        switch (mode) {
            case ENCODE_MODE:
                return getEncoder().encode(inputToBytes());
            case DECODE_MODE:
                return getDecoder().decode(inputToBytes());
        }
        throw new IllegalStateException("Unknown mode: " + mode);
    }

    @Override
    public String finalLatinString() {
        switch (mode) {
            case ENCODE_MODE:
                return getEncoder().encodeToString(inputToBytes());
            case DECODE_MODE:
                return GekString.of(getDecoder().decode(inputToBytes()), GekChars.ISO_8859_1);
        }
        throw new IllegalStateException("Unknown mode: " + mode);
    }

    @Override
    public InputStream finalStream() {
        switch (mode) {
            case ENCODE_MODE:
                Base64.Encoder encoder = getEncoder();
                InputStream enIn = inputToInputStream();
                ByteArrayOutputStream bsOut = new ByteArrayOutputStream();
                OutputStream wrapper = encoder.wrap(bsOut);
                return GekIO.transform(enIn, bufferSize, bytes -> {
                    try {
                        wrapper.write(bytes);
                        if (bytes.length != bufferSize) {
                            wrapper.close();
                        }
                        return bsOut.toByteArray();
                    } catch (IOException e) {
                        throw new GekCodecException(e);
                    }
                });

            case DECODE_MODE:
                Base64.Decoder decoder = getDecoder();
                InputStream deIn = inputToInputStream();
                return decoder.wrap(deIn);
        }
        throw new IllegalStateException("Unknown mode: " + mode);
    }

    private byte[] inputToBytes() {
        if (input instanceof byte[]) {
            return (byte[]) input;
        }
        if (input instanceof ByteBuffer) {
            return GekIO.read((ByteBuffer) input);
        }
        if (input instanceof InputStream) {
            return GekIO.read((InputStream) input);
        }
        throw new CodecException("Unknown input type: " + input.getClass());
    }

    private InputStream inputToInputStream() {
        if (input instanceof byte[]) {
            return GekIO.toInputStream((byte[]) input);
        }
        if (input instanceof ByteBuffer) {
            return GekIO.toInputStream((ByteBuffer) input);
        }
        if (input instanceof InputStream) {
            return (InputStream) input;
        }
        throw new CodecException("Unknown input type: " + input.getClass());
    }

    private Base64.Encoder getEncoder() {
        Base64.Encoder encoder = getEncoder0();
        return withoutPadding ? encoder.withoutPadding() : encoder;
    }

    private Base64.Encoder getEncoder0() {
        switch (type) {
            case BASIC_TYPE:
                return Base64.getEncoder();
            case URL_TYPE:
                return Base64.getUrlEncoder();
            case MIME_TYPE:
                return mimeParams == null ?
                    Base64.getMimeEncoder() : Base64.getMimeEncoder(mimeParams.lineLength, mimeParams.lineSeparator);
        }
        throw new IllegalStateException("Unknown type: " + type);
    }

    private Base64.Decoder getDecoder() {
        switch (type) {
            case BASIC_TYPE:
                return Base64.getDecoder();
            case URL_TYPE:
                return Base64.getUrlDecoder();
            case MIME_TYPE:
                return Base64.getMimeDecoder();
        }
        throw new IllegalStateException("Unknown type: " + type);
    }

    @Data
    private static final class MimeParams {
        private final int lineLength;
        private final byte[] lineSeparator;
    }

    private static final class OutputCounter extends OutputStream {

        private final OutputStream outputStream;
        private long count = 0;

        private OutputCounter(OutputStream outputStream) {
            this.outputStream = outputStream;
        }

        @Override
        public void write(byte[] b) throws IOException {
            outputStream.write(b);
            count += b.length;
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            outputStream.write(b, off, len);
            count += len;
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
            count++;
        }

        @Override
        public void close() {
            //do nothing
        }
    }
}
