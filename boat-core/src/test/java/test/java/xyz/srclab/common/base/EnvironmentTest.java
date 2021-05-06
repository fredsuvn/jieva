package test.java.xyz.srclab.common.base;

import org.apache.commons.lang3.SystemUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Environment;
import xyz.srclab.common.test.TestLogger;

import java.util.Map;

/**
 * @author sunqian
 */
public class EnvironmentTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testEnvironment() {
        int availableProcessors = Environment.availableProcessors();
        logger.log("availableProcessors: " + availableProcessors);
        Assert.assertEquals(Runtime.getRuntime().availableProcessors(), availableProcessors);
        logger.log("Environment.properties():");
        Map<String, String> properties = Environment.properties();
        properties.keySet().stream().sorted().forEach(k ->
            System.out.printf("%-60s: %s%n", k, properties.get(k)));
    }

    @Test
    public void testOs() {
        Assert.assertEquals(Environment.isOsAix(), SystemUtils.IS_OS_AIX);
        Assert.assertEquals(Environment.isOsHpUx(), SystemUtils.IS_OS_HP_UX);
        Assert.assertEquals(Environment.isOsOs400(), SystemUtils.IS_OS_400);
        Assert.assertEquals(Environment.isOsIrix(), SystemUtils.IS_OS_IRIX);
        Assert.assertEquals(Environment.isOsLinux(), SystemUtils.IS_OS_LINUX);
        Assert.assertEquals(Environment.isOsMac(), SystemUtils.IS_OS_MAC);
        Assert.assertEquals(Environment.isOsMacOsX(), SystemUtils.IS_OS_MAC_OSX);
        Assert.assertEquals(Environment.isOsFreeBsd(), SystemUtils.IS_OS_FREE_BSD);
        Assert.assertEquals(Environment.isOsOpenBsd(), SystemUtils.IS_OS_OPEN_BSD);
        Assert.assertEquals(Environment.isOsNetBsd(), SystemUtils.IS_OS_NET_BSD);
        Assert.assertEquals(Environment.isOsOs2(), SystemUtils.IS_OS_OS2);
        Assert.assertEquals(Environment.isOsSolaris(), SystemUtils.IS_OS_SOLARIS);
        Assert.assertEquals(Environment.isOsSunOs(), SystemUtils.IS_OS_SUN_OS);
        Assert.assertEquals(Environment.isOsUnix(), SystemUtils.IS_OS_UNIX);
        Assert.assertEquals(Environment.isOsWindows(), SystemUtils.IS_OS_WINDOWS);
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
