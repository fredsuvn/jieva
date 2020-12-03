package test.java.xyz.srclab.common.test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.test.TestMarker;

/**
 * @author sunqian
 */
public class TestMarkerTest {

    @Test
    public void testTestMarker() {
        TestMarker testMarker = TestMarker.newTestMarker();
        testMarker.mark("hello", "world");
        Assert.assertEquals(testMarker.getMark("hello"), "world");
        Assert.assertFalse(testMarker.asMap().isEmpty());
        testMarker.clearMarks();
        Assert.assertTrue(testMarker.asMap().isEmpty());
    }
}