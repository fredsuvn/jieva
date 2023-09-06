package xyz.srclab.common.security;

import xyz.srclab.common.data.FsData;

import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.SecureRandom;
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
     * Final encryption operation.
     * <p>
     * It should be noted that the actual computation may occur when invoking methods on the returned value,
     * and it's possible that the computation is performed anew with each invocation.
     *
     * @param key the key.
     */
    default FsData encrypt(Key key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Final decryption operation.
     * <p>
     * It should be noted that the actual computation may occur when invoking methods on the returned value,
     * and it's possible that the computation is performed anew with each invocation.
     *
     * @param key the key.
     */
    default FsData decrypt(Key key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Final MAC generation operation.
     * <p>
     * It should be noted that the actual computation may occur when invoking methods on the returned value,
     * and it's possible that the computation is performed anew with each invocation.
     *
     * @param key the key.
     */
    default FsData generateMac(Key key) {
        throw new UnsupportedOperationException();
    }
}
