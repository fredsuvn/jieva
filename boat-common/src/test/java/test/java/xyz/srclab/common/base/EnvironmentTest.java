package test.java.xyz.srclab.common.base;

import org.apache.commons.lang3.SystemUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Environment;

import java.util.Map;

/**
 * @author sunqian
 */
public class EnvironmentTest {

    @Test
    public void testEnvironment() {
        int availableProcessors = Environment.availableProcessors();
        System.out.println("availableProcessors: " + availableProcessors);
        Assert.assertEquals(Runtime.getRuntime().availableProcessors(), availableProcessors);
        System.out.println("Environment.properties():");
        Map<String, String> properties = Environment.properties();
        properties.keySet().stream().sorted().forEach(k ->
                System.out.printf("%-60s: %s%n", k, properties.get(k)));
    }

    @Test
    public void testOs() {
        Assert.assertEquals(Environment.isOsLinux(), SystemUtils.IS_OS_LINUX);
        Assert.assertEquals(Environment.isOsUnix(), SystemUtils.IS_OS_UNIX);
        Assert.assertEquals(Environment.isOsWindows(), SystemUtils.IS_OS_WINDOWS);
        Assert.assertEquals(Environment.isOsMac(), SystemUtils.IS_OS_MAC);
    }

    @Test
    public void testJavaVersion() {
        int versionNumber = Environment.javaVersionNumber();
        String version = Environment.javaSpecificationVersion();
        if (versionNumber <= 8) {
            Assert.assertTrue(version.startsWith("1." + versionNumber));
        } else {
            Assert.assertTrue(version.startsWith("" + versionNumber));
        }
    }
}
