package test;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.FsObject;

public class ObjectTest {

    @Test
    public void testEnum() {
        Assert.assertEquals(Te.A, FsObject.findEnum(Te.class, 0));
        Assert.assertEquals(Te.B, FsObject.findEnum(Te.class, "B", false));
        Assert.assertEquals(Te.C, FsObject.findEnum(Te.class, "c", true));
        Assert.assertNull(FsObject.findEnum(Te.class, 10));
        Assert.assertNull(FsObject.findEnum(Te.class, "d", false));
        Assert.expectThrows(IllegalArgumentException.class, () -> FsObject.findEnum(Te.class, -1));
        Assert.expectThrows(IllegalArgumentException.class, () -> FsObject.findEnum(ObjectTest.class, -1));
        Assert.expectThrows(IllegalArgumentException.class, () -> FsObject.findEnum(ObjectTest.class, "a", true));
    }

    public enum Te {
        A, B, C
    }
}
