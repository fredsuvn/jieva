package xyz.srclab.common.codec;

import xyz.srclab.common.base.FsString;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Encoder and decoder interface, can get from {@link FsCodec}.
 *
 * @author fredsuvn
 * @see FsCodec
 */
public interface FsEncoder {

    /**
     * Returns encoder algorithm.
     */
    FsAlgorithm getAlgorithm();

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
     * Encodes source array of specified length from source offset index,
     * into dest array from dest offset index, return number of bytes written.
     *
     * @param source       source array
     * @param sourceOffset source offset index
     * @param dest         dest array
     * @param destOffset   dest offset index
     * @param length       specified length
     */
    int encode(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length);

    /**
     * Encodes source byte buffer.
     * The returned buffer's position will be set to 0 and limit is length of result bytes.
     *
     * @param source source byte buffer
     */
    ByteBuffer encode(ByteBuffer source);

    /**
     * Encodes source byte buffer into dest byte buffer, return number of bytes written.
     *
     * @param source source byte buffer
     * @param dest   dest byte buffer
     */
    int encode(ByteBuffer source, ByteBuffer dest);

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
    int getEncodeBlockSize();

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
     * Decodes source array of specified length from source offset index,
     * into dest array from dest offset index, return number of bytes written.
     *
     * @param source       source array
     * @param sourceOffset source offset index
     * @param dest         dest array
     * @param destOffset   dest offset index
     * @param length       specified length
     */
    int decode(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length);

    /**
     * Decodes source byte buffer.
     * The returned buffer's position will be set to 0 and limit is length of result bytes.
     *
     * @param source source byte buffer
     */
    ByteBuffer decode(ByteBuffer source);

    /**
     * Decodes source byte buffer into dest byte buffer, return number of bytes written.
     *
     * @param source source byte buffer
     * @param dest   dest byte buffer
     */
    int decode(ByteBuffer source, ByteBuffer dest);

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
    int getDecodeBlockSize();
}
