package test.java.xyz.srclab.common.reflect;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.TypeKit;
import xyz.srclab.common.reflect.TypeRef;

import java.util.List;

/**
 * @author sunqian
 */
public class TypeKitTest {

    @Test
    public void testBoundClass() {
        Assert.assertEquals(
                TypeKit.rawClass(new TypeRef<String>() {
                }.type()),
                String.class);
        Assert.assertEquals(
                TypeKit.rawClass(new TypeRef<List<String>>() {
                }.type()),
                List.class
        );
        Assert.expectThrows(IllegalArgumentException.class, () ->
                TypeKit.rawClass(TypeUtils.wildcardType().withUpperBounds(Object.class).build()));
    }

    @Test
    public void testBoundType() {
        Assert.assertEquals(
                TypeKit.upperBound(String.class),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperBound(new TypeRef<List<String>>() {
                }.type()),
                new TypeRef<List<String>>() {
                }.type()
        );
        Assert.assertEquals(
                TypeKit.upperBound(TypeUtils.wildcardType().withUpperBounds(String.class).build()),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperBound(TypeUtils.wildcardType().withUpperBounds(
                        TypeKit.upperBound(TypeUtils.wildcardType().withUpperBounds(String.class).build())).build()),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperBound(C1.class.getTypeParameters()[2]),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperBound(C1.class.getTypeParameters()[0]),
                Object.class
        );
        Assert.assertEquals(
                TypeKit.upperBound(TypeUtils.genericArrayType(String.class)),
                TypeUtils.genericArrayType(String.class)
        );
    }

    public class C1<CT1, CT2 extends String, CT3 extends CT2> {
    }
}
