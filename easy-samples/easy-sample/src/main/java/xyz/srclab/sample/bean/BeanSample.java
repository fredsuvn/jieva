package xyz.srclab.sample.bean;

import xyz.srclab.common.bean.BeanHelper;
import xyz.srclab.common.bean.BeanOperator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanSample {

    public static void main(String[] args) throws Exception {
        A a = new A();
        a.setStringProperty("123");
        a.setIntProperty(456);
        a.setDateProperty("2020-02-02T02:02:22");
        Map<? super Integer, List<? extends String>> map = new HashMap<>();
        map.put(8, Arrays.asList("8", "9", "10"));
        a.setMap(map);
        C<String> c = new C<>();
        c.setT("666");
        a.setC(c);

        B b = new B();
        //BeanUtils.copyProperties(a, b);
        BeanHelper.copyProperties(a, b);
        System.out.println(b.getMap().get("8").get(1));
        System.out.println(b.getC().getT());
    }

    public static class A {
        private String stringProperty;
        private int intProperty;
        private String dateProperty;
        private Map<? super Integer, List<? extends String>> map;
        private C<String> c;

        public String getStringProperty() {
            return stringProperty;
        }

        public void setStringProperty(String stringProperty) {
            this.stringProperty = stringProperty;
        }

        public int getIntProperty() {
            return intProperty;
        }

        public void setIntProperty(int intProperty) {
            this.intProperty = intProperty;
        }

        public String getDateProperty() {
            return dateProperty;
        }

        public void setDateProperty(String dateProperty) {
            this.dateProperty = dateProperty;
        }

        public Map<? super Integer, List<? extends String>> getMap() {
            return map;
        }

        public void setMap(Map<? super Integer, List<? extends String>> map) {
            this.map = map;
        }

        public C<String> getC() {
            return c;
        }

        public void setC(C<String> c) {
            this.c = c;
        }
    }

    public static class B {
        private int stringProperty;
        private String intProperty;
        private LocalDateTime dateProperty;
        private Map<? extends String, List<? extends Integer>> map;
        private C<Integer> c;

        public int getStringProperty() {
            return stringProperty;
        }

        public void setStringProperty(int stringProperty) {
            this.stringProperty = stringProperty;
        }

        public String getIntProperty() {
            return intProperty;
        }

        public void setIntProperty(String intProperty) {
            this.intProperty = intProperty;
        }

        public LocalDateTime getDateProperty() {
            return dateProperty;
        }

        public void setDateProperty(LocalDateTime dateProperty) {
            this.dateProperty = dateProperty;
        }

        public Map<? extends String, List<? extends Integer>> getMap() {
            return map;
        }

        public void setMap(Map<? extends String, List<? extends Integer>> map) {
            this.map = map;
        }

        public C<Integer> getC() {
            return c;
        }

        public void setC(C<Integer> c) {
            this.c = c;
        }
    }

    public static class C<T> {
        private T t;

        public T getT() {
            return t;
        }

        public void setT(T t) {
            this.t = t;
        }
    }
}
