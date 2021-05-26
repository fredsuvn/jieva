package test.java.xyz.srclab.common.convert;

import org.testng.annotations.Test;
import xyz.srclab.common.convert.Converts;
import xyz.srclab.common.test.TestLogger;

/**
 * @author sunqian
 */
public class ConvertTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testConverts() {
        E e = Converts.convert("b", E.class);
        logger.log("e: {}", e);
    }

    public static enum E {
        A, B, C
    }
}
