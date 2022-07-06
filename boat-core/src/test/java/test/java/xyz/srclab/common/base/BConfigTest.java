package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BConfig;
import xyz.srclab.common.base.BResource;
import xyz.srclab.common.base.Bt;
import xyz.srclab.common.collect.BList;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class BConfigTest {

    @Test
    public void testProperties() {
        InputStream inputStream = Bt.loadStream("META-INF/test.properties");
        assert inputStream != null;
        Map<String, String> properties = Bt.readProperties(inputStream);
        Assert.assertEquals(properties.get("info"), "123");
    }

    @Test
    public void testLoadAll() {
        List<String> strings = Bt.loadStrings("META-INF/test.properties");
        Assert.assertEquals(strings, BtL.newList("info=123"));
    }
}
