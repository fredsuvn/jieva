package test.java.xyz.srclab.common.lang;

import org.testng.annotations.Test;
import xyz.srclab.common.lang.Randoms;
import xyz.srclab.common.test.TestLogger;

public class RandomsTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testRandoms() {
        for (int i = 0; i < 10; i++) {
            logger.log("random[10, 20]: {}", Randoms.between(10, 21));
        }
    }
}
