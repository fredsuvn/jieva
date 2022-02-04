@file:JvmName("BGm")

package xyz.srclab.common.codec.gm

import xyz.srclab.common.base.DEFAULT_CHARSET
import xyz.srclab.common.codec.CodecAlgorithm
import xyz.srclab.common.codec.base64PassphraseToKey
import xyz.srclab.common.codec.bcprov.DEFAULT_BCPROV_PROVIDER
import xyz.srclab.common.codec.bcprov.DEFAULT_EC_PARAMETER_SPEC
import xyz.srclab.common.codec.passphraseToKey
import java.nio.charset.Charset
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.Provider
import java.security.SecureRandom
import javax.crypto.SecretKey

const val DEFAULT_SM4_KEY_SIZE = 16

fun generateSm2KeyPair(): KeyPair {

    //KeyPairGenerator.getInstance("", DEFAULT_BCPROV_PROVIDER)
    //val domainParameters = ECDomainParameters(
    //    DEFAULT_X9_EC_PARAMETERS.curve, DEFAULT_X9_EC_PARAMETERS.g, DEFAULT_X9_EC_PARAMETERS.n);
    //val keyPairGenerator = ECKeyPairGenerator()
    //keyPairGenerator.init(ECKeyGenerationParameters(
    //    domainParameters, SecureRandom.getInstance(CodecAlgorithm.SHA1PRNG_NAME)));
    //val asymmetricCipherKeyPair = keyPairGenerator.generateKeyPair();
    //return asymmetricCipherKeyPair
    ////val publicKeyParameters = asymmetricCipherKeyPair.getPublic() as ECPublicKeyParameters
    ////ECPoint ecPoint = publicKeyParameters.getQ();
    ////// 把公钥放入map中,默认压缩公钥
    ////// 公钥前面的02或者03表示是压缩公钥,04表示未压缩公钥,04的时候,可以去掉前面的04
    ////String publicKey = Hex.toHexString(ecPoint.getEncoded(compressed));
    ////ECPrivateKeyParameters privateKeyParameters = (ECPrivateKeyParameters) asymmetricCipherKeyPair.getPrivate();
    ////BigInteger intPrivateKey = privateKeyParameters.getD();
    ////// 把私钥放入map中
    ////String privateKey = intPrivateKey.toString(16);
    ////logger.debug("\npublicKey：{}\nprivateKey：{}", publicKey, privateKey);
    ////return new KeyPairOfString(publicKey, privateKey);
    ////return object

    //val pkcs8Encoded: ByteArray = PrivateKeyInfoFactory.createPrivateKeyInfo(asymmetricCipherKeyPair.private).encoded
    //val pkcs8KeySpec = PKCS8EncodedKeySpec(pkcs8Encoded)
    //val spkiEncoded: ByteArray = SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(asymmetricCipherKeyPair.public).encoded
    //val spkiKeySpec = X509EncodedKeySpec(spkiEncoded)
    //val keyFac: KeyFactory = KeyFactory.getInstance("EC", DEFAULT_BCPROV_PROVIDER)
    //return KeyPair(keyFac.generatePublic(spkiKeySpec), keyFac.generatePrivate(pkcs8KeySpec))
    //val sm2Spec = ECGenParameterSpec("sm2p256v1")
    val keyPairGenerator = KeyPairGenerator.getInstance("EC", DEFAULT_BCPROV_PROVIDER)
    val secureRandom = SecureRandom.getInstance(CodecAlgorithm.SHA1PRNG_NAME)
    keyPairGenerator.initialize(DEFAULT_EC_PARAMETER_SPEC, secureRandom)
    return keyPairGenerator.generateKeyPair()
}

@JvmOverloads
fun CharSequence.base64PassphraseToSm4Key(
    size: Int = DEFAULT_SM4_KEY_SIZE * 8, provider: Provider? = null
): SecretKey {
    return this.base64PassphraseToKey(CodecAlgorithm.SM4_NAME, size, getProvider(provider))
}

@JvmOverloads
fun CharSequence.passphraseToSm4Key(
    charset: Charset = DEFAULT_CHARSET,
    size: Int = DEFAULT_SM4_KEY_SIZE * 8,
    provider: Provider? = null
): SecretKey {
    return this.passphraseToKey(CodecAlgorithm.SM4_NAME, size, charset, getProvider(provider))
}

@JvmOverloads
fun ByteArray.passphraseToSm4Key(
    size: Int = DEFAULT_SM4_KEY_SIZE * 8, provider: Provider? = null
): SecretKey {
    return this.passphraseToKey(CodecAlgorithm.SM4_NAME, size, getProvider(provider))
}

private fun getProvider(provider: Provider?): Provider {
    return provider ?: DEFAULT_BCPROV_PROVIDER
}