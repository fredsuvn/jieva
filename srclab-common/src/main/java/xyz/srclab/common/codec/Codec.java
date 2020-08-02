package xyz.srclab.common.codec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import xyz.srclab.common.base.Bytes;

import java.nio.ByteBuffer;

public interface Codec {

    static char[] encodeHex(byte[] data) {
        return Hex.encodeHex(data);
    }

    static char[] encodeHex(char[] data) {
        return Hex.encodeHex(Bytes.toBytes(data));
    }

    static char[] encodeHex(CharSequence data) {
        return Hex.encodeHex(Bytes.toBytes(data));
    }

    static char[] encodeHex(ByteBuffer data) {
        return Hex.encodeHex(data);
    }

    static String encodeHexString(byte[] data) {
        return new String(encodeHex(data));
    }

    static String encodeHexString(char[] data) {
        return new String(encodeHex(data));
    }

    static String encodeHexString(String data) {
        return new String(encodeHex(data));
    }

    static String encodeHexString(ByteBuffer data) {
        return new String(encodeHex(data));
    }

    static byte[] decodeHex(char[] hex) {
        try {
            return Hex.decodeHex(hex);
        } catch (DecoderException e) {
            throw new IllegalArgumentException(e);
        }
    }

    static byte[] decodeHex(String hex) {
        try {
            return Hex.decodeHex(hex);
        } catch (DecoderException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
