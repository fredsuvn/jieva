package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BResource;
import xyz.srclab.common.collect.BMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BResourceTest {

    @Test
    public void testLoadResource() {
        String test = BResource.loadClasspathString("/META-INF/test.properties");
        BLog.info("loadClasspathString: {}", test);
        Assert.assertEquals(test, "info=123");

        List<String> texts = BResource.loadClasspathStrings("META-INF/test.properties");
        BLog.info("loadClasspathStrings: {}", texts);
        Assert.assertEquals(
            texts,
            Collections.singletonList("info=123")
        );

        List<Map<String, String>> properties = BResource.loadClasspathPropertiesList("META-INF/test.properties");
        BLog.info("loadClasspathPropertiesList: {}", properties);
        Assert.assertEquals(
            properties,
            Collections.singletonList(BMap.newMap("info", "123"))
        );
    }
}
