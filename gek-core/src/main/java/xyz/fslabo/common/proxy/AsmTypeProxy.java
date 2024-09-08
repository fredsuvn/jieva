package xyz.fslabo.common.proxy;

import org.objectweb.asm.ClassWriter;
import xyz.fslabo.annotations.Nullable;
import xyz.fslabo.common.coll.JieArray;
import xyz.fslabo.common.coll.JieColl;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Predicate;

import org.objectweb.asm.Opcodes;
import xyz.fslabo.common.reflect.JieJvm;

public class AsmTypeProxy implements ProxyProvider{

    private static final AtomicLong counter = new AtomicLong();

    @Override
    public <T> T newProxyInstance(
        @Nullable ClassLoader loader,
        @Nullable Class<?> superClass,
        @Nullable Iterable<Class<?>> interfaces,
        Function<Method, @Nullable ProxyInvoker> invokerSupplier
    ) {

        return null;
    }

    private Class<?> buildClass(
        @Nullable ClassLoader loader,
        @Nullable Class<?> superClass,
        @Nullable Iterable<Class<?>> interfaces,
        Function<Method, @Nullable ProxyInvoker> invokerSupplier
    ) {
        if (superClass == null && JieColl.isEmpty(interfaces)) {
            throw new ProxyException("No super class or interface to proxy.");
        }
        ClassWriter cw = new ClassWriter(0);
        cw.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC,
            getClass().getName() + "$" + counter.getAndIncrement(),
            generateSignature(superClass, interfaces),
             JieJvm.getInternalName(superClass == null ?Object.class:superClass) ,
            interfaces == null ? null : JieColl.stream(interfaces).map(JieJvm::getInternalName).toArray(String[]::new)
        );

        return null;
    }

    private String generateSignature(@Nullable Class<?> superClass, @Nullable Iterable<Class<?>> interfaces) {
        StringBuilder sb = new StringBuilder();
        sb.append(JieJvm.getSignature(superClass == null ? Object.class : superClass));
        if (interfaces != null) {
            for (Class<?> i : interfaces) {
                sb.append(JieJvm.getSignature(i));
            }
        }
        return sb.toString();
    }














}
