package xyz.srclab.common.codec;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Parameters for encrypting and decrypting.
 *
 * @author fredsuvn
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class FsEncryptParams {

    /**
     * Specification of cryptographic parameters.
     */
    private AlgorithmParameterSpec parameterSpec;
    /**
     * Cryptographic parameters.
     */
    private AlgorithmParameters parameters;
    /**
     * Secure random.
     */
    private SecureRandom secureRandom;
    /**
     * Key size.
     */
    private int keySize;

    /**
     * Constructs with key size.
     *
     * @param keySize key size
     */
    public FsEncryptParams(int keySize) {
        this.keySize = keySize;
    }
}
