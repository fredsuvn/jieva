package xyz.srclab.sample.bean;

import xyz.srclab.common.bean.BeanConverter;
import xyz.srclab.common.bean.BeanHelper;
import xyz.srclab.common.bean.BeanOperator;
import xyz.srclab.common.bean.BeanResolver;
import xyz.srclab.common.lang.TypeRef;
import xyz.srclab.common.test.asserts.AssertHelper;

import java.util.HashMap;
import java.util.Map;

public class BeanSample {

    public void showConvert() throws Exception {
        String string = "10086";
        long l = BeanHelper.convert(string, Long.class);
        AssertHelper.printAssert(l, 10086L);

        Map<Long, Long> longMap = new HashMap<>();
        longMap.put(3L, 3L);
        longMap.put(6L, 6L);
        longMap.put(9L, 9L);
        Map<String, String> stringMap = BeanHelper.convert(longMap, new TypeRef<Map<String, String>>() {
        });
        System.out.println(stringMap);
        AssertHelper.printAssert(stringMap.get("6"), "6");
    }

    public void showCopyProperties() throws Exception {
        A a = new A();
        a.setString("A");
        Map<? super Integer, ? extends Integer> aMap = new HashMap<>();
        ((Map) aMap).put(1, 1);
        ((Map) aMap).put(2, 2);
        a.setMap(aMap);
        B b = new B();
        BeanHelper.copyProperties(a, b);
        Map<? extends String, ? extends String> bMap = b.getMap();
        System.out.println(bMap);
        AssertHelper.printAssert(bMap.get("1"), "1");

        BeanHelper.copyPropertiesIgnoreNull(a, b);
        BeanHelper.copyProperties(a, b,
                (sourcePropertyName, sourcePropertyValue, destPropertyType, destPropertySetter, beanOperator) -> {
                    // Do what you like here...
                });
    }

    public void showPopulateProperties() {
        A a = new A();
        a.setString("A");
        Map<? super Integer, ? extends Integer> aMap = new HashMap<>();
        ((Map) aMap).put(1, 1);
        ((Map) aMap).put(2, 2);
        a.setMap(aMap);
        Map map = new HashMap();
        BeanHelper.populateProperties(a, map);
        System.out.println(map);
        AssertHelper.printAssert(((Map) map.get("map")).get(1), 1);

        BeanHelper.populatePropertiesIgnoreNull(a, map);
        BeanHelper.populateProperties(a, map,
                (sourcePropertyName, sourcePropertyValue, destPropertyType, destPropertySetter, beanOperator) -> {
                    // Do what you like here...
                });
    }

    public void showCustom() {
        BeanOperator beanOperator = BeanOperator.newBuilder()
                .setBeanResolver(BeanResolver.newBuilder()
                        .build())
                .setBeanConverter(BeanConverter.newBuilder()
                        .build())
                .build();
    }

    public static class A {
        private String string;
        private Map<? super Integer, ? extends Integer> map;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public Map<? super Integer, ? extends Integer> getMap() {
            return map;
        }

        public void setMap(Map<? super Integer, ? extends Integer> map) {
            this.map = map;
        }

    }

    public static class B {
        private String string;
        private Map<? extends String, ? extends String> map;

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public Map<? extends String, ? extends String> getMap() {
            return map;
        }

        public void setMap(Map<? extends String, ? extends String> map) {
            this.map = map;
        }
    }
}
