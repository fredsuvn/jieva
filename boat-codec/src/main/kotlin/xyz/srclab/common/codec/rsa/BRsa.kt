@file:JvmName("BAes")

package xyz.srclab.common.codec.rsa

import xyz.srclab.common.base.DEFAULT_CHARSET
import xyz.srclab.common.codec.CodecAlgorithm
import xyz.srclab.common.codec.base64PassphraseToKey
import xyz.srclab.common.codec.passphraseToKey
import java.nio.charset.Charset
import javax.crypto.SecretKey

private const val DEFAULT_AES_KEY_SIZE = 16

@JvmName("base64PassphraseToKey")
fun CharSequence.base64PassphraseToAesKey(): SecretKey {
    return this.base64PassphraseToKey(CodecAlgorithm.AES_NAME, DEFAULT_AES_KEY_SIZE)
}

@JvmName("passphraseToKey")
@JvmOverloads
fun CharSequence.passphraseToAesKey(charset: Charset = DEFAULT_CHARSET): SecretKey {
    return this.passphraseToKey(CodecAlgorithm.AES_NAME, DEFAULT_AES_KEY_SIZE)
}

@JvmName("passphraseToKey")
fun ByteArray.passphraseToAesKey(): SecretKey {
    return this.passphraseToKey(CodecAlgorithm.AES_NAME, DEFAULT_AES_KEY_SIZE)
}