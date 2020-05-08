package xyz.srclab.common.bytecode.provider.invoke.asm.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.srclab.common.bytecode.*;
import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerHelper;
import xyz.srclab.common.invoke.ConstructorInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

final class ConstructorInvokerClassGenerator {

    public static Class<?> generateClass(Constructor<?> constructor) {
        try {
            String newClassName =
                    AsmInvokerHelper.generateConstructorInvokerClassName(constructor, GenerateConstants.GENERATOR_NAME);
            BType constructorType = new BType(constructor.getClass());
            BType generatedType = new BType(newClassName);
            BType interfaceType = new BType(ConstructorInvoker.class);
            BType targetType = new BType(constructor.getDeclaringClass());
            constructorType.addGenericTypes(targetType);
            interfaceType.addGenericTypes(targetType);

            ClassWriter classWriter = new ClassWriter(0);
            classWriter.visit(
                    Opcodes.V1_8,
                    Opcodes.ACC_PUBLIC,
                    generatedType.getInternalName(),
                    BTypeHelper.OBJECT_TYPE.getSignature() + interfaceType.getSignature(),
                    BTypeHelper.OBJECT_TYPE.getInternalName(),
                    new String[]{interfaceType.getInternalName()}
            );

            // private Constructor constructor;
            FieldVisitor fieldVisitor = classWriter.visitField(
                    Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                    "constructor",
                    constructorType.getDescriptor(),
                    constructorType.getSignature(),
                    null
            );
            fieldVisitor.visitEnd();

            // <init>(Constructor constructor)
            MethodVisitor methodVisitor = classWriter.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    ByteCodeConstants.CONSTRUCTOR_NAME,
                    ByteCodeHelper.getMethodDescriptor(new ByteCodeType[]{constructorType}, BTypeHelper.PRIMITIVE_VOID_TYPE),
                    null,
                    null
            );
            // super();
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    BTypeHelper.OBJECT_TYPE.getInternalName(),
                    ByteCodeConstants.CONSTRUCTOR_NAME,
                    ByteCodeConstants.EMPTY_CONSTRUCTOR_DESCRIPTOR,
                    false
            );
            // this.constructor = constructor
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitFieldInsn(
                    Opcodes.PUTFIELD,
                    generatedType.getInternalName(),
                    "constructor",
                    constructorType.getDescriptor()
            );
            methodVisitor.visitInsn(Opcodes.RETURN);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();

            Method getConstructorMethod = ConstructorInvoker.class.getMethod("getConstructor");
            methodVisitor = classWriter.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    getConstructorMethod.getName(),
                    ByteCodeHelper.getMethodDescriptor(BTypeHelper.EMPTY_TYPE_ARRAY, constructorType),
                    ByteCodeHelper.getMethodSignature(BTypeHelper.EMPTY_TYPE_ARRAY, constructorType),
                    null
            );
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitFieldInsn(
                    Opcodes.GETFIELD,
                    generatedType.getInternalName(),
                    "constructor",
                    constructorType.getDescriptor()
            );
            methodVisitor.visitInsn(Opcodes.ARETURN);
            methodVisitor.visitMaxs(1, 1);
            methodVisitor.visitEnd();

            Method invokeMethod = ConstructorInvoker.class.getMethod("invoke", Object[].class);
            methodVisitor = classWriter.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    invokeMethod.getName(),
                    ByteCodeHelper.getMethodDescriptor(BTypeHelper.OBJECT_ARRAY_PARAMETER, targetType),
                    ByteCodeHelper.getMethodSignature(BTypeHelper.OBJECT_ARRAY_PARAMETER, targetType),
                    null
            );
            methodVisitor.visitTypeInsn(Opcodes.NEW, targetType.getInternalName());
            methodVisitor.visitInsn(Opcodes.DUP);
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    targetType.getInternalName(),
                    ByteCodeConstants.CONSTRUCTOR_NAME,
                    ByteCodeConstants.EMPTY_CONSTRUCTOR_DESCRIPTOR,
                    false
            );
            methodVisitor.visitInsn(Opcodes.ARETURN);
            methodVisitor.visitMaxs(2, 2);
            methodVisitor.visitEnd();
            methodVisitor = classWriter.visitMethod(
                    Opcodes.ACC_PUBLIC | Opcodes.ACC_BRIDGE,
                    invokeMethod.getName(),
                    ByteCodeHelper.getMethodDescriptor(BTypeHelper.OBJECT_ARRAY_PARAMETER, BTypeHelper.OBJECT_TYPE),
                    ByteCodeHelper.getMethodSignature(BTypeHelper.OBJECT_ARRAY_PARAMETER, BTypeHelper.OBJECT_TYPE),
                    null
            );
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    generatedType.getInternalName(),
                    invokeMethod.getName(),
                    ByteCodeHelper.getMethodDescriptor(BTypeHelper.OBJECT_ARRAY_PARAMETER, targetType),
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
