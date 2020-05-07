package xyz.srclab.common.bytecode.provider.invoke.asm.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import xyz.srclab.common.bytecode.ByteCodeHelper;
import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerGenerator;
import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerHelper;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * @author sunqian
 */
final class AsmInvokerGeneratorImpl implements AsmInvokerGenerator {

    private static final String GENERATOR_NAME = "JdkAsm";

    @Override
    public <T> ConstructorInvoker<T> newConstructorInvoker(Constructor<T> constructor) {
        try {

            String newClassName =
                    AsmInvokerHelper.generateConstructorInvokerClassName(constructor, GENERATOR_NAME);
            String newClassInternalName = ByteCodeHelper.getTypeInternalName(newClassName);
            String newClassSignature = "Ljava/lang/Object;Lxyz/srclab/common/invoke/ConstructorInvoker<Lxyz/srclab/common/bytecode/provider/invoke/asm/jdk/JdkAsmInvokerGeneratorImpl$A;>;";
            String constructorInvokerInternalName = ConstructorInvoker.class.getName().replaceAll("\\.", "/");
            ClassWriter classWriter = new ClassWriter(0);

            classWriter.visit(
                    Opcodes.V1_8,
                    Opcodes.ACC_PUBLIC,
                    newClassInternalName,
                    newClassSignature,
                    null,
                    new String[]{constructorInvokerInternalName}
            );

            classWriter.visitField(
                    Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                    "constructor",
                    "Ljava/lang/reflect/Constructor",
                    "Ljava/lang/reflect/Constructor<Lxyz/srclab/common/bytecode/provider/invoke/asm/jdk/JdkAsmInvokerGeneratorImpl$A;>;",
                    null
            );
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

    public static void main(String[] args) throws Exception {
        //Constructor<A> constructor = A.class.getConstructor();
        //ConstructorInvoker<A<String>> constructorInvoker = cast(new AConstructorInvoker());
        //FastClass fastClass = FastClass.create(A.class);
        //FastConstructor fastConstructor = fastClass.getConstructor(ReflectConstants.EMPTY_PARAMETER_TYPES);
        //long times = 10000000000L;
        //showTime("Reflect invoker", times, constructor::newInstance);
        //showTime("Static invoker", times, constructorInvoker::invoke);
        //showTime("Fast constructor invoker", times, fastConstructor::newInstance);

        System.out.println("--------");

        //BTypeVariable
        //BNewType bNewType = BNewType.of(
        //        "a.b.C",
        //
        //)
    }

    private static <T> T cast(Object any) {
        return (T) any;
    }

    public static class A<T> {

    }

    public static final class AConstructorInvoker implements ConstructorInvoker<A> {

        private final Constructor<A> constructor;

        {
            try {
                constructor = A.class.getConstructor();
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public Constructor<A> getConstructor() {
            return constructor;
        }

        @Override
        public A invoke(Object... args) {
            return new A();
        }
    }
}
