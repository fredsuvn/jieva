package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.MemberKit;

public class MemberKitTest {

    @Test
    public void testAccessibleFor() throws Exception {
        Assert.assertTrue(MemberKit.isAccessibleFor(
                NewClass.class.getDeclaredField("protectedParam"),
                SubNewClass.class
        ));
        Assert.assertFalse(MemberKit.isAccessibleFor(
                NewClass.class.getDeclaredField("privateParam"),
                SubNewClass.class
        ));
    }
}
