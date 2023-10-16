package xyz.fsgik.common.security;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.annotations.ThreadSafe;
import xyz.fsgik.common.base.FsCheck;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

/**
 * Denotes key-pair generator, maybe has a back {@link KeyPairGenerator}.
 *
 * @author fredsuvn
 * @see KeyPairGenerator
 */
@ThreadSafe
public interface FsKeyPairGen extends SecurityAlgorithm {

    /**
     * Returns new instance of specified algorithm.
     * Returned instance has a back thread-local {@link KeyPairGenerator}
     * which supplied with {@link KeyPairGenerator#getInstance(String)}.
     *
     * @param algorithm specified algorithm
     * @return new instance of specified algorithm
     */
    static FsKeyPairGen getInstance(String algorithm) {
        return new KeyPairGenImpl(algorithm, () -> {
            try {
                return KeyPairGenerator.getInstance(algorithm);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local {@link KeyPairGenerator}
     * which supplied with {@link KeyPairGenerator#getInstance(String, String)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return new instance of specified algorithm
     */
    static FsKeyPairGen getInstance(String algorithm, String provider) {
        return new KeyPairGenImpl(algorithm, () -> {
            try {
                return KeyPairGenerator.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local {@link KeyPairGenerator}
     * which supplied with {@link KeyPairGenerator#getInstance(String, Provider)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     * @return new instance of specified algorithm
     */
    static FsKeyPairGen getInstance(String algorithm, Provider provider) {
        return new KeyPairGenImpl(algorithm, () -> {
            try {
                return KeyPairGenerator.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Generates key of x509-format from given array.
     *
     * @param bytes given array
     * @return key of x509-format
     */
    static X509EncodedKeySpec generateX509(byte[] bytes) {
        try {
            return new X509EncodedKeySpec(bytes);
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Generates key of x509-format from given array of specified length start from given offset.
     *
     * @param bytes  given array
     * @param offset start offset
     * @param length specified length
     * @return key of x509-format
     */
    static X509EncodedKeySpec generateX509(byte[] bytes, int offset, int length) {
        try {
            FsCheck.checkRangeInBounds(offset, offset + length, 0, bytes.length);
            return generateX509(Arrays.copyOfRange(bytes, offset, offset + length));
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Generates key of PKCS8-format from given array.
     *
     * @param bytes given array
     * @return key of PKCS8-format
     */
    static PKCS8EncodedKeySpec generatePkcs8(byte[] bytes) {
        try {
            return new PKCS8EncodedKeySpec(bytes);
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Generates key of x509-format from given array of specified length start from given offset.
     *
     * @param bytes  given array
     * @param offset start offset
     * @param length specified length
     * @return key of PKCS8-format
     */
    static PKCS8EncodedKeySpec generatePkcs8(byte[] bytes, int offset, int length) {
        try {
            FsCheck.checkRangeInBounds(offset, offset + length, 0, bytes.length);
            return generatePkcs8(Arrays.copyOfRange(bytes, offset, offset + length));
        } catch (FsSecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new FsSecurityException(e);
        }
    }

    /**
     * Returns back {@link KeyPairGenerator} if it has, or null if it doesn't have one.
     * The back {@link KeyPairGenerator} maybe thread-local, that is, returned value may be not only one instance.
     *
     * @return back {@link KeyPairGenerator} if it has, or null if it doesn't have one
     */
    @Nullable
    KeyPairGenerator getKeyPairGenerator();

    /**
     * Generates key pair.
     *
     * @return the key pair
     */
    KeyPair generateKeyPair();

    /**
     * Generates key pair with specified size.
     *
     * @param size specified key size
     * @return the key pair
     */
    KeyPair generateKeyPair(int size);

    /**
     * Generates key pair with specified key size and secure random.
     *
     * @param size         specified key size
     * @param secureRandom secure random
     * @return the key pair
     */
    KeyPair generateKeyPair(int size, SecureRandom secureRandom);

    /**
     * Generates key pair with algorithm parameter spec.
     *
     * @param spec algorithm parameter spec
     * @return the key pair
     */
    KeyPair generateKeyPair(AlgorithmParameterSpec spec);

    /**
     * Generates key pair with algorithm parameter spec and secure random.
     *
     * @param spec         algorithm parameter spec
     * @param secureRandom secure random
     * @return the key pair
     */
    KeyPair generateKeyPair(AlgorithmParameterSpec spec, SecureRandom secureRandom);
}
