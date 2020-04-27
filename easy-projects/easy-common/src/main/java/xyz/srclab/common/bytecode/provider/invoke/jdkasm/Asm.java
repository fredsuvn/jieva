package xyz.srclab.common.bytecode.provider.invoke.jdkasm;

import com.google.common.base.CharMatcher;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import xyz.srclab.common.base.Shares;
import xyz.srclab.common.bytecode.ByteCodeHelper;
import xyz.srclab.common.invoke.ConstructorInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author sunqian
 */
final class Asm {

    private static final CharMatcher charMatcher = Shares.DOT_CHAR_MATCHER;

    private static final AtomicLong counter = new AtomicLong();

    private static final String OBJECT_INTERNAL_NAME =
            ByteCodeHelper.getInternalName(Object.class);

    private static final String OBJECT_DESCRIPTOR =
            ByteCodeHelper.getDescriptor(Object.class);

    private static final String OBJECT_ARRAY_DESCRIPTOR =
            ByteCodeHelper.getDescriptor(Object[].class);

    private static final String CONSTRUCTOR_INVOKER_INTERNAL_NAME =
            ByteCodeHelper.getInternalName(ConstructorInvoker.class);

    private static final String CONSTRUCTOR_INVOKER_SIGNATURE = OBJECT_DESCRIPTOR + "L" +
            CONSTRUCTOR_INVOKER_INTERNAL_NAME +
            "<Ljava/lang/Object;>;";

    private static final String[] CONSTRUCTOR_INVOKER_INTERFACE_DECLARATION = {CONSTRUCTOR_INVOKER_INTERNAL_NAME};

    private static final Method CONSTRUCTOR_INVOKE_METHOD;

    private static final String CONSTRUCTOR_INVOKE_METHOD_DESCRIPTOR =
            "(" + OBJECT_ARRAY_DESCRIPTOR + ")" + OBJECT_DESCRIPTOR;

    static {
        try {
            CONSTRUCTOR_INVOKE_METHOD = ConstructorInvoker.class.getMethod("invoke", Object[].class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    Class<?> generateConstructorInvoker(Constructor<?> constructor) {
        Class<?> declareClass = constructor.getDeclaringClass();
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        String newClassName = generateClassName("Constructor", parameterTypes);

        ClassWriter classWriter = new ClassWriter(0);
        classWriter.visit(
                Opcodes.V1_8,
                Opcodes.ACC_PUBLIC,
                ByteCodeHelper.getInternalName(newClassName),
                CONSTRUCTOR_INVOKER_SIGNATURE,
                null,
                CONSTRUCTOR_INVOKER_INTERFACE_DECLARATION
        );

        createEmptyConstructor(classWriter);

        MethodVisitor invokeMethod = classWriter.visitMethod(
                Opcodes.ACC_PUBLIC,
                CONSTRUCTOR_INVOKE_METHOD.getName(),
                CONSTRUCTOR_INVOKE_METHOD_DESCRIPTOR,
                null,
                null
        );
    }

    private static MethodVisitor createEmptyConstructor(ClassWriter classWriter) {
        MethodVisitor result = classWriter.visitMethod(
                Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        result.visitVarInsn(Opcodes.ALOAD, 0);
        result.visitMethodInsn(
                Opcodes.INVOKESPECIAL, OBJECT_INTERNAL_NAME, "<init>", "()V", false);
        result.visitInsn(Opcodes.RETURN);
        result.visitMaxs(1, 1);
        result.visitEnd();
        return result;
    }

    private static String generateConstructorInvokerSignature() {

    }

    private static String generateClassName(String scope, Class<?>... types) {
        StringBuilder typesBuf = new StringBuilder();
        for (Class<?> type : types) {
            typesBuf.append(charMatcher.replaceFrom(type.getName(), "$"));
            typesBuf.append("$");
        }
        return Asm.class.getName() + "$$" +
                scope +
                "$$" +
                typesBuf +
                "$$GeneratedByAsm$$" + counter.getAndIncrement();
    }
}
