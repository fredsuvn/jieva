package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Systems;
import xyz.srclab.common.logging.Logs;

import java.util.Map;

/**
 * @author sunqian
 */
public class SystemsTest {

    @Test
    public void testEnvironment() {
        int availableProcessors = Systems.availableProcessors();
        Logs.info("availableProcessors: " + availableProcessors);
        Assert.assertEquals(Runtime.getRuntime().availableProcessors(), availableProcessors);
        Logs.info("Environment.properties():");
        Map<String, String> properties = Systems.getProperties();
        properties.keySet().stream().sorted().forEach(k ->
            System.out.printf("%-60s: %s%n", k, properties.get(k)));
    }

    @Test
    public void testJavaVersion() {
        int versionNumber = Systems.getJavaMajorVersion();
        String version = Systems.getJavaSpecificationVersion();
        if (versionNumber <= 8) {
            Assert.assertTrue(version.startsWith("1." + versionNumber));
        } else {
            Assert.assertTrue(version.startsWith("" + versionNumber));
        }
    }
}
