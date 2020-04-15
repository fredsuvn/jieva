package xyz.srclab.common.bytecode.provider.cglib;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.bytecode.enhance.EnhancedClass;
import xyz.srclab.common.collection.iterable.IterableHelper;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.reflect.invoke.MethodInvoker;
import xyz.srclab.common.reflect.method.MethodHelper;
import xyz.srclab.common.reflect.method.ProxyMethod;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

final class CglibProxyClassBuilder<T> extends CacheStateBuilder<EnhancedClass<T>> implements EnhancedClass.Builder<T> {

    private final CglibAdaptor cglibAdaptor;
    private final Class<?> superClass;
    private final List<Class<?>> interfaces = new LinkedList<>();
    private final List<MethodInfo> overrideMethods = new LinkedList<>();

    public CglibProxyClassBuilder(CglibAdaptor cglibAdaptor, Class<?> superClass) {
        this.cglibAdaptor = cglibAdaptor;
        this.superClass = superClass;
    }

    @Override
    public CglibProxyClassBuilder<T> addInterfaces(Iterable<Class<?>> interfaces) {
        this.changeState();
        this.interfaces.addAll(IterableHelper.asList(interfaces));
        return this;
    }

    @Override
    public EnhancedClass.Builder<T> overrideMethod(
            String name, Class<?>[] parameterTypes, ProxyMethod proxyMethod) {
        this.changeState();
        this.overrideMethods.add(new MethodInfo(name, parameterTypes, proxyMethod));
        return this;
    }

    @Override
    protected EnhancedClass<T> buildNew() {
        return new EnhancedClassImpl<>(buildEnhancer());
    }

    private Enhancer buildEnhancer() {
        Callback[] callbacks = new Callback[overrideMethods.size() + 1];
        int i = 0;
        for (MethodInfo overrideMethod : overrideMethods) {
            callbacks[i++] = buildMethodInterceptor(overrideMethod);
        }
        callbacks[i] = NoOp.INSTANCE;
        CallbackFilter callbackFilter = method -> {
            int j = 0;
            for (MethodInfo methodInfo : overrideMethods) {
                if (method.getName().equals(methodInfo.getName())
                        && Arrays.deepEquals(method.getParameterTypes(), methodInfo.getParameterTypes())) {
                    return j;
                }
                j++;
            }
            return callbacks.length - 1;
        };

        Enhancer enhancer = cglibAdaptor.newEnhancer();
        enhancer.setSuperclass(superClass);
        if (!interfaces.isEmpty()) {
            enhancer.setInterfaces(interfaces.toArray(MethodHelper.EMPTY_PARAMETER_TYPES));
        }
        enhancer.setCallbacks(callbacks);
        enhancer.setCallbackFilter(callbackFilter);
        return enhancer;
    }

    private MethodInterceptor buildMethodInterceptor(MethodInfo methodInfo) {
        MethodInterceptor methodInterceptor = (object, method, args, proxy) ->
                methodInfo.getProxyMethod().invoke(object, args, method, new MethodInvoker() {
                    @Override
                    public @Nullable Object invoke(Object object, Object... args) {
                        try {
                            return proxy.invokeSuper(object, args);
                        } catch (Throwable throwable) {
                            throw new ExceptionWrapper(throwable);
                        }
                    }
                });
        return methodInterceptor;
    }

    private static final class EnhancedClassImpl<T> implements EnhancedClass<T> {

        private final Enhancer enhancer;

        private EnhancedClassImpl(Enhancer enhancer) {
            this.enhancer = enhancer;
        }

        @Override
        public T newInstance() {
            return (T) enhancer.create();
        }

        @Override
        public T newInstance(Class<?>[] parameterTypes, Object[] args) {
            return (T) enhancer.create(parameterTypes, args);
        }
    }

    private static final class MethodInfo {

        private final String name;
        private final Class<?>[] parameterTypes;
        private final ProxyMethod proxyMethod;

        MethodInfo(String name, Class<?>[] parameterTypes, ProxyMethod proxyMethod) {
            this.name = name;
            this.parameterTypes = parameterTypes;
            this.proxyMethod = proxyMethod;
        }

        public String getName() {
            return name;
        }

        public Class<?>[] getParameterTypes() {
            return parameterTypes;
        }

        public ProxyMethod getProxyMethod() {
            return proxyMethod;
        }
    }
}
