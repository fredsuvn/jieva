package test.java.xyz.srclab.common.jvm;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BDefault;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BResource;
import xyz.srclab.common.io.BFile;
import xyz.srclab.common.io.BIO;
import xyz.srclab.common.jvm.BJvm;

import java.io.File;
import java.net.URL;

public class JvmTest {

    @Test
    public void testFile() {
        String s1 = BJvm.getTypeSignature(byte[].class);
        BLog.info("JVM byte[] signature: {}", s1);
        String s2 = BJvm.getTypeSignature(Byte[].class);
        BLog.info("JVM Byte[] signature: {}", s2);
        String s3 = BJvm.getTypeSignature(String.class);
        BLog.info("JVM Byte[] signature: {}", s3);
    }
}
