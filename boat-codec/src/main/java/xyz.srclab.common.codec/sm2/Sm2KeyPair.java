package xyz.srclab.common.codec.sm2;

import xyz.srclab.common.codec.AbstractCodecKeyPair;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;

/**
 * SM2 key pair.
 *
 * @author sunqian
 */
public class Sm2KeyPair extends AbstractCodecKeyPair<ECPoint, BigInteger> {

    public Sm2KeyPair(ECPoint publicKey, BigInteger privateKey) {
        super(publicKey, privateKey);
    }

    @Override
    protected byte[] publicKeyToBytes(ECPoint publicKey) {
        return publicKey.getEncoded(false);
    }

    @Override
    protected byte[] privateKeyToBytes(BigInteger privateKey) {
        return privateKey.toByteArray();
    }
}
