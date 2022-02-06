@file:JvmName("BCert")

package xyz.srclab.common.codec

import java.io.InputStream
import java.security.Key
import java.security.KeyStore
import java.security.cert.Certificate
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

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
        if (this.isCertificateEntry(alias)) {
            return this.getCertificate(alias) as X509Certificate
        }
    }
    return null
}

@Throws(NoSuchElementException::class)
fun KeyStore.getKey(password: CharArray): Key {
    return getKeyOrNull(password) ?: throw NoSuchElementException("Key not found!")
}

fun KeyStore.getKeyOrNull(password: CharArray): Key? {
    for (alias in this.aliases()) {
        if (this.isKeyEntry(alias)) {
            return this.getKey(alias, password)
        }
    }
    return null
}