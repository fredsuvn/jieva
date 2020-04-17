package xyz.srclab.common.invoke.provider.bytecode;

import xyz.srclab.common.bytecode.provider.ByteCodeProvider;
import xyz.srclab.common.bytecode.provider.ByteCodeProviderManager;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.InvokerProvider;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ByteCodeInvokerProvider implements InvokerProvider {

    public static final ByteCodeInvokerProvider INSTANCE = new ByteCodeInvokerProvider();

    private final ByteCodeProvider byteCodeProvider;

    public ByteCodeInvokerProvider() {
        this(ByteCodeProviderManager.INSTANCE.getProvider());
    }

    public ByteCodeInvokerProvider(ByteCodeProvider byteCodeProvider) {
        this.byteCodeProvider = byteCodeProvider;
    }

    @Override
    public <T> ConstructorInvoker<T> getConstructorInvoker(Constructor<T> constructor) {
        Class<T> type = constructor.getDeclaringClass();
        return getConstructorInvoker(type, constructor.getParameterTypes());
    }

    @Override
    public <T> ConstructorInvoker<T> getConstructorInvoker(Class<T> type, Class<?>... parameterTypes) {
        return byteCodeProvider.getInvokerClass(type).getConstructorInvoker(parameterTypes);
    }

    @Override
    public MethodInvoker getMethodInvoker(Method method) {
        Class<?> type = method.getDeclaringClass();
        return getMethodInvoker(type, method.getName(), method.getParameterTypes());
    }

    @Override
    public MethodInvoker getMethodInvoker(Class<?> type, String methodName, Class<?>... parameterTypes) {
        return byteCodeProvider.getInvokerClass(type).getMethodInvoker(methodName, parameterTypes);
    }
}
