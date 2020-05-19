package xyz.srclab.common.bytecode.provider.invoke.asm.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.srclab.common.bytecode.BRefType;
import xyz.srclab.common.bytecode.BType;
import xyz.srclab.common.reflect.ClassHelper;

/**
 * @author sunqian
 */
final class AsmSupport {

    static final String GENERATOR_NAME = "Asm";

    static void checkCast(MethodVisitor methodVisitor, Class<?> parameterType, BType bType) {
        if (parameterType.isPrimitive()) {
            checkCastPrimitive(methodVisitor, parameterType, bType);
            return;
        }
        methodVisitor.visitTypeInsn(
                Opcodes.CHECKCAST,
                bType.getInternalName()
        );
    }

    private static void checkCastPrimitive(
            MethodVisitor methodVisitor, Class<?> parameterType, BType primitiveType) {
        Class<?> wrapperClass = ClassHelper.primitiveToWrapper(parameterType);
        BRefType wrapperType = new BRefType(wrapperClass);
        methodVisitor.visitTypeInsn(
                Opcodes.CHECKCAST,
                wrapperType.getInternalName()
        );
        String valueMethodName;
        String valueMethodDescriptor;
        switch (primitiveType.getDescriptor()) {
            case "Z":
                valueMethodName = "booleanValue";
                valueMethodDescriptor = "()Z";
                break;
            case "B":
                valueMethodName = "byteValue";
                valueMethodDescriptor = "()B";
                break;
            case "S":
                valueMethodName = "shortValue";
                valueMethodDescriptor = "()S";
                break;
            case "C":
                valueMethodName = "charValue";
                valueMethodDescriptor = "()C";
                break;
            case "I":
                valueMethodName = "intValue";
                valueMethodDescriptor = "()I";
                break;
            case "J":
                valueMethodName = "longValue";
                valueMethodDescriptor = "()J";
                break;
            case "F":
                valueMethodName = "floatValue";
                valueMethodDescriptor = "()F";
                break;
            case "D":
                valueMethodName = "doubleValue";
                valueMethodDescriptor = "()D";
                break;
            default:
                throw new IllegalArgumentException("Illegal primitive type: " + primitiveType.getDescriptor());
        }
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                wrapperType.getInternalName(),
                valueMethodName,
                valueMethodDescriptor,
                false
        );
    }
}
