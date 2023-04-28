package test.xyz.srclab.common.codec;

import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BResource;
import xyz.srclab.common.codec.BCert;
import xyz.srclab.common.codec.BCodec;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

/**
 * @author sunqian
 */
public class CertTest {

    @Test
    public void testCert() throws Exception {
        X509Certificate x509 = BCert.readX509(BResource.loadStream("ca.crt"));
        PrivateKey privateKey = BCert.readPemPKCS1PrivateKey(BResource.loadStream("ca.key"), "RSA");
        BLog.info("x509: {}", x509);
        BLog.info("privateKey: {}", privateKey);
        CipherTester.testCipher(BCodec.rsa(), x509.getPublicKey(), privateKey);
    }

    @Test
    public void testKeyStore() throws Exception {
        KeyStore keyStore = BCert.readP12(BResource.loadStream("server.p12"), "ca654321".toCharArray());
        BLog.info("p12.x509: {}", BCert.getX509(keyStore));
        BLog.info("p12.key: {}", BCert.getKey(keyStore, "ca654321".toCharArray()));
        CipherTester.testCipher(
            BCodec.rsa(),
            BCert.getX509(keyStore).getPublicKey(),
            BCert.getKey(keyStore, "ca654321".toCharArray())
        );
    }
}
