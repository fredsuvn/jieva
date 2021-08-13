package test.java.xyz.srclab.common.lang;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.Compares;
import xyz.srclab.common.test.TestLogger;

public class ComparesTest {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testCompares() {
        Assert.assertEquals(Compares.between(100, 101, 666), Integer.valueOf(101));
        Assert.assertEquals(Compares.between(100, 0, 99), Integer.valueOf(99));
        Assert.assertEquals(Compares.between(100, 0, 666), Integer.valueOf(100));
    }
}
