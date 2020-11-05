package test.xyz.srclab.common.test;

import org.testng.annotations.Test;
import xyz.srclab.common.test.TestMarker;

/**
 * @author sunqian
 */
public class TestMarkerTest {

    @Test
    public void testTestMarker() {
        TestMarker testMarker = TestMarker.newTestMarker();
        testMarker.mark("hello");
        testMarker.mark("world", "!");
        System.out.println(testMarker);
    }
}