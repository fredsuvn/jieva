package xyz.srclab.common.security;

import xyz.srclab.annotations.Nullable;

import javax.crypto.Mac;
import java.security.MessageDigest;
import java.security.Provider;

/**
 * Denotes cipher for message digestion, maybe has a back {@link MessageDigest}.
 * <p>
 * It is recommended to use method-chaining:
 * <pre>
 *     byte[] digestBytes = digest.prepare(bytes).bufferSize(size).digest().doFinal();
 * </pre>
 *
 * @author fredsuvn
 * @see Mac
 */
public interface FsDigest extends Prepareable {

    /**
     * Returns new instance of specified algorithm.
     * Returned instance has a back thread-local mac which supplied with
     * {@link MessageDigest#getInstance(String)}.
     *
     * @param algorithm specified algorithm
     */
    static FsDigest getDigest(String algorithm) {
        return new DigestImpl(() -> {
            try {
                return MessageDigest.getInstance(algorithm);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local mac which supplied with
     * {@link MessageDigest#getInstance(String, String)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     */
    static FsDigest getDigest(String algorithm, String provider) {
        return new DigestImpl(() -> {
            try {
                return MessageDigest.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns new instance of specified algorithm and provider.
     * Returned instance has a back thread-local mac which supplied with
     * {@link MessageDigest#getInstance(String, Provider)}.
     *
     * @param algorithm specified algorithm
     * @param provider  specified provider
     */
    static FsDigest getDigest(String algorithm, Provider provider) {
        return new DigestImpl(() -> {
            try {
                return MessageDigest.getInstance(algorithm, provider);
            } catch (Exception e) {
                throw new FsSecurityException(e);
            }
        });
    }

    /**
     * Returns back {@link MessageDigest} if it has, or null if it doesn't have one.
     * The back {@link MessageDigest} maybe thread-local, that is, returned value may be not only one instance.
     */
    @Nullable
    MessageDigest getDigest();

    /**
     * Returns digest length.
     */
    int getDigestLength();
}
