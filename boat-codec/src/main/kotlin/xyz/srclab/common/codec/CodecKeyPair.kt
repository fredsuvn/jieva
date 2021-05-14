package xyz.srclab.common.codec

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.toChars
import xyz.srclab.common.codec.Codec.Companion.encodeBase64String
import xyz.srclab.common.codec.Codec.Companion.encodeHexString

/**
 * Key pair of codec.
 *
 * @param [PUB] public key
 * @param [PRI] private key
 */
interface CodecKeyPair<PUB, PRI> {

    /**
     * Returns public key.
     *
     * @return public key
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    val publicKey: PUB
        @JvmName("publicKey") get

    /**
     * Returns private key.
     *
     * @return private key
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    val privateKey: PRI
        @JvmName("privateKey") get

    /**
     * Returns public key as bytes.
     *
     * @return public key as bytes
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    val publicKeyBytes: ByteArray
        @JvmName("publicKeyBytes") get

    /**
     * Returns private key as bytes.
     *
     * @return private key as bytes
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    val privateKeyBytes: ByteArray
        @JvmName("privateKeyBytes") get

    /**
     * Returns public key as string.
     *
     * @return public key as string
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val publicKeyString: String
        @JvmName("publicKeyString") get() = publicKeyBytes.toChars()

    /**
     * Returns private key as string.
     *
     * @return private key as string
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val privateKeyString: String
        @JvmName("privateKeyString") get() = privateKeyBytes.toChars()

    /**
     * Returns public key as hex string.
     *
     * @return public key as hex string
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val publicKeyHexString: String
        @JvmName("publicKeyHexString") get() = publicKeyBytes.encodeHexString()

    /**
     * Returns private key as hex string.
     *
     * @return private key as hex string
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val privateKeyHexString: String
        @JvmName("privateKeyHexString") get() = privateKeyBytes.encodeHexString()

    /**
     * Returns public key as base64 string.
     *
     * @return public key as base64 string
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val publicKeyBase64String: String
        @JvmName("publicKeyBase64String") get() = publicKeyBytes.encodeBase64String()

    /**
     * Returns private key as base64 string.
     *
     * @return private key as base64 string
     */
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val privateKeyBase64String: String
        @JvmName("privateKeyBase64String") get() = privateKeyBytes.encodeBase64String()
}