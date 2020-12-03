package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Loader;

import java.util.Collections;

public class LoaderTest {

    @Test
    public void testLoader() {
        Assert.assertEquals(
                Loader.findStringResources("META-INF/test.info"),
                Collections.singletonList("test.info")
        );
    }
}
