package xyz.srclab.common.codec.sm2;

import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import xyz.srclab.annotations.Nullable;
import xyz.srclab.common.codec.AsymmetricCipher;
import xyz.srclab.common.codec.CodecAlgorithm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * SM2 cipher.
 *
 * @author sunqian
 */
public class Sm2CipherJavaImpl implements AsymmetricCipher<ECPoint, BigInteger> {

    private static final int DIGEST_LENGTH = 32;

    private final Sm2Params sm2Params;
    private final SecureRandom random = new SecureRandom();
    private final ECDomainParameters ecc_bc_spec;
    private final ECCurve.Fp curve;
    private final ECPoint G;

    public Sm2CipherJavaImpl() {
        this(Sm2Params.defaultParams());
    }

    public Sm2CipherJavaImpl(Sm2Params sm2Params) {
        this.sm2Params = sm2Params;
        //int w = (int) Math.ceil(sm2Params.n().bitLength() * 1.0 / 2) - 1;
        //BigInteger _2w = new BigInteger("2").pow(w);
        curve = new ECCurve.Fp(sm2Params.p(), // q
            sm2Params.a(), // a
            sm2Params.b()); // b
        G = curve.createPoint(sm2Params.gx(), sm2Params.gy());
        ecc_bc_spec = new ECDomainParameters(curve, G, sm2Params.n());
    }

    @Override
    public Sm2KeyPair newKeyPair() {
        BigInteger d = random(sm2Params.n().subtract(new BigInteger("1")));
        Sm2KeyPair keyPair = new Sm2KeyPair(G.multiply(d).normalize(), d);
        if (isLegal(keyPair.publicKey())) {
            return keyPair;
        } else {
            throw new IllegalStateException("Failed to generate SM2 key pair.");
        }
    }

    @Override
    public Sm2KeyPair newKeyPair(int size) {
        return newKeyPair();
    }

    @Override
    public byte[] encrypt(ECPoint publicKey, byte[] data) {
        return doEncrypt(data, publicKey);
    }

    @Override
    public byte[] encrypt(byte[] publicKey, byte[] data) {
        return doEncrypt(data, curve.decodePoint(publicKey));
    }

    @Override
    public byte[] encryptWithAny(Object publicKey, byte[] data) {
        if (publicKey instanceof ECPoint) {
            return encrypt((ECPoint) publicKey, data);
        }
        if (publicKey instanceof byte[]) {
            return encrypt((byte[]) publicKey, data);
        }
        throw new IllegalArgumentException("Unsupported SM2 public key type: " + publicKey.getClass());
    }

    @Override
    public byte[] decrypt(BigInteger privateKey, byte[] encrypted) {
        return doDecrypt(encrypted, privateKey);
    }

    @Override
    public byte[] decrypt(byte[] privateKey, byte[] encrypted) {
        return doDecrypt(encrypted, new BigInteger(privateKey));
    }

    @Override
    public byte[] decryptWithAny(Object privateKey, byte[] encrypted) {
        if (privateKey instanceof BigInteger) {
            return decrypt((BigInteger) privateKey, encrypted);
        }
        if (privateKey instanceof byte[]) {
            return decrypt((byte[]) privateKey, encrypted);
        }
        throw new IllegalArgumentException("Unsupported SM2 private key type: " + privateKey.getClass());
    }

    @Override
    public String name() {
        return CodecAlgorithm.RSA_NAME;
    }

    public String getName() {
        return name();
    }

    /**
     * 公钥加密
     *
     * @param input     加密原文
     * @param publicKey 公钥
     * @return
     */
    private byte[] doEncrypt(byte[] input, ECPoint publicKey) {

        byte[] C1Buffer;
        ECPoint kpb;
        byte[] t;
        do {
            /* 1 产生随机数k，k属于[1, n-1] */
            BigInteger k = random(sm2Params.n());

            /* 2 计算椭圆曲线点C1 = [k]G = (x1, y1) */
            ECPoint C1 = G.multiply(k);
            C1Buffer = C1.getEncoded(false);

            /*
             * 3 计算椭圆曲线点 S = [h]Pb
             */
            BigInteger h = ecc_bc_spec.getH();
            if (h != null) {
                ECPoint S = publicKey.multiply(h);
                if (S.isInfinity())
                    throw new IllegalStateException();
            }

            /* 4 计算 [k]PB = (x2, y2) */
            kpb = publicKey.multiply(k).normalize();

            /* 5 计算 t = KDF(x2||y2, klen) */
            byte[] kpbBytes = kpb.getEncoded(false);
            t = KDF(kpbBytes, input.length);
            // DerivationFunction kdf = new KDF1BytesGenerator(new
            // ShortenedDigest(new SHA256Digest(), DIGEST_LENGTH));
            //
            // t = new byte[inputBuffer.length];
            // kdf.init(new ISO18033KDFParameters(kpbBytes));
            // kdf.generateBytes(t, 0, t.length);
        } while (isAllZero(t));

        /* 6 计算C2=M^t */
        byte[] C2 = new byte[input.length];
        for (int i = 0; i < input.length; i++) {
            C2[i] = (byte) (input[i] ^ t[i]);
        }

        /* 7 计算C3 = Hash(x2 || M || y2) */
        byte[] C3 = sm3hash(kpb.getXCoord().toBigInteger().toByteArray(), input,
            kpb.getYCoord().toBigInteger().toByteArray());

        /* 8 输出密文 C=C1 || C2 || C3 */

        byte[] encryptResult = new byte[C1Buffer.length + C2.length + C3.length];

        System.arraycopy(C1Buffer, 0, encryptResult, 0, C1Buffer.length);
        System.arraycopy(C2, 0, encryptResult, C1Buffer.length, C2.length);
        System.arraycopy(C3, 0, encryptResult, C1Buffer.length + C2.length, C3.length);

        return encryptResult;
    }

    /**
     * 私钥解密
     *
     * @param encryptData 密文数据字节数组
     * @param privateKey  解密私钥
     * @return
     */
    private byte[] doDecrypt(byte[] encryptData, BigInteger privateKey) {

        byte[] C1Byte = new byte[65];
        System.arraycopy(encryptData, 0, C1Byte, 0, C1Byte.length);

        ECPoint C1 = curve.decodePoint(C1Byte).normalize();

        /*
         * 计算椭圆曲线点 S = [h]C1 是否为无穷点
         */
        BigInteger h = ecc_bc_spec.getH();
        if (h != null) {
            ECPoint S = C1.multiply(h);
            if (S.isInfinity())
                throw new IllegalStateException();
        }
        /* 计算[dB]C1 = (x2, y2) */
        ECPoint dBC1 = C1.multiply(privateKey).normalize();

        /* 计算t = KDF(x2 || y2, klen) */
        byte[] dBC1Bytes = dBC1.getEncoded(false);
        int klen = encryptData.length - 65 - DIGEST_LENGTH;
        @Nullable byte[] t = KDF(dBC1Bytes, klen);
        // DerivationFunction kdf = new KDF1BytesGenerator(new
        // ShortenedDigest(new SHA256Digest(), DIGEST_LENGTH));
        // if (debug)
        // System.out.println("klen = " + klen);
        // kdf.init(new ISO18033KDFParameters(dBC1Bytes));
        // kdf.generateBytes(t, 0, t.length);

        if (t == null) {
            throw new IllegalStateException("Failed to decrypt by SM2 cipher.");
        }

        if (isAllZero(t)) {
            //System.err.println("all zero");
            throw new IllegalStateException("Failed to decrypt by SM2 cipher, all bytes are zero.");
        }

        /* 5 计算M'=C2^t */
        byte[] M = new byte[klen];
        for (int i = 0; i < M.length; i++) {
            M[i] = (byte) (encryptData[C1Byte.length + i] ^ t[i]);
        }

        /* 6 计算 u = Hash(x2 || M' || y2) 判断 u == C3是否成立 */
        byte[] C3 = new byte[DIGEST_LENGTH];

        System.arraycopy(encryptData, encryptData.length - DIGEST_LENGTH, C3, 0, DIGEST_LENGTH);
        byte[] u = sm3hash(dBC1.getXCoord().toBigInteger().toByteArray(), M,
            dBC1.getYCoord().toBigInteger().toByteArray());
        if (Arrays.equals(u, C3)) {
            return M;
        }
        throw new IllegalStateException("Failed to decrypt by SM2 cipher.");
    }

    private BigInteger random(BigInteger max) {
        BigInteger r = new BigInteger(256, random);
        while (r.compareTo(max) >= 0) {
            r = new BigInteger(128, random);
        }
        return r;
    }

    private boolean isAllZero(byte[] buffer) {
        for (byte value : buffer) {
            if (value != 0)
                return false;
        }
        return true;
    }

    private boolean isBetween(BigInteger param, BigInteger min, BigInteger max) {
        return param.compareTo(min) >= 0 && param.compareTo(max) < 0;
    }

    private boolean isLegal(ECPoint publicKey) {
        if (!publicKey.isInfinity()) {
            BigInteger x = publicKey.getXCoord().toBigInteger();
            BigInteger y = publicKey.getYCoord().toBigInteger();
            if (isBetween(x, new BigInteger("0"), sm2Params.p()) && isBetween(y, new BigInteger("0"), sm2Params.p())) {
                BigInteger xResult = x.pow(3).add(sm2Params.a().multiply(x)).add(sm2Params.b()).mod(sm2Params.p());
                BigInteger yResult = y.pow(2).mod(sm2Params.p());
                return yResult.equals(xResult) && publicKey.multiply(sm2Params.n()).isInfinity();
            }
        }
        return false;
    }

    private byte[] join(byte[]... params) {
        try {
            ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
            for (byte[] param : params) {
                bytesStream.write(param);
            }
            return bytesStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private byte[] sm3hash(byte[]... params) {
        try {
            return Sm3.hash(join(params));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 密钥派生函数
     *
     * @param Z
     * @param klen 生成klen字节数长度的密钥
     * @return
     */
    @Nullable
    private byte[] KDF(byte[] Z, int klen) {
        int ct = 1;
        int end = (int) Math.ceil(klen * 1.0 / 32);
        ByteArrayOutputStream bytesStream = new ByteArrayOutputStream();
        try {
            for (int i = 1; i < end; i++) {
                bytesStream.write(sm3hash(Z, Sm3.toByteArray(ct)));
                ct++;
            }
            byte[] last = sm3hash(Z, Sm3.toByteArray(ct));
            if (klen % 32 == 0) {
                bytesStream.write(last);
            } else
                bytesStream.write(last, 0, klen % 32);
            return bytesStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
