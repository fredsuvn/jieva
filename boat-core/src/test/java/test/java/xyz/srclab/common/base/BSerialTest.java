package test.java.xyz.srclab.common.base;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BDefault;
import xyz.srclab.common.base.BSerial;
import xyz.srclab.common.collect.BArray;
import xyz.srclab.common.io.BFile;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BSerialTest {

    @Test
    public void testSerial() throws Exception {
        File temp = File.createTempFile("ttt", ".txt");
        S s = new S();
        s.setS1("555555");
        BSerial.writeObject(s, BFile.openOutputStream(temp), true);
        S s2 = BSerial.readObject(BFile.openInputStream(temp), true);
        Assert.assertEquals(s2.getS1(), s.getS1());
        List<Byte> list = BArray.asList("1234".getBytes(BDefault.charset()));
        BSerial.writeObject(list, BFile.openOutputStream(temp), true);
        List<Byte> list2 = BSerial.readObject(BFile.openInputStream(temp), true);
        Assert.assertEquals(list2, list);
        temp.delete();

        File temp2 = File.createTempFile("ttt", ".txt");
        A a = new A();
        B b = new B();
        a.setS("123");
        a.setB(b);
        b.setS("456");
        b.setA(a);
        Map<Object, Object> map = new HashMap<>();
        map.put("a", a);
        map.put("b", b);
        BSerial.writeObject(map, temp2, true);
        Map<Object, Object> mapRead = BSerial.readObject(temp2, true);
        A ar = (A) mapRead.get("a");
        B br = (B) mapRead.get("b");
        Assert.assertEquals(ar.getS(), a.getS());
        Assert.assertEquals(br.getS(), b.getS());
        Assert.assertEquals(ar.getB(), br);
        Assert.assertEquals(br.getA(), ar);
        temp2.delete();
    }

    public static class S implements Serializable {

        private static final long serialVersionUID = BDefault.serialVersion();

        private String s1 = "s1";

        public String getS1() {
            return s1;
        }

        public void setS1(String s1) {
            this.s1 = s1;
        }
    }

    public static class A implements Serializable {
        private String s = "A";
        private B b;

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }

    public static class B implements Serializable {
        private String s = "B";
        private A a;

        public String getS() {
            return s;
        }

        public void setS(String s) {
            this.s = s;
        }

        public A getA() {
            return a;
        }

        public void setA(A a) {
            this.a = a;
        }
    }
}
