package test.java.xyz.srclab.common.io;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BDefault;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.collect.BArray;
import xyz.srclab.common.io.BFile;
import xyz.srclab.common.io.BIO;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sunqian
 */
public class IOTest {

    @Test
    public void testStream() throws Exception {
        String text = "123456\r\n234567\r\n";
        InputStream input = BIO.asInputStream(text.getBytes());
        String inputString = BIO.readString(input);
        input.reset();
        BLog.info("inputString: {}", inputString);
        Assert.assertEquals(inputString, text);
        byte[] bytes = BIO.readBytes(input);
        input.reset();
        Assert.assertEquals(bytes, text.getBytes());
        List<String> inputStrings = BIO.readLines(input);
        input.reset();
        Assert.assertEquals(inputStrings, Arrays.asList("123456", "234567", ""));//note end with \r\n
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BIO.readTo(input, output);
        input.reset();
        Assert.assertEquals(output.toByteArray(), bytes);
    }

    @Test
    public void testReader() throws Exception {
        String text = "123456\r\n234567\r\n";
        InputStream input = BIO.asInputStream(text.getBytes());
        Reader reader = BIO.asReader(input);
        String readString = BIO.readString(reader);
        input.reset();
        BLog.info("readString: {}", readString);
        Assert.assertEquals(readString, text);
        char[] chars = BIO.readString(reader).toCharArray();
        input.reset();
        Assert.assertEquals(chars, text.toCharArray());
        List<String> readStrings = BIO.readLines(reader);
        input.reset();
        Assert.assertEquals(readStrings, Arrays.asList("123456", "234567", ""));//note end with \r\n
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Writer writer = BIO.asWriter(output);
        BIO.readTo(reader, writer);
        input.reset();
        writer.flush();
        Assert.assertEquals(output.toByteArray(), text.getBytes());
    }

    @Test
    public void testSerial() throws Exception {
        File temp = File.createTempFile("ttt", ".txt");
        S s = new S();
        s.setS1("555555");
        BIO.writeObject(s, BFile.openOutputStream(temp), true);
        S s2 = BIO.readObject(BFile.openInputStream(temp), true);
        Assert.assertEquals(s2.getS1(), s.getS1());
        List<Byte> list = BArray.asList("1234".getBytes(BDefault.charset()));
        BIO.writeObject(list, BFile.openOutputStream(temp), true);
        List<Byte> list2 = BIO.readObject(BFile.openInputStream(temp), true);
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
        BIO.writeObject(map, temp2, true);
        Map<Object, Object> mapRead = BIO.readObject(temp2, true);
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
