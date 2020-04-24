package xyz.srclab.common.bytecode;

import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @author sunqian
 */
public interface BClass<T> {

    T newInstance();

    T newInstance(Class<?>[] parameterTypes, Object[] args);

    interface Builder<T> {

        Builder<T> addInterface(Class<?> interfaceClass);

        default Builder<T> addInterfaces(Class<?>... interfaces) {
            return addInterfaces(Arrays.asList(interfaces));
        }

        Builder<T> addInterfaces(Iterable<Class<?>> interfaces);

        Builder<T> addConstructor(Class<?>[] parameterTypes, ConstructorBody<T> constructorBody);

        default Builder<T> addProperty(String propertyName, Class<?> type) {
            return addProperty(propertyName, type, true, true);
        }

        Builder<T> addProperty(String propertyName, Class<?> type, boolean readable, boolean writeable);

        Builder<T> addMethod(String methodName, Class<?>[] parameterTypes, MethodBody methodBody);
    }

    interface ConstructorBody<T> {

        @Nullable
        Object invoke(@Nullable Constructor<T> constructor, Object... args);
    }

    interface MethodBody {

        @Nullable
        Object invoke(Object instance, @Nullable Method method, Object... args);
    }

//    package cn.com.essence.untitled.asm;
//
//import jdk.internal.org.objectweb.asm.ClassVisitor;
//import jdk.internal.org.objectweb.asm.ClassWriter;
//import jdk.internal.org.objectweb.asm.MethodVisitor;
//import jdk.internal.org.objectweb.asm.Opcodes;
//
///**
// * @author sunqian
// */
//public class ClassEditor extends ClassLoader implements Opcodes {
//
//    private static final ClassEditor classEditor = new ClassEditor();
//
//    public static void main(String[] args) throws Exception {
//        String parentName = AsmBean.class.getName().replaceAll("\\.", "/");
//        ClassWriter classWriter = new ClassWriter(0);
//        classWriter.visit(
//                V1_8,
//                ACC_PUBLIC,
//                "abc/A",
//                null,
//                parentName,
//                null
//        );
//
////        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null,
////                null);
////        methodVisitor.visitVarInsn(ALOAD, 0);
////        methodVisitor.visitMethodInsn(INVOKESPECIAL, parentName, "<init>", "()V", false);
////        methodVisitor.visitInsn(RETURN);
////        methodVisitor.visitMaxs(1, 1);
////        methodVisitor.visitEnd();
//
//        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;)V", null,
//                null);
//        methodVisitor.visitVarInsn(ALOAD, 0);
//        methodVisitor.visitVarInsn(ALOAD, 1);
//        methodVisitor.visitMethodInsn(
//                INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
////        methodVisitor.visitLocalVariable("i", "int", "I", null, null, 1);
//        methodVisitor.visitMethodInsn(INVOKESPECIAL, parentName, "<init>", "(I)V", false);
//        methodVisitor.visitInsn(RETURN);
//        methodVisitor.visitMaxs(2, 2);
//        methodVisitor.visitEnd();
//
////        methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
////                "([Ljava/lang/String;)V", null, null);
////        methodVisitor.visitFieldInsn(GETSTATIC, "java/lang/System", "out",
////                "Ljava/io/PrintStream;");
////        methodVisitor.visitLdcInsn("Hello world!");
////        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println",
////                "(Ljava/lang/String;)V");
////        methodVisitor.visitInsn(RETURN);
////        methodVisitor.visitMaxs(2, 2);
////        methodVisitor.visitEnd();
//        classWriter.visitEnd();
//        byte[] byteCode = classWriter.toByteArray();
//        Class<?> newClass = classEditor.loadClass0(byteCode);
//        System.out.println(newClass);
//        System.out.println(AsmBean.class.isAssignableFrom(newClass));
//        System.out.println(newClass.getClassLoader());
//        System.out.println(AsmBean.class.getClassLoader());
//        AsmBean asmBean = (AsmBean) newClass.getConstructor(String.class).newInstance("100");
//        System.out.println(asmBean instanceof AsmBean);
//        System.out.println(asmBean.getValue());
//    }
//
//    private Class<?> loadClass0(byte[] byteCode) {
//        return super.defineClass(null, byteCode, 0, byteCode.length);
//    }
//
//    private static final class BClassVisitor extends ClassVisitor {
//
//        public BClassVisitor() {
//            super(ClassWriter.COMPUTE_MAXS);
//        }
//    }
//}

}
