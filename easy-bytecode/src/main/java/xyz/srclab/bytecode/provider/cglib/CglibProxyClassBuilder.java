package xyz.srclab.bytecode.provider.cglib;

import org.apache.commons.lang3.ArrayUtils;
import xyz.srclab.bytecode.proxy.ProxyClass;
import xyz.srclab.common.builder.CacheStateBuilder;
import xyz.srclab.common.collection.iterable.IterableHelper;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.reflect.method.MethodBody;
import xyz.srclab.common.reflect.method.MethodDefinition;
import xyz.srclab.common.reflect.method.MethodInvoker;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class CglibProxyClassBuilder<T> extends CacheStateBuilder<ProxyClass<T>> implements ProxyClass.Builder<T> {

    public static <T> CglibProxyClassBuilder<T> newBuilder(Class<?> superClass) {
        return new CglibProxyClassBuilder<>(superClass);
    }

    private final Class<?> superClass;
    private final List<Class<?>> interfaces = new LinkedList<>();
    private final List<MethodInfo> overrideMethods = new LinkedList<>();

    public CglibProxyClassBuilder(Class<?> superClass) {
        this.superClass = superClass;
    }

    @Override
    public CglibProxyClassBuilder<T> addInterfaces(Iterable<Class<?>> interfaces) {
        this.interfaces.addAll(IterableHelper.castToList(interfaces));
        return this;
    }

    @Override
    public <R> CglibProxyClassBuilder<T> overrideMethod(String name, Class<?>[] parameterTypes, MethodBody<R> methodBody) {
        this.overrideMethods.add(new MethodInfo(name, parameterTypes, methodBody));
        return this;
    }

    @Override
    public <R> CglibProxyClassBuilder<T> overrideMethod(MethodDefinition<R> methodDefinition) {
        this.overrideMethods.add(new MethodInfo(
                methodDefinition.getName(), methodDefinition.getParameterTypes(), methodDefinition.getBody()));
        return this;
    }

    @Override
    protected ProxyClass<T> buildNew() {
        return new ProxyClassImpl<>(buildEnhancer());
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

        Enhancer enhancer = CglibAdaptor.getInstance().newEnhancer();
        enhancer.setSuperclass(superClass);
        if (!interfaces.isEmpty()) {
            enhancer.setInterfaces(interfaces.toArray(ArrayUtils.EMPTY_CLASS_ARRAY));
        }
        enhancer.setCallbacks(callbacks);
        enhancer.setCallbackFilter(callbackFilter);
        return enhancer;
    }

    private MethodInterceptor buildMethodInterceptor(MethodInfo methodInfo) {
        MethodInterceptor methodInterceptor = (object, method1, args, proxy) ->
                methodInfo.getBody().invoke(object, method1, args, new MethodInvoker() {
                    @Override
                    public Object invoke(Object object) {
                        try {
                            return proxy.invokeSuper(object, args);
                        } catch (Throwable throwable) {
                            throw new ExceptionWrapper(throwable);
                        }
                    }

                    @Override
                    public Object invoke(Object object, Object[] args) {
                        try {
                            return proxy.invokeSuper(object, args);
                        } catch (Throwable throwable) {
                            throw new ExceptionWrapper(throwable);
                        }
                    }
                });
        return methodInterceptor;
    }

    private static final class ProxyClassImpl<T> implements ProxyClass<T> {

        private final Enhancer enhancer;

        private ProxyClassImpl(Enhancer enhancer) {
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
        private final MethodBody<?> body;

        MethodInfo(String name, Class<?>[] parameterTypes, MethodBody<?> body) {
            this.name = name;
            this.parameterTypes = parameterTypes;
            this.body = body;
        }

        public String getName() {
            return name;
        }

        public Class<?>[] getParameterTypes() {
            return parameterTypes;
        }

        public MethodBody<?> getBody() {
            return body;
        }
    }
}
