package test.java.xyz.srclab.common.base;

import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.*;
import xyz.srclab.common.collect.BtArray;
import xyz.srclab.common.collect.BtList;
import xyz.srclab.common.collect.BtMap;
import xyz.srclab.common.io.BtFile;
import xyz.srclab.common.io.BtIO;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.time.Duration;
import java.util.*;

/**
 * @author sunqian
 */
public class BtTest {

    private static final String ECHO_CONTENT = "ECHO_CONTENT";

    @Test
    public void testDefault() {
        System.out.println("Default radix: " + Bt.defaultRadix());
        System.out.println("Default charset: " + Bt.defaultCharset());
        System.out.println("Default bufferSize: " + Bt.defaultBufferSize());
        System.out.println("Default locale: " + Bt.defaultLocale());
        System.out.println("Default serialVersion: " + Bt.defaultSerialVersion());
        System.out.println("Default timestampPattern: " + Bt.defaultTimestampPattern());
        System.out.println("Default concurrency level: " + Bt.defaultConcurrencyLevel());
        System.out.println("Default null string: " + Bt.defaultNullString());
    }

    @Test
    public void testEqual() {
        Assert.assertTrue(Bt.equals("", ""));

        Assert.assertTrue(Bt.deepEquals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));
        Assert.assertFalse(Bt.equals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));

        Assert.assertTrue(Bt.deepEquals(
            new Object[]{1, new int[]{1, 2, 3}, 3}, new Object[]{1, new int[]{1, 2, 3}, 3}));
        Assert.assertFalse(Bt.equals(
            new Object[]{1, new int[]{1, 2, 3}, 3}, new Object[]{1, new int[]{1, 2, 3}, 3}));
    }

    @Test
    public void testHash() {
        Object obj = new Object();
        Assert.assertEquals(Bt.hash(obj), obj.hashCode());
        Assert.assertNotEquals(Bt.hash(new int[]{1, 2, 3}), Bt.hash(new int[]{1, 2, 3}));

        Assert.assertEquals(Bt.hash(new int[]{1, 2, 3}), Bt.hash(new int[]{1, 2, 3}));
        Assert.assertNotEquals(
            Bt.hash(1, new int[]{1, 2, 3}, 3), Bt.hash(1, new int[]{1, 2, 3}, 3));

        Assert.assertEquals(
            Bt.deepHash(1, new int[]{1, 2, 3}, 3), Bt.deepHash(1, new int[]{1, 2, 3}, 3));
    }

    @Test
    public void testToString() {
        Object obj = new Object();
        Assert.assertEquals(Bt.toString(obj), obj.toString());
        BtLog.info(Bt.arrayToString(new int[]{1, 2, 3}));
        Assert.assertEquals(Bt.arrayToString(new int[]{1, 2, 3}), Arrays.toString(new int[]{1, 2, 3}));
        BtLog.info(Bt.deepToString(new Object[]{1, new int[]{1, 2, 3}, 3}));
        Assert.assertEquals(
            Bt.deepToString(new Object[]{1, new int[]{1, 2, 3}, 3}),
            Arrays.deepToString(new Object[]{1, new int[]{1, 2, 3}, 3})
        );
    }

    @Test
    public void testProperties() {
        InputStream inputStream = Bt.loadStream("META-INF/test.properties");
        assert inputStream != null;
        Map<String, String> properties = Bt.readProperties(inputStream);
        Assert.assertEquals(properties.get("info"), "123");
    }

    @Test
    public void testLoadAll() {
        List<String> strings = Bt.loadStrings("META-INF/test.properties");
        Assert.assertEquals(strings, BtList.of("info=123"));
    }

    @Test
    public void testMath() {
        org.junit.Assert.assertEquals(Bt.remLength(100, 10), 90);
        org.junit.Assert.assertEquals(Bt.countSeg(100, 10), 10);
        org.junit.Assert.assertEquals(Bt.countSeg(101, 10), 11);
        org.junit.Assert.assertEquals(Bt.countSeg(9, 10), 1);
        org.junit.Assert.assertEquals(Bt.resize(12, 3, 4), 16);
        org.junit.Assert.assertEquals(Bt.resize(13, 3, 4), 20);
        org.junit.Assert.assertEquals(Bt.endIndex(0, 10), 10);
        org.junit.Assert.assertEquals(Bt.endIndex(1, 10), 11);
    }

    @Test
    public void testBoolean() {
        Assert.assertTrue(Bt.toBoolean("true"));
        Assert.assertTrue(Bt.allTrue("true", "TRUE"));
        Assert.assertTrue(Bt.anyFalse("true", "x"));
        Assert.assertFalse(Bt.anyTrue("x", "y"));
    }

    @Test
    public void testProcess() {
        if (BtSystem.isLinux()) {
            echo("echo", ECHO_CONTENT);
        }
        if (BtSystem.isWindows()) {
            echo("cmd.exe", "/c", "echo " + ECHO_CONTENT);
        }
    }

    @Test
    public void testPing() {
        Process process = Bt.runProcess("ping 127.0.0.1");
        BtThread.sleep(Duration.ofSeconds(2));
        String output = BtIO.availableString(process.getInputStream());
        BtLog.info(output);
        process.destroy();
    }

    private void echo(String... command) {
        Process process = Bt.runProcess(command);
        String output = BtIO.readString(process.getInputStream());
        BtLog.info(output);
        Assert.assertEquals(output, ECHO_CONTENT + BtSystem.getLineSeparator());
    }

    @Test
    public void testLoadResource() {
        String test = Bt.loadString("/META-INF/test.properties");
        BtLog.info("loadClasspathString: {}", test);
        Assert.assertEquals(test, "info=123");

        List<String> texts = Bt.loadStrings("META-INF/test.properties");
        BtLog.info("loadClasspathStrings: {}", texts);
        Assert.assertEquals(
            texts,
            Collections.singletonList("info=123")
        );

        List<Map<String, String>> properties = Bt.loadPropertiesList("META-INF/test.properties");
        BtLog.info("loadClasspathPropertiesList: {}", properties);
        Assert.assertEquals(
            properties,
            Collections.singletonList(BtMap.of("info", "123"))
        );
    }

    @Test
    public void testSerial() throws Exception {
        File temp = File.createTempFile("ttt", ".txt");
        S s = new S();
        s.setS1("555555");
        Bt.writeObject(s, BtFile.openOutputStream(temp), true);
        S s2 = Bt.readObject(BtFile.openInputStream(temp), true);
        Assert.assertEquals(s2.getS1(), s.getS1());
        List<Byte> list = BtArray.asList("1234".getBytes(Bt.defaultCharset()));
        Bt.writeObject(list, BtFile.openOutputStream(temp), true);
        List<Byte> list2 = Bt.readObject(BtFile.openInputStream(temp), true);
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
        Bt.writeObject(map, temp2, true);
        Map<Object, Object> mapRead = Bt.readObject(temp2, true);
        A ar = (A) mapRead.get("a");
        B br = (B) mapRead.get("b");
        Assert.assertEquals(ar.getS(), a.getS());
        Assert.assertEquals(br.getS(), b.getS());
        Assert.assertEquals(ar.getB(), br);
        Assert.assertEquals(br.getA(), ar);
        temp2.delete();
    }

    @Test
    public void testCompares() {
        Assert.assertEquals(Bt.atBetween(100, 101, 666), Integer.valueOf(101));
        Assert.assertEquals(Bt.atBetween(100, 0, 99), Integer.valueOf(99));
        Assert.assertEquals(Bt.atBetween(100, 0, 666), Integer.valueOf(100));
        Assert.assertEquals(Bt.atLeast(50, 100), Integer.valueOf(100));
        Assert.assertEquals(Bt.atLeast(111, 100), Integer.valueOf(111));
        Assert.assertEquals(Bt.atMost(50, 100), Integer.valueOf(50));
        Assert.assertEquals(Bt.atMost(111, 100), Integer.valueOf(100));
    }

    @Test
    public void testFinalClass() {

        IntVar i = IntVar.of(1);

        class TestFinal extends FinalClass {

            @Override
            protected int hashCode0() {
                int r = i.getValue();
                i.setValue(r + 1);
                return r;
            }

            @NotNull
            @Override
            protected String toString0() {
                return hashCode() + "";
            }
        }

        TestFinal tf = new TestFinal();
        Assert.assertEquals(i.getValue(), 1);
        Assert.assertEquals(tf.hashCode(), 1);
        Assert.assertEquals(i.getValue(), 2);
        Assert.assertEquals(tf.toString(), "1");
        Assert.assertEquals(i.getValue(), 2);
        Assert.assertEquals(tf.hashCode(), 1);
        Assert.assertEquals(tf.toString(), "1");
        Assert.assertEquals(i.getValue(), 2);
        i.setValue(100);
        Assert.assertEquals(tf.hashCode(), 1);
        Assert.assertEquals(tf.toString(), "1");
        Assert.assertEquals(i.getValue(), 100);
    }

    public static class S implements Serializable {

        private static final long serialVersionUID = Bt.defaultSerialVersion();

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
