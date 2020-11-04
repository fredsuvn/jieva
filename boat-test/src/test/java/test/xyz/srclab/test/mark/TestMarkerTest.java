package test.xyz.srclab.test.mark;

import org.testng.annotations.Test;
import xyz.srclab.test.mark.TestMarker;

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
