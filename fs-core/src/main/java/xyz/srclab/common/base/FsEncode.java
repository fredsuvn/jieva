package xyz.srclab.common.base;

import xyz.srclab.common.io.FsIO;

import java.nio.ByteBuffer;
import java.util.Base64;

/**
 * Encoding (base64 and hex) utilities.
 *
 * @author fredsuvn
 */
public class FsEncode {

    /**
     * Encodes given bytes in base64.
     *
     * @param bytes given bytes
     */
    public static byte[] base64(byte[] bytes) {
        return Base64.getEncoder().encode(bytes);
    }

    /**
     * Encodes given string in base64, the string will be to bytes with {@link FsString#CHARSET}.
     *
     * @param string given string
     */
    public static byte[] base64(String string) {
        return Base64.getEncoder().encode(string.getBytes(FsString.CHARSET));
    }

    /**
     * Encodes given bytes in base64.
     *
     * @param bytes given bytes
     */
    public static ByteBuffer base64(ByteBuffer bytes) {
        return Base64.getEncoder().encode(bytes);
    }

    /**
     * Encodes given bytes to String in base64.
     *
     * @param bytes given bytes
     */
    public static String base64ToString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Encodes given string to String in base64, the string will be to bytes with {@link FsString#CHARSET}.
     *
     * @param string given string
     */
    public static String base64ToString(String string) {
        return Base64.getEncoder().encodeToString(string.getBytes(FsString.CHARSET));
    }

    /**
     * Encodes given bytes to String in base64.
     *
     * @param bytes given bytes
     */
    public static String base64ToString(ByteBuffer bytes) {
        return Base64.getEncoder().encodeToString(FsIO.toByteArray(bytes));
    }

    /**
     * Decodes given base64 bytes.
     *
     * @param base64 given base64 bytes
     */
    public static byte[] deBase64(byte[] base64) {
        return Base64.getDecoder().decode(base64);
    }

    /**
     * Decodes given base64 bytes.
     *
     * @param base64 given base64 bytes
     */
    public static ByteBuffer deBase64(ByteBuffer base64) {
        return Base64.getDecoder().decode(base64);
    }

    /**
     * Decodes given base64 string.
     *
     * @param base64 given base64 string
     */
    public static byte[] deBase64(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    /**
     * Decodes given base64 string to String with {@link FsString#CHARSET}.
     *
     * @param base64 given base64 string
     */
    public static String deBase64ToString(String base64) {
        return new String(Base64.getDecoder().decode(base64), FsString.CHARSET);
    }
}
