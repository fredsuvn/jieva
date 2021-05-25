package xyz.srclab.common.codec

import xyz.srclab.common.lang.INAPPLICABLE_JVM_NAME
import xyz.srclab.common.lang.toChars

/**
 * Key pair of codec.
 *
 * @param [PUB] public key
 * @param [PRI] private key
 */
interface CodecKeyPair<PUB, PRI> {

    @get:JvmName("publicKey")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val publicKey: PUB

    @get:JvmName("privateKey")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val privateKey: PRI

    @get:JvmName("publicKeyBytes")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val publicKeyBytes: ByteArray

    @get:JvmName("privateKeyBytes")
    @Suppress(INAPPLICABLE_JVM_NAME)
    val privateKeyBytes: ByteArray

    @get:JvmName("publicKeyString")
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val publicKeyString: String
        get() = publicKeyBytes.toChars()

    @get:JvmName("privateKeyString")
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val privateKeyString: String
        get() = privateKeyBytes.toChars()

    @get:JvmName("publicKeyHexString")
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val publicKeyHexString: String
        get() = publicKeyBytes.toHexString()

    @get:JvmName("privateKeyHexString")
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val privateKeyHexString: String
        get() = privateKeyBytes.toHexString()

    @get:JvmName("publicKeyBase64String")
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val publicKeyBase64String: String
        get() = publicKeyBytes.toBase64String()

    @get:JvmName("privateKeyBase64String")
    @Suppress(INAPPLICABLE_JVM_NAME)
    @JvmDefault
    val privateKeyBase64String: String
        get() = privateKeyBytes.toBase64String()
}