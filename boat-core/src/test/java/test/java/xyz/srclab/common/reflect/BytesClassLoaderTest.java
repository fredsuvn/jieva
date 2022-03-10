package test.java.xyz.srclab.common.reflect;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.BLog;
import xyz.srclab.common.reflect.BytesClassLoader;

public class BytesClassLoaderTest {

    @Test
    public void testLoadClass() {
        String newClassName = "test.xyz.srclab.A";
        Class<?> clazz = BytesClassLoader.INSTANCE.loadClass(createClass(newClassName));
        BLog.info("class: {}", clazz);
        BLog.info("class loader: {}", clazz.getClassLoader());
        Assert.assertEquals(clazz.getName(), newClassName);
    }

    private byte[] createClass(String name) {
        String internalName = name.replaceAll("\\.", "/");
        ClassWriter classWriter = new ClassWriter(0);
        classWriter.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC,
            internalName,
            "L" + internalName + ";",
            "java/lang/Object",
            null
        );
        MethodVisitor methodVisitor = classWriter.visitMethod(
            Opcodes.ACC_PUBLIC,
            "<init>",
            "()V",
            null,
            null
        );
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            "java/lang/Object",
            "<init>",
            "()V",
            false
        );
        methodVisitor.visitInsn(Opcodes.RETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }
}
