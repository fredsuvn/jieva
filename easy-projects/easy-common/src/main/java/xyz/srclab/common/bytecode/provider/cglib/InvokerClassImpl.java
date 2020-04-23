package xyz.srclab.common.bytecode.provider.cglib;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastConstructor;
import net.sf.cglib.reflect.FastMethod;
import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.lang.key.KeySupport;
import xyz.srclab.common.bytecode.InvokerClass;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.reflect.signature.SignatureHelper;
import xyz.srclab.common.invoke.ConstructorInvoker;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.reflect.InvocationTargetException;

/**
 * @author sunqian
 */
final class InvokerClassImpl<T> implements InvokerClass<T> {

    private static final Cache<Class<?>, InvokerClassImpl<?>> classCache = new ThreadLocalCache<>();
    private static final Cache<Object, ConstructorInvoker<?>> constructorCache = new ThreadLocalCache<>();
    private static final Cache<String, MethodInvoker> methodInvokerCache = new ThreadLocalCache<>();

    static <T> InvokerClassImpl<T> getInvokerClassImpl(Class<T> type) {
        InvokerClassImpl<?> impl = classCache.getNonNull(
                type,
                InvokerClassImpl::new
        );
        return (InvokerClassImpl<T>) impl;
    }

    private final FastClass fastClass;

    private InvokerClassImpl(Class<?> type) {
        this.fastClass = FastClass.create(type);
    }

    @Override
    public ConstructorInvoker<T> getConstructorInvoker(Class<?>... parameterTypes) {
        Object key = KeySupport.buildKey(parameterTypes);
        ConstructorInvoker<?> constructorInvoker = constructorCache.getNonNull(
                key,
                k -> new ConstructorInvokerImpl(parameterTypes)
        );
        return (ConstructorInvoker<T>) constructorInvoker;
    }

    @Override
    public MethodInvoker getMethodInvoker(String methodName, Class<?>... parameterTypes) {
        String key = SignatureHelper.signMethod(methodName, parameterTypes);
        return methodInvokerCache.getNonNull(
                key,
                k -> new MethodInvokerImpl(methodName, parameterTypes)
        );
    }

    private final class ConstructorInvokerImpl implements ConstructorInvoker<T> {

        private final FastConstructor fastConstructor;

        private ConstructorInvokerImpl(Class<?>... parameterTypes) {
            this.fastConstructor = fastClass.getConstructor(parameterTypes);
        }

        @Override
        public T invoke(Object... args) {
            try {
                return (T) fastConstructor.newInstance(args);
            } catch (InvocationTargetException e) {
                throw new ExceptionWrapper(e);
            }
        }
    }

    private final class MethodInvokerImpl implements MethodInvoker {

        private final FastMethod fastMethod;

        private MethodInvokerImpl(String methodName, Class<?>... parameterTypes) {
            this.fastMethod = fastClass.getMethod(methodName, parameterTypes);
        }

        @Override
        public @Nullable Object invoke(@Nullable Object object, Object... args) {
            try {
                return fastMethod.invoke(object, args);
            } catch (InvocationTargetException e) {
                throw new ExceptionWrapper(e);
            }
        }
    }
}
