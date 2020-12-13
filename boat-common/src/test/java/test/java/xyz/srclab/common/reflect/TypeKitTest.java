package test.java.xyz.srclab.common.reflect;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.TypeKit;
import xyz.srclab.common.reflect.TypeRef;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Type;
import java.math.BigDecimal;
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
                TypeKit.upperClass(BoundClass.class.getTypeParameters()[0]),
                Object.class
        );
        Assert.assertEquals(
                TypeKit.upperClass(BoundClass.class.getTypeParameters()[1]),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperClass(BoundClass.class.getTypeParameters()[2]),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperClass(BoundClass.class.getTypeParameters()[3]),
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
        Assert.assertNull(TypeKit.lowerClass(BoundClass.class.getTypeParameters()[0]));
        Assert.assertEquals(
                TypeKit.lowerClass(BoundClass.class.getTypeParameters()[1]),
                String.class
        );
        Assert.assertEquals(
                TypeKit.lowerClass(BoundClass.class.getTypeParameters()[2]),
                String.class
        );
        Assert.assertNull(TypeKit.lowerClass(BoundClass.class.getTypeParameters()[3]));
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
                TypeKit.upperBound(BoundClass.class.getTypeParameters()[0]),
                Object.class
        );
        Assert.assertEquals(
                TypeKit.upperBound(BoundClass.class.getTypeParameters()[1]),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperBound(BoundClass.class.getTypeParameters()[2]),
                String.class
        );
        Assert.assertEquals(
                TypeKit.upperBound(BoundClass.class.getTypeParameters()[3]).toString(),
                "java.util.List<? extends T1>"
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
        Assert.assertNull(TypeKit.lowerBound(BoundClass.class.getTypeParameters()[0]));
        Assert.assertEquals(
                TypeKit.lowerBound(BoundClass.class.getTypeParameters()[1]),
                String.class
        );
        Assert.assertEquals(
                TypeKit.lowerBound(BoundClass.class.getTypeParameters()[2]),
                String.class
        );
        Assert.assertNull(TypeKit.lowerBound(BoundClass.class.getTypeParameters()[3]));
        Assert.assertEquals(
                TypeKit.lowerBound(TypeUtils.genericArrayType(String.class)),
                TypeUtils.genericArrayType(String.class)
        );
    }

    @Test
    public void testTypeArguments() {
        testLogger.log(TypeKit.findTypeArguments(F.class));
        Type sscType = new TypeRef<SSC<BigDecimal>>() {}.type();
        testLogger.log(TypeKit.findTypeArguments(sscType));
        testLogger.log(TypeUtils.getTypeArguments(sscType, Object.class));
        testLogger.log(TypeKit.findTypeArguments(G.class));
        testLogger.log(TypeKit.findTypeArguments(G.class, F.FI.class));
        testLogger.log(TypeUtils.getTypeArguments(G.class, F.FI.class));

        Type sscs = new TypeRef<SSC<? extends String>.S>() {}.type();
        testLogger.log(TypeKit.findTypeArguments(sscs, I1.class));
        testLogger.log(TypeUtils.getTypeArguments(sscs, I1.class));
    }

    public static class BoundClass<
            T1,
            T2 extends String,
            T3 extends T2,
            T4 extends List<? extends T1>
            > {
    }

    public interface I1<I1T1> {}

    public interface I2<I2T1> {}

    public static class C<CT1, CT2> implements I1<CT2>, I2<String> {}

    public static class SC<SCT1, SCT2> extends C<Long, SCT2> implements I1<SCT2>, I2<String> {}

    public static class SSC<SSCT2> extends SC<SSCT2, Long> {

        class S implements I1<SSCT2> {}
    }

    public static class F extends SSC<BigDecimal> {
        interface FI<FI> {}
    }

    public static class G implements F.FI<String> {}
}
