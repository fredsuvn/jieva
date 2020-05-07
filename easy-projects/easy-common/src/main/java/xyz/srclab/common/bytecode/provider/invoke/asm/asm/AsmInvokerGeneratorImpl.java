package xyz.srclab.common.bytecode.provider.invoke.asm.asm;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastConstructor;
import net.sf.cglib.reflect.FastMethod;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.bytecode.BType;
import xyz.srclab.common.bytecode.ByteCodeConstants;
import xyz.srclab.common.bytecode.ByteCodeHelper;
import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerGenerator;
import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerHelper;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.MethodInvoker;
import xyz.srclab.common.reflect.ReflectConstants;
import xyz.srclab.test.perform.PerformInfo;
import xyz.srclab.test.perform.Performer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

/**
 * @author sunqian
 */
final class AsmInvokerGeneratorImpl implements AsmInvokerGenerator {

    private static final String GENERATOR_NAME = "Asm";

    @Override
    public <T> ConstructorInvoker<T> newConstructorInvoker(Constructor<T> constructor) {
        try {
            String newClassName =
                    AsmInvokerHelper.generateConstructorInvokerClassName(constructor, GENERATOR_NAME);
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
                    BType.OBJECT_TYPE.getSignature() + interfaceType.getSignature(),
                    null,
                    new String[]{interfaceType.getInternalName()}
            );
            classWriter.visitField(
                    Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                    "constructor",
                    constructorType.getDescriptor(),
                    constructorType.getSignature(),
                    null
            );
            classWriter.visitMethod(
                    Opcodes.ACC_PUBLIC,
                    ByteCodeConstants.CONSTRUCTOR_NAME,

            )
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return null;
    }

    @Override
    public MethodInvoker newMethodInvoker(Method method) {
        return null;
    }

    @Override
    public FunctionInvoker newFunctionInvoker(Method method) {
        return null;
    }
}
