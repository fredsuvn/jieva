package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Fs;

public class ObjectTest {

    @Test
    public void testEnum() {
        Assert.assertEquals(Te.A, Fs.findEnum(Te.class, 0));
        Assert.assertEquals(Te.B, Fs.findEnum(Te.class, "B", false));
        Assert.assertEquals(Te.C, Fs.findEnum(Te.class, "c", true));
        Assert.assertNull(Fs.findEnum(Te.class, 10));
        Assert.assertNull(Fs.findEnum(Te.class, "d", false));
        Assert.expectThrows(IllegalArgumentException.class, () -> Fs.findEnum(Te.class, -1));
        Assert.expectThrows(IllegalArgumentException.class, () -> Fs.findEnum(ObjectTest.class, -1));
        Assert.expectThrows(IllegalArgumentException.class, () -> Fs.findEnum(ObjectTest.class, "a", true));
    }

    public enum Te {
        A, B, C
    }
}
