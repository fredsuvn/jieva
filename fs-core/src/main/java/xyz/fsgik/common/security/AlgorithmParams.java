package xyz.fsgik.common.security;

import lombok.*;

import java.security.AlgorithmParameters;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Parameters for algorithm, such as
 * {@link AlgorithmParameters}, {@link AlgorithmParameterSpec}. {@link SecureRandom} and so on.
 *
 * @author fredsuvn
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@EqualsAndHashCode
public class AlgorithmParams {

    /**
     * Specification of cryptographic parameters.
     */
    private AlgorithmParameterSpec algorithmParameterSpec;
    /**
     * Cryptographic parameters.
     */
    private AlgorithmParameters algorithmParameters;
    /**
     * Secure random.
     */
    private SecureRandom secureRandom;
    /**
     * The certificate.
     */
    private Certificate certificate;
    /**
     * Key size.
     */
    private int keySize;

    /**
     * Constructs with key size.
     *
     * @param keySize key size
     */
    public AlgorithmParams(int keySize) {
        this.keySize = keySize;
    }
}
