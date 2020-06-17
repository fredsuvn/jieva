package xyz.srclab.common.codec;

import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;

/**
 * @author sunqian
 */
public class CodecKit {

    public static char[] encodeHex(byte[] data) {
        return Hex.encodeHex(data);
    }

    public static char[] encodeHex(ByteBuffer data) {
        return Hex.encodeHex(data);
    }
}
