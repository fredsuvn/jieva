package test.xyz.srclab.common.codec;

import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BResource;
import xyz.srclab.common.codec.BCert;

import java.security.KeyStore;
import java.security.cert.X509Certificate;

/**
 * @author sunqian
 */
public class CertTest {

    @Test
    public void testCert() {
        X509Certificate x509 = BCert.readX509(BResource.loadStream("ca.crt"));
        BLog.info("x509: {}", x509);
    }

    @Test
    public void testKeyStore() {
        KeyStore keyStore = BCert.readP12(BResource.loadStream("server.p12"), "ca654321".toCharArray());
        BLog.info("p12.x509: {}", BCert.getX509(keyStore));
        BLog.info("p12.key: {}", BCert.getKey(keyStore, "ca654321".toCharArray()));
    }
}
