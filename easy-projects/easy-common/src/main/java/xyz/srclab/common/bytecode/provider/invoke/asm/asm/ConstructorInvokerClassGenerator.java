package xyz.srclab.common.bytecode.provider.invoke.asm.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.srclab.common.array.ArrayHelper;
import xyz.srclab.common.bytecode.*;
import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerHelper;
import xyz.srclab.common.invoke.ConstructorInvoker;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;

final class ConstructorInvokerClassGenerator {

    public static Class<?> generateClass(Constructor<?> constructor) {
        try {
            String newClassName =
                    AsmInvokerHelper.generateConstructorInvokerClassName(constructor, GenerateSupper.GENERATOR_NAME);
            BType constructorClass = new BType(constructor.getClass());
            BType constructorInvokerInterface = new BType(ConstructorInvoker.class);
            BType targetType = new BType(constructor.getDeclaringClass());
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
                    ArrayHelper.toArray(newTypeClass.getInterfaces(), String.class, BType::getInternalName)
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
            BMethod invokeMethod = new BMethod(
                    "invoke",
                    targetType,
                    Arrays.asList(ByteCodeHelper.OBJECT_ARRAY_PARAMETER),
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
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    targetType.getInternalName(),
                    ByteCodeHelper.OBJECT_INIT.getName(),
                    ByteCodeHelper.OBJECT_INIT.getDescriptor(),
                    false
            );
            methodVisitor.visitInsn(Opcodes.ARETURN);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
            //Object invoke()
            BMethod invokeMethodBridge = new BMethod(
                    "invoke",
                    ByteCodeHelper.OBJECT,
                    Arrays.asList(ByteCodeHelper.OBJECT_ARRAY_PARAMETER),
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
