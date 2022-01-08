package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BSystem;

import java.util.Map;

/**
 * @author sunqian
 */
public class BSystemTest {

    @Test
    public void testEnvironment() {
        int availableProcessors = BSystem.availableProcessors();
        BLog.info("availableProcessors: " + availableProcessors);
        Assert.assertEquals(Runtime.getRuntime().availableProcessors(), availableProcessors);
        BLog.info("Environment.properties():");
        Map<String, String> properties = BSystem.getProperties();
        properties.keySet().stream().sorted().forEach(k ->
            System.out.printf("%-60s: %s%n", k, properties.get(k)));
    }

    @Test
    public void testJavaVersion() {
        int versionNumber = BSystem.getJavaMajorVersion();
        String version = BSystem.getJavaSpecificationVersion();
        if (versionNumber <= 8) {
            Assert.assertTrue(version.startsWith("1." + versionNumber));
        } else {
            Assert.assertTrue(version.startsWith("" + versionNumber));
        }
    }
}
