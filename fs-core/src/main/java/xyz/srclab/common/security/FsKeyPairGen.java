//package xyz.srclab.common.security;
//
//import xyz.srclab.annotations.Nullable;
//import xyz.srclab.common.base.FsCheck;
//
//import javax.crypto.KeyGenerator;
//import javax.crypto.spec.SecretKeySpec;
//import java.security.Key;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.SecureRandom;
//import java.security.spec.AlgorithmParameterSpec;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.security.spec.X509EncodedKeySpec;
//import java.util.Arrays;
//
///**
// * Denotes cipher for MAC generation, maybe has a back {@link KeyGenerator} or {@link KeyPairGenerator}.
// *
// * @author fredsuvn
// * @see KeyGenerator
// * @see KeyPairGenerator
// */
//public interface FsKeyPairGen {
//
////        /**
////         * Returns new instance of specified algorithm.
////         * Returned instance has a back thread-local mac which supplied with {@link Mac#getInstance(String)}.
////         *
////         * @param algorithm specified algorithm
////         */
////        static FsKeyGen getInstance(String algorithm) {
////            return new MacImpl(() -> {
////                try {
////                    return Mac.getInstance(algorithm);
////                } catch (Exception e) {
////                    throw new FsSecurityException(e);
////                }
////            });
////        }
////
////        /**
////         * Returns new instance of specified algorithm and provider.
////         * Returned instance has a back thread-local mac which supplied with {@link Mac#getInstance(String, String)}.
////         *
////         * @param algorithm specified algorithm
////         * @param provider  specified provider
////         */
////        static FsKeyGen getInstance(String algorithm, String provider) {
////            return new MacImpl(() -> {
////                try {
////                    return Mac.getInstance(algorithm, provider);
////                } catch (Exception e) {
////                    throw new FsSecurityException(e);
////                }
////            });
////        }
////
////        /**
////         * Returns new instance of specified algorithm and provider.
////         * Returned instance has a back thread-local mac which supplied with {@link Mac#getInstance(String, Provider)}.
////         *
////         * @param algorithm specified algorithm
////         * @param provider  specified provider
////         */
////        static FsKeyGen getInstance(String algorithm, Provider provider) {
////            return new MacImpl(() -> {
////                try {
////                    return Mac.getInstance(algorithm, provider);
////                } catch (Exception e) {
////                    throw new FsSecurityException(e);
////                }
////            });
////        }
//
//    /**
//     * Generates key of specified algorithm from given array.
//     *
//     * @param algorithm specified algorithm
//     * @param bytes     given array
//     */
//    static SecretKeySpec generate(String algorithm, byte[] bytes) {
//        try {
//            return generate(algorithm, bytes, 0, bytes.length);
//        } catch (FsSecurityException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new FsSecurityException(e);
//        }
//    }
//
//    /**
//     * Generates key of specified algorithm from given array of specified length start from given offset.
//     *
//     * @param algorithm specified algorithm
//     * @param bytes     given array
//     * @param offset    start offset
//     * @param length    specified length
//     */
//    static SecretKeySpec generate(String algorithm, byte[] bytes, int offset, int length) {
//        try {
//            return new SecretKeySpec(bytes, offset, length, algorithm);
//        } catch (FsSecurityException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new FsSecurityException(e);
//        }
//    }
//
//    /**
//     * Generates key of x509-format from given array.
//     *
//     * @param bytes given array
//     */
//    static X509EncodedKeySpec generateX509(byte[] bytes) {
//        try {
//            return new X509EncodedKeySpec(bytes);
//        } catch (FsSecurityException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new FsSecurityException(e);
//        }
//    }
//
//    /**
//     * Generates key of x509-format from given array of specified length start from given offset.
//     *
//     * @param bytes  given array
//     * @param offset start offset
//     * @param length specified length
//     */
//    static X509EncodedKeySpec generateX509(byte[] bytes, int offset, int length) {
//        try {
//            FsCheck.checkRangeInBounds(offset, offset + length, 0, bytes.length);
//            return generateX509(Arrays.copyOfRange(bytes, offset, offset + length));
//        } catch (FsSecurityException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new FsSecurityException(e);
//        }
//    }
//
//    /**
//     * Generates key of x509-format from given array.
//     *
//     * @param bytes given array
//     */
//    static PKCS8EncodedKeySpec generatePkcs8(byte[] bytes) {
//        try {
//            return new PKCS8EncodedKeySpec(bytes);
//        } catch (FsSecurityException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new FsSecurityException(e);
//        }
//    }
//
//    /**
//     * Generates key of x509-format from given array of specified length start from given offset.
//     *
//     * @param bytes  given array
//     * @param offset start offset
//     * @param length specified length
//     */
//    static PKCS8EncodedKeySpec generatePkcs8(byte[] bytes, int offset, int length) {
//        try {
//            FsCheck.checkRangeInBounds(offset, offset + length, 0, bytes.length);
//            return generatePkcs8(Arrays.copyOfRange(bytes, offset, offset + length));
//        } catch (FsSecurityException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new FsSecurityException(e);
//        }
//    }
//
//    /**
//     * Returns back {@link KeyGenerator} if it has, or null if it doesn't have one.
//     * The back {@link KeyGenerator} maybe thread-local, that is, returned value may be not only one instance.
//     */
//    @Nullable
//    KeyGenerator getKeyGenerator();
//
//    /**
//     * Returns back {@link KeyPairGenerator} if it has, or null if it doesn't have one.
//     * The back {@link KeyPairGenerator} maybe thread-local, that is, returned value may be not only one instance.
//     */
//    @Nullable
//    KeyPairGenerator getKeyPairGenerator();
//
//    /**
//     * Generates key.
//     */
//    Key generateKey();
//
//    /**
//     * Generates key with specified size.
//     *
//     * @param size specified key size
//     */
//    Key generateKey(int size);
//
//    /**
//     * Generates with specified key size and algorithm parameter spec.
//     *
//     * @param size specified key size
//     * @param spec algorithm parameter spec
//     */
//    default Key generateKey(int size, AlgorithmParameterSpec spec) {
//        return generateKey(size, spec, null);
//    }
//
//    /**
//     * Generates with specified key size and secure random.
//     *
//     * @param size         specified key size
//     * @param secureRandom secure random
//     */
//    default Key generateKey(int size, SecureRandom secureRandom) {
//        return generateKey(size, null, secureRandom);
//    }
//
//    /**
//     * Generates with specified key size, algorithm parameter spec and secure random.
//     *
//     * @param size         specified key size
//     * @param spec         algorithm parameter spec
//     * @param secureRandom secure random
//     */
//    Key generateKey(int size, @Nullable AlgorithmParameterSpec spec, @Nullable SecureRandom secureRandom);
//
//    /**
//     * Generates with algorithm parameter spec.
//     *
//     * @param spec algorithm parameter spec
//     */
//    default Key generateKey(AlgorithmParameterSpec spec) {
//        return generateKey(spec, null);
//    }
//
//    /**
//     * Generates with secure random.
//     *
//     * @param secureRandom secure random
//     */
//    default Key generateKey(SecureRandom secureRandom) {
//        return generateKey(null, secureRandom);
//    }
//
//    /**
//     * Generates with algorithm parameter spec and secure random.
//     *
//     * @param spec         algorithm parameter spec
//     * @param secureRandom secure random
//     */
//    Key generateKey(@Nullable AlgorithmParameterSpec spec, @Nullable SecureRandom secureRandom);
//
//    /**
//     * Generates key.
//     */
//    KeyPair generateKeyPair();
//
//    /**
//     * Generates key with specified size.
//     *
//     * @param size specified key size
//     */
//    KeyPair generateKeyPair(int size);
//
//    /**
//     * Generates with specified key size and algorithm parameter spec.
//     *
//     * @param size specified key size
//     * @param spec algorithm parameter spec
//     */
//    default KeyPair generateKeyPair(int size, AlgorithmParameterSpec spec) {
//        return generateKeyPair(size, spec, null);
//    }
//
//    /**
//     * Generates with specified key size and secure random.
//     *
//     * @param size         specified key size
//     * @param secureRandom secure random
//     */
//    default KeyPair generateKeyPair(int size, SecureRandom secureRandom) {
//        return generateKeyPair(size, null, secureRandom);
//    }
//
//    /**
//     * Generates with specified key size, algorithm parameter spec and secure random.
//     *
//     * @param size         specified key size
//     * @param spec         algorithm parameter spec
//     * @param secureRandom secure random
//     */
//    KeyPair generateKeyPair(int size, @Nullable AlgorithmParameterSpec spec, @Nullable SecureRandom secureRandom);
//
//    /**
//     * Generates with algorithm parameter spec.
//     *
//     * @param spec algorithm parameter spec
//     */
//    default KeyPair generateKeyPair(AlgorithmParameterSpec spec) {
//        return generateKeyPair(spec, null);
//    }
//
//    /**
//     * Generates with secure random.
//     *
//     * @param secureRandom secure random
//     */
//    default KeyPair generateKeyPair(SecureRandom secureRandom) {
//        return generateKeyPair(null, secureRandom);
//    }
//
//    /**
//     * Generates with algorithm parameter spec and secure random.
//     *
//     * @param spec         algorithm parameter spec
//     * @param secureRandom secure random
//     */
//    KeyPair generateKeyPair(@Nullable AlgorithmParameterSpec spec, @Nullable SecureRandom secureRandom);
//}
