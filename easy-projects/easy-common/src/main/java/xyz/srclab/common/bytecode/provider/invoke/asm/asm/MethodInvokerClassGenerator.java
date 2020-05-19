package xyz.srclab.common.bytecode.provider.invoke.asm.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.srclab.common.array.ArrayHelper;
import xyz.srclab.common.bytecode.*;
import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerHelper;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

final class MethodInvokerClassGenerator {

    public static Class<?> generateClass(Method method) {
        try {
            String newClassName =
                    AsmInvokerHelper.generateMethodInvokerClassName(method, AsmSupport.GENERATOR_NAME);
            BRefType methodClass = new BRefType(method.getClass());
            BRefType methodInvokerInterface = new BRefType(MethodInvoker.class);
            BRefType functionInvokerInterface = new BRefType(FunctionInvoker.class);
            BRefType ownerType = new BRefType(method.getDeclaringClass());

            BNewType newTypeClass = new BNewType(
                    newClassName,
                    null,
                    null,
                    Arrays.asList(methodInvokerInterface, functionInvokerInterface)
            );

            ClassWriter classWriter = new ClassWriter(0);
            classWriter.visit(
                    Opcodes.V1_8,
                    Opcodes.ACC_PUBLIC,
                    newTypeClass.getInternalName(),
                    newTypeClass.getSignature(),
                    newTypeClass.getSuperClass().getInternalName(),
                    ArrayHelper.map(newTypeClass.getInterfaces(), String.class, BRefType::getInternalName)
            );

            // private Method method;
            FieldVisitor fieldVisitor = classWriter.visitField(
                    Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                    "method",
                    methodClass.getDescriptor(),
                    methodClass.getSignature(),
                    null
            );
            fieldVisitor.visitEnd();

            // <init>(Method method)
            BMethod init = new BMethod(
                    ByteCodeHelper.OBJECT_INIT.getName(),
                    null,
                    Collections.singletonList(methodClass),
                    null
            );
            MethodVisitor methodVisitor = classWriter.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    init.getName(),
                    init.getDescriptor(),
                    null,
                    null
            );
            // super();
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    ByteCodeHelper.OBJECT.getInternalName(),
                    ByteCodeHelper.OBJECT_INIT.getName(),
                    ByteCodeHelper.OBJECT_INIT.getDescriptor(),
                    false
            );
            // this.constructor = constructor
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitFieldInsn(
                    Opcodes.PUTFIELD,
                    newTypeClass.getInternalName(),
                    "method",
                    methodClass.getDescriptor()
            );
            methodVisitor.visitInsn(Opcodes.RETURN);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();

            //Method getMethod()
            BMethod getMethodMethod =
                    new BMethod("getMethod", methodClass, null, null);
            methodVisitor = classWriter.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    getMethodMethod.getName(),
                    getMethodMethod.getDescriptor(),
                    getMethodMethod.getSignature(),
                    null
            );
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitFieldInsn(
                    Opcodes.GETFIELD,
                    newTypeClass.getInternalName(),
                    "method",
                    methodClass.getDescriptor()
            );
            methodVisitor.visitInsn(Opcodes.ARETURN);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();

            //T invoke(@Nullable Object, Object[])
            Class<?>[] parameterTypes = method.getParameterTypes();
            BMethod targetMethod = new BMethod(method);
            BMethod invokeMethod = new BMethod(
                    "invoke",
                    ByteCodeHelper.OBJECT,
                    Arrays.asList(ByteCodeHelper.OBJECT, ByteCodeHelper.OBJECT_ARRAY),
                    null
            );
            methodVisitor = classWriter.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    invokeMethod.getName(),
                    invokeMethod.getDescriptor(),
                    invokeMethod.getSignature(),
                    null
            );
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitTypeInsn(
                    Opcodes.CHECKCAST,
                    ownerType.getInternalName()
            );
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 3);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 3);
            for (int i = 0; i < parameterTypes.length; i++) {
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);
                methodVisitor.visitLdcInsn(i);
                methodVisitor.visitInsn(Opcodes.AALOAD);
                AsmSupport.checkCast(methodVisitor, parameterTypes[i], targetMethod.getParameterType(i));
            }
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    ownerType.getInternalName(),
                    targetMethod.getName(),
                    targetMethod.getDescriptor(),
                    false
            );
            if (void.class.equals(method.getReturnType())) {
                methodVisitor.visitInsn(Opcodes.ACONST_NULL);
            }
            methodVisitor.visitInsn(Opcodes.ARETURN);
            int maxStack = parameterTypes.length == 0 ? 2 : 2 + parameterTypes.length + 1;
            methodVisitor.visitMaxs(
                    maxStack,
                    4
            );
            methodVisitor.visitEnd();

            //Object invoke(Object[])
            BMethod functionMethod = new BMethod(
                    "invoke",
                    ByteCodeHelper.OBJECT,
                    Collections.singletonList(ByteCodeHelper.OBJECT_ARRAY),
                    null
            );
            methodVisitor = classWriter.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    functionMethod.getName(),
                    functionMethod.getDescriptor(),
                    functionMethod.getSignature(),
                    null
            );
            for (int i = 0; i < parameterTypes.length; i++) {
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                methodVisitor.visitLdcInsn(i);
                methodVisitor.visitInsn(Opcodes.AALOAD);
                AsmSupport.checkCast(methodVisitor, parameterTypes[i], targetMethod.getParameterType(i));
            }
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    ownerType.getInternalName(),
                    targetMethod.getName(),
                    targetMethod.getDescriptor(),
                    false
            );
            if (void.class.equals(method.getReturnType())) {
                methodVisitor.visitInsn(Opcodes.ACONST_NULL);
            }
            methodVisitor.visitInsn(Opcodes.ARETURN);
            maxStack = parameterTypes.length == 0 ? 1 : parameterTypes.length + 1;
            methodVisitor.visitMaxs(
                    maxStack,
                    2
            );
            methodVisitor.visitEnd();

            classWriter.visitEnd();

            return ByteCodeLoader.loadClass(classWriter.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
