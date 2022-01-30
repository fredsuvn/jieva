@file:JvmName("BSm2")

package xyz.srclab.common.codec.sm2

import org.bouncycastle.crypto.generators.ECKeyPairGenerator
import org.bouncycastle.crypto.params.ECDomainParameters
import org.bouncycastle.crypto.params.ECKeyGenerationParameters
import org.bouncycastle.crypto.params.ECPublicKeyParameters
import xyz.srclab.common.codec.CodecAlgorithm
import xyz.srclab.common.codec.bcprov.DEFAULT_X9_EC_PARAMETERS
import java.security.KeyPair
import java.security.SecureRandom

@JvmName("generateKeyPair")
fun generateSm2KeyPair(): KeyPair {
    val domainParameters = ECDomainParameters(
        DEFAULT_X9_EC_PARAMETERS.curve, DEFAULT_X9_EC_PARAMETERS.g, DEFAULT_X9_EC_PARAMETERS.n);
    val keyPairGenerator = ECKeyPairGenerator()
    keyPairGenerator.init(ECKeyGenerationParameters(
        domainParameters, SecureRandom.getInstance(CodecAlgorithm.SHA1PRNG_NAME)));
    val asymmetricCipherKeyPair = keyPairGenerator.generateKeyPair();
    //val publicKeyParameters = asymmetricCipherKeyPair.getPublic() as ECPublicKeyParameters
    //ECPoint ecPoint = publicKeyParameters.getQ();
    //// 把公钥放入map中,默认压缩公钥
    //// 公钥前面的02或者03表示是压缩公钥,04表示未压缩公钥,04的时候,可以去掉前面的04
    //String publicKey = Hex.toHexString(ecPoint.getEncoded(compressed));
    //ECPrivateKeyParameters privateKeyParameters = (ECPrivateKeyParameters) asymmetricCipherKeyPair.getPrivate();
    //BigInteger intPrivateKey = privateKeyParameters.getD();
    //// 把私钥放入map中
    //String privateKey = intPrivateKey.toString(16);
    //logger.debug("\npublicKey：{}\nprivateKey：{}", publicKey, privateKey);
    //return new KeyPairOfString(publicKey, privateKey);
    //return object
}