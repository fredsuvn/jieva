package samples;

import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Fs;
import xyz.srclab.common.base.FsString;
import xyz.srclab.common.codec.*;
import xyz.srclab.common.codec.bouncycastle.BouncyCastleCodecProvider;
import xyz.srclab.common.codec.jdk.JdkCodecProvider;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CASample {

    public static void main(String[] args) throws Exception {
        createCA();
    }

    private static void createCA() throws Exception {
        Map<String, String> map = new HashMap<>();
        //keytool -genkeypair -alias ca1 -keyalg RSA -keysize 1024 -keystore ca.jks
        map.put("alias", "a1");
        map.put("keyalg", "RSA");
        map.put("keysize", "1024");
        map.put("keystore", "ca2.jks");
        map.put("CN", "ss");
        map.put("OU", "ss");
        map.put("O", "ss");
        map.put("L", "ss");
        map.put("ST", "ss");
        map.put("C", "CN");
        FsKeyTool.genKeyPair("\"C:\\Program Files\\Java\\jdk1.8.0_301\\bin\\keytool\"", "anxin123!", map);
//        FsEncryptor encryptor = FsEncryptor.rsa();
//        KeyPair keyPair = encryptor.generateKeyPair(new FsEncryptParams(1024));
////        X509v3CertificateBuilder.
    }
}
