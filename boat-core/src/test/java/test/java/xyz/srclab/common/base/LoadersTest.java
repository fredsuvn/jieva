package test.java.xyz.srclab.common.base;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.base.Loaders;
import xyz.srclab.common.collect.Collects;
import xyz.srclab.common.logging.Logs;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadersTest {

    @Test
    public void testLoader() {
        List<String> texts = Loaders.loadAllResourcesAsStrings("META-INF/test.properties");
        Logs.info("Load texts: {}", texts);
        Assert.assertEquals(
            texts,
            Collections.singletonList("info=123")
        );

        List<Map<String, String>> properties = Loaders.loadAllResourcesAsProperties("META-INF/test.properties");
        Logs.info("Load properties: {}", properties);
        Assert.assertEquals(
            properties,
            Collections.singletonList(Collects.putEntries(new HashMap<>(), "info", "123"))
        );

        String newClassName = "test.xyz.srclab.A";
        Class<?> clazz = Loaders.loadClass(createClass(newClassName));
        Logs.info("class: {}", clazz);
        Logs.info("class loader: {}", clazz.getClassLoader());
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
