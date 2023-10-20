package xyz.fsgek.common.security;

import xyz.fsgek.common.encode.GekEncoder;

import javax.crypto.Mac;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Provides method chaining operation for crypto operation.
 *
 * @author fredsuvn
 * @see GekCipher
 * @see GekMac
 */
public interface CryptoProcess {

    /**
     * Sets key for crypto.
     *
     * @param key key for crypto
     * @return this object
     */
    CryptoProcess key(Key key);

    /**
     * Sets specification of cryptographic parameters and return itself.
     *
     * @param parameterSpec specification of cryptographic parameters
     * @return this object
     */
    CryptoProcess algorithmParameterSpec(AlgorithmParameterSpec parameterSpec);

    /**
     * Sets cryptographic parameters and return itself.
     *
     * @param parameters cryptographic parameters
     * @return this object
     */
    CryptoProcess algorithmParameters(AlgorithmParameters parameters);

    /**
     * Sets secure random and return itself.
     *
     * @param secureRandom secure random
     * @return this object
     */
    CryptoProcess secureRandom(SecureRandom secureRandom);

    /**
     * Sets certificate info and return itself.
     *
     * @param certificate certificate info
     * @return this object
     */
    CryptoProcess certificate(Certificate certificate);

    /**
     * Sets key size and return itself.
     *
     * @param keySize key size
     * @return this object
     */
    CryptoProcess keySize(int keySize);

    /**
     * Sets block size and return itself.
     *
     * @param blockSize block size
     * @return this object
     */
    CryptoProcess blockSize(int blockSize);

    /**
     * Sets buffer size and return itself.
     *
     * @param bufferSize buffer size
     * @return this object
     */
    CryptoProcess bufferSize(int bufferSize);

    /**
     * Sets encryption mode.
     *
     * @return this object
     */
    default CryptoProcess encrypt() {
        throw new GekSecurityException(new UnsupportedOperationException("Encryption"));
    }

    /**
     * Sets decryption mode.
     *
     * @return this object
     */
    default CryptoProcess decrypt() {
        throw new GekSecurityException(new UnsupportedOperationException("Decryption"));
    }

    /**
     * Sets MAC mode (for {@link Mac}).
     *
     * @return this object
     */
    default CryptoProcess mac() {
        throw new GekSecurityException(new UnsupportedOperationException("MAC"));
    }

    /**
     * Sets digestion mode (for {@link MessageDigest}).
     *
     * @return this object
     */
    default CryptoProcess digest() {
        throw new GekSecurityException(new UnsupportedOperationException("Digestion"));
    }

    /**
     * Sets digestion mode (for {@link Signature}).
     *
     * @return this object
     */
    default CryptoProcess sign() {
        throw new GekSecurityException(new UnsupportedOperationException("Sign"));
    }

    /**
     * Does final computation and returns result.
     *
     * @return result of final computation
     */
    byte[] doFinal();

    /**
     * Does final computation and writes result into dest array, return written bytes count.
     *
     * @param dest dest array
     * @return result of final computation
     */
    default int doFinal(byte[] dest) {
        return doFinal(dest, 0);
    }

    /**
     * Does final computation and writes result into dest array from start offset, return written bytes count.
     *
     * @param dest   dest array
     * @param offset start offset
     * @return written bytes count
     */
    int doFinal(byte[] dest, int offset);

    /**
     * Does final computation and writes result into dest buffer, return written bytes count.
     *
     * @param dest dest buffer
     * @return written bytes count
     */
    int doFinal(ByteBuffer dest);

    /**
     * Does final computation and writes result into dest stream, return written bytes count.
     *
     * @param dest dest stream
     * @return written bytes count
     */
    long doFinal(OutputStream dest);

    /**
     * Does final computation and returns result as input stream.
     * <p>
     * The returned stream is lazy, it can be affected by any changes of the process before the stream is fully read.
     *
     * @return result of final computation
     */
    InputStream doFinalStream();

    /**
     * Does final computation and returns base64 string of result.
     *
     * @return result of final computation
     */
    default String doFinalBase64() {
        return GekEncoder.base64().encodeToString(doFinal());
    }

    /**
     * Verifies sign for {@link GekSign}.
     *
     * @param sign sign to be verified
     * @return result of verifying
     */
    default boolean verify(byte[] sign) {
        return verify(sign, 0, sign.length);
    }

    /**
     * Verifies sign for {@link GekSign}.
     *
     * @param sign   sign to be verified
     * @param offset start offset of sign
     * @param length length of sign
     * @return result of verifying
     */
    default boolean verify(byte[] sign, int offset, int length) {
        throw new GekSecurityException(new UnsupportedOperationException("Verification"));
    }
}
