package test.java.xyz.srclab.common.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.BMember;

public class MemberTest {

    @Test
    public void testAccessibleFor() throws Exception {
        Assert.assertTrue(BMember.isAccessibleFor(
            ReflectClass.class.getDeclaredField("protectedField"),
            SubReflectClass.class
        ));
        Assert.assertFalse(BMember.isAccessibleFor(
            ReflectClass.class.getDeclaredField("privateField"),
            SubReflectClass.class
        ));
    }
}
