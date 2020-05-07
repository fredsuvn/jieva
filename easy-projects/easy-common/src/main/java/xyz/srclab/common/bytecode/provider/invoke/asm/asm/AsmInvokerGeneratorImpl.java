package xyz.srclab.common.bytecode.provider.invoke.asm.asm;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastConstructor;
import net.sf.cglib.reflect.FastMethod;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import xyz.srclab.annotation.Nullable;
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
        Constructor<A> constructor = A.class.getConstructor();
        Method method = A.class.getMethod("doSomething");
        ConstructorInvoker<A<String>> constructorInvoker = cast(new AConstructorInvoker(constructor));
        MethodInvoker methodInvoker = new AMethodInvoker(method);
        FastClass fastClass = FastClass.create(A.class);
        FastConstructor fastConstructor = fastClass.getConstructor(ReflectConstants.EMPTY_PARAMETER_TYPES);
        FastMethod fastMethod = fastClass.getMethod(method);
        long times = 3000000000L;
        A a = new A();
        Object[] empty = new Object[0];
        Performer.doPerforms(Arrays.asList(
                new PerformInfo("Direct constructor invoker", () -> new A()),
                new PerformInfo("Direct method invoker", () -> a.doSomething()),
                new PerformInfo("Reflect constructor invoker", () -> {
                    try {
                        constructor.newInstance();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }),
                new PerformInfo("Reflect method invoker", () -> {
                    try {
                        method.invoke(a);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }),
                new PerformInfo("Static constructor invoker", () -> constructorInvoker.invoke()),
                new PerformInfo("Static method invoker", () -> methodInvoker.invoke(a)),
                new PerformInfo("Fast constructor invoker", () -> {
                    try {
                        fastConstructor.newInstance();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                }),
                new PerformInfo("Fast method invoker", () -> {
                    try {
                        fastMethod.invoke(a, empty);
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                })
                ),
                times,
                System.out,
                ChronoUnit.MILLIS
        );

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

        public A() {
        }

        public Object doSomething() {
            return null;
        }
    }

    public static final class AConstructorInvoker implements ConstructorInvoker<A> {

        private final Constructor<A> constructor;

        public AConstructorInvoker(Constructor<A> constructor) {
            this.constructor = constructor;
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

    public static final class AMethodInvoker implements MethodInvoker {

        private final Method method;

        public AMethodInvoker(Method method) {
            this.method = method;
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public Object invoke(@Nullable Object object, Object... args) {
            return ((A) object).doSomething();
        }
    }
}
