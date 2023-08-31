package xyz.srclab.common.codec;

import xyz.srclab.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Implementation of {@link FsEncryptor} with {@link Cipher}.
 *
 * @author fredsuvn
 */
public class ProviderEncryptor implements FsEncryptor {

    private final ThreadLocal<Param> localParam;
    private final String algorithmName;

    /**
     * Constructs with specified provider.
     *
     * @param provider specified supplier
     */
    public ProviderEncryptor(String algorithmName, @Nullable Provider provider) {
        this.algorithmName = algorithmName;
        this.localParam = ThreadLocal.withInitial(() -> new Param(provider));
    }

    @Override
    public String algorithmName() {
        return algorithmName;
    }

    @Override
    public KeyPair generateKeyPair(FsEncryptParams params) {
        try {
            KeyPairGenerator generator = localParam.get().getKeyPairGenerator();
            initKeyPairGenerator(generator, params);
            return generator.generateKeyPair();
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public Key toKey(byte[] keyBytes, int offset, int length) {
        try {
            return new SecretKeySpec(keyBytes, offset, length, algorithmName);
        } catch (Exception e) {
            throw new FsCodecException(e);
        }
    }

    @Override
    public PublicKey toPublicKey(byte[] keyBytes, int offset, int length) {
        return null;
    }

    @Override
    public PrivateKey toPrivateKey(byte[] keyBytes, int offset, int length) {
        return null;
    }

    private void initKeyPairGenerator(
        KeyPairGenerator generator, FsEncryptParams params) throws InvalidAlgorithmParameterException {
        if (params.getSecureRandom() != null) {
            if (params.getParameterSpec() != null) {
                generator.initialize(params.getParameterSpec(), params.getSecureRandom());
            } else {
                generator.initialize(params.getKeySize(), params.getSecureRandom());
            }
        } else if (params.getParameterSpec() != null) {
            generator.initialize(params.getParameterSpec());
        } else {
            generator.initialize(params.getKeySize());
        }
    }


    private final class Param {

        @Nullable
        private final Provider provider;

        private KeyPairGenerator keyPairGenerator;
        private Cipher cipher;
        private KeyGenerator keyGenerator;

        private Param(Provider provider) {
            this.provider = provider;
        }

        KeyPairGenerator getKeyPairGenerator() throws NoSuchPaddingException, NoSuchAlgorithmException {
            if (keyPairGenerator != null) {
                return keyPairGenerator;
            }
            KeyPairGenerator newGenerator = provider == null ?
                KeyPairGenerator.getInstance(algorithmName)
                :
                KeyPairGenerator.getInstance(algorithmName, provider);
            keyPairGenerator = newGenerator;
            return newGenerator;
        }

        Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
            if (cipher != null) {
                return cipher;
            }
            Cipher newCipher = Cipher.getInstance(algorithmName, provider);
            cipher = newCipher;
            return newCipher;
        }

        KeyGenerator getKeyGenerator() throws NoSuchPaddingException, NoSuchAlgorithmException {
            if (keyGenerator != null) {
                return keyGenerator;
            }
            KeyGenerator newKeyGenerator = KeyGenerator.getInstance(algorithmName, provider);
            keyGenerator = newKeyGenerator;
            return newKeyGenerator;
        }
    }
}
