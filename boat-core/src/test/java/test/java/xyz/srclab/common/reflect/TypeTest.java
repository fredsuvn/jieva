package test.java.xyz.srclab.common.reflect;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.reflect.Reflects;
import xyz.srclab.common.reflect.TypeRef;
import xyz.srclab.common.reflect.Types;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;

/**
 * @author sunqian
 */
public class TypeTest {

    private static final TestLogger testLogger = TestLogger.DEFAULT;

    @Test
    public void testTypeGenerator() {
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

    @Test
    public void testRawClass() {
        Assert.assertEquals(
                Reflects.rawClass(new TypeRef<String>() {
                }.type()),
                String.class);
        Assert.assertEquals(
                Reflects.rawClass(new TypeRef<List<String>>() {
                }.type()),
                List.class
        );
        Assert.expectThrows(IllegalArgumentException.class, () ->
                Reflects.rawClass(TypeUtils.wildcardType().withUpperBounds(Object.class).build()));
    }

    @Test
    public void testBoundClass() {
        Assert.assertEquals(
                Reflects.rawOrUpperClass(String.class),
                String.class
        );
        Assert.assertEquals(
                Reflects.rawOrUpperClass(new TypeRef<List<String>>() {
                }.type()),
                List.class
        );
        Assert.assertEquals(
                Reflects.upperClass(Types.wildcardType(new Type[]{String.class}, null)),
                String.class
        );
        Assert.assertEquals(
                Reflects.upperClass(Types.wildcardType(new Type[]{
                        Types.wildcardType(new Type[]{String.class}, null)
                }, null)),
                String.class
        );
        Assert.assertEquals(
                Reflects.upperClass(Types.wildcardType(new Type[]{
                                Types.wildcardType(new Type[]{
                                        new TypeRef<List<String>>() {
                                        }.type()
                                }, null)
                        }, null)
                ),
                List.class
        );
        Assert.assertEquals(
                Reflects.upperClass(BoundClass.class.getTypeParameters()[0]),
                Object.class
        );
        Assert.assertEquals(
                Reflects.upperClass(BoundClass.class.getTypeParameters()[1]),
                String.class
        );
        Assert.assertEquals(
                Reflects.upperClass(BoundClass.class.getTypeParameters()[2]),
                String.class
        );
        Assert.assertEquals(
                Reflects.upperClass(BoundClass.class.getTypeParameters()[3]),
                List.class
        );
        Assert.assertEquals(
                Reflects.rawOrUpperClass(TypeUtils.genericArrayType(String.class)),
                String[].class
        );

        Assert.assertEquals(
                Reflects.rawOrLowerClass(String.class),
                String.class
        );
        Assert.assertEquals(
                Reflects.rawOrLowerClass(new TypeRef<List<String>>() {
                }.type()),
                List.class
        );
        Assert.assertEquals(
                Reflects.lowerClass(Types.wildcardType(null, new Type[]{String.class})),
                String.class
        );
        Assert.assertEquals(
                Reflects.lowerClass(Types.wildcardType(null, new Type[]{
                        Types.wildcardType(null, new Type[]{String.class})
                })),
                String.class
        );
        Assert.assertEquals(
                Reflects.lowerClass(Types.wildcardType(null, new Type[]{
                                Types.wildcardType(null, new Type[]{
                                        new TypeRef<List<String>>() {
                                        }.type()
                                })
                        })
                ),
                List.class
        );
        Assert.assertNull(Reflects.lowerClass(BoundClass.class.getTypeParameters()[0]));
        Assert.assertEquals(
                Reflects.lowerClass(BoundClass.class.getTypeParameters()[1]),
                String.class
        );
        Assert.assertEquals(
                Reflects.lowerClass(BoundClass.class.getTypeParameters()[2]),
                String.class
        );
        Assert.assertNull(Reflects.lowerClass(BoundClass.class.getTypeParameters()[3]));
        Assert.assertEquals(
                Reflects.rawOrLowerClass(TypeUtils.genericArrayType(String.class)),
                String[].class
        );
        Assert.assertEquals(
                Reflects.rawOrLowerClass(TypeUtils.genericArrayType(TypeUtils.genericArrayType(String.class))),
                String[][].class
        );
    }

    @Test
    public void testBoundType() {
        Assert.assertEquals(
                Reflects.thisOrUpperBound(String.class),
                String.class
        );
        Assert.assertEquals(
                Reflects.thisOrUpperBound(new TypeRef<List<String>>() {
                }.type()),
                new TypeRef<List<String>>() {
                }.type()
        );
        Assert.assertEquals(
                Reflects.upperBound(Types.wildcardType(new Type[]{String.class}, null)),
                String.class
        );
        Assert.assertEquals(
                Reflects.upperBound(Types.wildcardType(new Type[]{
                        Types.wildcardType(new Type[]{String.class}, null)
                }, null)),
                String.class
        );
        Assert.assertEquals(
                Reflects.upperBound(Types.wildcardType(new Type[]{
                                Types.wildcardType(new Type[]{
                                        new TypeRef<List<String>>() {
                                        }.type()
                                }, null)
                        }, null)
                ),
                new TypeRef<List<String>>() {
                }.type()
        );
        Assert.assertEquals(
                Reflects.upperBound(BoundClass.class.getTypeParameters()[0]),
                Object.class
        );
        Assert.assertEquals(
                Reflects.upperBound(BoundClass.class.getTypeParameters()[1]),
                String.class
        );
        Assert.assertEquals(
                Reflects.upperBound(BoundClass.class.getTypeParameters()[2]),
                String.class
        );
        Assert.assertEquals(
                Reflects.upperBound(BoundClass.class.getTypeParameters()[3]).toString(),
                "java.util.List<? extends T1>"
        );
        Assert.assertEquals(
                Reflects.thisOrUpperBound(TypeUtils.genericArrayType(String.class)),
                TypeUtils.genericArrayType(String.class)
        );

        Assert.assertEquals(
                Reflects.thisOrLowerBound(String.class),
                String.class
        );
        Assert.assertEquals(
                Reflects.thisOrLowerBound(new TypeRef<List<String>>() {
                }.type()),
                new TypeRef<List<String>>() {
                }.type()
        );
        Assert.assertEquals(
                Reflects.lowerBound(Types.wildcardType(null, new Type[]{String.class})),
                String.class
        );
        Assert.assertEquals(
                Reflects.lowerBound(Types.wildcardType(null, new Type[]{
                        Types.wildcardType(null, new Type[]{String.class})
                })),
                String.class
        );
        Assert.assertEquals(
                Reflects.lowerBound(Types.wildcardType(null, new Type[]{
                                Types.wildcardType(null, new Type[]{
                                        new TypeRef<List<String>>() {
                                        }.type()
                                })
                        })
                ),
                new TypeRef<List<String>>() {
                }.type()
        );
        Assert.assertNull(Reflects.lowerBound(BoundClass.class.getTypeParameters()[0]));
        Assert.assertEquals(
                Reflects.lowerBound(BoundClass.class.getTypeParameters()[1]),
                String.class
        );
        Assert.assertEquals(
                Reflects.lowerBound(BoundClass.class.getTypeParameters()[2]),
                String.class
        );
        Assert.assertNull(Reflects.lowerBound(BoundClass.class.getTypeParameters()[3]));
        Assert.assertEquals(
                Reflects.thisOrLowerBound(TypeUtils.genericArrayType(String.class)),
                TypeUtils.genericArrayType(String.class)
        );
    }

    @Test
    public void testTypeArguments() {
        Map<TypeVariable<?>, Type> f1Map = Reflects.typeArguments(F1.class);
        testLogger.log("f1Map: " + f1Map);
        Assert.assertEquals(
                f1Map.toString(),
                "{S1T1=class java.lang.String, C1T1=class java.lang.String, C1T2=java.util.List<? extends java.lang.String>, I1T1=class java.lang.String, I2T1=java.util.List<? extends java.lang.String>, I3T1=class java.lang.String, I4T1=java.util.List<? extends java.lang.String>}"
        );


        Map<TypeVariable<?>, Type> f1c1Map = Reflects.typeArguments(F1.class, C1.class);
        testLogger.log("f1c1Map: " + f1c1Map);
        Assert.assertEquals(
                f1c1Map.toString(),
                "{S1T1=class java.lang.String, C1T1=class java.lang.String, C1T2=java.util.List<? extends java.lang.String>}"
        );


        Map<TypeVariable<?>, Type> f2Map = Reflects.typeArguments(F2.class);
        testLogger.log("f2Map: " + f2Map);
        Assert.assertEquals(
                f2Map.toString(),
                "{S1T1=class java.lang.String, C1T1=class java.lang.String, C1T2=java.util.List<? extends java.lang.String>, I1T1=class java.lang.String, I2T1=java.util.List<? extends java.lang.String>, I3T1=class java.lang.String, I4T1=java.util.List<? extends java.lang.String>}"
        );

        Map<TypeVariable<?>, Type> f2i3Map = Reflects.typeArguments(F2.class, I3.class);
        testLogger.log("f2i3Map: " + f2i3Map);
        Assert.assertEquals(
                f2i3Map.toString(),
                "{S1T1=class java.lang.String, C1T1=class java.lang.String, C1T2=java.util.List<? extends java.lang.String>, I1T1=class java.lang.String, I2T1=java.util.List<? extends java.lang.String>, I3T1=class java.lang.String}"
        );


        Map<TypeVariable<?>, Type> s1GenericMap = Reflects.typeArguments(
                Types.parameterizedType(S1.class, S1.class.getTypeParameters()[0]));
        testLogger.log("s1GenericMap: " + s1GenericMap);
        Assert.assertEquals(
                s1GenericMap.toString(),
                "{S1T1=S1T1, C1T1=S1T1, C1T2=java.util.List<? extends S1T1>, I1T1=S1T1, I2T1=java.util.List<? extends S1T1>, I3T1=S1T1, I4T1=java.util.List<? extends S1T1>}"
        );

        Map<TypeVariable<?>, Type> s1i1GenericMap = Reflects.typeArguments(
                Types.parameterizedType(S1.class, S1.class.getTypeParameters()[0]),
                I2.class
        );
        testLogger.log("s1i1GenericMap: " + s1i1GenericMap);
        Assert.assertEquals(
                s1i1GenericMap.toString(),
                "{S1T1=S1T1, C1T1=S1T1, C1T2=java.util.List<? extends S1T1>, I1T1=S1T1, I2T1=java.util.List<? extends S1T1>}"
        );

        Map<TypeVariable<?>, Type> s1Map = Reflects.typeArguments(
                Types.parameterizedType(S1.class, String.class));
        testLogger.log("s1Map: " + s1Map);
        Assert.assertEquals(
                s1Map.toString(),
                "{S1T1=class java.lang.String, C1T1=class java.lang.String, C1T2=java.util.List<? extends java.lang.String>, I1T1=class java.lang.String, I2T1=java.util.List<? extends java.lang.String>, I3T1=class java.lang.String, I4T1=java.util.List<? extends java.lang.String>}"
        );

        Type c1c1GenericTypeRef = new TypeRef<C1<Long, Double>.C1C1<String>>() {
        }.type();
        Map<TypeVariable<?>, Type> c1c1GenericMap = Reflects.typeArguments(c1c1GenericTypeRef);
        testLogger.log("c1c1GenericMap: " + c1c1GenericMap);
        Assert.assertEquals(
                c1c1GenericMap.toString(),
                "{C1T1=class java.lang.Long, C1T2=class java.lang.Double, I1T1=class java.lang.Long, I2T1=java.util.List<? extends java.lang.Long>, C1C1T1=class java.lang.String, I3T1=class java.lang.Double}"
        );
    }

    @Test
    public void testGenericSignature() {
        testLogger.log(Reflects.generalize(F2.class, I4.class));

        Reflects.genericInterface(String.class);
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
