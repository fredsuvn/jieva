package test.java.xyz.srclab.common.reflect;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.Types;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author sunqian
 */
public class TypesTest {

    @Test
    public void testTypes() {
        Assert.assertEquals(
            Types.parameterizedType(List.class, String.class),
            TypeUtils.parameterize(List.class, String.class)
        );
        Assert.assertEquals(
            Types.wildcardType(new Type[]{String.class}, new Type[]{String.class}),
            TypeUtils.wildcardType().withUpperBounds(String.class).withLowerBounds(String.class).build()
        );
        Assert.assertEquals(
            Types.genericArrayType(String.class),
            TypeUtils.genericArrayType(String.class)
        );
    }
}
