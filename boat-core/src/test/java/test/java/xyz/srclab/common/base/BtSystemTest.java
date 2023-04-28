package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BtLog;
import xyz.srclab.common.base.BtSystem;

import java.util.Map;

/**
 * @author sunqian
 */
public class BtSystemTest {

    @Test
    public void testEnvironment() {
        int availableProcessors = BtSystem.availableProcessors();
        BtLog.info("availableProcessors: " + availableProcessors);
        Assert.assertEquals(Runtime.getRuntime().availableProcessors(), availableProcessors);
        BtLog.info("Environment.properties():");
        Map<String, String> properties = BtSystem.getProperties();
        properties.keySet().stream().sorted().forEach(k ->
            System.out.printf("%-60s: %s%n", k, properties.get(k)));
    }

    @Test
    public void testJavaVersion() {
        int versionNumber = BtSystem.getJavaMajorVersion();
        String version = BtSystem.getJavaSpecificationVersion();
        BtLog.info("Current version: {}, number: {}", version, versionNumber);
        if (versionNumber <= 8) {
            Assert.assertTrue(version.startsWith("1." + versionNumber));
        } else {
            Assert.assertTrue(version.startsWith("" + versionNumber));
        }
    }

    @Test
    public void testSystemDefault() {
        BtLog.info("JVM charset: {}", BtSystem.jvmCharset());
        BtLog.info("Native charset: {}", BtSystem.nativeCharset());
    }
}
