package test.java.xyz.srclab.common.bean;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.bean.BBean;
import xyz.srclab.common.bean.BeanMap;
import xyz.srclab.common.bean.BeanResolver;

import java.util.*;

/**
 * @author sunqian
 */
public class BeanTest {

    @Test
    public void testSimpleBean() {
        TestBean a = new TestBean();
        a.setP1("123");
        a.setP2(6);
        a.setP3(Arrays.asList("1", "2", "3"));
        a.setP4(new long[]{7, 8, 9});

        TestBean b = new TestBean();
        BBean.copyProperties(a, b);
        Assert.assertEquals(b.getP1(), a.getP1());
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());
        Assert.assertEquals(b.getP4(), a.getP4());

        a.setP1(null);
        BBean.copyProperties(a, b);
        Assert.assertEquals(b.getP1(), "null");
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());
        Assert.assertEquals(b.getP4(), a.getP4());

        a.setP1(null);
        b.setP1("234");
        BBean.copyProperties(a, b, false);
        Assert.assertEquals(b.getP1(), "234");
        Assert.assertEquals(b.getP2(), a.getP2());
        Assert.assertEquals(b.getP3(), a.getP3());
        Assert.assertEquals(b.getP4(), a.getP4());

        a.setP1("123");
        a.setP2(222);
        a.setP3(Arrays.asList("1", "2", "3"));
        long[] p4 = new long[]{7, 8, 9};
        a.setP4(p4);
        LinkedHashMap<String, Object> map = BBean.copyProperties(a, new LinkedHashMap<>());
        Assert.assertEquals(map.size(), 5);//include class
        Assert.assertEquals(map.get("p1"), "123");
        Assert.assertEquals(map.get("p2"), 222);
        Assert.assertEquals(map.get("p2"), 222);
        Assert.assertEquals(map.get("p4"), p4);

        map.put("p2", 999);
        BBean.copyProperties(map, a);
        Assert.assertEquals(a.getP2(), 999);

        LinkedHashMap<String, Object> map2 = BBean.copyProperties(map, new LinkedHashMap<>());
        Assert.assertEquals(map2.size(), 5);//include class
        Assert.assertEquals(map2.get("p1"), "123");
        Assert.assertEquals(map2.get("p2"), 999);
        Assert.assertEquals(map2.get("p3"), Arrays.asList("1", "2", "3"));
        Assert.assertEquals(map.get("p4"), p4);
    }

    @Test
    public void testBeanMap() {
        TestBean testBean = new TestBean();
        testBean.setP1("123");
        testBean.setP2(6);
        testBean.setP3(Arrays.asList("1", "2", "3"));
        Map<String, Object> testMap = BBean.asBeanMap(testBean);
        BLog.info("testMap: {}", testMap);
        Assert.assertEquals(testMap.get("p1"), "123");
        Assert.assertEquals(testMap.get("p2"), 6);
        Assert.assertEquals(testMap.get("p3"), Arrays.asList("1", "2", "3"));
        testMap.put("p1", "555");
        Assert.assertEquals(testBean.getP1(), "555");
        Assert.expectThrows(UnsupportedOperationException.class, () -> testMap.put("p5", "p5"));
        testBean.setP2(888);
        Assert.assertEquals(testMap.get("p2"), 888);

        SimpleBean simpleBean = new SimpleBean();
        simpleBean.setP1("789");
        simpleBean.setP2(999);
        BeanMap simpleMap = BBean.asBeanMap(simpleBean);
        BLog.info("simpleMap: {}", simpleMap);
        Assert.assertEquals(simpleMap.get("p1"), "789");
        Assert.assertEquals(simpleMap.get("p2"), 999);
        Assert.assertEquals(simpleMap.size(), 3);//include class
        Assert.assertEquals(SimpleBean.class, simpleMap.getBeanType().getType());
        simpleMap.put("p1", "10086");
        simpleMap.put("p2", 10000);
        Assert.assertEquals(simpleBean.getP1(), "10086");
        Assert.assertEquals(simpleBean.getP2(), 10000);
    }

    @Test
    public void testNewBeanResolver() {
        // Test possible cyclic dependence in stupid Kotlin.
        BeanResolver resolver = BeanResolver.newBeanResolver(Collections.emptyList());
    }

    public static class TestBean {

        private String p1;
        private int p2;
        private List<String> p3;
        private long[] p4;

        public String getP1() {
            return p1;
        }

        public void setP1(String p1) {
            this.p1 = p1;
        }

        public int getP2() {
            return p2;
        }

        public void setP2(int p2) {
            this.p2 = p2;
        }

        public List<String> getP3() {
            return p3;
        }

        public void setP3(List<String> p3) {
            this.p3 = p3;
        }

        public long[] getP4() {
            return p4;
        }

        public void setP4(long[] p4) {
            this.p4 = p4;
        }

        public enum TestEnum {
            A, B, C
        }
    }

    public static class SimpleBean {

        private String p1;
        private int p2;

        public String getP1() {
            return p1;
        }

        public void setP1(String p1) {
            this.p1 = p1;
        }

        public int getP2() {
            return p2;
        }

        public void setP2(int p2) {
            this.p2 = p2;
        }
    }
}
