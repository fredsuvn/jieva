package test.java.xyz.srclab.common.bean;

import kotlin.jvm.functions.Function1;
import org.apache.commons.beanutils.BeanUtils;
import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.bean.BeanKit;
import xyz.srclab.common.bean.BeanResolver;
import xyz.srclab.common.bean.BeanSchema;
import xyz.srclab.common.bean.PropertySchema;
import xyz.srclab.common.reflect.TypeKit;
import xyz.srclab.common.reflect.TypeRef;
import xyz.srclab.common.test.TestTask;
import xyz.srclab.common.test.Tester;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sunqian
 */
public class BeanTest {

    @Test
    public void testSimpleBean() {
        SimpleBean a = new SimpleBean();
        a.setP1("123");
        a.setP2(6);
        a.setP3(Arrays.asList("1", "2", "3"));

        SimpleBean b = new SimpleBean();
        BeanKit.copyProperties(a, b);
        Assert.assertEquals(b.getP1(), a.getP1());
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        a.setP1(null);
        BeanKit.copyProperties(a, b);
        Assert.assertEquals(b.getP1(), a.getP1());
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        a.setP1(null);
        b.setP1("234");
        BeanKit.copyPropertiesIgnoreNull(a, b);
        Assert.assertEquals(b.getP1(), "234");
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());

        a.setP1("111");
        b.setP1("567");
        BeanKit.copyProperties(a, b, new BeanResolver.CopyOptions() {
            @NotNull
            @Override
            public Function1<Object, Boolean> propertyNameFilter() {
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
        Map<String, Object> aMap = BeanKit.asMap(a);
        Assert.assertEquals(aMap, map);
        aMap.put("p1", "555");
        Assert.assertEquals(a.getP1(), "555");
        Assert.expectThrows(UnsupportedOperationException.class, () -> aMap.put("p4", "p4"));
    }

    /*
     * Task BeanKit was accomplished, cost: PT1M59.624S
     * Task Beanutils was accomplished, cost: PT13M2.245S
     * All tasks were accomplished, await cost: PT13M2.248S, total cost: PT15M1.869S, average cost: PT7M30.9345S
     */
    @Test(enabled = false)
    public void testPerformance() {
        PerformanceBean a = new PerformanceBean();
        a.setS1("s1");
        a.setS2("s2");
        a.setS3("s3");
        a.setS4("s4");
        a.setS5("s5");
        a.setS6("s6");
        a.setS7("s7");
        a.setS8("s8");
        a.setI1(1);
        a.setI2(2);
        a.setI3(3);
        a.setI4(4);
        a.setI5(5);
        a.setI6(6);
        a.setI7(7);
        a.setI8(8);
        long times = 50000000;
        Tester.testTasksParallel(
                TestTask.newTask("BeanKit", times, () -> {
                    PerformanceBean b = new PerformanceBean();
                    BeanKit.copyProperties(a, b);
                    return null;
                }),
                TestTask.newTask("Beanutils", times, () -> {
                    PerformanceBean b = new PerformanceBean();
                    try {
                        BeanUtils.copyProperties(b, a);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                    return null;
                })
        );
    }

    @Test
    public void testGenericBeanASchema() {
        GenericBeanA a = new GenericBeanA();
        BeanSchema aSchema = BeanKit.resolve(a.getClass());
        Map<String, PropertySchema> aPropertySchemas = aSchema.properties();
        System.out.println(aPropertySchemas);
        Assert.assertEquals(aPropertySchemas.get("a1").genericType(), String.class);
        Assert.assertEquals(aPropertySchemas.get("a2").genericType(), new TypeRef<List<String>>() {
        }.type());
        Assert.assertEquals(aPropertySchemas.get("i1").genericType(), Integer.class);
        Assert.assertEquals(aPropertySchemas.get("i2").genericType(), new TypeRef<List<Integer>>() {
        }.type());
        Assert.assertEquals(aPropertySchemas.get("class").genericType(), new TypeRef<Class<?>>() {
        }.type());
    }

    @Test
    public void testGenericBeanSchema() {
        Type gType = new TypeRef<GenericBean<String, ?, Iterable<? extends String>>>() {
        }.type();
        BeanSchema gSchema = BeanKit.resolve(gType);
        Map<String, PropertySchema> gPropertySchemas = gSchema.properties();
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
        BeanSchema sSchema = BeanKit.resolve(s.getClass());
        Map<String, PropertySchema> sPropertySchemas = sSchema.properties();
        System.out.println(sPropertySchemas);

        TypeKit.rawClass(String.class);
    }
}
