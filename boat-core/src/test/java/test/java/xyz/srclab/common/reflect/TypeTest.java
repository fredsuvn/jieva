package test.java.xyz.srclab.common.reflect;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.collect.ListMap;
import xyz.srclab.common.collect.SetMap;
import xyz.srclab.common.logging.Logs;
import xyz.srclab.common.reflect.Reflects;
import xyz.srclab.common.reflect.TypeRef;
import xyz.srclab.common.reflect.Types;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

/**
 * @author sunqian
 */
public class TypeTest {

    @Test
    public void testRawClass() {
        Assert.assertEquals(
            Reflects.getRawClass(new TypeRef<String>() {
            }.getType()),
            String.class);
        Assert.assertEquals(
            Reflects.getRawClass(new TypeRef<List<String>>() {
            }.getType()),
            List.class
        );
        Assert.assertEquals(
            Reflects.getRawClass(new TypeRef<List<String>[][]>() {
            }.getType()),
            List[][].class
        );
        Assert.expectThrows(IllegalArgumentException.class, () ->
            Reflects.getRawClass(TypeUtils.wildcardType().withUpperBounds(Object.class).build()));
    }

    @Test
    public void testBound() {
        Assert.assertEquals(
            Reflects.getUpperBound(Types.wildcardType(new Type[]{String.class}, null)),
            String.class
        );
        Assert.assertEquals(
            Reflects.getUpperBound(Types.wildcardType(new Type[]{
                Types.wildcardType(new Type[]{String.class}, null)
            }, null)),
            String.class
        );
        Assert.assertEquals(
            Reflects.getUpperBound(Types.wildcardType(new Type[]{
                    Types.wildcardType(new Type[]{
                        new TypeRef<List<String>>() {
                        }.getType()
                    }, null)
                }, null)
            ),
            new TypeRef<List<String>>() {
            }.getType()
        );
        Assert.assertEquals(
            Reflects.getUpperBound(BoundClass.class.getTypeParameters()[0]),
            Object.class
        );
        Assert.assertEquals(
            Reflects.getUpperBound(BoundClass.class.getTypeParameters()[1]),
            String.class
        );
        Assert.assertEquals(
            Reflects.getUpperBound(BoundClass.class.getTypeParameters()[2]),
            String.class
        );
        Assert.assertEquals(
            Reflects.getUpperBound(BoundClass.class.getTypeParameters()[3]).toString(),
            "java.util.List<? extends T1>"
        );
        Assert.assertEquals(
            Reflects.getLowerBound(Types.wildcardType(null, new Type[]{String.class})),
            String.class
        );
        Assert.assertEquals(
            Reflects.getLowerBound(Types.wildcardType(null, new Type[]{
                Types.wildcardType(null, new Type[]{String.class})
            })),
            String.class
        );
        Assert.assertEquals(
            Reflects.getLowerBound(Types.wildcardType(null, new Type[]{
                    Types.wildcardType(null, new Type[]{
                        new TypeRef<List<String>>() {
                        }.getType()
                    })
                })
            ),
            new TypeRef<List<String>>() {
            }.getType()
        );
        Assert.assertNull(Reflects.getLowerBound(BoundClass.class.getTypeParameters()[0]));
        Assert.assertNull(Reflects.getLowerBound(BoundClass.class.getTypeParameters()[1]));
        Assert.assertNull(Reflects.getLowerBound(BoundClass.class.getTypeParameters()[2]));
        Assert.assertNull(Reflects.getLowerBound(BoundClass.class.getTypeParameters()[3]));
    }

    @Test
    public void testTypeArguments() {
        SetMap<String, String> expected = new SetMap<>(
            new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
            (key) -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)
        );

        Map<TypeVariable<?>, Type> f1Map = Reflects.getTypeArguments(F1.class);
        Logs.info("f1Map: " + f1Map);
        expected.addAll("C1T1", "java.lang.String");
        expected.addAll("C1T2", "java.util.List<? extends java.lang.String>");
        expected.addAll("I1T1", "java.lang.String");
        expected.addAll("I2T1", "java.util.List<? extends java.lang.String>");
        expected.addAll("I3T1", "java.lang.String");
        expected.addAll("S1T1", "java.lang.String");
        expected.addAll("I4T1", "java.util.List<? extends java.lang.String>");
        assertTypeArgumentsEquals(f1Map, expected);
        expected.clear();

        Map<TypeVariable<?>, Type> f2Map = Reflects.getTypeArguments(F2.class);
        Logs.info("f2Map: " + f2Map);
        expected.addAll("C1T1", "java.lang.String");
        expected.addAll("C1T2", "java.util.List<? extends java.lang.String>");
        expected.addAll("I1T1", "java.lang.String");
        expected.addAll("I2T1", "java.util.List<? extends java.lang.String>");
        expected.addAll("I3T1", "java.lang.String");
        expected.addAll("S1T1", "java.lang.String");
        expected.addAll("I4T1", "java.util.List<? extends java.lang.String>");
        assertTypeArgumentsEquals(f2Map, expected);
        expected.clear();

        Map<TypeVariable<?>, Type> s1GenericMap = Reflects.getTypeArguments(
            Types.parameterizedType(S1.class, S1.class.getTypeParameters()[0]));
        Logs.info("s1GenericMap: " + s1GenericMap);
        expected.addAll("C1T1", "S1T1");
        expected.addAll("C1T2", "java.util.List<? extends S1T1>");
        expected.addAll("I1T1", "S1T1");
        expected.addAll("I2T1", "java.util.List<? extends S1T1>");
        expected.addAll("I3T1", "S1T1");
        expected.addAll("S1T1", "S1T1");
        expected.addAll("I4T1", "java.util.List<? extends S1T1>");
        assertTypeArgumentsEquals(s1GenericMap, expected);
        expected.clear();

        Map<TypeVariable<?>, Type> s1Map = Reflects.getTypeArguments(
            Types.parameterizedType(S1.class, String.class));
        Logs.info("s1StringMap: " + s1Map);
        expected.addAll("C1T1", "java.lang.String");
        expected.addAll("C1T2", "java.util.List<? extends java.lang.String>");
        expected.addAll("I1T1", "java.lang.String");
        expected.addAll("I2T1", "java.util.List<? extends java.lang.String>");
        expected.addAll("I3T1", "java.lang.String");
        expected.addAll("S1T1", "java.lang.String");
        expected.addAll("I4T1", "java.util.List<? extends java.lang.String>");
        assertTypeArgumentsEquals(s1Map, expected);
        expected.clear();

        Type c1c1GenericTypeRef = new TypeRef<C1<Long, Double>.C1C1<String>>() {
        }.getType();
        Map<TypeVariable<?>, Type> c1c1Map = Reflects.getTypeArguments(c1c1GenericTypeRef);
        Logs.info("c1c1Map: " + c1c1Map);
        expected.addAll("C1T1", "java.lang.Long");
        expected.addAll("C1C1T1", "java.lang.String");
        expected.addAll("C1T2", "java.lang.Double");
        expected.addAll("I1T1", "java.lang.Long");
        expected.addAll("I2T1", "java.util.List<? extends java.lang.Long>");
        expected.addAll("I3T1", "java.lang.Double");
        assertTypeArgumentsEquals(c1c1Map, expected);
        expected.clear();
    }

    private void assertTypeArgumentsEquals(Map<TypeVariable<?>, Type> actual, SetMap<String, String> expected) {
        SetMap<String, String> actualSorted = new SetMap<>(
            new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
            (key) -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)
        );
        actual.forEach((k, v) -> {
            actualSorted.add(k.getTypeName(), v.getTypeName());
        });
        Assert.assertEquals(actualSorted, expected);
    }

    @Test
    public void testTypeSignature() {
        Type setMapType = new TypeRef<SetMap<String, String>>() {
        }.getType();
        Type mapType = Reflects.getTypeSignature(setMapType, Map.class);
        Logs.info("mapType: {}", mapType);
        Assert.assertEquals(
            mapType,
            new TypeRef<Map<String, Set<String>>>() {
            }.getType()
        );

        Type listMapType = new TypeRef<ListMap<String, String>>() {
        }.getType();
        mapType = Reflects.getTypeSignature(listMapType, Map.class);
        Logs.info("mapType: {}", mapType);
        Assert.assertEquals(
            mapType,
            new TypeRef<Map<String, List<String>>>() {
            }.getType()
        );
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
