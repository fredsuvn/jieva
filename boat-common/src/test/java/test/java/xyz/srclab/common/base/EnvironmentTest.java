package test.java.xyz.srclab.common.base;

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
}
