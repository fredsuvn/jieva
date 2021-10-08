package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Compares;

public class ComparesTest {

    @Test
    public void testCompares() {
        Assert.assertEquals(Compares.between(100, 101, 666), Integer.valueOf(101));
        Assert.assertEquals(Compares.between(100, 0, 99), Integer.valueOf(99));
        Assert.assertEquals(Compares.between(100, 0, 666), Integer.valueOf(100));

        Assert.assertEquals(Compares.atLeast(50, 100), Integer.valueOf(100));
        Assert.assertEquals(Compares.atLeast(111, 100), Integer.valueOf(111));
        Assert.assertEquals(Compares.atMost(50, 100), Integer.valueOf(50));
        Assert.assertEquals(Compares.atMost(111, 100), Integer.valueOf(100));
    }
}
