package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.base.BResource;
import xyz.srclab.common.collect.BMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BResourceTest {

    @Test
    public void testLoadResource() {
        List<String> texts = BResource.loadStrings("META-INF/test.properties");
        BLog.info("Load texts: {}", texts);
        Assert.assertEquals(
            texts,
            Collections.singletonList("info=123")
        );

        List<Map<String, String>> properties = BResource.loadPropertiesList("META-INF/test.properties");
        BLog.info("Load properties: {}", properties);
        Assert.assertEquals(
            properties,
            Collections.singletonList(BMap.collect(new HashMap<>(), "info", "123"))
        );
    }
}
