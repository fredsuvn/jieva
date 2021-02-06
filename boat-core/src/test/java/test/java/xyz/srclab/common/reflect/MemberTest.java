package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.Reflects;

public class MemberTest {

    @Test
    public void testAccessibleFor() throws Exception {
        Assert.assertTrue(Reflects.isAccessibleFor(
                NewClass.class.getDeclaredField("protectedField"),
                SubNewClass.class
        ));
        Assert.assertFalse(Reflects.isAccessibleFor(
                NewClass.class.getDeclaredField("privateField"),
                SubNewClass.class
        ));
    }
}
