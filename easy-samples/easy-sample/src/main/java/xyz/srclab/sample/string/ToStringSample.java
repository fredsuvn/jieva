package xyz.srclab.sample.string;

import xyz.srclab.common.string.tostring.ToString;
import xyz.srclab.common.string.tostring.ToStringStyle;

import java.util.Arrays;
import java.util.List;

/**
 * @author sunqian
 */
public class ToStringSample {

    public static void main(String[] args) {
        System.out.println(ToString.buildToString(new A()));
        System.out.println(ToString.buildToString(new A(), ToStringStyle.HUMAN_READABLE));
    }

    public static class A {
        private String string = "string";
        private List<String> list = Arrays.asList("string1", "string2");
        private B b = new B();

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }

    public static class B {
        private String string = "string";
        private List<String> list = Arrays.asList("string1", "string2");

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
    }
}
