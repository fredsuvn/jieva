@file:JvmName("BCert")

package xyz.srclab.common.codec

import xyz.srclab.common.base.deBase64
import xyz.srclab.common.base.remainingLength
import xyz.srclab.common.base.to8BitBytes
import xyz.srclab.common.base.to8BitString
import xyz.srclab.common.codec.bc.DEFAULT_BCPROV_PROVIDER
import xyz.srclab.common.collect.indexOfArray
import java.io.InputStream
import java.security.*
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.spec.PKCS8EncodedKeySpec

fun InputStream.readX509(): X509Certificate {
    return readCert("X.509") as X509Certificate
}

fun InputStream.readCert(certType: CharSequence): Certificate {
    val fact = CertificateFactory.getInstance(certType.toString())
    return fact.generateCertificate(this)
}

fun InputStream.readP12(password: CharArray): KeyStore {
    return readKeyStore("PKCS12", password)
}

fun InputStream.readKeyStore(certType: CharSequence, password: CharArray): KeyStore {
    val ks = KeyStore.getInstance(certType.toString())
    ks.load(this, password)
    return ks
}

@Throws(NoSuchElementException::class)
fun KeyStore.getX509(): X509Certificate {
    return getX509OrNull() ?: throw NoSuchElementException("X509 not found!")
}

fun KeyStore.getX509OrNull(): X509Certificate? {
    for (alias in this.aliases()) {
        //if (this.isCertificateEntry(alias)) {
        return this.getCertificate(alias) as X509Certificate
        //}
    }
    return null
}

@Throws(NoSuchElementException::class)
fun KeyStore.getKey(password: CharArray): Key {
    return getKeyOrNull(password) ?: throw NoSuchElementException("Key not found!")
}

fun KeyStore.getKeyOrNull(password: CharArray): Key? {
    for (alias in this.aliases()) {
        //if (this.isKeyEntry(alias)) {
        return this.getKey(alias, password)
        //}
    }
    return null
}

@JvmOverloads
fun InputStream.readPKCS1PrivateKey(
    algorithm: CharSequence,
    provider: Provider? = DEFAULT_BCPROV_PROVIDER
): PrivateKey {
    return this.readBytes().readPKCS1PrivateKey(algorithm, provider)
}

@JvmOverloads
fun ByteArray.readPKCS1PrivateKey(
    algorithm: CharSequence,
    provider: Provider? = DEFAULT_BCPROV_PROVIDER
): PrivateKey {
    val spec = PKCS8EncodedKeySpec(this)
    val kf = if (provider === null)
        KeyFactory.getInstance(algorithm.toString())
    else
        KeyFactory.getInstance(algorithm.toString(), provider)
    return kf.generatePrivate(spec)
}

@JvmOverloads
fun InputStream.readPemPKCS1PrivateKey(
    algorithm: CharSequence,
    provider: Provider? = DEFAULT_BCPROV_PROVIDER
): PrivateKey {
    return this.readBytes().readPemPKCS1PrivateKey(algorithm, provider)
}

@JvmOverloads
fun ByteArray.readPemPKCS1PrivateKey(
    algorithm: CharSequence,
    provider: Provider? = DEFAULT_BCPROV_PROVIDER
): PrivateKey {
    val pemContent = this.readPemContent("-----BEGIN RSA PRIVATE KEY-----", "-----END RSA PRIVATE KEY-----")
    return pemContent.readPKCS1PrivateKey(algorithm, provider)
}

@JvmOverloads
fun InputStream.readPKCS8PrivateKey(algorithm: CharSequence, provider: Provider? = null): PrivateKey {
    return this.readBytes().readPKCS8PrivateKey(algorithm, provider)
}

@JvmOverloads
fun ByteArray.readPKCS8PrivateKey(algorithm: CharSequence, provider: Provider? = null): PrivateKey {
    val spec = PKCS8EncodedKeySpec(this)
    val kf = if (provider === null)
        KeyFactory.getInstance(algorithm.toString())
    else
        KeyFactory.getInstance(algorithm.toString(), provider)
    return kf.generatePrivate(spec)
}

@JvmOverloads
fun InputStream.readPemPKCS8PrivateKey(algorithm: CharSequence, provider: Provider? = null): PrivateKey {
    return this.readBytes().readPemPKCS8PrivateKey(algorithm, provider)
}

@JvmOverloads
fun ByteArray.readPemPKCS8PrivateKey(algorithm: CharSequence, provider: Provider? = null): PrivateKey {
    val pemContent = this.readPemContent("-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----")
    return pemContent.readPKCS8PrivateKey(algorithm, provider)
}

fun ByteArray.readPemContent(begin: CharSequence, end: CharSequence): ByteArray {
    return readPemContentOrNull(begin, end)
        ?: throw IllegalArgumentException("Pem content not found between \"$begin\" and \"$end\"")
}

fun ByteArray.readPemContentOrNull(begin: CharSequence, end: CharSequence): ByteArray? {
    val beginBytes = begin.to8BitBytes()
    val beginIndex = this.indexOfArray(beginBytes)
    if (beginIndex < 0) {
        return null
    }
    val endBytes = end.to8BitBytes()
    val contentBeginIndex = beginIndex + beginBytes.size
    val endIndex = this.indexOfArray(contentBeginIndex, endBytes)
    if (endIndex < 0) {
        return null
    }
    val chars = this.to8BitString(contentBeginIndex, remainingLength(endIndex, contentBeginIndex))
    val pureChars = chars.replace(Regex("\r|\n"), "")
    return pureChars.to8BitBytes().deBase64()
}