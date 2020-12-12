package test.java.xyz.srclab.common.reflect;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.TypeKit;
import xyz.srclab.common.reflect.TypeRef;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author sunqian
 */
public class TypeKitTest {

    private static final TestLogger testLogger = TestLogger.DEFAULT;

    @Test
    public void testTypeGenerator() {
        Assert.assertEquals(
                TypeKit.parameterizedType(List.class, new Type[]{String.class}),
                TypeUtils.parameterize(List.class, String.class)
        );
        Assert.assertEquals(
                TypeKit.wildcardType(new Type[]{String.class}, new Type[]{String.class}),
                TypeUtils.wildcardType().withUpperBounds(String.class).withLowerBounds(String.class).build()
        );
        Assert.assertEquals(
                TypeKit.genericArrayType(String.class),
                TypeUtils.genericArrayType(String.class)
        );
    }

    @Test
    public void testRawClass() {
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
    public void testBoundClass() {
        Assert.assertEquals(
                TypeKit.upperClass(String.class),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperClass(new TypeRef<List<String>>() {
                }.type()),
                List.class
        );
        Assert.assertEquals(
                TypeKit.upperClass(TypeKit.wildcardType(new Type[]{String.class}, null)),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperClass(TypeKit.wildcardType(new Type[]{
                        TypeKit.wildcardType(new Type[]{String.class}, null)
                }, null)),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperClass(TypeKit.wildcardType(new Type[]{
                                TypeKit.wildcardType(new Type[]{
                                        new TypeRef<List<String>>() {
                                        }.type()
                                }, null)
                        }, null)
                ),
                List.class
        );
        Assert.assertEquals(
                TypeKit.upperClass(C1.class.getTypeParameters()[0]),
                Object.class
        );
        Assert.assertEquals(
                TypeKit.upperClass(C1.class.getTypeParameters()[1]),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperClass(C1.class.getTypeParameters()[2]),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperClass(C1.class.getTypeParameters()[3]),
                List.class
        );
        Assert.assertEquals(
                TypeKit.upperClass(TypeUtils.genericArrayType(String.class)),
                Object.class
        );

        Assert.assertEquals(
                TypeKit.lowerClass(String.class),
                String.class
        );
        Assert.assertEquals(
                TypeKit.lowerClass(new TypeRef<List<String>>() {
                }.type()),
                List.class
        );
        Assert.assertEquals(
                TypeKit.lowerClass(TypeKit.wildcardType(null, new Type[]{String.class})),
                String.class
        );
        Assert.assertEquals(
                TypeKit.lowerClass(TypeKit.wildcardType(null, new Type[]{
                        TypeKit.wildcardType(null, new Type[]{String.class})
                })),
                String.class
        );
        Assert.assertEquals(
                TypeKit.lowerClass(TypeKit.wildcardType(null, new Type[]{
                                TypeKit.wildcardType(null, new Type[]{
                                        new TypeRef<List<String>>() {
                                        }.type()
                                })
                        })
                ),
                List.class
        );
        Assert.assertNull(TypeKit.lowerClass(C1.class.getTypeParameters()[0]));
        Assert.assertEquals(
                TypeKit.lowerClass(C1.class.getTypeParameters()[1]),
                String.class
        );
        Assert.assertEquals(
                TypeKit.lowerClass(C1.class.getTypeParameters()[2]),
                String.class
        );
        Assert.assertNull(TypeKit.lowerClass(C1.class.getTypeParameters()[3]));
        Assert.expectThrows(IllegalArgumentException.class, () ->
                TypeKit.lowerClass(TypeUtils.genericArrayType(String.class)));
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
                TypeKit.upperBound(TypeKit.wildcardType(new Type[]{String.class}, null)),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperBound(TypeKit.wildcardType(new Type[]{
                        TypeKit.wildcardType(new Type[]{String.class}, null)
                }, null)),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperBound(TypeKit.wildcardType(new Type[]{
                                TypeKit.wildcardType(new Type[]{
                                        new TypeRef<List<String>>() {
                                        }.type()
                                }, null)
                        }, null)
                ),
                new TypeRef<List<String>>() {
                }.type()
        );
        Assert.assertEquals(
                TypeKit.upperBound(C1.class.getTypeParameters()[0]),
                Object.class
        );
        Assert.assertEquals(
                TypeKit.upperBound(C1.class.getTypeParameters()[1]),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperBound(C1.class.getTypeParameters()[2]),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperBound(C1.class.getTypeParameters()[3]).toString(),
                "java.util.List<? extends CT1>"
        );
        Assert.assertEquals(
                TypeKit.upperBound(TypeUtils.genericArrayType(String.class)),
                TypeUtils.genericArrayType(String.class)
        );

        Assert.assertEquals(
                TypeKit.lowerBound(String.class),
                String.class
        );
        Assert.assertEquals(
                TypeKit.lowerBound(new TypeRef<List<String>>() {
                }.type()),
                new TypeRef<List<String>>() {
                }.type()
        );
        Assert.assertEquals(
                TypeKit.lowerBound(TypeKit.wildcardType(null, new Type[]{String.class})),
                String.class
        );
        Assert.assertEquals(
                TypeKit.lowerBound(TypeKit.wildcardType(null, new Type[]{
                        TypeKit.wildcardType(null, new Type[]{String.class})
                })),
                String.class
        );
        Assert.assertEquals(
                TypeKit.lowerBound(TypeKit.wildcardType(null, new Type[]{
                                TypeKit.wildcardType(null, new Type[]{
                                        new TypeRef<List<String>>() {
                                        }.type()
                                })
                        })
                ),
                new TypeRef<List<String>>() {
                }.type()
        );
        Assert.assertNull(TypeKit.lowerBound(C1.class.getTypeParameters()[0]));
        Assert.assertEquals(
                TypeKit.lowerBound(C1.class.getTypeParameters()[1]),
                String.class
        );
        Assert.assertEquals(
                TypeKit.lowerBound(C1.class.getTypeParameters()[2]),
                String.class
        );
        Assert.assertNull(TypeKit.lowerBound(C1.class.getTypeParameters()[3]));
        Assert.assertEquals(
                TypeKit.lowerBound(TypeUtils.genericArrayType(String.class)),
                TypeUtils.genericArrayType(String.class)
        );
    }

    public class C1<
            CT1,
            CT2 extends String,
            CT3 extends CT2,
            CT4 extends List<? extends CT1>
            > {
    }
}
