package xyz.fsgek.common.encode;

import xyz.fsgek.annotations.ThreadSafe;
import xyz.fsgek.common.io.GekBuffer;
import xyz.fsgek.common.io.GekIO;
import xyz.fsgek.common.base.GekChars;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Encoder interface for encoding and decoding (such as Base64 and Hex).
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface GekEncoder {

    /**
     * Returns base64 encoder.
     *
     * @return base64 encoder
     */
    static GekEncoder base64() {
        return Encoders.BASE64;
    }

    /**
     * Returns base64 encoder (without padding).
     *
     * @return base64 encoder (without padding)
     */
    static GekEncoder base64NoPadding() {
        return Encoders.BASE64_NO_PADDING;
    }

    /**
     * Returns base64 encoder (URL format).
     *
     * @return base64 encoder (URL format)
     */
    static GekEncoder base64Url() {
        return Encoders.BASE64_URL;
    }

    /**
     * Returns base64 encoder (URL format without padding).
     *
     * @return base64 encoder (URL format without padding)
     */
    static GekEncoder base64UrlNoPadding() {
        return Encoders.BASE64_URL_NO_PADDING;
    }

    /**
     * Returns base64 encoder (MIME format).
     *
     * @return base64 encoder (MIME format)
     */
    static GekEncoder base64Mime() {
        return Encoders.BASE64_MIME;
    }

    /**
     * Returns base64 encoder (MIME format without padding).
     *
     * @return base64 encoder (MIME format without padding)
     */
    static GekEncoder base64MimeNoPadding() {
        return Encoders.BASE64_MIME_NO_PADDING;
    }

    /**
     * Returns hex encoder.
     *
     * @return hex encoder
     */
    static GekEncoder hex() {
        return Encoders.HEX;
    }

    /**
     * Encodes source array.
     *
     * @param source source array
     * @return encoded bytes
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
     * @return encoded bytes
     */
    byte[] encode(byte[] source, int offset, int length);

    /**
     * Encodes source array into dest array, return number of bytes written.
     *
     * @param source source array
     * @param dest   dest array
     * @return number of bytes written
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
     * @return number of bytes written
     */
    default int encode(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        try {
            InputStream in = GekIO.toInputStream(source, sourceOffset, length);
            OutputStream out = GekIO.toOutputStream(dest, destOffset, dest.length - destOffset);
            return (int) encode(in, out);
        } catch (GekEncodeException e) {
            throw e;
        } catch (Exception e) {
            throw new GekEncodeException(e);
        }
    }

    /**
     * Encodes source byte buffer.
     * The returned buffer's position will be set to 0 and limit is length of result bytes.
     *
     * @param source source byte buffer
     * @return encoded buffer
     */
    default ByteBuffer encode(ByteBuffer source) {
        try {
            byte[] src = GekBuffer.getBytes(source);
            return ByteBuffer.wrap(encode(src));
        } catch (GekEncodeException e) {
            throw e;
        } catch (Exception e) {
            throw new GekEncodeException(e);
        }
    }

    /**
     * Encodes source byte buffer into dest byte buffer, return number of bytes written.
     *
     * @param source source byte buffer
     * @param dest   dest byte buffer
     * @return number of bytes written
     */
    default int encode(ByteBuffer source, ByteBuffer dest) {
        try {
            OutputStream out = GekIO.toOutputStream(dest);
            int result = (int) encode(GekIO.toInputStream(source), out);
            out.flush();
            return result;
        } catch (GekEncodeException e) {
            throw e;
        } catch (Exception e) {
            throw new GekEncodeException(e);
        }
    }

    /**
     * Encodes source stream into dest stream, return number of bytes written.
     *
     * @param source source stream
     * @param dest   dest stream
     * @return number of bytes written
     */
    long encode(InputStream source, OutputStream dest);

    /**
     * Encodes source array and build encoding result to string with {@link StandardCharsets#ISO_8859_1}.
     *
     * @param source source array
     * @return encoded string
     */
    default String encodeToString(byte[] source) {
        return new String(encode(source), StandardCharsets.ISO_8859_1);
    }

    /**
     * Resolves source string with {@link GekChars#defaultCharset()},
     * and build encoding result to string with {@link StandardCharsets#ISO_8859_1}.
     *
     * @param source source string
     * @return encoded string
     */
    default String encodeToString(String source) {
        return encodeToString(source.getBytes(GekChars.defaultCharset()));
    }

    /**
     * Returns block size for encoding.
     *
     * @return block size for encoding
     */
    int encodeBlockSize();

    /**
     * Decodes source array.
     *
     * @param source source array
     * @return decoded bytes
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
     * @return decoded bytes
     */
    byte[] decode(byte[] source, int offset, int length);

    /**
     * Decodes source array into dest array, return number of bytes written.
     *
     * @param source source array
     * @param dest   dest array
     * @return number of bytes written
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
     * @return number of bytes written
     */
    default int decode(byte[] source, int sourceOffset, byte[] dest, int destOffset, int length) {
        try {
            InputStream in = GekIO.toInputStream(source, sourceOffset, length);
            OutputStream out = GekIO.toOutputStream(dest, destOffset, dest.length - destOffset);
            return (int) decode(in, out);
        } catch (GekEncodeException e) {
            throw e;
        } catch (Exception e) {
            throw new GekEncodeException(e);
        }
    }

    /**
     * Decodes source byte buffer.
     * The returned buffer's position will be set to 0 and limit is length of result bytes.
     *
     * @param source source byte buffer
     * @return decoded buffer
     */
    default ByteBuffer decode(ByteBuffer source) {
        try {
            byte[] src = GekBuffer.getBytes(source);
            return ByteBuffer.wrap(decode(src));
        } catch (GekEncodeException e) {
            throw e;
        } catch (Exception e) {
            throw new GekEncodeException(e);
        }
    }

    /**
     * Decodes source byte buffer into dest byte buffer, return number of bytes written.
     *
     * @param source source byte buffer
     * @param dest   dest byte buffer
     * @return number of bytes written
     */
    default int decode(ByteBuffer source, ByteBuffer dest) {
        try {
            OutputStream out = GekIO.toOutputStream(dest);
            int result = (int) decode(GekIO.toInputStream(source), out);
            out.flush();
            return result;
        } catch (GekEncodeException e) {
            throw e;
        } catch (Exception e) {
            throw new GekEncodeException(e);
        }
    }

    /**
     * Decodes source stream into dest stream, return number of bytes written.
     *
     * @param source source stream
     * @param dest   dest stream
     * @return number of bytes written
     */
    long decode(InputStream source, OutputStream dest);

    /**
     * Decodes source array and build encoding result to string with {@link GekChars#defaultCharset()}.
     *
     * @param source source array
     * @return decoded string
     */
    default String decodeToString(byte[] source) {
        return new String(decode(source), GekChars.defaultCharset());
    }

    /**
     * Resolves source string with {@link StandardCharsets#ISO_8859_1},
     * and build encoding result to string with {@link GekChars#defaultCharset()}.
     *
     * @param source source string
     * @return decoded string
     */
    default String decode(String source) {
        return new String(decode(source.getBytes(StandardCharsets.ISO_8859_1)), GekChars.defaultCharset());
    }

    /**
     * Returns block size for decoding.
     *
     * @return block size for decoding
     */
    int decodeBlockSize();
}
