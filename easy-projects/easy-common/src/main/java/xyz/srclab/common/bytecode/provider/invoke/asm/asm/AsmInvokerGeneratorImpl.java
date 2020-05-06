package xyz.srclab.common.bytecode.provider.invoke.asm.asm;

import org.objectweb.asm.*;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.base.Context;
import xyz.srclab.common.bytecode.ByteCodeHelper;
import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerGenerator;
import xyz.srclab.common.bytecode.provider.invoke.asm.AsmInvokerHelper;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.FunctionInvoker;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * @author sunqian
 */
final class AsmInvokerGeneratorImpl implements AsmInvokerGenerator {

    private static final String GENERATOR_NAME = "JdkAsm";

    @Override
    public <T> ConstructorInvoker<T> newConstructorInvoker(Constructor<T> constructor) {
        try {
            ClassReader classReader = new ClassReader(AConstructorInvoker.class.getName());
            classReader.accept(new ReadVisitor(), ClassReader.SKIP_CODE);

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

        ClassReader classReader = new ClassReader(Abc.class.getName());
        classReader.accept(new ReadVisitor(), ClassReader.SKIP_CODE);

        System.out.println("--------");

        //BTypeVariable
        //BNewType bNewType = BNewType.of(
        //        "a.b.C",
        //
        //)
    }

    private static void showTime(String title, long times, RunThrow runnable) {
        try {
            long startTime = Context.millis();
            for (long i = 0; i < times; i++) {
                runnable.run();
            }
            long endTime = Context.millis();
            System.out.println(title + ": " + (endTime - startTime));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
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

    interface RunThrow {

        void run() throws Exception;
    }

    static final class ReadVisitor extends ClassVisitor {

        public ReadVisitor() {
            super(Opcodes.ASM7);
        }

        @Override
        public void visit(int i, int i1, String s, String s1, String s2, String[] strings) {
            System.out.println("class: " + s + " : " + s1 + " : " + s2 + " : " + Arrays.toString(strings));
            super.visit(i, i1, s, s1, s2, strings);
        }

        @Override
        public FieldVisitor visitField(int i, String s, String s1, String s2, Object o) {
            System.out.println("field: " + s + " : " + s1 + " : " + s2 + " : " + o);
            return super.visitField(i, s, s1, s2, o);
        }

        @Override
        public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings) {
            System.out.println("method: " + s + " : " + s1 + " : " + s2 + " : " + Arrays.toString(strings));
            return super.visitMethod(i, s, s1, s2, strings);
        }
    }

    public static class Fff<T> {

    }

    public static class Abc<T extends List<V> & Runnable & Callable<V>, U extends Set<String>, V extends T> extends Fff<U> implements Function<U, String> {

        private final @Nullable U u = null;

        @Override
        public String apply(U strings) {
            return null;
        }

        public <S extends Integer & Runnable> List<? extends S>[] ss(
                String string, S s, List<? super S> list, List<? extends String> list2,
                List<String> list3, List<S> list4, List<S[]> list5) {
            return null;
        }
    }
}
