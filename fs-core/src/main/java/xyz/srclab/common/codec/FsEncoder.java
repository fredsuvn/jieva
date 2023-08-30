package xyz.srclab.common.codec;

import xyz.srclab.common.base.FsString;
import xyz.srclab.common.io.FsIO;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Encoder and decoder interface.
 *
 * @author fredsuvn
 */
public interface FsEncoder {

    /**
     * Returns encoder of specified algorithm name from {@link FsCodecProvider#defaultProvider()}.
     *
     * @param algorithmName specified algorithm name
     */
    static FsEncoder getEncoder(String algorithmName) {
        return FsCodecProvider.defaultProvider().getEncoder(algorithmName);
    }

    /**
     * Returns encoder of specified algorithm name from given codec provider.
     *
     * @param algorithmName specified algorithm name
     * @param provider      given codec provider
     */
    static FsEncoder getEncoder(String algorithmName, FsCodecProvider provider) {
        return provider.getEncoder(algorithmName);
    }

    /**
     * Returns base64 encoder.
     */
    static FsEncoder base64() {
        return getEncoder(FsAlgorithm.BASE64.getName());
    }

    /**
     * Returns base64 encoder from given codec provider.
     *
     * @param provider given codec provider
     */
    static FsEncoder base64(FsCodecProvider provider) {
        return getEncoder(FsAlgorithm.BASE64.getName(), provider);
    }

    /**
     * Returns hex encoder.
     */
    static FsEncoder hex() {
        return getEncoder(FsAlgorithm.HEX.getName());
    }

    /**
     * Returns hex encoder from given codec provider.
     *
     * @param provider given codec provider
     */
    static FsEncoder hex(FsCodecProvider provider) {
        return getEncoder(FsAlgorithm.HEX.getName(), provider);
    }

    /**
     * Returns algorithm name.
     */
    String algorithmName();

    /**
     * Encodes source array.
     *
     * @param source source array
     */
    default byte[] encode(byte[] source) {
        return encode(source, 0, source.length);
    }

    /**
     * Encodes source array of specified length from offset index.
     *
     * @param source source array
     * @param offset offset index
     * @param length specified length
     */
    byte[] encode(byte[] source, int offset, int length);

    /**
     * Encodes source array into dest array, return number of bytes written.
     *
     * @param source source array
     * @param dest   dest array
     */
    default int encode(byte[] source, byte[] dest) {
        return encode(source, 0, dest, 0, source.length);
    }

    /**
     * Encodes source array of specified length from source offset index,
     * into dest array from dest offset index, return number of bytes written.
     *
     * @param source       source array
     * @param sourceOffset source offset index
     * @param dest         dest array
     * @param destOffset   dest offset index
     * @param length       specified length
     */
    default int encode(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        try {
            ByteBuffer in = ByteBuffer.wrap(source, sourceOffset, length);
            ByteBuffer out = ByteBuffer.wrap(dest, destOffset, dest.length - destOffset);
            return encode(in, out);
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    /**
     * Encodes source byte buffer.
     * The returned buffer's position will be set to 0 and limit is length of result bytes.
     *
     * @param source source byte buffer
     */
    default ByteBuffer encode(ByteBuffer source) {
        try {
            byte[] src = FsIO.getBytes(source);
            return ByteBuffer.wrap(encode(src));
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    /**
     * Encodes source byte buffer into dest byte buffer, return number of bytes written.
     *
     * @param source source byte buffer
     * @param dest   dest byte buffer
     */
    default int encode(ByteBuffer source, ByteBuffer dest) {
        try {
            OutputStream out = FsIO.toOutputStream(dest);
            int result = (int) encode(FsIO.toInputStream(source), out);
            out.flush();
            return result;
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    /**
     * Encodes source stream into dest stream, return number of bytes written.
     *
     * @param source source stream
     * @param dest   dest stream
     */
    long encode(InputStream source, OutputStream dest);

    /**
     * Encodes source array and build encoding result to string with {@link StandardCharsets#ISO_8859_1}.
     *
     * @param source source array
     */
    default String encodeToString(byte[] source) {
        return new String(encode(source), StandardCharsets.ISO_8859_1);
    }

    /**
     * Resolves source string with {@link FsString#CHARSET},
     * and build encoding result to string with {@link StandardCharsets#ISO_8859_1}.
     *
     * @param source source string
     */
    default String encodeToString(String source) {
        return encodeToString(source.getBytes(FsString.CHARSET));
    }

    /**
     * Returns block size for encoding.
     */
    int encodeBlockSize();

    /**
     * Decodes source array.
     *
     * @param source source array
     */
    default byte[] decode(byte[] source) {
        return decode(source, 0, source.length);
    }

    /**
     * Decodes source array of specified length from offset index.
     *
     * @param source source array
     * @param offset offset index
     * @param length specified length
     */
    byte[] decode(byte[] source, int offset, int length);

    /**
     * Decodes source array into dest array, return number of bytes written.
     *
     * @param source source array
     * @param dest   dest array
     */
    default int decode(byte[] source, byte[] dest) {
        return decode(source, 0, dest, 0, source.length);
    }

    /**
     * Decodes source array of specified length from source offset index,
     * into dest array from dest offset index, return number of bytes written.
     *
     * @param source       source array
     * @param sourceOffset source offset index
     * @param dest         dest array
     * @param destOffset   dest offset index
     * @param length       specified length
     */
    default int decode(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        try {
            ByteBuffer in = ByteBuffer.wrap(source, sourceOffset, length);
            ByteBuffer out = ByteBuffer.wrap(dest, destOffset, dest.length - destOffset);
            return decode(in, out);
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    /**
     * Decodes source byte buffer.
     * The returned buffer's position will be set to 0 and limit is length of result bytes.
     *
     * @param source source byte buffer
     */
    default ByteBuffer decode(ByteBuffer source) {
        try {
            byte[] src = FsIO.getBytes(source);
            return ByteBuffer.wrap(decode(src));
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    /**
     * Decodes source byte buffer into dest byte buffer, return number of bytes written.
     *
     * @param source source byte buffer
     * @param dest   dest byte buffer
     */
    default int decode(ByteBuffer source, ByteBuffer dest) {
        try {
            OutputStream out = FsIO.toOutputStream(dest);
            int result = (int) decode(FsIO.toInputStream(source), out);
            out.flush();
            return result;
        } catch (FsCodecException e) {
            throw e;
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    /**
     * Decodes source stream into dest stream, return number of bytes written.
     *
     * @param source source stream
     * @param dest   dest stream
     */
    long decode(InputStream source, OutputStream dest);

    /**
     * Decodes source array and build encoding result to string with {@link FsString#CHARSET}.
     *
     * @param source source array
     */
    default String decodeToString(byte[] source) {
        return new String(decode(source), FsString.CHARSET);
    }

    /**
     * Resolves source string with {@link StandardCharsets#ISO_8859_1},
     * and build encoding result to string with {@link FsString#CHARSET}.
     *
     * @param source source string
     */
    default String decode(String source) {
        return new String(decode(source.getBytes(StandardCharsets.ISO_8859_1)), FsString.CHARSET);
    }

    /**
     * Returns block size for decoding.
     */
    int decodeBlockSize();
}
