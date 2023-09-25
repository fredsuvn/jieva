package xyz.srclab.common.encode;

import xyz.srclab.annotations.ThreadSafe;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.io.FsIO;

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
public interface FsEncoder {

    /**
     * Returns base64 encoder.
     */
    static FsEncoder base64() {
        return Encoders.BASE64;
    }

    /**
     * Returns base64 encoder (without padding).
     */
    static FsEncoder base64NoPadding() {
        return Encoders.BASE64_NO_PADDING;
    }

    /**
     * Returns base64 encoder (URL format).
     */
    static FsEncoder base64Url() {
        return Encoders.BASE64_URL;
    }

    /**
     * Returns base64 encoder (URL format without padding).
     */
    static FsEncoder base64UrlNoPadding() {
        return Encoders.BASE64_URL_NO_PADDING;
    }

    /**
     * Returns base64 encoder (MIME format).
     */
    static FsEncoder base64Mime() {
        return Encoders.BASE64_MIME;
    }

    /**
     * Returns base64 encoder (MIME format without padding).
     */
    static FsEncoder base64MimeNoPadding() {
        return Encoders.BASE64_MIME_NO_PADDING;
    }

    /**
     * Returns hex encoder.
     */
    static FsEncoder hex() {
        return Encoders.HEX;
    }

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
            InputStream in = FsIO.toInputStream(source, sourceOffset, length);
            OutputStream out = FsIO.toOutputStream(dest, destOffset, dest.length - destOffset);
            return (int) encode(in, out);
        } catch (FsEncodeException e) {
            throw e;
        } catch (Exception e) {
            throw new FsEncodeException(e);
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
        } catch (FsEncodeException e) {
            throw e;
        } catch (Exception e) {
            throw new FsEncodeException(e);
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
        } catch (FsEncodeException e) {
            throw e;
        } catch (Exception e) {
            throw new FsEncodeException(e);
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
            InputStream in = FsIO.toInputStream(source, sourceOffset, length);
            OutputStream out = FsIO.toOutputStream(dest, destOffset, dest.length - destOffset);
            return (int) decode(in, out);
        } catch (FsEncodeException e) {
            throw e;
        } catch (Exception e) {
            throw new FsEncodeException(e);
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
        } catch (FsEncodeException e) {
            throw e;
        } catch (Exception e) {
            throw new FsEncodeException(e);
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
        } catch (FsEncodeException e) {
            throw e;
        } catch (Exception e) {
            throw new FsEncodeException(e);
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
