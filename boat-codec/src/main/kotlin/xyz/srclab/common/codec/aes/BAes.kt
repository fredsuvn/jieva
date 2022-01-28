@file:JvmName("BAes")

package xyz.srclab.common.codec.aes

import xyz.srclab.common.base.DEFAULT_CHARSET
import xyz.srclab.common.codec.CipherCodec
import xyz.srclab.common.codec.CodecAlgorithm
import xyz.srclab.common.codec.base64PassphraseToKey
import xyz.srclab.common.codec.passphraseToKey
import java.nio.charset.Charset
import javax.crypto.SecretKey

private const val DEFAULT_AES_KEY_SIZE = 16

@JvmName("base64PassphraseToKey")
@JvmOverloads
fun CharSequence.base64PassphraseToAesKey(size: Int = DEFAULT_AES_KEY_SIZE * 8): SecretKey {
    return this.base64PassphraseToKey(CodecAlgorithm.AES_NAME, size)
}

@JvmName("passphraseToKey")
@JvmOverloads
fun CharSequence.passphraseToAesKey(
    charset: Charset = DEFAULT_CHARSET,
    size: Int = DEFAULT_AES_KEY_SIZE * 8
): SecretKey {
    return this.passphraseToKey(CodecAlgorithm.AES_NAME, size)
}

@JvmName("passphraseToKey")
@JvmOverloads
fun ByteArray.passphraseToAesKey(size: Int = DEFAULT_AES_KEY_SIZE * 8): SecretKey {
    return this.passphraseToKey(CodecAlgorithm.AES_NAME, size)
}

fun newAesCodec(): CipherCodec {
    return CipherCodec.aes()
}