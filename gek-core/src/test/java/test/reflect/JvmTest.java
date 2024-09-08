package test.reflect;

import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.fslabo.common.reflect.JieJvm;
import xyz.fslabo.common.reflect.JieType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

public class JvmTest {

    @Test
    public void testName() throws Exception {
        Assert.assertEquals(JieJvm.getInternalName(boolean.class), "Z");
        Assert.assertEquals(JieJvm.getInternalName(byte.class), "B");
        Assert.assertEquals(JieJvm.getInternalName(short.class), "S");
        Assert.assertEquals(JieJvm.getInternalName(char.class), "C");
        Assert.assertEquals(JieJvm.getInternalName(int.class), "I");
        Assert.assertEquals(JieJvm.getInternalName(long.class), "J");
        Assert.assertEquals(JieJvm.getInternalName(float.class), "F");
        Assert.assertEquals(JieJvm.getInternalName(double.class), "D");
        Assert.assertEquals(JieJvm.getInternalName(void.class), "V");
        Assert.assertEquals(
            JieJvm.getInternalName(Object.class),
            org.objectweb.asm.Type.getInternalName(Object.class)
        );
    }

    @Test
    public void testDescriptor() throws Exception {
        Assert.assertEquals(JieJvm.getDescriptor(boolean.class), "Z");
        Assert.assertEquals(JieJvm.getDescriptor(byte.class), "B");
        Assert.assertEquals(JieJvm.getDescriptor(short.class), "S");
        Assert.assertEquals(JieJvm.getDescriptor(char.class), "C");
        Assert.assertEquals(JieJvm.getDescriptor(int.class), "I");
        Assert.assertEquals(JieJvm.getDescriptor(long.class), "J");
        Assert.assertEquals(JieJvm.getDescriptor(float.class), "F");
        Assert.assertEquals(JieJvm.getDescriptor(double.class), "D");
        Assert.assertEquals(JieJvm.getDescriptor(void.class), "V");
        Assert.assertEquals(
            JieJvm.getDescriptor(Object.class),
            org.objectweb.asm.Type.getDescriptor(Object.class)
        );
        Assert.assertEquals(
            JieJvm.getDescriptor(BaseClass.class.getMethod("m1")),
            org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m1"))
        );
        Assert.assertEquals(
            JieJvm.getDescriptor(BaseClass.class.getMethod("m2", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m2", String.class))
        );
        Assert.assertEquals(
            JieJvm.getDescriptor(BaseClass.class.getMethod("m3", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m3", String.class))
        );
        Assert.assertEquals(
            JieJvm.getDescriptor(BaseInter.class.getMethod("i1", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseInter.class.getMethod("i1", String.class))
        );
        Assert.assertEquals(
            JieJvm.getDescriptor(BaseInter.class.getMethod("i2", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseInter.class.getMethod("i2", String.class))
        );
        Assert.assertEquals(
            JieJvm.getDescriptor(BaseClass.class.getMethod("m4", String.class, List.class, List.class, List.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m4", String.class, List.class, List.class, List.class))
        );
        Assert.assertEquals(
            JieJvm.getDescriptor(BaseClass.class.getConstructor()),
            org.objectweb.asm.Type.getConstructorDescriptor(BaseClass.class.getConstructor())
        );
        Assert.assertEquals(
            JieJvm.getDescriptor(BaseClass.class.getConstructor(String.class, List.class, List.class, List.class)),
            org.objectweb.asm.Type.getConstructorDescriptor(BaseClass.class.getConstructor(String.class, List.class, List.class, List.class))
        );
    }

    @Test
    public void testSignature() throws Exception {
        Assert.expectThrows(IllegalArgumentException.class, () -> JieJvm.getSignature(JieType.other()));
        Assert.assertEquals(
            JieJvm.getSignature(BaseClass.class.getMethod("m1")),
            org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m1"))
        );
        Assert.assertEquals(
            JieJvm.getSignature(BaseClass.class.getMethod("m2", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m2", String.class))
        );
        Assert.assertEquals(
            JieJvm.getSignature(BaseClass.class.getMethod("m3", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseClass.class.getMethod("m3", String.class))
        );
        Assert.assertEquals(
            JieJvm.getSignature(BaseInter.class.getMethod("i1", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseInter.class.getMethod("i1", String.class))
        );
        Assert.assertEquals(
            JieJvm.getSignature(BaseInter.class.getMethod("i2", String.class)),
            org.objectweb.asm.Type.getMethodDescriptor(BaseInter.class.getMethod("i2", String.class))
        );
        Assert.assertEquals(
            JieJvm.getSignature(BaseClass.class.getMethod("m4", String.class, List.class, List.class, List.class)),
            "(Ljava/lang/String;Ljava/util/List<Ljava/lang/String;>;Ljava/util/List<-Ljava/lang/String;>;Ljava/util/List<+Ljava/lang/String;>;)Ljava/lang/String;"
        );
        Assert.assertEquals(
            JieJvm.getSignature(BaseClass.class.getConstructor()),
            org.objectweb.asm.Type.getConstructorDescriptor(BaseClass.class.getConstructor())
        );
        Assert.assertEquals(
            JieJvm.getSignature(BaseClass.class.getConstructor(String.class, List.class, List.class, List.class)),
            "(Ljava/lang/String;Ljava/util/List<Ljava/lang/String;>;Ljava/util/List<-Ljava/lang/String;>;Ljava/util/List<+Ljava/lang/String;>;)V"
        );

        Assert.assertEquals(
            JieJvm.getSignature(XClass.class.getDeclaredField("f1").getGenericType()),
            org.objectweb.asm.Type.getDescriptor(XClass.class.getDeclaredField("f1").getType())
        );
        Assert.assertEquals(
            JieJvm.getSignature(XClass.class.getDeclaredField("f2").getGenericType()),
            org.objectweb.asm.Type.getDescriptor(XClass.class.getDeclaredField("f2").getType())
        );
        Assert.assertEquals(
            JieJvm.getSignature(XClass.class.getDeclaredField("f3").getGenericType()),
            org.objectweb.asm.Type.getDescriptor(XClass.class.getDeclaredField("f3").getType())
        );
        Assert.assertEquals(
            JieJvm.getSignature(XClass.class.getDeclaredField("f4").getGenericType()),
            "Ljava/util/List<+TV;>;"
        );
        Assert.assertEquals(
            JieJvm.getSignature(XClass.class.getDeclaredField("f5").getGenericType()),
            "Ljava/util/List<-TV;>;"
        );
        Assert.assertEquals(
            JieJvm.getSignature(XClass.class.getDeclaredField("f6").getGenericType()),
            "[Ljava/util/List<+TV;>;"
        );
        Assert.assertEquals(
            JieJvm.getSignature(XClass.class.getDeclaredField("f7").getGenericType()),
            "[Ljava/util/List<-Ljava/lang/String;>;"
        );

        Assert.assertEquals(
            JieJvm.getSignature(XClass.class.getMethod("x1", List.class, List.class, List[].class, List.class)),
            "(TX;Ljava/util/List<+TT;>;[Ljava/util/List;Ljava/util/List<+Ljava/lang/String;>;)Ljava/util/List<+TU;>;"
        );
        Assert.assertEquals(
            JieJvm.getSignature(XClass.class.getConstructor(List.class, List.class, List[].class, List.class)),
            "(TX;Ljava/util/List<+TT;>;[Ljava/util/List;Ljava/util/List<+Ljava/lang/String;>;)V"
        );

        Assert.assertEquals(
            JieJvm.declareSignature(BaseClass.class),
            org.objectweb.asm.Type.getDescriptor(Object.class)
        );
        Assert.assertEquals(
            JieJvm.declareSignature(XClass.class),
            "<T:Ljava/lang/Number;:Ljava/lang/CharSequence;U:Ljava/lang/Object;V:TT;X::Ljava/util/List<-Ljava/lang/Integer;>;Y::Ljava/io/Serializable;W::Ljava/lang/CharSequence;:Ljava/util/RandomAccess;P:Ljava/lang/String;O:TY;M:Ljava/util/ArrayList<Ljava/lang/String;>;>Ltest/reflect/JvmTest$BaseClass;Ltest/reflect/JvmTest$BaseInter;Ltest/reflect/JvmTest$XInter<TV;TV;Ljava/lang/String;>;"
        );
    }

    public static class BaseClass {

        public BaseClass() {
        }

        public BaseClass(String a, List<String> b, List<? super String> c, List<? extends String> d) {

        }

        public void m1() {
        }

        public void m2(String a) {
        }

        public String m3(String a) {
            return null;
        }

        public String m4(String a, List<String> b, List<? super String> c, List<? extends String> d) {
            return null;
        }
    }

    public interface BaseInter {
        default String i1(String a) {
            return null;
        }

        String i2(String a);
    }

    public interface XInter<A, B extends A, C extends String> {
    }

    public static class XClass<T extends Number & CharSequence, U, V extends T, X extends List<? super Integer>,
        Y extends Serializable, W extends CharSequence & RandomAccess, P extends String, O extends Y, M extends ArrayList<String>>
        extends BaseClass implements BaseInter, XInter<V, V, String> {

        private int f1;
        private List f2;
        private List[] f3;
        private List<? extends V> f4;
        private List<? super V> f5;
        private List<? extends V>[] f6;
        private List<? super String>[] f7;

        public XClass(X a, List<? extends T> b, List[] c, List<? extends String> d) {
        }

        @Override
        public String i2(String a) {
            return "";
        }

        public List<? extends U> x1(X a, List<? extends T> b, List[] c, List<? extends String> d) {
            return null;
        }
    }
}
