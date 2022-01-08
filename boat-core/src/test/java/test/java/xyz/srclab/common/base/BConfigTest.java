package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BConfig;
import xyz.srclab.common.base.BResource;

import java.io.InputStream;
import java.util.Map;

public class BConfigTest {

    @Test
    public void testProperties() {
        InputStream inputStream = BResource.loadStream("META-INF/test.properties");
        Map<String, String> properties = BConfig.readProperties(inputStream);
        Assert.assertEquals(properties.get("info"), "123");
    }
}
