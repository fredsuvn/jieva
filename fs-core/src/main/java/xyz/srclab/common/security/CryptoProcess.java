package xyz.srclab.common.security;

import xyz.srclab.common.encode.FsEncoder;

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
 * @see FsCipher
 * @see FsMac
 */
public interface CryptoProcess {

    /**
     * Sets key for crypto.
     *
     * @param key key for crypto
     */
    CryptoProcess key(Key key);

    /**
     * Sets specification of cryptographic parameters and return itself.
     *
     * @param parameterSpec specification of cryptographic parameters
     */
    CryptoProcess algorithmParameterSpec(AlgorithmParameterSpec parameterSpec);

    /**
     * Sets cryptographic parameters and return itself.
     *
     * @param parameters cryptographic parameters
     */
    CryptoProcess algorithmParameters(AlgorithmParameters parameters);

    /**
     * Sets secure random and return itself.
     *
     * @param secureRandom secure random
     */
    CryptoProcess secureRandom(SecureRandom secureRandom);

    /**
     * Sets certificate info and return itself.
     *
     * @param certificate certificate info
     */
    CryptoProcess certificate(Certificate certificate);

    /**
     * Sets key size and return itself.
     *
     * @param keySize key size
     */
    CryptoProcess keySize(int keySize);

    /**
     * Sets block size and return itself.
     *
     * @param blockSize block size
     */
    CryptoProcess blockSize(int blockSize);

    /**
     * Sets buffer size and return itself.
     *
     * @param bufferSize buffer size
     */
    CryptoProcess bufferSize(int bufferSize);

    /**
     * Sets encryption mode.
     */
    default CryptoProcess encrypt() {
        throw new FsSecurityException(new UnsupportedOperationException("Encryption"));
    }

    /**
     * Sets decryption mode.
     */
    default CryptoProcess decrypt() {
        throw new FsSecurityException(new UnsupportedOperationException("Decryption"));
    }

    /**
     * Sets MAC mode (for {@link Mac}).
     */
    default CryptoProcess mac() {
        throw new FsSecurityException(new UnsupportedOperationException("MAC"));
    }

    /**
     * Sets digestion mode (for {@link MessageDigest}).
     */
    default CryptoProcess digest() {
        throw new FsSecurityException(new UnsupportedOperationException("Digestion"));
    }

    /**
     * Sets digestion mode (for {@link Signature}).
     */
    default CryptoProcess sign() {
        throw new FsSecurityException(new UnsupportedOperationException("Sign"));
    }

    /**
     * Does final computation and returns result.
     */
    byte[] doFinal();

    /**
     * Does final computation and writes result into dest array, return written bytes count.
     *
     * @param dest dest array
     */
    default int doFinal(byte[] dest) {
        return doFinal(dest, 0);
    }

    /**
     * Does final computation and writes result into dest array from start offset, return written bytes count.
     *
     * @param dest   dest array
     * @param offset start offset
     */
    int doFinal(byte[] dest, int offset);

    /**
     * Does final computation and writes result into dest buffer, return written bytes count.
     *
     * @param dest dest buffer
     */
    int doFinal(ByteBuffer dest);

    /**
     * Does final computation and writes result into dest stream, return written bytes count.
     *
     * @param dest dest stream
     */
    long doFinal(OutputStream dest);

    /**
     * Does final computation and returns result as input stream.
     * <p>
     * The returned stream is lazy, it can be affected by any changes of the process before the stream is fully read.
     */
    InputStream doFinalStream();

    /**
     * Does final computation and returns base64 string of result.
     */
    default String doFinalBase64() {
        return FsEncoder.base64().encodeToString(doFinal());
    }

    /**
     * Verifies sign for {@link FsSign}.
     *
     * @param sign sign to be verified
     */
    default boolean verify(byte[] sign) {
        return verify(sign, 0, sign.length);
    }

    /**
     * Verifies sign for {@link FsSign}.
     *
     * @param sign   sign to be verified
     * @param offset start offset of sign
     * @param length length of sign
     */
    default boolean verify(byte[] sign, int offset, int length) {
        throw new FsSecurityException(new UnsupportedOperationException("Verification"));
    }
}
