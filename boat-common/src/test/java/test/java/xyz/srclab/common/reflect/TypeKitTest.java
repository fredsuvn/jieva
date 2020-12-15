package test.java.xyz.srclab.common.reflect;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.TypeKit;
import xyz.srclab.common.reflect.TypeRef;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

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
        Map<TypeVariable<?>, Type> f1Map = TypeKit.findTypeArguments(F1.class);
        testLogger.log("f1Map: " + f1Map);
        Assert.assertEquals(
                f1Map.toString(),
                "{S1T1=class java.lang.String, C1T1=class java.lang.String, C1T2=java.util.List<? extends java.lang.String>, I1T1=class java.lang.String, I2T1=java.util.List<? extends java.lang.String>, I3T1=class java.lang.String, I4T1=java.util.List<? extends java.lang.String>}"
        );


        Map<TypeVariable<?>, Type> f1c1Map = TypeKit.findTypeArguments(F1.class, C1.class);
        testLogger.log("f1c1Map: " + f1c1Map);
        Assert.assertEquals(
                f1c1Map.toString(),
                "{S1T1=class java.lang.String, C1T1=class java.lang.String, C1T2=java.util.List<? extends java.lang.String>}"
        );


        Map<TypeVariable<?>, Type> f2Map = TypeKit.findTypeArguments(F2.class);
        testLogger.log("f2Map: " + f2Map);
        Assert.assertEquals(
                f2Map.toString(),
                "{S1T1=class java.lang.String, C1T1=class java.lang.String, C1T2=java.util.List<? extends java.lang.String>, I1T1=class java.lang.String, I2T1=java.util.List<? extends java.lang.String>, I3T1=class java.lang.String, I4T1=java.util.List<? extends java.lang.String>}"
        );

        Map<TypeVariable<?>, Type> f2i3Map = TypeKit.findTypeArguments(F2.class, I3.class);
        testLogger.log("f2i3Map: " + f2i3Map);
        Assert.assertEquals(
                f2i3Map.toString(),
                "{S1T1=class java.lang.String, C1T1=class java.lang.String, C1T2=java.util.List<? extends java.lang.String>, I1T1=class java.lang.String, I2T1=java.util.List<? extends java.lang.String>, I3T1=class java.lang.String}"
        );


        Map<TypeVariable<?>, Type> s1GenericMap = TypeKit.findTypeArguments(
                TypeKit.parameterizedType(S1.class, new Type[]{S1.class.getTypeParameters()[0]}));
        testLogger.log("s1GenericMap: " + s1GenericMap);
        Assert.assertEquals(
                s1GenericMap.toString(),
                "{S1T1=S1T1, C1T1=S1T1, C1T2=java.util.List<? extends S1T1>, I1T1=S1T1, I2T1=java.util.List<? extends S1T1>, I3T1=S1T1, I4T1=java.util.List<? extends S1T1>}"
        );

        Map<TypeVariable<?>, Type> s1i1GenericMap = TypeKit.findTypeArguments(
                TypeKit.parameterizedType(S1.class, new Type[]{S1.class.getTypeParameters()[0]}),
                I2.class
        );
        testLogger.log("s1i1GenericMap: " + s1i1GenericMap);
        Assert.assertEquals(
                s1i1GenericMap.toString(),
                "{S1T1=S1T1, C1T1=S1T1, C1T2=java.util.List<? extends S1T1>, I1T1=S1T1, I2T1=java.util.List<? extends S1T1>}"
        );

        Map<TypeVariable<?>, Type> s1Map = TypeKit.findTypeArguments(
                TypeKit.parameterizedType(S1.class, new Type[]{String.class}));
        testLogger.log("s1Map: " + s1Map);
        Assert.assertEquals(
                s1Map.toString(),
                "{S1T1=class java.lang.String, C1T1=class java.lang.String, C1T2=java.util.List<? extends java.lang.String>, I1T1=class java.lang.String, I2T1=java.util.List<? extends java.lang.String>, I3T1=class java.lang.String, I4T1=java.util.List<? extends java.lang.String>}"
        );

        Type c1c1GenericTypeRef = new TypeRef<C1<Long, Double>.C1C1<String>>() {
        }.type();
        Map<TypeVariable<?>, Type> c1c1GenericMap = TypeKit.findTypeArguments(c1c1GenericTypeRef);
        testLogger.log("c1c1GenericMap: " + c1c1GenericMap);
        Assert.assertEquals(
                c1c1GenericMap.toString(),
                "{C1T1=class java.lang.Long, C1T2=class java.lang.Double, I1T1=class java.lang.Long, I2T1=java.util.List<? extends java.lang.Long>, C1C1T1=class java.lang.String, I3T1=class java.lang.Double}"
        );
    }

    @Test
    public void testGenericSignature() {
        testLogger.log(TypeKit.genericSignature(F2.class, I4.class));

        TypeKit.findGenericInterface(String.class);
    }

    public static class BoundClass<
            T1,
            T2 extends String,
            T3 extends T2,
            T4 extends List<? extends T1>
            > {
    }

    public interface I1<I1T1> {
    }

    public interface I2<I2T1> {
    }

    public interface I3<I3T1> {
    }

    public interface I4<I4T1> {
    }

    public static class C1<C1T1, C1T2> implements I1<C1T1>, I2<List<? extends C1T1>> {
        class C1C1<C1C1T1> implements I3<C1T2> {
        }
    }

    public static class S1<S1T1> extends C1<S1T1, List<? extends S1T1>> implements I3<S1T1>, I4<List<? extends S1T1>> {
    }

    public static class F1 extends S1<String> {
    }

    public static class F2 extends S1<String> implements I2<List<? extends String>>, I3<String> {
    }
}
