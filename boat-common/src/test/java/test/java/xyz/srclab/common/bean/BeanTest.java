package test.java.xyz.srclab.common.bean;

import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Anys;
import xyz.srclab.common.bean.BeanResolver;
import xyz.srclab.common.bean.BeanType;
import xyz.srclab.common.bean.Beans;
import xyz.srclab.common.bean.PropertyType;
import xyz.srclab.common.reflect.Reflects;
import xyz.srclab.common.reflect.TypeRef;
import xyz.srclab.common.test.TestLogger;

import java.lang.reflect.Type;
import java.util.*;

/**
 * @author sunqian
 */
public class BeanTest {

    private static final TestLogger testLogger = TestLogger.DEFAULT;

    @Test
    public void testMap() {
        SimpleBean simpleBean = new SimpleBean();
        simpleBean.setP1("123");
        simpleBean.setP2(6);
        simpleBean.setP3(Arrays.asList("1", "2", "3"));
        Map<String, Object> simpleMap = Beans.asMap(simpleBean);
        Assert.assertEquals(simpleMap.get("p1"), "123");
        Assert.assertEquals(simpleMap.get("p2"), 6);
        Assert.assertEquals(simpleMap.get("p3"), Arrays.asList("1", "2", "3"));
        simpleMap.put("p1", "555");
        Assert.assertEquals(simpleBean.getP1(), "555");
        Assert.expectThrows(UnsupportedOperationException.class, () -> simpleMap.put("p4", "p4"));

        BeanResolver.CopyOptions copyOptions = BeanResolver.CopyOptions.DEFAULT
                .withTypes(SimpleBean.class, Reflects.parameterizedType(Map.class, String.class, int.class))
                .withNameFilter(n -> "p1".equals(n) || "p2".equals(n));
        Map<String, Integer> siMap = Anys.as(Beans.asMap(simpleBean, copyOptions));
        testLogger.log("siMap: {}", siMap);
        Assert.assertEquals(siMap.get("p1"), (Integer) 555);
        Assert.assertEquals(siMap.get("p2"), (Integer) 6);
        Assert.assertEquals(siMap.size(), 2);
    }

    @Test
    public void testCopyProperties() {
        SimpleBean simpleBean = new SimpleBean();
        simpleBean.setP1("123");
        simpleBean.setP2(6);
        simpleBean.setP3(Arrays.asList("1", "2", "3"));
        Map<String, Object> simpleMap = new HashMap<>();
        simpleMap.put("p1", null);
        simpleMap.put("p2", null);
        simpleMap.put("p3", null);
        Beans.copyProperties(simpleBean, simpleMap);
        Assert.assertEquals(simpleMap.get("p1"), "123");
        Assert.assertEquals(simpleMap.get("p2"), 6);
        Assert.assertEquals(simpleMap.get("p3"), Arrays.asList("1", "2", "3"));
        Assert.assertEquals(simpleMap.size(), 3);

    }

    @Test
    public void testSimpleBean() {
        SimpleBean a = new SimpleBean();
        a.setP1("123");
        a.setP2(6);
        a.setP3(Arrays.asList("1", "2", "3"));

        SimpleBean b = new SimpleBean();
        Beans.copyProperties(a, b);
        Assert.assertEquals(b.getP1(), a.getP1());
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        a.setP1(null);
        Beans.copyProperties(a, b);
        Assert.assertEquals(b.getP1(), a.getP1());
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        a.setP1(null);
        b.setP1("234");
        Beans.copyPropertiesIgnoreNull(a, b);
        Assert.assertEquals(b.getP1(), "234");
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        a.setP1("111");
        b.setP1("567");
        Beans.copyProperties(a, b, new BeanResolver.CopyOptions() {
            @NotNull
            public Function1<Object, Boolean> getNameFilter() {
                return name -> !name.equals("p1");
            }
        });
        Assert.assertEquals(b.getP1(), "567");
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("p1", "123");
        map.put("p2", 6);
        map.put("p3", Arrays.asList("1", "2", "3"));
        map.put("class", SimpleBean.class);
        a.setP1("123");
    }

    @Test
    public void testGenericBeanASchema() {
        GenericBeanA a = new GenericBeanA();
        BeanType aSchema = Beans.resolve(a.getClass());
        Map<String, PropertyType> aPropertySchemas = aSchema.properties();
        System.out.println(aPropertySchemas);
        Assert.assertEquals(aPropertySchemas.get("a1").type(), String.class);
        Assert.assertEquals(aPropertySchemas.get("a2").type(), new TypeRef<List<String>>() {
        }.type());
        Assert.assertEquals(aPropertySchemas.get("i1").type(), Integer.class);
        Assert.assertEquals(aPropertySchemas.get("i2").type(), new TypeRef<List<Integer>>() {
        }.type());
        Assert.assertEquals(aPropertySchemas.get("class").type(), new TypeRef<Class<?>>() {
        }.type());
    }

    @Test
    public void testGenericBeanSchema() {
        Type gType = new TypeRef<GenericBean<String, ?, Iterable<? extends String>>>() {
        }.type();
        BeanType gSchema = Beans.resolve(gType);
        Map<String, PropertyType> gPropertySchemas = gSchema.properties();
        System.out.println(gPropertySchemas);
        //Assert.assertEquals(gPropertySchemas.get("a1").genericType(), String.class);
        //Assert.assertEquals(gPropertySchemas.get("a2").genericType(), new TypeRef<List<? extends ?>>() {
        //}.type());
        //Assert.assertEquals(gPropertySchemas.get("i1").genericType(), Integer.class);
        //Assert.assertEquals(gPropertySchemas.get("i2").genericType(), new TypeRef<List<Integer>>() {
        //}.type());
        //Assert.assertEquals(gPropertySchemas.get("class").genericType(), new TypeRef<Class<?>>() {
        //}.type());
    }

    @Test
    public void testSubGenericBeanSchema() {
        SubGenericBean s = new SubGenericBean();
        BeanType sSchema = Beans.resolve(s.getClass());
        Map<String, PropertyType> sPropertySchemas = sSchema.properties();
        System.out.println(sPropertySchemas);

        Reflects.rawClass(String.class);
    }
}
