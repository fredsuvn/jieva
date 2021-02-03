package xyz.srclab.common.codec

import cn.com.essence.galaxy.annotation.Nullable

/**
 * A skeletal implementation of the [CodecKeyPair]. Implements [.publicKeyToBytes] and
 * [.privateKeyToBytes] to fast to implements a full [CodecKeyPair].
 *
 * @param <PUB> public key
 * @param <PRI> private key
 * @author sunqian
</PRI></PUB> */
abstract class AbstractCodecKeyPair<PUB, PRI> protected constructor(
    override val encryptKey: PUB,
    override val privateKey: PRI
) : CodecKeyPair<PUB, PRI> {

    @Nullable
    override var encryptKeyBytes: ByteArray?
        get() {
            if (field == null) {
                field = publicKeyToBytes(encryptKey)
            }
            return field
        }
        private set

    @Nullable
    override var privateKeyBytes: ByteArray?
        get() {
            if (field == null) {
                field = privateKeyToBytes(privateKey)
            }
            return field
        }
        private set

    @Nullable
    override var publicKeyString: String? = null
        get() {
            if (field == null) {
                field = CodecBytes.toString(encryptKeyBytes)
            }
            return field
        }
        private set

    @Nullable
    override var privateKeyString: String? = null
        get() {
            if (field == null) {
                field = CodecBytes.toString(privateKeyBytes)
            }
            return field
        }
        private set

    @Nullable
    override var publicKeyHexString: String? = null
        get() {
            if (field == null) {
                field = Codec.Companion.encodeHexString(encryptKeyBytes)
            }
            return field
        }
        private set

    @Nullable
    override var privateKeyHexString: String? = null
        get() {
            if (field == null) {
                field = Codec.Companion.encodeHexString(privateKeyBytes)
            }
            return field
        }
        private set

    @Nullable
    override var publicKeyBase64String: String? = null
        get() {
            if (field == null) {
                field = Codec.Companion.encodeBase64String(encryptKeyBytes)
            }
            return field
        }
        private set

    @Nullable
    override var privateKeyBase64String: String? = null
        get() {
            if (field == null) {
                field = Codec.Companion.encodeBase64String(privateKeyBytes)
            }
            return field
        }
        private set

    protected abstract fun publicKeyToBytes(publicKey: PUB): ByteArray?
    protected abstract fun privateKeyToBytes(privateKey: PRI): ByteArray?
}