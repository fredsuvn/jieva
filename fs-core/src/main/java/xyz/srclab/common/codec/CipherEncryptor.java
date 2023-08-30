package xyz.srclab.common.codec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.*;
import java.security.spec.KeySpec;
import java.util.function.Supplier;

/**
 * Implementation of {@link FsEncryptor} with {@link Cipher}.
 *
 * @author fredsuvn
 */
public class CipherEncryptor implements FsEncryptor {

    private final ThreadLocal<Cipher> cipher;
    private final Supplier<? extends Cipher> supplier;
    private final String algorithmName;

    /**
     * Constructs with cipher supplier.
     * The supplier needs to ensure that each invocation of {@link Supplier#get()} returns a new instance.
     *
     * @param supplier cipher supplier
     */
    public CipherEncryptor(String algorithmName, SecureRandom secureRandom, Supplier<? extends Cipher> supplier) {
        this.algorithmName = algorithmName;
        this.supplier = supplier;
        this.cipher = ThreadLocal.withInitial(supplier);
    }

    @Override
    public String algorithmName() {
        return algorithmName;
    }

    @Override
    public Key toKey(byte[] keyBytes, int offset, int length) {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("");
            // keyGenerator.
            return null;
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


}
