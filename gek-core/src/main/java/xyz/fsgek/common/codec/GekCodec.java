package xyz.fsgek.common.codec;

import javax.crypto.Cipher;

public class GekCodec {

    public static CipherDataProcessor cipher(Cipher cipher) {
        return new CipherDataProcessor(cipher);
    }
}
