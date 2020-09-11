package xyz.srclab.common.bytecode.provider.invoke.asm.spring;

import org.springframework.asm.ClassWriter;
import org.springframework.asm.FieldVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import xyz.srclab.common.array.ArrayKit;
import xyz.srclab.common.bytecode.*;
import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerHelper;
import xyz.srclab.common.collection.ListOps;
import xyz.srclab.common.invoke.ConstructorInvoker;

import java.lang.reflect.Constructor;
import java.util.Collections;

final class ConstructorInvokerClassGenerator {

    public static Class<?> generateClass(Constructor<?> constructor) {
        try {
            String newClassName =
                    AsmInvokerHelper.generateConstructorInvokerClassName(constructor, AsmSupport.GENERATOR_NAME);
            BRefType constructorClass = new BRefType(constructor.getClass());
            BRefType constructorInvokerInterface = new BRefType(ConstructorInvoker.class);
            BRefType targetType = new BRefType(constructor.getDeclaringClass());
            constructorClass.addGenericTypes(targetType);
            constructorInvokerInterface.addGenericTypes(targetType);

            BNewType newTypeClass = new BNewType(
                    newClassName,
                    null,
                    null,
                    Collections.singletonList(constructorInvokerInterface)
            );

            ClassWriter classWriter = new ClassWriter(0);
            classWriter.visit(
                    Opcodes.V1_8,
                    Opcodes.ACC_PUBLIC,
                    newTypeClass.getInternalName(),
                    newTypeClass.getSignature(),
                    newTypeClass.getSuperClass().getInternalName(),
                    ArrayKit.map(newTypeClass.getInterfaces(), String.class, BRefType::getInternalName)
            );

            // private Constructor constructor;
            FieldVisitor fieldVisitor = classWriter.visitField(
                    Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                    "constructor",
                    constructorClass.getDescriptor(),
                    constructorClass.getSignature(),
                    null
            );
            fieldVisitor.visitEnd();

            // <init>(Constructor constructor)
            BMethod init = new BMethod(
                    ByteCodeHelper.OBJECT_INIT.getName(),
                    null,
                    Collections.singletonList(constructorClass),
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
                    "constructor",
                    constructorClass.getDescriptor()
            );
            methodVisitor.visitInsn(Opcodes.RETURN);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();

            //Constructor getConstructor()
            BMethod getConstructorMethod =
                    new BMethod("getConstructor", constructorClass, null, null);
            methodVisitor = classWriter.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    getConstructorMethod.getName(),
                    getConstructorMethod.getDescriptor(),
                    getConstructorMethod.getSignature(),
                    null
            );
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitFieldInsn(
                    Opcodes.GETFIELD,
                    newTypeClass.getInternalName(),
                    "constructor",
                    constructorClass.getDescriptor()
            );
            methodVisitor.visitInsn(Opcodes.ARETURN);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();

            //T invoke()
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            BMethod targetConstructorMethod = new BMethod(
                    ByteCodeHelper.OBJECT_INIT.getName(),
                    null,
                    ListOps.map(parameterTypes, BRefType::new),
                    null
            );
            BMethod invokeMethod = new BMethod(
                    "invoke",
                    targetType,
                    Collections.singletonList(ByteCodeHelper.OBJECT_ARRAY),
                    null
            );
            methodVisitor = classWriter.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    invokeMethod.getName(),
                    invokeMethod.getDescriptor(),
                    invokeMethod.getSignature(),
                    null
            );
            methodVisitor.visitTypeInsn(Opcodes.NEW, targetType.getInternalName());
            methodVisitor.visitInsn(Opcodes.DUP);
            for (int i = 0; i < parameterTypes.length; i++) {
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                methodVisitor.visitLdcInsn(i);
                methodVisitor.visitInsn(Opcodes.AALOAD);
                AsmSupport.checkCast(methodVisitor, parameterTypes[i], targetConstructorMethod.getParameterType(i));
            }
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    targetType.getInternalName(),
                    targetConstructorMethod.getName(),
                    targetConstructorMethod.getDescriptor(),
                    false
            );
            methodVisitor.visitInsn(Opcodes.ARETURN);
            int maxStack = parameterTypes.length == 0 ? 2 : 2 + parameterTypes.length + 1;
            methodVisitor.visitMaxs(
                    maxStack,
                    2
            );
            methodVisitor.visitEnd();
            //Object invoke()
            BMethod invokeMethodBridge = new BMethod(
                    "invoke",
                    ByteCodeHelper.OBJECT,
                    Collections.singletonList(ByteCodeHelper.OBJECT_ARRAY),
                    null
            );
            methodVisitor = classWriter.visitMethod(
                    Opcodes.ACC_PUBLIC | Opcodes.ACC_BRIDGE,
                    invokeMethodBridge.getName(),
                    invokeMethodBridge.getDescriptor(),
                    invokeMethodBridge.getSignature(),
                    null
            );
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    newTypeClass.getInternalName(),
                    invokeMethod.getName(),
                    invokeMethod.getDescriptor(),
                    false
            );
            methodVisitor.visitInsn(Opcodes.ARETURN);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();

            classWriter.visitEnd();

            return ByteCodeLoader.loadClass(classWriter.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
